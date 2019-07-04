package com.alibaba.jvm.sandbox.repater.plugin.http;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperRequest;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperTransModel;

import javax.servlet.AsyncContext;

/**
 * {@link InvokeAdvice} http请求感知；包含同步/异步调用
 * <p>
 *
 * @author zhaoyb1990
 */
public interface InvokeAdvice {

    /**
     * 开启异步调用
     *
     * @param request WrapperRequest
     * @see javax.servlet.http.HttpServletRequest#startAsync
     */
    void onStartAsync(WrapperRequest request);

    /**
     * 异步调用完成
     *
     * @param request WrapperRequest
     * @param model   模型
     * @see AsyncContext#complete()
     */
    void onComplete(WrapperRequest request, WrapperTransModel model);

    /**
     * 同步调用开始
     *
     * @param request WrapperRequest
     * @param event   sandbox的before事件
     */
    void onRequest(WrapperRequest request, BeforeEvent event);

    /**
     * 同步调用完成
     *
     * @param request WrapperRequest
     * @param model   模型
     */
    void onResponse(WrapperRequest request, WrapperTransModel model);
}
