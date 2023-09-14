package org.tony.console.biz.components.saveRepeat;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Comparable;
import com.alibaba.jvm.sandbox.repeater.aide.compare.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.aide.compare.CompareResult;
import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.domain.ReplayType;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.Replay;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.CaseConfigService;
import org.tony.console.service.convert.DifferenceConvert;
import org.tony.console.service.model.AppCompareConfigDO;
import org.tony.console.service.model.caseConfig.CaseCompareSortConfig;
import org.tony.console.service.utils.ConvertUtil;
import org.tony.console.service.utils.JacksonUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/10 17:02
 */
@Slf4j
@Order(30)
@Component
public class MainInvokeCompare implements SaveRepeatComponent {

    @Resource
    AppConfigService appConfigService;

    @Resource
    DifferenceConvert differenceConvert;

    @Resource
    CaseConfigService caseConfigService;

    @Override
    public void execute(SaveRepeatRequest saveRepeatRequest) throws BizException {
        RepeatModel rm = saveRepeatRequest.getRepeatModel();
        Replay replay = saveRepeatRequest.getReplay();
        Record record = saveRepeatRequest.getRecord();

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
            throw BizException.build("system error");
        }

        AppCompareConfigDO appCompareConfigDO = appConfigService.queryCompareConfig(replay.getAppName());
        saveRepeatRequest.setAppCompareConfigDO(appCompareConfigDO);
        appCompareConfigDO.getIgnoreCompareNodes();

        Map<String, String> arraySortConfig = null;

        if (replay.getType() ==  ReplayType.TESTCASE.type) {
            String caseId = replay.getCaseId();

            CaseCompareSortConfig config = caseConfigService.getCompareSortConfig(caseId);
            if (config!=null) {
                arraySortConfig = config.getConfig();
            }
        }

        Comparable comparable = ComparableFactory.instance().create(
                Comparator.CompareMode.DEFAULT,
                appCompareConfigDO.getIgnoreCompareNodes(),
                null,
                arraySortConfig
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
            throw BizException.build("system error");
        }

        //支持java主调用异常抛出场景下的校验，详情咨询peng.hu1
        if ("null".equals(record.getResponse()) && rm.getResponse() == null) {

            Serializer hessian = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
            try {
                RecordWrapper wrapper = hessian.deserialize(record.getWrapperRecord(), RecordWrapper.class);

                Invocation mainInvoke = wrapper.getEntranceInvocation();

                Serializer serializer = SerializerWrapper.getSerializer(mainInvoke.getSerializeType());

                String expectThr = wrapper.getEntranceInvocation().getThrowableSerialized();
                String actualThr = rm.getThrowableSerialized();
                if (expectThr == null && actualThr == null) {
                    return;
                }

                HashMap expectObj = null;
                HashMap actualObj = null;

                if (expectThr !=null ) {
                    expectObj = (HashMap) serializer.deserialize(expectThr);
                    //修复栈溢出的问题
                    expectObj.put("cause", null);
                }

                if (actualThr !=null ) {
                    actualObj = (HashMap) serializer.deserialize(actualThr);
                    if (actualObj!=null) {
                        //修复栈溢出的问题
                        actualObj.put("cause", null);
                    }
                }

                result = comparable.compare(actualObj, expectObj);
                replay.setSuccess(!result.hasDifference());

                replay.setDiffResult(JacksonUtil.serialize(result.getDifferences()
                        .stream()
                        .map(differenceConvert::convert)
                        .collect(Collectors.toList()), false));


            } catch (SerializeException e) {
                log.error("system error", e);
            }
        }
    }

    @Override
    public boolean isSupport(SaveRepeatRequest saveRepeatRequest) {
        return true;
    }
}
