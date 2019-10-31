# redis插件

基于Jedis库实现的redis插件，能够录制和mock基于Jedis库执行操作的redis的常见操作。


## 详细设计

参考[插件开发手册](../../docs/plugin-development.md)。

`jvm-sandbox-repeater`已经实现了对于一个java方法的调用进行录制和mock的功能，当需要对Jedis相关操作进行录制和mock时，只需要了解在Jedis库中执行redis操作的方法并进行埋点即可。

而该插件埋点的方法在两个类中。`redis.clients.jedis.Jedis`和`redis.clients.jedis.BinaryJedis`。
埋点的方法为：

1. `redis.clients.jedis.Jedis`中实现了这几个接口的方法：`JedisCommands`, `MultiKeyCommands`,`ScriptingCommands`。
2. `redis.clients.jedis.BinaryJedis`中实现了这几个接口的方法：`BinaryJedisCommands`, `MultiKeyBinaryCommands`,`BinaryScriptingCommands`。

具体实现代码见`com.alibaba.jvm.sandbox.repeater.plugin.redis.RedisPlugin`类。

> 原本方案是通过类来获取需要埋点的方法名，则引入了Jedis的3.0.0的库。而引入库会导致插件体积过大，可能会在挂载时导致OOM。
> 于是改为代码中从上述接口类读取出方法名称后，采取硬编码的方式写入到插件中。
> 并且对比了2.9、2.10.2、3.0.0、3.1.0等版本的对应接口，将这几个版本的接口方法都进行了埋点。理论上是兼容这几个版本的。


## 设计思路

`redis.clients.jedis.Jedis`类是封装redis操作的类。
无论是哨兵模式还是集群模式都是通过调用这个类来执行redis操作的。

这个类有一百多个方法，实际上再更底层还有更加统一的方法，那就是`redis.clients.jedis.Connection.sendCommand(redis.clients.jedis.commands.ProtocolCommand, byte[]...)`方法。
所有的Jedis操作最后都会通过`Connection.sendCommand`方法来执行。无论是数据操作请求还是连接鉴权请求。

由于大多数的使用场景中，会使用连接池来管理redis的连接，所以并不是每一次redis数据操作都必须重新连接redis。
如果直接将埋点设置在sendCommand方法时，会由于不定时连接的特性，若在录制时没有执行连接，而在回放时有执行连接，则会回放失败。

于是最终设计将埋点设置在`redis.clients.jedis.Jedis`中。

确定了埋点的类之后，需要确定埋点的方法。
`redis.clients.jedis.Jedis`的方法中，也包含了连接操作、服务端操作、数据操作、高级操作等等方法，不同方法的分类可以通过接口来帮助理解。

```java
public class Jedis extends BinaryJedis implements JedisCommands, MultiKeyCommands,
    AdvancedJedisCommands, ScriptingCommands, BasicCommands, ClusterCommands, SentinelCommands, ModuleCommands {}
```

经过分析，进行数据操作的常用的方法主要集中在`JedisCommands`, `MultiKeyCommands`,`ScriptingCommands`这三个接口中，所以埋点确定为在`redis.clients.jedis.Jedis`类中这几个接口的方法。


在实际项目的实践中发现，通过RedisTemplate使用Jedis库来进行redis操作时，实际上调用到的方法是`redis.clients.jedis.BinaryJedis`类中的方法。
参考`redis.clients.jedis.Jedis`类的埋点思路，最终分别在`redis.clients.jedis.BinaryJedis`和`redis.clients.jedis.Jedis`都进行了埋点，形成详细设计。

## 测试验证

经过验证，以下Jedis版本的操作均可进行录制和mock。

| 验证版本    | 配套框架                                      | 验证操作                                   |
| ----------- | --------------------------------------------- | ------------------------------------------ |
| Jedis:2.9.0 | spring-boot-starter-data-redis :1.5.9 RELEASE | `GET`、`EVALSHA`、`hgetAll` |
| Jedis:2.9.1 | spring-boot-starter-redis:1.3.2.RELEASE       | `GET`、`SET`、`EXISTS`、`PEEXPIRE`、`DEL`  |
| Jedis 3.1.0 | 直接调用`Jedis`、`BinaryJedis`                | `GET`、`SET`、`EXISTS`、`EXPIRE`、`DEL`    |

如果使用`spring-boot-starter-data-redis`进行redis操作，需要注意在2.0以上的版本默认依赖`lettuce`作为redis的操作库，使用该插件需要调整为jedis依赖库进行操作。

