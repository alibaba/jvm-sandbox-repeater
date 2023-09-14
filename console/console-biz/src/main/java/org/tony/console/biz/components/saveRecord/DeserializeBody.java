package org.tony.console.biz.components.saveRecord;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRecordRequest;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:53
 */
@Slf4j
@Order(0)
@Component
public class DeserializeBody implements SaveRecordComponent {

    @Override
    public void execute(SaveRecordRequest saveRecordRequest) throws BizException {
        try {
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(saveRecordRequest.getBody(), RecordWrapper.class);
            if (wrapper == null || StringUtils.isEmpty(wrapper.getAppName())) {
                throw BizException.build("invalid request");
            }
            saveRecordRequest.setRecordWrapper(wrapper);
        } catch (SerializeException e) {
            log.error("system error",e);
            throw BizException.build("系统异常");
        }
    }

    @Override
    public boolean isSupport(SaveRecordRequest saveRecordRequest) {
        return true;
    }
}
