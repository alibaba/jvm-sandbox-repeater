package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Comparable;
import com.alibaba.jvm.sandbox.repeater.aide.compare.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.aide.compare.CompareResult;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.domain.ReplayStatus;
import com.alibaba.repeater.console.common.params.ReplayParams;
import com.alibaba.repeater.console.dal.dao.RecordDao;
import com.alibaba.repeater.console.dal.dao.ReplayDao;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.repeater.console.dal.model.Replay;
import com.alibaba.repeater.console.service.ModuleInfoService;
import com.alibaba.repeater.console.service.ReplayService;
import com.alibaba.repeater.console.service.convert.DifferenceConvert;
import com.alibaba.repeater.console.service.convert.ReplayConverter;
import com.alibaba.repeater.console.service.util.ConvertUtil;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import com.alibaba.repeater.console.service.util.ResultHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link ReplayServiceImpl}
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("replayService")
@Slf4j
public class ReplayServiceImpl implements ReplayService {

    @Value("${repeat.repeat.url}")
    private String repeatURL;

    @Resource
    private ModuleInfoService moduleInfoService;
    @Resource
    private RecordDao recordDao;
    @Resource
    private ReplayDao replayDao;
    @Resource
    private ReplayConverter replayConverter;
    @Resource
    private DifferenceConvert differenceConvert;

    @Override
    public RepeaterResult<String> replay(ReplayParams params) {
        Optional.ofNullable(params.getIp()).orElseThrow(() -> new RuntimeException("ip can not be null"));
        Optional.ofNullable(params.getAppName()).orElseThrow(() -> new RuntimeException("appName can not be null"));
        Optional.ofNullable(params.getTraceId()).orElseThrow(() -> new RuntimeException("traceId can not be null"));
        RepeaterResult<ModuleInfoBO> result = moduleInfoService.query(params.getAppName(), params.getIp());
        if (!result.isSuccess() || result.getData() == null) {
            return ResultHelper.copy(result);
        }
        params.setPort(result.getData().getPort());
        params.setEnvironment(result.getData().getEnvironment());
        final Record record = recordDao.selectByAppNameAndTraceId(params.getAppName(), params.getTraceId());
        if (record == null) {
            return RepeaterResult.builder().success(false).message("data does not exist").build();
        }
        if (StringUtils.isEmpty(params.getRepeatId())) {
            params.setRepeatId(TraceGenerator.generate());
        }
        // save replay record
        Replay replay = saveReplay(record, params);
        if (replay == null) {
            return RepeaterResult.builder().success(false).message("save replay record failed").build();
        }
        return doRepeat(record, params);
    }

    @Override
    public RepeaterResult<String> saveRepeat(String body) {
        RepeatModel rm;
        try {
            rm = SerializerWrapper.hessianDeserialize(body, RepeatModel.class);
        } catch (SerializeException e) {
            log.error("error occurred when deserialize repeat model", e);
            return RepeaterResult.builder().message("operate failed").build();
        }
        // this process must handle by async
        Replay replay = replayDao.findByRepeatId(rm.getRepeatId());
        replay.setStatus(rm.isFinish() ? ReplayStatus.FINISH.getStatus() : ReplayStatus.FAILED.getStatus());
        replay.setTraceId(rm.getTraceId());
        replay.setCost(rm.getCost());
        Object expect;
        Object actual;
        try {
            if (rm.getResponse() instanceof String) {
                replay.setResponse(ConvertUtil.convert2Json((String)rm.getResponse()));
                try {
                    actual = JacksonUtil.deserialize((String)rm.getResponse(), Object.class);
                } catch (SerializeException e) {
                    actual = rm.getResponse();
                }
            } else {
                replay.setResponse(JacksonUtil.serialize(rm.getResponse()));
                actual = rm.getResponse();
            }
            replay.setMockInvocation(JacksonUtil.serialize(rm.getMockInvocations()));
            try {
                expect = JacksonUtil.deserialize(replay.getRecord().getResponse(), Object.class);
            } catch (SerializeException e) {
                expect = replay.getRecord().getResponse();
            }
        } catch (SerializeException e) {
            log.error("error occurred serialize replay response", e);
            return RepeaterResult.builder().message("operate failed").build();
        }
        Comparable comparable = ComparableFactory.instance().createDefault();
        // simple compare
        CompareResult result = comparable.compare(actual, expect);
        replay.setSuccess(!result.hasDifference());
        try {
            replay.setDiffResult(JacksonUtil.serialize(result.getDifferences()
                    .stream()
                    .map(differenceConvert::convert)
                    .collect(Collectors.toList()), false));
        } catch (SerializeException e) {
            log.error("error occurred serialize diff result", e);
            return RepeaterResult.builder().message("operate failed").build();
        }
        Replay calllback = replayDao.saveAndFlush(replay);
        return RepeaterResult.builder().success(true).message("operate success").data("-/-").build();
    }

    @Override
    public RepeaterResult<ReplayBO> query(ReplayParams params) {
        Replay replay = replayDao.findByRepeatId(params.getRepeatId());
        if (replay == null) {
            return RepeaterResult.builder().message("data not exist").build();
        }
        return RepeaterResult.builder().success(true).data(replayConverter.convert(replay)).build();
    }

    private RepeaterResult<String> doRepeat(Record record, ReplayParams params) {
        RepeatMeta meta = new RepeatMeta();
        meta.setAppName(record.getAppName());
        meta.setTraceId(record.getTraceId());
        meta.setMock(params.isMock());
        meta.setRepeatId(params.getRepeatId());
        meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);
        Map<String, String> requestParams = new HashMap<String, String>(2);
        try {
            requestParams.put(Constants.DATA_TRANSPORT_IDENTIFY, SerializerWrapper.hessianSerialize(meta));
        } catch (SerializeException e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        }
        HttpUtil.Resp resp = HttpUtil.doPost(String.format(repeatURL,params.getIp(),params.getPort()), requestParams);
        if (resp.isSuccess()) {
            return RepeaterResult.builder().success(true).message("operate success").data(meta.getRepeatId()).build();
        }
        return RepeaterResult.builder().success(false).message("operate failed").data(resp).build();
    }

    private Replay saveReplay(Record record, ReplayParams params) {
        Replay replay = new Replay();
        replay.setRecord(record);
        replay.setAppName(params.getAppName());
        replay.setEnvironment(params.getEnvironment());
        replay.setIp(params.getIp());
        replay.setRepeatId(params.getRepeatId());
        replay.setGmtCreate(new Date());
        replay.setGmtModified(new Date());
        replay.setStatus(ReplayStatus.PROCESSING.getStatus());
        // 冗余了一个repeatID，实际可以直接使用replay#id
        return replayDao.save(replay);
    }
}
