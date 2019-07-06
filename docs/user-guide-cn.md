# 用户使用手册

> [jvm-sandbox-repeater](https://github.com/alibaba/jvm-sandbox-repeater) 仅仅提供了录制回放的能力，如果需要完成`业务回归`、`实时监控`、`压测`等平台，后面须要有一个`数据中心`负责采集数据的加工、存储、搜索，repeater-console提供了简单的demo示例；一个`模块管理`平台负责管理JVM-Sandbox各模块生命周期；一个`配置管理`平台负责维护和推送jvm-sandbox-repeater采集所须要的各种配置变更
> 
> 在阿里集团淘系技术质量内部，已有一套完整的体系在持续运行，从17年开始支持了淘系技术质量部的CI、建站、系统重构等多方面质量保障任务，后续如有需要也会考虑把更多的东西开源回馈社区


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

> 想要知道应用层面发生了什么吗？请看《[Slogan Demo究竟发生了什么](docs/slogan-demo.md)》
> 
#### step2 开始回放

"昨日重现"  [Slogan Repeat](http://127.0.0.1:8001/regress/slogan?Repeat-TraceId-X=127000000001156034386424510000ed)  << 单击打开链接或执行`curl操作`

```shell
curl -s 'http://127.0.0.1:8001/regress/slogan?Repeat-TraceId-X=127000000001156034386424510000ed'
```

> 无论我们多少次访问这个地址，都将返回Repeat-TraceId=127000000001156034386424510000ed绑定的录制信息`JAVA是世界上最好的语言!`；如果重新访问[Slogan](http://127.0.0.1:8001/regress/slogan?Repeat-TraceId=127000000001156034386424510000ed)后又会将最新的返回结果绑定到Repeat-TraceId=127000000001156034386424510000ed（为了快速演示，将链路追踪的标志提到参数中进行透传了）

### 2. 快速录制自己应用

#### step0 安装sandbox和插件到应用服务器

```shell
curl -s http://sandbox-ecological.oss-cn-hangzhou.aliyuncs.com/install-repeater.sh | sh
```
#### step1 修改repeater-config.json，启用拦截点和插件信息

> 根据需要修改[repeater-config.json](bin/repeater-config.json)配置文件，具体配置含义参见：[RepeaterConfig.java](repeater-plugin-api/src/main/java/com/alibaba/jvm/sandbox/repeater/plugin/domain/RepeaterConfig.java)

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
./sandbox.sh -p 2343
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

#### step3 enjoy it！