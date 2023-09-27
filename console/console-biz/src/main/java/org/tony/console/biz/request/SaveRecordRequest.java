package org.tony.console.biz.request;

import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.db.model.Record;
import org.tony.console.mongo.model.RecordMDO;
import org.tony.console.common.domain.Tag;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:51
 */
@Data
public class SaveRecordRequest implements BizRequest {

    String body;

    RecordWrapper recordWrapper;

    Record record;

    RecordMDO recordMDO;

    private List<Tag> tagList;

    @Override
    public void check() throws BizException {

    }
}
