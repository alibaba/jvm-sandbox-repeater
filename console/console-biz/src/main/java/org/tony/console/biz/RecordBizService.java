package org.tony.console.biz;

import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:47
 */
public interface RecordBizService {

    /**
     * 保存记录
     * @param body
     * @return
     */
    public void saveRecord(String body) throws BizException;


    /**
     * 保存回放结果
     * @param body
     * @throws BizException
     */
    public void saveRepeat(String body) throws BizException;


    public RecordDetailBO get(String appName, String traceId);


    public void saveRecord(String msgKey, String msgBody);
}
