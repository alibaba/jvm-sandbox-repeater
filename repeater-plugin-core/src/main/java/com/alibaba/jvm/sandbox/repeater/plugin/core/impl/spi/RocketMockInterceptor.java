package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.ReplaceObject;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockInterceptor;

/**
 * rocketMQ Mock策略执行中的拦截器
 * 
 * @author zzm
 * @date 2021年6月7日
 */
@MetaInfServices(MockInterceptor.class)
public class RocketMockInterceptor implements MockInterceptor {

    private static Logger log = LoggerFactory.getLogger(RocketMockInterceptor.class);

    @Override
    public void beforeSelect(MockRequest request) {
        // 如果一次调用里有多次rocketMq的子调用，会进入这个方法多次。这是必须的，因为要设置ModifiedInvocationIdentity，给ParameterMatchMockStrategy.calcSimilarity的判断使用
        List<ReplaceObject> rockMqTopic = ApplicationModel.instance().getConfig().getRockMqTopic();

        // DefaultMQProducerImpl#sendDefaultImpl(Message msg,final CommunicationMode communicationMode,final
        // SendCallback sendCallback,final long timeout)
        Object[] argumentArray = request.getArgumentArray();
        Object messageObj = argumentArray[0];
        replaceTopic(messageObj, rockMqTopic);
        replaceProperties(messageObj);

        List<Invocation> subInvocations = request.getRecordModel().getSubInvocations();
        if (CollectionUtils.isEmpty(subInvocations)) {
            return;
        }

        Set<Identity> modifySet = new HashSet<>();
        for (Invocation subInvocation : subInvocations) {
            if (InvokeType.ROCKET_MQ.equals(subInvocation.getType())) {
                messageObj = subInvocation.getRequest()[0];
                replaceTopic(messageObj, rockMqTopic);
                replaceProperties(messageObj);
                modifySet.add(subInvocation.getIdentity());
            }
        }
        request.setModifiedInvocationIdentity(modifySet);
    }

    private void replaceProperties(Object messageObj) {
        Field propertiesField = FieldUtils.getDeclaredField(messageObj.getClass(), "properties", true);
        if (propertiesField == null) {
            return;
        }
        try {
            // {traceid=a38e3769b72178a4, KEYS=rocketMqtest1, id=b74774f8-fc54-03a7-f3d3-3d8ea0aa85b0, WAIT=false,
            // contentType=application/json;charset=UTF-8, TAGS=rocketMqtest1, timestamp=1624868276567}
            Object properties = propertiesField.get(messageObj);
            MethodUtils.invokeMethod(properties, "remove", "traceid");
            MethodUtils.invokeMethod(properties, "remove", "id");
            MethodUtils.invokeMethod(properties, "remove", "timestamp");
            MethodUtils.invokeMethod(properties, "remove", "contentType");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void replaceTopic(Object messageObj, List<ReplaceObject> rockMqTopic) {
        if (CollectionUtils.isEmpty(rockMqTopic)) {
            return;
        }
        Field topicField = FieldUtils.getDeclaredField(messageObj.getClass(), "topic", true);
        if (topicField == null) {
            return;
        }

        try {
            Object sourceTopic = topicField.get(messageObj);
            String targetTopic = getTarget(sourceTopic, rockMqTopic);
            if (StringUtils.isNotBlank(targetTopic) && !targetTopic.equals(sourceTopic)) {
                topicField.setAccessible(true);
                topicField.set(messageObj, targetTopic);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getTarget(Object sourceTopic, List<ReplaceObject> rockMqTopic) {
        Optional<ReplaceObject> findAny = rockMqTopic.stream()
            .filter(r -> sourceTopic != null && r.getSource() != null && sourceTopic.toString().equals(r.getSource()))
            .findAny();

        if (findAny.isPresent()) {
            Object target = findAny.get().getTarget();
            if (target != null) {
                return target.toString();
            }
        } else {
            findAny = rockMqTopic.stream().filter(r -> r.getSource() == null).findAny();
            if (findAny.isPresent()) {
                Object target = findAny.get().getTarget();
                if (target != null) {
                    return target.toString();
                }
            }
        }
        return null;
    }

    @Override
    public void beforeReturn(MockRequest request, MockResponse response) {}

    @Override
    public boolean matchingSelect(MockRequest request) {
        if (InvokeType.ROCKET_MQ.equals(request.getType())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean matchingReturn(MockRequest request, MockResponse response) {
        return false;
    }

}