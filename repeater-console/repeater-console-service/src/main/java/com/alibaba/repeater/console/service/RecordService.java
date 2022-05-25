package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.RecordBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.domain.RecordDetailBO;
import com.alibaba.repeater.console.common.params.RecordParams;
import com.alibaba.repeater.console.common.params.ReplayParams;

/**
 * {@link RecordService} 存储服务示例
 * <p>
 *
 * @author zhaoyb1990
 */
public interface RecordService {

    /**
     * 存储record
     *
     * @param body post内存
     * @return 存储结果
     */
    RepeaterResult<String> saveRecord(String body);

    /**
     * 查询
     *
     * @param appName 应用名
     * @param traceId traceId
     * @return 查询结果
     */
    RepeaterResult<String> get(String appName, String traceId);

    /**
     * 查询记录
     *
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<RecordBO> query(RecordParams params);

    /**
     * 查询详情
     * @param params 查询参数
     * @return 详情结构
     */
    RepeaterResult<RecordDetailBO> getDetail(RecordParams params);


    /**
     * 查询回放结果
     *
     * @param repeatId 回放ID
     * @return 回放结果
     */
    RepeaterResult<RepeatModel> callback(String repeatId);
}
