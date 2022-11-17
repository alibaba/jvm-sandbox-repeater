# rocketmq插件

基于Apache rocketmq Client 库实现的rocketmq插件，能够录制发送方和消費方的消息体数据


## 详细设计

### 消息发送方
> 对org.apache.rocketmq.client.producer.DefaultMQProducer的send方法进行插装增强，
 

### 消息消费方
> 消息消费时，一般两种方式MessageListenerConcurrently和MessageListenerOrderly，都是一个consumeMessage()方法，因此对consumeMessage方法进行增强，获取org.apache.rocketmq.common.message.MessageExt对象中的消息体数据
 - 注意的是，一般消息消费时，会是一次调用的入口
```java
@Override
    public boolean isEntrance() {
        return true;
    }
```
## 测试验证

**rocketmq:4.2.0** 版本，可以正常录制消費方和发送方的数据
