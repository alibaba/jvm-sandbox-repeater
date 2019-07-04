![logo](https://sandbox-ecological.oss-cn-hangzhou.aliyuncs.com/repeater-logo.png)

[![Build Status](https://travis-ci.org/alibaba/jvm-sandbox-repeater.svg?branch=master)](https://travis-ci.org/alibaba/jvm-sandbox-repeater)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub issues](https://img.shields.io/github/issues/alibaba/jvm-sandbox-repeater.svg)](https://github.com/alibaba/jvm-sandbox-repeater/issues)

# 基于[JVM-Sandbox](https://github.com/alibaba/JVM-Sandbox)的录制/回放通用解决方案

## 项目简介：

### repeater的核心能力是什么？

#### 1. 通用录制/回放能力

- 无侵入式录制HTTP/Java/Dubbo入参/返回值录制能力（业务系统无感知）
- 基于[TTL](https://github.com/alibaba/transmittable-thread-local)提供多线程子调用追踪，完整追踪一次请求的调用路径
- 入口请求（HTTP/Dubbo/Java）流量回放、子调用（Java/Dubbo）返回值Mock能力

#### 2. 快速可扩展API实现

- 录制/回放插件式架构
- 提供标准接口，可通过配置/简单编码实现一类通用插件

#### 3. standalone工作模式

- 无需依赖任何服务端/存储，可以单机工作，提供录制/回放能力

### repeater的可以应用到哪些场景？

#### 1. 业务快速回归

- 基于线上流量的录制/回放，无需人肉准备自动化测试脚本、准备测试数据

#### 2. 线上问题排查

- 录制回放提供"昨日重现"能力，还原线上真实场景到线下做问题排查和Debug
- 动态方法入参/返回值录制，提供线上快速问题定位

#### 3. 压测流量准备

- 0成本录制HTTP/Dubbo等入口流量，作为压测流量模型进行压测

## 快速开始

### 1. 本地standalone工作

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

> 想要知道应用层面发生了什么吗？请看《[Slogan Demo究竟发生了什么]()》

### 2. 快速录制目标应用数据

#### step0 安装sandbox和插件到应用服务器（**请不要直接在生产服务器使用**）

#### step1 修改repeater-config.json，启用拦截点和插件信息

#### step2 attach sandbox到目标进程

#### step3 enjoy it！

### 3. 更多请看[用户手册]()