package org.tony.console.biz.request;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import lombok.Data;
import org.tony.console.common.domain.ReplayType;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.Replay;
import org.tony.console.service.model.AppCompareConfigDO;

/**
 * @author peng.hu1
 * @Date 2023/1/10 16:47
 */
@Data
public class SaveRepeatRequest implements BizRequest {

    private String body;

    private RepeatModel repeatModel;

    private Replay replay;

    private Record record;

    /**
     * 全局忽略比对配置
     */
    private AppCompareConfigDO appCompareConfigDO;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(body, "body is null");
    }

    public ReplayType get() {
        if (replay == null) {
            return ReplayType.RECORD;
        }

        return ReplayType.getByCode(replay.getType());
    }
}
