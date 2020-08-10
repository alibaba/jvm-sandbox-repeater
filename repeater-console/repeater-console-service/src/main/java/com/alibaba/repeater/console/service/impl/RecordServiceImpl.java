package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.domain.RecordBO;
import com.alibaba.repeater.console.common.domain.RecordDetailBO;
import com.alibaba.repeater.console.common.params.RecordParams;
import com.alibaba.repeater.console.dal.dao.RecordDao;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.repeater.console.dal.repository.RecordRepository;
import com.alibaba.repeater.console.service.RecordService;
import com.alibaba.repeater.console.service.convert.ModelConverter;
import com.alibaba.repeater.console.service.util.ConvertUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RecordServiceImpl} 使用mysql实现存储
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("recordService")
@Slf4j
public class RecordServiceImpl implements RecordService {
    @Resource
    private RecordDao recordDao;
    @Resource
    private ModelConverter<Record, RecordBO> recordConverter;
    @Resource
    private ModelConverter<Record, RecordDetailBO> recordDetailConverter;

    @Resource
    private RecordRepository recordRepository;

    public PageResult<RecordBO> list(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "id"));
        Page<Record> pageData = recordRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = Lists.newArrayList();
                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );

        PageResult<RecordBO> result = new PageResult<>();
        if (pageData.hasContent()) {
            result.setSuccess(true);
            result.setPageIndex(page);
            result.setCount(pageData.getTotalElements());
            result.setTotalPage(pageData.getTotalPages());
            result.setPageSize(size);
            result.setData(pageData.getContent().stream().map(recordConverter::convert).collect(Collectors.toList()));

        }
        return result;
    }

    @Override
    public RepeaterResult<String> saveRecord(String body) {
        try {
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(body, RecordWrapper.class);
            if (wrapper == null || StringUtils.isEmpty(wrapper.getAppName())) {
                return RepeaterResult.builder().success(false).message("invalid request").build();
            }
            Record record = ConvertUtil.convertWrapper(wrapper, body);
            recordDao.insert(record);
            return RepeaterResult.builder().success(true).message("operate success").data("-/-").build();
        } catch (Throwable throwable) {
            return RepeaterResult.builder().success(false).message(throwable.getMessage()).build();
        }
    }

    @Override
    public RepeaterResult<String> get(String appName, String traceId) {
        Record record = recordDao.selectByAppNameAndTraceId(appName, traceId);
        if (record == null) {
            return RepeaterResult.builder().success(false).message("data not exits").build();
        }
        return RepeaterResult.builder().success(true).message("operate success").data(record.getWrapperRecord()).build();
    }

    @Override
    public PageResult<RecordBO> query(RecordParams params) {
        Page<Record> page = recordDao.selectByAppNameOrTraceId(params);
        PageResult<RecordBO> result = new PageResult<>();
        if (page.hasContent()) {
            result.setSuccess(true);
            result.setCount(page.getTotalElements());
            result.setTotalPage(page.getTotalPages());
            result.setPageIndex(params.getPage());
            result.setPageSize(params.getSize());
            result.setData(page.getContent().stream().map(recordConverter::convert).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public RepeaterResult<RecordDetailBO> getDetail(RecordParams params) {
        Record record = recordDao.selectByAppNameAndTraceId(params.getAppName(), params.getTraceId());
        if (record == null) {
            return RepeaterResult.builder().message("data not found").build();
        }
        return RepeaterResult.builder().success(true).data(recordDetailConverter.convert(record)).build();
    }

    @Override
    public RepeaterResult<RepeatModel> callback(String repeatId) {
        return null;
    }

    public RecordDetailBO detail(Long id) {
        return recordDetailConverter.convert(recordRepository.getOne(id));
    }
}
