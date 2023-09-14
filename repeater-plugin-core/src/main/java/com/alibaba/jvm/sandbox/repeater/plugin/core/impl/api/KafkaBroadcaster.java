package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.KafkaClient;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.PropertyUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 基于kafka的投递模块
 * @author peng.hu1
 * @Date 2022/12/6 17:34
 */
public class KafkaBroadcaster extends AbstractBroadcaster {

    /**
     * 回放消息拉取URL
     */
    private String pullRecordUrl = PropertyUtil.getPropertyOrDefault(Constants.DEFAULT_REPEAT_DATASOURCE, "");

    private KafkaClient client;

    public KafkaBroadcaster() {

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
            Thread.currentThread().setContextClassLoader(KafkaBroadcaster.class.getClassLoader());
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(pr.getData(), RecordWrapper.class);
            SerializerWrapper.inTimeDeserialize(wrapper.getEntranceInvocation());
            if (meta.isMock() && CollectionUtils.isNotEmpty(wrapper.getSubInvocations())) {
                int i=0;
                for (Invocation invocation : wrapper.getSubInvocations()) {
                    System.out.println(i);
                    i++;
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

    @Override
    protected void broadcastRecord(RecordModel recordModel) {
        try {
            RecordWrapper wrapper = new RecordWrapper(recordModel);
            String body = SerializerWrapper.hessianSerialize(wrapper);
            client.sendRecordMsg("record-"+recordModel.getTraceId(), body);
            log.info("success broadcast the traceId={} record={}", recordModel.getTraceId(), recordModel.getEntranceInvocation());
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
            client.sendRepeatMsg("repeat-"+record.getTraceId(), body);
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    public void init() {
        if (client!=null) {
            return;
        }
        log.info("=====================[begin init kafka broadcaster]===================");
        RepeaterConfig repeaterConfig = ApplicationModel.instance().getConfig();
        client = new KafkaClient(repeaterConfig.getKafkaConfig());
        log.info("=====================[success init kafka broadcaster]===================");
    }
}
