package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * @author deipss
 * @since 1.0.0
 */
public class RocketInvocation extends Invocation{

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息主題
     */
    private String messageTopic;

    /**
     * 消息tags
     */
    private String messageTags;

    /**
     * 消息
     */
    private String messageBody;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public void setMessageTopic(String messageTopic) {
        this.messageTopic = messageTopic;
    }

    public String getMessageTags() {
        return messageTags;
    }

    public void setMessageTags(String messageTags) {
        this.messageTags = messageTags;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
