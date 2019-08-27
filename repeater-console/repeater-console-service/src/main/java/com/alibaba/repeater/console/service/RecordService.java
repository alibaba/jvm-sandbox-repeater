package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;

import java.util.List;

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
     * 存储record
     *
     * @param body post内存
     * @return 存储结果
     */
    RepeaterResult<String> saveRepeat(String body);


    /**
     * 查询
     *
     * @param appName 应用名
     * @param traceId traceId
     * @return 查询结果
     */
    RepeaterResult<String> get(String appName, String traceId);

    /**
     * 执行回放
     *
     * @param appName  应用名
     * @param traceId  traceId
     * @param repeatId 回放ID
     * @return 回放结果
     */
    RepeaterResult<String> repeat(String appName, String traceId, String repeatId);



    /**
     * 批量执行回放
     *
     * @param appName  应用名
     * @return 回放结果
     */
    RepeaterResult<List<RepeaterResult>> batchRepeat(String appName);



    /**
     * 查询回放结果
     *
     * @param repeatId 回放ID
     * @return 回放结果
     */
    RepeaterResult<RepeatModel> callback(String repeatId);

    /**
     * 查询回放结果
     *
     * @param appName 应用名
     * @return 回放结果
     */
    RepeaterResult<List<RepeatModel>> batchCallback(String appName);
}
