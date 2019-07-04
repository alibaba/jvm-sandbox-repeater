package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.standalone;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.PathUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * {@link StandaloneBroadcaster} 能够脱机工作，不依赖服务端的实现
 * <p>
 *
 * @author zhaoyb1990
 */
public class StandaloneBroadcaster extends AbstractBroadcaster {

    private String recordSuffix = "record";

    private String repeatSuffix = "repeat";

    @Override
    protected void broadcastRecord(RecordModel rm) {
        try {
            String body = SerializerWrapper.hessianSerialize(rm);
            broadcast(body, rm.getTraceId(), recordSuffix);
            log.info("broadcast success,traceId={},resp={}", rm.getTraceId(), "success");
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    @Override
    protected void broadcastRepeat(RepeatModel rm) {
        try {
            String body = SerializerWrapper.hessianSerialize(rm);
            broadcast(body, rm.getRepeatId(), repeatSuffix);
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    @Override
    public RepeaterResult<RecordModel> pullRecord(RepeatMeta meta) {
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            String record = FileUtils.readFileToString(new File(assembleFileName(meta.getTraceId(), recordSuffix)), "UTF-8");
            Thread.currentThread().setContextClassLoader(DefaultBroadcaster.class.getClassLoader());
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(record, RecordWrapper.class);
            if (meta.isMock() && CollectionUtils.isNotEmpty(wrapper.getSubInvocations())) {
                for (Invocation invocation : wrapper.getSubInvocations()) {
                    SerializerWrapper.inTimeDeserialize(invocation);
                }
            }
            SerializerWrapper.inTimeDeserialize(wrapper.getEntranceInvocation());
            return RepeaterResult.builder().success(true).message("operate success").data(wrapper.reTransform()).build();
        } catch (Throwable e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        } finally {
            Thread.currentThread().setContextClassLoader(swap);
        }
    }

    private void broadcast(String body, String name, String folder) throws IOException {
        FileUtils.writeStringToFile(new File(assembleFileName(name, folder)), body, "UTF-8");
    }


    private String assembleFileName(String name, String folder) {
        return PathUtils.getModulePath() + File.separator + "repeater-data" + File.separator + folder + File.separator + name;
    }
}
