package org.tony.console.biz.components.saveRepeat;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2023/1/10 16:51
 */
@Slf4j
@Order(0)
@Component
public class DeserializeBodyOfRepeat implements SaveRepeatComponent {

    @Override
    public void execute(SaveRepeatRequest saveRepeatRequest) throws BizException {
        String body = saveRepeatRequest.getBody();

        try {
            RepeatModel repeatModel = SerializerWrapper.hessianDeserialize(body, RepeatModel.class);
            saveRepeatRequest.setRepeatModel(repeatModel);
        } catch (SerializeException e) {
            log.error("error occurred when deserialize repeat model", e);
            throw BizException.build("operate failed");
        }


    }

    @Override
    public boolean isSupport(SaveRepeatRequest saveRepeatRequest) {
        return true;
    }
}
