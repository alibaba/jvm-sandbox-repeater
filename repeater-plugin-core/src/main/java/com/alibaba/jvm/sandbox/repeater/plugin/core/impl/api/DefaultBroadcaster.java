package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil.Resp;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.PropertyUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link DefaultBroadcaster} 默认的Http方式的消息投递实现
 *
 * @author zhaoyb1990
 */
public class DefaultBroadcaster extends AbstractBroadcaster {

    /**
     * 录制消息投递的URL
     */
    private String broadcastRecordUrl = PropertyUtil.getPropertyOrDefault(Constants.DEFAULT_RECORD_BROADCASTER, "");

    /**
     * 回放消息投递URL
     */
    private String broadcastRepeatUrl = PropertyUtil.getPropertyOrDefault(Constants.DEFAULT_REPEAT_BROADCASTER, "");

    /**
     * 回放消息拉取URL
     */
    private String pullRecordUrl = PropertyUtil.getPropertyOrDefault(Constants.DEFAULT_REPEAT_DATASOURCE, "");


    public void setBroadcastRecordUrl(String broadcastRecordUrl) {
        this.broadcastRecordUrl = broadcastRecordUrl;
    }

    public void setBroadcastRepeatUrl(String broadcastRepeatUrl) {
        this.broadcastRepeatUrl = broadcastRepeatUrl;
    }

    public String getPullRecordUrl() {
        return pullRecordUrl;
    }

    public DefaultBroadcaster() {
        super();
    }

    @Override
    protected void broadcastRecord(RecordModel recordModel) {
        try {
            RecordWrapper wrapper = new RecordWrapper(recordModel);
            String body = SerializerWrapper.hessianSerialize(wrapper);
            broadcast(broadcastRecordUrl, body, recordModel.getTraceId());
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    @Override
    protected void broadcastRepeat(RepeatModel record) {
        try {
            String body = SerializerWrapper.hessianSerialize(record);
            broadcast(broadcastRepeatUrl, body, record.getTraceId());
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    /**
     * 请求发送
     * @param url 地址
     * @param body 请求内容
     * @param traceId traceId
     */
    private void broadcast(String url, String body, String traceId) {
        HashMap<String, String> headers = Maps.newHashMap();
        headers.put("content-type", "application/json");
        Resp resp = HttpUtil.invokePostBody(url, headers, body);
        if (resp.isSuccess()) {
            log.info("broadcast success,traceId={},resp={}", traceId, resp);
        } else {
            log.info("broadcast failed ,traceId={},resp={}", traceId, resp);
        }
    }

    @Override
    public RepeaterResult<RecordModel> pullRecord(RepeatMeta meta) {
        String url;
        if (StringUtils.isEmpty(meta.getDatasource())) {
            url = String.format(pullRecordUrl, meta.getAppName(), meta.getTraceId());
        } else {
            url = meta.getDatasource();
        }
        final HttpUtil.Resp resp = HttpUtil.doGet(url);
        if (!resp.isSuccess() || StringUtils.isEmpty(resp.getBody())) {
            log.info("get repeat data failed, datasource={}, response={}", meta.getDatasource(), resp);
            return RepeaterResult.builder().success(false).message("get repeat data failed").build();
        }
        RepeaterResult<String> pr = JSON.parseObject(resp.getBody(), new TypeReference<RepeaterResult<String>>() {
        });
        if (!pr.isSuccess()) {
            log.info("invalid repeat data found, datasource={}, response={}", meta.getDatasource(), resp);
            return RepeaterResult.builder().success(false).message("repeat data found").build();
        }
        // swap classloader cause this method will be call in target app thread
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(DefaultBroadcaster.class.getClassLoader());
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(pr.getData(), RecordWrapper.class);
            SerializerWrapper.inTimeDeserialize(wrapper.getEntranceInvocation());
            if (meta.isMock() && CollectionUtils.isNotEmpty(wrapper.getSubInvocations())) {
                for (Invocation invocation : wrapper.getSubInvocations()) {
                    SerializerWrapper.inTimeDeserialize(invocation);
                }
            }
            return RepeaterResult.builder().success(true).message("operate success").data(wrapper.reTransform()).build();
        } catch (SerializeException e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        } finally {
            Thread.currentThread().setContextClassLoader(swap);
        }
    }
}
