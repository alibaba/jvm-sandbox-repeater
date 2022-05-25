# repeater-console 控制台

> repeater-console工程集成录制/回放的配置管理；数据存储/数据对比等具备多种能力，因各系统架构差异较大，目前仅开源简单的demo工程，后续会提供统一的工程，也希望有能力和时间的同学来提PR


```shell
curl -s http://127.0.0.1:8001/regress/getAsync/repeater -H 'Repeat-TraceId:030010083212156034386424510101ed'
curl -s http://127.0.0.1:8001/facade/api/repeat/repeater/030010083212156034386424510101ed -H "RepeatId:030010083212156034386424510201ed"
curl -s http://127.0.0.1:8001/facade/api/repeat/callback/030010083212156034386424510201ed
```


> console临时采用[AdminLTE](https://github.com/ColorlibHQ/AdminLTE) + Velocity来写页面，后续找一批有兴趣的同学一起基于[ant-design](https://github.com/ant-design/ant-design)或者[ice](https://github.com/alibaba/ice)进行重构


### 引用开源框架

- [AdminLTE](https://github.com/ColorlibHQ/AdminLTE)
- [jquery](https://github.com/jquery/jquery)
- [ace](https://github.com/ajaxorg/ace/)
- [ace-diff](https://github.com/ace-diff/ace-diff)
- [bootstrap](https://github.com/twbs/bootstrap)