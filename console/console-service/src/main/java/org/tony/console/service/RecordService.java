package org.tony.console.service;

import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.domain.RecordBO;
import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.common.domain.RepeatModel;
import org.tony.console.db.query.RecordQuery;

import java.util.List;

public interface RecordService {

    /**
     * 存储record
     *
     * @param body post内存
     * @return 存储结果
     */
    Result<String> saveRecord(String body);

    /**
     * 查询
     *
     * @param appName 应用名
     * @param traceId traceId
     * @return 查询结果
     */
    Result<String> get(String appName, String traceId);

    /**
     * 查询记录
     *
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<RecordBO> query(RecordQuery params);

    /**
     * 从mongo查询
     * @param params
     * @return
     */
    PageResult<RecordBO> queryMongo(RecordQuery params);

    /**
     * 查询详情
     * @param params 查询参数
     * @return 详情结构
     */
    Result<RecordDetailBO> getDetail(RecordQuery params);


    /**
     * 查询回放结果
     *
     * @param repeatId 回放ID
     * @return 回放结果
     */
    Result<RepeatModel> callback(String repeatId);
}
