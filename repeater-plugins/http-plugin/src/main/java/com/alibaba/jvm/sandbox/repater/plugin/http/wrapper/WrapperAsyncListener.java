package com.alibaba.jvm.sandbox.repater.plugin.http.wrapper;

import com.alibaba.jvm.sandbox.repater.plugin.http.HttpStandaloneListener;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class WrapperAsyncListener implements AsyncListener {

    private final HttpStandaloneListener listener;
    private final WrapperRequest request;
    private final WrapperResponseCopier copier;

    WrapperAsyncListener(HttpStandaloneListener listener, WrapperRequest request, WrapperResponseCopier copier) {
        this.listener = listener;
        this.request = request;
        this.copier = copier;
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        WrapperTransModel wtm = WrapperTransModel.build(request);
        wtm.setBody(request.getBody());
        wtm.setResponse(new String(copier.getResponseData(), copier.getCharacterEncoding()));
        listener.onComplete(request, wtm);
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        WrapperTransModel wtm = WrapperTransModel.build(request);
        wtm.setBody(request.getBody());
        wtm.setResponse(new String(copier.getResponseData(), copier.getCharacterEncoding()));
        listener.onComplete(request, wtm);
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        WrapperTransModel wtm = WrapperTransModel.build(request);
        wtm.setBody(request.getBody());
        wtm.setResponse(new String(copier.getResponseData(), copier.getCharacterEncoding()));
        listener.onComplete(request, wtm);
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        listener.onStartAsync(request);
    }
}