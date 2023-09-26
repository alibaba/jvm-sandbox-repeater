![logo](https://github.com/alibaba/jvm-sandbox-repeater/releases/download/v1.0.0/repeater-logo.png)

[![Build Status](https://travis-ci.org/alibaba/jvm-sandbox-repeater.svg?branch=master)](https://travis-ci.org/alibaba/jvm-sandbox-repeater)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub issues](https://img.shields.io/github/issues/alibaba/jvm-sandbox-repeater.svg)](https://github.com/alibaba/jvm-sandbox-repeater/issues)

# 基于[JVM-Sandbox](https://github.com/alibaba/JVM-Sandbox)的录制/回放通用解决方案

> [jvm-sandbox-repeater](https://github.com/alibaba/jvm-sandbox-repeater)是[JVM-Sandbox](https://github.com/alibaba/JVM-Sandbox)生态体系下的重要模块，它具备了JVM-Sandbox的所有特点，插件式设计便于快速适配各种中间件，封装请求录制/回放基础协议，也提供了通用可扩展的各种丰富API。

### 前言
作为阿里开源的录制回放框架，该框架基本上解决了录制&回放的基础流程，但是还不能够立马作为自动化测试框架，我基于该框架扩展了很多特性，同时也修了很多bug，并且重新开发了一套UI操作界面，使其成为一个开箱可用的自动化平台。

### 二次开发的必要性
1. 开源版本有很多bug
开源版本在实际项目应用过程中，有非常多的bug，例如回放子调用匹配问题，部分业务hession序列化不支持等等；

2. 开源版本插件较少，且能力不足
目前仅支持http、java主子调用、mybatis等等插件，不支持feign、mybatis-plus、redis、spring-data、spring-Async等等

3. 用例管理能力的缺失
用例管理是自动化测试平台必备的一个能力，因此必须开发

### 扩展特性列表
- 支持mybatis keyGenerator特性的录制 & 回放
- 支持 java Date、System.currentTimeMillis()时间的采集+mock能力
- 支持java bootstap classloader的相关class的采集+mock
- 支持Java Exception的mock + 比对
- 支持采集使用kafka作为传播媒介，降低对宿主应用的性能的影响
- 支持子调用比对
- 支持主子调用结果的替换
- 支持自定义忽略比对 +  自定义子调用比对
- 支持调度任务
- 支持用例管理
- 支持Http get 请求带body的回放
- 支持Http put、delete的回放
- 解决类加载延迟出现的注入失败的bug
- 解决抽象类注入之后采集跟回放异常的bug
- 解决了Http主调用（采样false） -> Java主调用 嵌套调用场景下 java主调用不采集的问题
- 升级序列化方式为fastjson2, 支持jdk17
- 支持spring @Async @Cache 注解的拦截
- 支持webflux
- 支持流量标签+基于标签的流量推荐
![image](https://github.com/penghu2/sandbox-repeater/assets/10905318/60edf4a3-340f-4fa6-8a2e-80bbbe8a6ca9)
![image](https://github.com/penghu2/sandbox-repeater/assets/10905318/02ec69ad-e957-43ae-9664-9ab3d1a60c5e)

![image](https://github.com/penghu2/sandbox-repeater/assets/10905318/2f099cb2-9626-4368-9a3d-623c4c3cb8eb)
![image](https://github.com/penghu2/sandbox-repeater/assets/10905318/f07294ee-db10-40f6-866d-81ed5f4ac54a)
![image](https://github.com/penghu2/sandbox-repeater/assets/10905318/e6caecfb-4d52-4e3a-8b95-de734fe38fc0)

