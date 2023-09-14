package org.tony.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tony.console.common.Page;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.domain.RecordBO;
import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.common.domain.RepeatModel;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.mongo.RecordMongoService;
import org.tony.console.mongo.model.RecordMDO;
import org.tony.console.service.RecordService;
import org.tony.console.service.convert.RecordConverter;
import org.tony.console.service.convert.RecordDetailConverter;
import org.tony.console.service.convert.RecordMongoConvert;
import org.tony.console.service.utils.ConvertUtil;

import javax.annotation.Resource;

@Component
public class RecordServiceImpl implements RecordService {

    @Resource
    RecordDao recordDao;

    @Resource
    RecordConverter recordConverter;

    @Resource
    RecordDetailConverter recordDetailConverter;

    @Resource
    RecordMongoService recordMongoService;

    @Resource
    RecordMongoConvert recordMongoConvert;


    @Override
    public Result<String> saveRecord(String body) {
        try {
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(body, RecordWrapper.class);
            if (wrapper == null || StringUtils.isEmpty(wrapper.getAppName())) {
                return Result.builder().success(false).message("invalid request").build();
            }
            Record record = ConvertUtil.convertWrapper(wrapper, body);
            recordDao.insert(record);
            return Result.builder().success(true).message("operate success").data("-/-").build();
        } catch (Throwable throwable) {
            return Result.builder().success(false).message(throwable.getMessage()).build();
        }
    }

    @Override
    public Result<String> get(String appName, String traceId) {
        Record record = recordDao.selectByAppNameAndTraceId(appName, traceId);
        if (record == null) {
            return Result.builder().success(false).message("data not exits").build();
        }
        return Result.builder().success(true).message("operate success").data(record.getWrapperRecord()).build();
    }

    @Override
    public PageResult<RecordBO> query(RecordQuery params) {
        Page<Record> page = recordDao.selectByAppNameOrTraceId(params);
        PageResult<RecordBO> result = new PageResult<>();

        result.setSuccess(true);
        result.setCount(page.getTotal());
        result.setPageIndex(params.getPage());
        result.setPageSize(params.getPageSize());
        result.setData(recordConverter.convert(page.getData()));

        return result;
    }

    @Override
    public PageResult<RecordBO> queryMongo(RecordQuery params) {
        PageResult<RecordMDO> page = recordMongoService.queryRecord(params);
        PageResult<RecordBO> result = new PageResult<>();

        result.setSuccess(true);
        result.setCount(page.getCount());
        result.setPageIndex(params.getPage());
        result.setPageSize(params.getPageSize());
        result.setData(recordMongoConvert.convert(page.getData()));

        return result;
    }

    @Override
    public Result<RecordDetailBO> getDetail(RecordQuery params) {
        Record record = recordDao.selectByAppNameAndTraceId(params.getAppName(), params.getTraceId());
        if (record == null) {
            return Result.builder().message("data not found").build();
        }
        return Result.builder().success(true).data(recordDetailConverter.convert(record)).build();
    }

    @Override
    public Result<RepeatModel> callback(String repeatId) {
        return null;
    }
}
