package org.tony.console.service.trxMsg;

/**
 * 消息监听器
 */
public interface TrxMsgListener<Msg> {

    /**
     * 监听哪一类消息
     * @return
     */
    public Topic getTopic();

    /**
     * 获取消息并执行
     * @param msg
     * @return
     */
    public ExecResult execute(Msg msg);
}
