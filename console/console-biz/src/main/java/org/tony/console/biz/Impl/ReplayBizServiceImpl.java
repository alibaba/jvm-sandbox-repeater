package org.tony.console.biz.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.biz.Constant;
import org.tony.console.biz.ReplayBizService;
import org.tony.console.biz.request.Command;
import org.tony.console.biz.request.ReplayRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ReplayStatus;
import org.tony.console.common.domain.ReplayType;
import org.tony.console.common.enums.Env;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.dao.ReplayDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.Replay;
import org.tony.console.service.AppService;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.service.model.app.AppDTO;
import org.tony.console.service.model.app.Region;
import org.tony.console.service.utils.ResultHelper;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author peng.hu1
 * @Date 2023/1/3 14:57
 */
@Slf4j
@Component
public class ReplayBizServiceImpl  implements ReplayBizService {

    @Value("${repeat.repeat.url}")
    private String repeatURL;

    @Value("${command.url}")
    private String commandUrl;

    @Value("${replay.dataSource.url}")
    private String dataSourceURL;

    @Resource
    ModuleInfoService moduleInfoService;

    @Resource
    TestCaseService testCaseService;

    @Resource
    ReplayDao replayDao;

    @Resource
    RecordDao recordDao;

    @Resource
    AppService appService;

    @Override
    public Result replay(ReplayRequest request) throws BizException {
        request.check();

        ModuleInfoBO moduleInfoBO = request.getModuleInfoBO();
        if (moduleInfoBO== null) {
            Result<ModuleInfoBO> result = moduleInfoService.query(request.getAppName(), request.getIp());
            if (!result.isSuccess() || result.getData() == null) {
                return ResultHelper.copy(result);
            }
            moduleInfoBO = result.getData();
        }

        if (moduleInfoBO.isOffline()) {
            return Result.buildFail("机器已下线");
        }

        AppDTO appDTO = appService.queryApp(request.getAppName());

        Record record;
        if (StringUtils.isNotEmpty(request.getCaseId())) {
            TestCaseDTO caseDTO = testCaseService.queryTestCaseDTO(request.getCaseId());
            if (caseDTO == null || caseDTO.getRecord() == null) {
                throw BizException.build("case不存在");
            }

            record = caseDTO.getRecord();
        } else {
            record = recordDao.selectByAppNameAndTraceId(request.getAppName(), request.getTraceId());
            if (record == null) {
                return Result.builder().success(false).message("data does not exist").build();
            }
        }


        if (StringUtils.isEmpty(request.getRepeatId())) {
            request.setRepeatId(TraceGenerator.generate());
        }

        Replay replay = saveReplay(record, moduleInfoBO, request);
        if (replay == null) {
            return Result.builder().success(false).message("save replay record failed").build();
        }

        return doRepeat(record, request, moduleInfoBO, true, request.isSingle(), appDTO.getRegion());
    }

    @Override
    public Result replayV2(ReplayRequest request) throws BizException {
        if (request.getRepeatId()==null) {
            request.setRepeatId(TraceGenerator.generate());
        }
        AppDTO appDTO = appService.queryApp(request.getAppName());
        Record record = testCaseService.queryTestCaseRecord(request.getCaseId());

        Replay replay = saveReplay(record, request.getModuleInfoBO(), request);

        return doRepeat(record, request, request.getModuleInfoBO(), request.isMock(), request.isSingle(), appDTO.getRegion());
    }

    /*=====================================================[私有方法区]================================================*/

    private Replay saveReplay(Record record, ModuleInfoBO moduleInfoBO, ReplayRequest request) {
        Replay replay = new Replay();
        replay.setRecordId(record.getId());
        replay.setAppName(moduleInfoBO.getAppName());
        replay.setEnvironment(moduleInfoBO.getEnvironment().name());
        replay.setIp(moduleInfoBO.getIp());
        replay.setRepeatId(request.getRepeatId());
        replay.setGmtCreate(new Date());
        replay.setGmtModified(new Date());
        replay.setStatus(ReplayStatus.PROCESSING.getStatus());

        if (request.getCaseId()!=null) {
            replay.setType(ReplayType.TESTCASE.type);
            replay.setCaseId(request.getCaseId());
        } else {
            replay.setType(ReplayType.RECORD.type);
        }

        // 冗余了一个repeatID，实际可以直接使用replay#id
        return replayDao.save(replay);
    }


    private Result<String> doRepeat(
            Record record,
            ReplayRequest request,
            ModuleInfoBO moduleInfoBO,
            boolean isMock,
            boolean single,
            Region region
    ) {
        RepeatMeta meta = new RepeatMeta();
        meta.setAppName(record.getAppName());
        meta.setTraceId(record.getTraceId());
        meta.setMock(isMock);
        meta.setRepeatId(request.getRepeatId());
        meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);

        if (StringUtils.isNotEmpty(request.getCaseId())) {
            meta.setDatasource(dataSourceURL+request.getCaseId());
        }

        meta.setSingle(single);
        if (request.getTaskItemId()!=null) {
            Map<String, String> extend = new HashMap<>();
            extend.put(Constant.TASK_ITEM_ID, request.getTaskItemId().toString());
            meta.setExtension(extend);
        }

        Map<String, String> requestParams = new HashMap<String, String>(2);
        try {
            requestParams.put(Constants.DATA_TRANSPORT_IDENTIFY, SerializerWrapper.hessianSerialize(meta));
            requestParams.put(Constants.DATA_TRANSPORT_VERSION, "1");
        } catch (SerializeException e) {
            return Result.builder().success(false).message(e.getMessage()).build();
        }

        HttpUtil.Resp resp;

        Env env = moduleInfoBO.getEnvironment();
        if (Env.PROD.equals(env) || Env.STG.equals(env)) {
            Command command = new Command();
            command.setIp(moduleInfoBO.getIp());
            command.setPort(moduleInfoBO.getPort());
            command.setRequestParams(requestParams);
            command.setRegion(region.name());
            command.setType(0);
            HashMap<String,String> headerMap = new HashMap<>();
            headerMap.put("content-type", "application/json");
            resp = HttpUtil.invoke(getCommandUrl(region), "POST", headerMap, null, JSON.toJSONString(command));
        } else {
            //如果是test dev 环境，直接走这个接口就可以，如果是stg或者prod的回放，则必须通过网关跳转过去
            resp = HttpUtil.doPost(String.format(repeatURL,moduleInfoBO.getIp(),moduleInfoBO.getPort()), requestParams);

        }
        if (resp.isSuccess()) {
            return Result.builder().success(true).message("operate success").data(meta.getRepeatId()).build();
        }
        return Result.builder().success(false).message("operate failed").data(resp).build();
    }

    private String getCommandUrl(Region region) {
        switch (region) {
            case cn:
            case eu:
                return commandUrl;
        }
        return commandUrl;
    }
}
