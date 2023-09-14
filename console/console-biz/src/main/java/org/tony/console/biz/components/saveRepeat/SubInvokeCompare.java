package org.tony.console.biz.components.saveRepeat;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Comparable;
import com.alibaba.jvm.sandbox.repeater.aide.compare.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.aide.compare.CompareResult;
import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Replay;
import org.tony.console.service.convert.DifferenceConvert;
import org.tony.console.service.model.AppCompareConfigDO;
import org.tony.console.service.model.SubInvokeIgnoreNode;
import org.tony.console.service.utils.JacksonUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/10 17:07
 */
@Slf4j
@Order(40)
@Component
public class SubInvokeCompare implements SaveRepeatComponent {

    @Resource
    DifferenceConvert differenceConvert;

    @Override
    public void execute(SaveRepeatRequest saveRepeatRequest) throws BizException {
        RepeatModel rm = saveRepeatRequest.getRepeatModel();
        Replay replay = saveRepeatRequest.getReplay();

        try {
            /**
             * 全局比对配置
             */
            AppCompareConfigDO appCompareConfigDO = saveRepeatRequest.getAppCompareConfigDO();

            List<String> needToCompareIdList = appCompareConfigDO.getSubInvokeToCompare();
            if (CollectionUtils.isEmpty(needToCompareIdList)) {
                return;
            }

            List<MockInvocation> mockInvocations =  rm.getMockInvocations();
            if (CollectionUtils.isEmpty(mockInvocations)) {
                return;
            }

            List<MockInvocation> needToCompareInvocations = mockInvocations
                    .stream()
                    .filter(item->needToCompareIdList.contains(item.getOriginUri()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(needToCompareInvocations)) {
                return;
            }

            Comparable comparable = ComparableFactory.instance().create(
                    Comparator.CompareMode.DEFAULT,
                    null,
                    null,
                    null
            );

            boolean success = replay.isSuccess();

            for (MockInvocation invocation: needToCompareInvocations) {
                Object[] expect = invocation.getOriginArgs();
                Object[] actual = invocation.getCurrentArgs();

                comparable.setIgnoreCompareString(getIgnoreNodes(invocation, appCompareConfigDO));

                CompareResult result = comparable.compare(actual, expect);
                if (result.hasDifference()) {
                    invocation.setDiffs(result.getDifferences().stream()
                            .map(differenceConvert::convert)
                            .collect(Collectors.toList()));

                    success = false;
                }
            }
            replay.setMockInvocation(JacksonUtil.serialize(rm.getMockInvocations()));
            replay.setSuccess(success);
            if(!success) {
                replay.setFailReason("子调用比对不过");
            }
        } catch (Exception e) {
            log.error("system error", e);
        }
    }

    private List<String> getIgnoreNodes(MockInvocation invocation,  AppCompareConfigDO appCompareConfigDO) {
        List<String> ignoreList = new ArrayList<>();

        //先把全局的加进去
        if(!CollectionUtils.isEmpty(appCompareConfigDO.getIgnoreCompareNodes())) {
            ignoreList.addAll(appCompareConfigDO.getIgnoreCompareNodes());
        }

        for (SubInvokeIgnoreNode subInvokeIgnoreNode : appCompareConfigDO.getSubInvokeIgnoreCompareNodes()) {
            if (subInvokeIgnoreNode.getIdentity().equals(invocation.getOriginUri())) {
                ignoreList.addAll(subInvokeIgnoreNode.getIgnoreNodes());
            }
        }

        return ignoreList;
    }

    @Override
    public boolean isSupport(SaveRepeatRequest saveRepeatRequest) {
        return true;
    }
}
