package org.tony.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Comparable;
import com.alibaba.jvm.sandbox.repeater.aide.compare.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.aide.compare.CompareResult;
import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tony.console.common.Result;
import org.tony.console.common.domain.*;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.dao.ReplayDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.Replay;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.service.ReplayService;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.convert.DifferenceConvert;
import org.tony.console.service.convert.RecordConverter;
import org.tony.console.service.convert.RecordDetailConverter;
import org.tony.console.service.convert.ReplayConverter;
import org.tony.console.service.model.AppCompareConfigDO;
import org.tony.console.service.utils.ConvertUtil;
import org.tony.console.service.utils.JacksonUtil;
import org.tony.console.service.utils.ResultHelper;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ReplayServiceImpl implements ReplayService {

    @Resource
    RecordDao recordDao;

    @Resource
    ReplayDao replayDao;

    @Resource
    ModuleInfoService moduleInfoService;

    @Resource
    AppConfigService appConfigService;

    @Resource
    private ReplayConverter replayConverter;

    @Resource
    private DifferenceConvert differenceConvert;

    @Resource
    private RecordDetailConverter recordDetailConverter;

    @Value("${repeat.repeat.url}")
    private String repeatURL;

    @Resource
    TestCaseService testCaseService;

    @Autowired
    ApplicationContext applicationContext;


    @Override
    public Result<String> replay(ReplayParams params) {
        Optional.ofNullable(params.getIp()).orElseThrow(() -> new RuntimeException("ip can not be null"));
        Optional.ofNullable(params.getAppName()).orElseThrow(() -> new RuntimeException("appName can not be null"));
        Optional.ofNullable(params.getTraceId()).orElseThrow(() -> new RuntimeException("traceId can not be null"));
        Result<ModuleInfoBO> result = moduleInfoService.query(params.getAppName(), params.getIp());
        if (!result.isSuccess() || result.getData() == null) {
            return ResultHelper.copy(result);
        }
        params.setPort(result.getData().getPort());
        params.setEnvironment(result.getData().getEnvironment().name());
        final Record record = recordDao.selectByAppNameAndTraceId(params.getAppName(), params.getTraceId());
        if (record == null) {
            return Result.builder().success(false).message("data does not exist").build();
        }
        if (StringUtils.isEmpty(params.getRepeatId())) {
            params.setRepeatId(TraceGenerator.generate());
        }
        // save replay record
        Replay replay = saveReplay(record, params);
        if (replay == null) {
            return Result.builder().success(false).message("save replay record failed").build();
        }
        return doRepeat(record, params);
    }

    @Override
    public Result<String> saveRepeat(String body) {
        RepeatModel rm;

        try {
            rm = SerializerWrapper.hessianDeserialize(body, RepeatModel.class);
        } catch (SerializeException e) {
            log.error("error occurred when deserialize repeat model", e);
            return Result.builder().message("operate failed").build();
        }
        // this process must handle by async
        Replay replay = replayDao.findByRepeatId(rm.getRepeatId());
        replay.setExtend(rm.getExtension());
        Record record;
        if (replay.getType() == ReplayType.RECORD.type) {
             record = recordDao.selectById(replay.getRecordId());
        } else {
            record = testCaseService.queryTestCaseRecordById(replay.getRecordId());
        }

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
                expect = JacksonUtil.deserialize(record.getResponse(), Object.class);
            } catch (SerializeException e) {
                expect = record.getResponse();
            }
        } catch (SerializeException e) {
            log.error("error occurred serialize replay response", e);
            return Result.builder().message("operate failed").build();
        }

        AppCompareConfigDO appCompareConfigDO = appConfigService.queryCompareConfig(replay.getAppName());
        appCompareConfigDO.getIgnoreCompareNodes();


        Comparable comparable = ComparableFactory.instance().create(
                Comparator.CompareMode.DEFAULT,
                appCompareConfigDO.getIgnoreCompareNodes(),
                null,
                null
        );
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
            return Result.builder().message("operate failed").build();
        }
        Replay calllback = replayDao.saveAndFlush(replay);

        applicationContext.publishEvent(replay);
        return Result.builder().success(true).message("operate success").data("-/-").build();
    }

    @Override
    public Result<ReplayBO> query(ReplayParams params) {

        return null;
    }

    @Override
    public Result<ReplayBO> queryById(String repeatId) {
        Replay replay = replayDao.findByRepeatId(repeatId);
        Long recordId = replay.getRecordId();

        Record record;
        if (replay.getType() == ReplayType.RECORD.type) {
            record = recordDao.selectById(recordId);
        } else {
            record = testCaseService.queryTestCaseRecordById(recordId);
        }

        RecordDetailBO recordDetailBO = recordDetailConverter.convert(record);
        ReplayBO replayBO = replayConverter.convert(replay);
        if (replayBO.getStatus().equals(ReplayStatus.FINISH) && !replayBO.getSuccess()) {
            replayBO.setStatus(ReplayStatus.FAILED);
        }
        replayBO.setRecord(recordDetailBO);

        return Result.builder().success(true).data(replayBO).build();
    }

    private Replay saveReplay(Record record, ReplayParams params) {
        Replay replay = new Replay();
        replay.setRecordId(record.getId());
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

    private Result<String> doRepeat(Record record, ReplayParams params) {
        RepeatMeta meta = new RepeatMeta();
        meta.setAppName(record.getAppName());
        meta.setTraceId(record.getTraceId());
        meta.setMock(params.isMock());
        meta.setRepeatId(params.getRepeatId());
        meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);
        meta.setSingle(params.isSingle());

        Map<String, String> requestParams = new HashMap<String, String>(2);
        try {
            requestParams.put(Constants.DATA_TRANSPORT_IDENTIFY, SerializerWrapper.hessianSerialize(meta));
        } catch (SerializeException e) {
            return Result.builder().success(false).message(e.getMessage()).build();
        }
        HttpUtil.Resp resp = HttpUtil.doPost(String.format(repeatURL,params.getIp(),params.getPort()), requestParams);
        if (resp.isSuccess()) {
            return Result.builder().success(true).message("operate success").data(meta.getRepeatId()).build();
        }
        return Result.builder().success(false).message("operate failed").data(resp).build();
    }
}
