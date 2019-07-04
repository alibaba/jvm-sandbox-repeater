# repeater-console 控制台

> repeater-console工程集成录制/回放的配置管理；数据存储/数据对比等具备多种能力，因各系统架构差异较大，目前仅开源简单的demo工程，后续会提供统一的工程，也希望有能力和时间的同学来提PR


```shell
curl -s http://127.0.0.1:8001/regress/getAsync/repeater -H 'Repeat-TraceId:030010083212156034386424510101ed'
curl -s http://127.0.0.1:8001/facade/api/repeat/repeater/030010083212156034386424510101ed -H "RepeatId:030010083212156034386424510201ed"
curl -s http://127.0.0.1:8001/facade/api/repeat/callback/030010083212156034386424510201ed
```