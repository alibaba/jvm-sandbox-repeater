# 用户使用手册

> [jvm-sandbox-repeater](https://github.com/alibaba/jvm-sandbox-repeater) 仅仅提供了录制回放的能力，如果需要完成`业务回归`、`实时监控`、`压测`等平台，后面须要有一个`数据中心`负责采集数据的加工、存储、搜索，repeater-console提供了简单的demo示例；一个`模块管理`平台负责管理JVM-Sandbox各模块生命周期；一个`配置管理`平台负责维护和推送jvm-sandbox-repeater采集所须要的各种配置变更
> 
> 在阿里集团淘系技术质量内部，已有一套完整的体系在持续运行，从17年开始支持了淘系技术质量部的CI、建站、系统重构等多方面质量保障任务，后续如有需要也会考虑把更多的东西开源回馈社区

> 注意：目前项目代码默认启动standalone模式，不需要依赖任何服务端和存储，能够简单快速的实现单机的录制回放，
> 控制单机模式的开关在~/.sandbox-module/cfg/repeater.propertiesrepeat.standalone.mode=true，开启或关闭单机工作模式，关闭单机模式后，配置拉取/消息投递等都依赖repeater.properties中配置的具体url；如不想通过http拉取和消息投递的也可以自己实现`Broadcaster`和`ConfigManager`。稍后我们会公布一份录制回放所需的完整架构图以及jvm-sandbox-repeater在整个体系中的位置供大家工程使用做参考。

## 快速开始

### 1. 本地standalone工作（剥离服务端和存储，本机实现录制/回放）

#### step0 安装sandbox/启动bootstrap

```shell
cd bin
./bootstrap.sh
```
等待SpringBoot应用启动完成 -> `Started Application in 4.797 seconds (JVM running for 6.586)`

#### step1 开始录制

一起喊出我们的 [Slogan](http://127.0.0.1:8001/regress/slogan?Repeat-TraceId=127000000001156034386424510000ed) << 单击打开链接单击打开链接或执行`curl操作`

```shell
curl -s 'http://127.0.0.1:8001/regress/slogan?Repeat-TraceId=127000000001156034386424510000ed'
```

> 是不是看到了Java程序员多年的心声；我们希望让这一刻永远定格；

> 访问链接时，repeater插件通过Repeat-TraceId=127000000001156034386424510000ed，唯一追踪到了这一次请求，后台服务返回了`JAVA是世界上最好的语言!`，repeater把画面定格在了这一秒并将结果和127000000001156034386424510000ed绑定

#### step2 开始回放

"昨日重现"  [Slogan Repeat](http://127.0.0.1:8001/regress/slogan?Repeat-TraceId-X=127000000001156034386424510000ed)  << 单击打开链接或执行`curl操作`

```shell
curl -s 'http://127.0.0.1:8001/regress/slogan?Repeat-TraceId-X=127000000001156034386424510000ed'
```

> 无论我们多少次访问这个地址，都将返回Repeat-TraceId=127000000001156034386424510000ed绑定的录制信息`JAVA是世界上最好的语言!`；如果重新访问[Slogan](http://127.0.0.1:8001/regress/slogan?Repeat-TraceId=127000000001156034386424510000ed)后又会将最新的返回结果绑定到Repeat-TraceId=127000000001156034386424510000ed（为了快速演示，将链路追踪的标志提到参数中进行透传了）

[RegressController](/repeater-console/repeater-console-start/src/main/java/com/alibaba/repeater/console/start/controller/RegressController.java)中提供了更多的测试用例，包括异步servlet、多线程调用、复杂结构返回对象，可以根据slogan类似的方式进行测试。

> 简单揭秘：`/regress/slogan`接口调用了`RegressServiceImpl#slogan`方法

```java

private AtomicInteger sequence = new AtomicInteger(0);

private String[] slogans = new String[]{"JAVA", "Python", "PHP", "C#", "C++", "Javascript", "GO"};

public String slogan() {
    return slogans[sequence.getAndIncrement() % slogans.length] + "是世界上最好的语言!";
}
```

> 仔细查看该方法代码会发现，每次请求时都会返回不同的语言，为什么回放时每次都返回同样的结果呢？原因很简单，我们对`RegressServiceImpl#slogan`进行了mock，在回放时开启了mock能力，调用slogan的`BEFORE`事件时找到了合适值，直接利用`ProcessControlException.throwReturnImmediately`进行了直接返回，RegressServiceImpl的第72行代码在mock回放时永远不会走到。得益于在[repeater-config.json](/bin/repeater-config.json)中开启了java插件并且默认拦截了RegressServiceImpl#slogan方法，录制slogan时同时录制java子调用

> 想要知道应用层面发生了什么吗？请看《[Slogan Demo究竟发生了什么](/docs/slogan-demo.md)》

### 2. 快速录制自己应用

#### step0 安装sandbox和插件到应用服务器

```shell
curl -s http://sandbox-ecological.oss-cn-hangzhou.aliyuncs.com/install-repeater.sh | sh
```
#### step1 修改repeater-config.json，启用拦截点和插件信息

> 根据需要修改[repeater-config.json](/bin/repeater-config.json)配置文件，具体配置含义参见：[RepeaterConfig.java](/repeater-plugin-api/src/main/java/com/alibaba/jvm/sandbox/repeater/plugin/domain/RepeaterConfig.java)

> `repeater-config.json`默认下载在`~/.sandbox-module/cfg/repeater-config.json`

> 本使用手册是单机方式使用，实际项目应用时，配置文件需要从服务端拉取，配置变更时服务端推送到对应模块，框架也提供了拉取和推送配置接口。

```json
{
  "degrade": false,
  "exceptionThreshold": 1000,
  "httpEntrancePatterns": [
    "^/regress/.*$"
  ],
  "javaEntranceBehaviors": [
  ],
  "javaSubInvokeBehaviors": [
  ],
  "pluginIdentities": [
    "http",
    "mybatis",
    "ibatis",
    "dubbo-provider",
    "dubbo-consumer"
  ],
  "repeatIdentities": [
    "java",
    "http"
  ],
  "sampleRate": 10000,
  "useTtl": true
}
```
#### step2 attach sandbox到目标进程
```shell
cd ~/sandbox/bin
# 假设目标JVM进程号为'2343'
./sandbox.sh -p 2343 -P 12580
```
如果控制台输出，则说明启动成功

```shell
                    NAMESPACE : default
                      VERSION : 1.2.1
                         MODE : ATTACH
                  SERVER_ADDR : 0.0.0.0
                  SERVER_PORT : 12580
               UNSAFE_SUPPORT : ENABLE
                 SANDBOX_HOME : /Users/froggen/sandbox
            SYSTEM_MODULE_LIB : /Users/froggen/sandbox/module
              USER_MODULE_LIB : ~/.sandbox-module;
          SYSTEM_PROVIDER_LIB : /Users/froggen/sandbox/provider
           EVENT_POOL_SUPPORT : DISABLE
```
查看repeater日志看模块和插件加载情况

```java
tail -200f ~/logs/sandbox/repeater/repeater.log
```

#### step3 开始录制

> 根据自己配置的协议，`HTTP/Java/Dubbo`进行请求录制，如录制成功在`repeater.log`会打出具体的`TraceId`，同时在`~/.sandbox-module/repeater-data/record/`文件中会写入`TraceId`关联的录制数据；

#### step4 关于回放

#### 方式一：利用模块暴露的http接口(Hessian)发起回放

模块暴露了回放接口，用于服务端发起远程回放，具体如下：

```shell
url			: http://ip:port/sandbox/default/module/http/repeater/repeat
params		: _data
```

> 其中 port 是jvm-sandbox启动时候绑定的port，可以在attach sandbox时增加`-P 12580`指定，或者执行`~/sandbox/bin/sandbox.sh -p {pid} -v` 查看`SERVER_PORT`
> _data 是由[RepeatMeta](/repeater-plugin-api/src/main/java/com/alibaba/jvm/sandbox/repeater/plugin/domain/RepeatMeta.java)经过hessian序列化之后的值，具体调用方式参见[AbstractRecordService](/repeater-console/repeater-console-service/src/main/java/com/alibaba/repeater/console/service/impl/AbstractRecordService.java) 和[RecordFacadeApi](/repeater-console/repeater-console-start/src/main/java/com/alibaba/repeater/console/start/controller/RecordFacadeApi.java)

#### 方式二：利用模块暴露的http接口(JSON)发起回放

模块暴露了回放接口，用于服务端发起远程回放，具体如下：

```shell
url			: http://ip:port/sandbox/default/module/http/repeater/repeatWithJson
params		: _data
```

> 其中 port 是jvm-sandbox启动时候绑定的port，可以在attach sandbox时增加`-P 12580`指定，或者执行`~/sandbox/bin/sandbox.sh -p {pid} -v` 查看`SERVER_PORT`
> _data 是由[RepeatMeta](/repeater-plugin-api/src/main/java/com/alibaba/jvm/sandbox/repeater/plugin/domain/RepeatMeta.java)经过JSON序列化之后的值

#### 方式三：针对HTTP接口，可以像`Slogan Demo`一样进行参数或者Header透传方式进行MOCK回放

> 针对http接口，插件中特意针对透传`Repeat-TraceId-X`的参数或者Header进行识别，如果有录制数据，则会拉取对应录制记录进行MOCK回放；因此针对http接口如果录制成功，则可以在请求参数或者Header中透传`Repeat-TraceId-X`即可实现MOCK回放

例如：

```shell
curl -s 'http://127.0.0.1:8001/regress/slogan?Repeat-TraceId-X=127000000001156034386424510000ed'
```
或者

```shell
curl -s 'http://127.0.0.1:8001/regress/slogan' -H "Repeat-TraceId-X:127000000001156034386424510000ed"
```