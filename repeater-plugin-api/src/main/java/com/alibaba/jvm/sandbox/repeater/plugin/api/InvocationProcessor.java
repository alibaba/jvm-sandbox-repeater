package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ThrowsEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * {@link InvocationProcessor} 调用处理器；组装各种请求、返回、mock请求等
 * <p>
 *
 * @author zhaoyb1990
 */
public interface InvocationProcessor extends InvocationFilter {

    /**
     * 组装请求参数
     *
     * @param event before事件
     * @return 请求参数
     */
    Object[] assembleRequest(BeforeEvent event);

    /**
     * 组装response；比较特殊，没有确定event类型，因为在回调方式的场景下，可能通过before事件获取response
     *
     * @param event 事件
     * @return 返回结果
     */
    Object assembleResponse(Event event);

    /**
     * 组装异常
     *
     * @param event 事件
     * @return 异常对象
     */
    Throwable assembleThrowable(ThrowsEvent event);

    /**
     * 组装identity；
     *
     * @param event 事件
     * @return identity标志
     */
    Identity assembleIdentity(BeforeEvent event);

    /**
     * 执行mock功能
     *
     * @param event    before事件
     * @param entrance 是否入口
     * @param type     调用类型
     * @throws ProcessControlException 流程控制异常
     */
    void doMock(BeforeEvent event, Boolean entrance, InvokeType type) throws ProcessControlException;

    /**
     * 组装需要返回的mockResponse
     *
     * @param event      before事件
     * @param invocation 调用信息
     * @return mock结果
     */
    Object assembleMockResponse(BeforeEvent event, Invocation invocation);


    /**
     * 是否及时序列化请求参数；
     *
     * 因为请求参数在后续的调用过程中，可能会被篡改；因此在录制的过程中，默认会在before事件时候直接序列化
     *
     * 但有一些特殊场景例如：Mybatis的insert开启自动生成ID，会把入参里面ID补全，及时序列化录不到ID，会导致后续流程出错；
     *
     * 这样的场景可以在return时去序列化
     *
     * @param invocation 调用
     * @param event before事件
     * @return true / false
     */
    boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event);
}
