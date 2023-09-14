package org.tony.console.biz.model.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.model.BehaviorVO;
import org.tony.console.biz.model.PluginConfigVO;
import org.tony.console.biz.model.StaticConfigVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/2/21 10:29
 */
@Component
public class StaticConfigVOConvert {

    @Value("${plugin.name.list}")
    private String pluginNameConfig;

    public StaticConfigVO convert(RepeaterConfig repeaterConfig) {
        StaticConfigVO configVO = new StaticConfigVO();

        configVO.setDegrade(!repeaterConfig.isDegrade());
        configVO.setSampleRate(repeaterConfig.getSampleRate());
        configVO.setUseTtl(repeaterConfig.isUseTtl());
        configVO.setHttpEntrancePatternsWithSampleRate(repeaterConfig.getHttpEntrancePatternsWithSampleRate());

        configVO.setMainInvokes(convertBehaviors(repeaterConfig.getJavaEntranceBehaviors()));
        configVO.setJavaSubInvokes(convertBehaviors(repeaterConfig.getJavaSubInvokeBehaviors()));

        configVO.setPluginConfigVOS(convertPlugins(repeaterConfig.getPluginIdentities()));

        configVO.setDelayTime(repeaterConfig.getDelayTime());
        configVO.setSerializeType(repeaterConfig.getSerializeType());

        return configVO;
    }

    public List<BehaviorVO> convertBehaviors(List<Behavior> behaviors) {
        if (CollectionUtils.isEmpty(behaviors)) {
            return new ArrayList<>(0);
        }

        return behaviors.stream().map(this::convertBehavior).collect(Collectors.toList());
    }

    public BehaviorVO convertBehavior(Behavior behavior) {
        BehaviorVO vo = new BehaviorVO();

        vo.setClassPattern(behavior.getClassPattern());
        vo.setMethods(StringUtils.join(behavior.getMethodPatterns(), ","));

        return vo;
    }

    public List<PluginConfigVO> convertPlugins(List<String> pluginIdentities) {

        HashMap<String, String> pluginIdentity2Name = getPluginIdentity2Name();

        List<PluginConfigVO> vos = new LinkedList<>();

        for (String key : pluginIdentity2Name.keySet()) {
            PluginConfigVO vo = new PluginConfigVO();
            vo.setIdentity(key);
            vo.setName(pluginIdentity2Name.get(key));
            vo.setOpen(pluginIdentities.contains(key));
            vos.add(vo);
        }

        return vos;
    }

    private HashMap<String, String> getPluginIdentity2Name() {
        return JSON.parseObject(pluginNameConfig, HashMap.class);
    }

    public void reconvert(RepeaterConfig repeaterConfig, StaticConfigVO staticConfigVO) {
        repeaterConfig.setUseTtl(staticConfigVO.getUseTtl());
        repeaterConfig.setSampleRate(staticConfigVO.getSampleRate());
        repeaterConfig.setDegrade(!staticConfigVO.isDegrade());
        repeaterConfig.setDelayTime(staticConfigVO.getDelayTime());

        repeaterConfig.setHttpEntrancePatternsWithSampleRate(staticConfigVO.getHttpEntrancePatternsWithSampleRate());

        repeaterConfig.setJavaEntranceBehaviors(reconvert(staticConfigVO.getMainInvokes()));
        repeaterConfig.setJavaSubInvokeBehaviors(reconvert(staticConfigVO.getJavaSubInvokes()));

        //支持序列化方式修改
        repeaterConfig.setSerializeType(staticConfigVO.getSerializeType());

        List<PluginConfigVO> pluginConfigVOS = staticConfigVO.getPluginConfigVOS();
        if (pluginConfigVOS == null) {
            repeaterConfig.setPluginIdentities(Collections.EMPTY_LIST);
        } else {
            List<String> pluginIdentities = new LinkedList<>();
            pluginConfigVOS.forEach(item->{
                if (item.getOpen()) {
                    pluginIdentities.add(item.getIdentity());
                }
            });
            repeaterConfig.setPluginIdentities(pluginIdentities);
        }

        repeaterConfig.getRepeatIdentities().clear();
        repeaterConfig.getRepeatIdentities().add("java");
        repeaterConfig.getRepeatIdentities().add("http");
        repeaterConfig.setExceptionThreshold(1000);

    }

    public List<Behavior> reconvert(List<BehaviorVO> behaviorVOS) {
        if (CollectionUtils.isEmpty(behaviorVOS)) {
            return new ArrayList<>(0);
        }

        List<Behavior> behaviorList = new LinkedList<>();
        behaviorVOS.forEach(item->{
            Behavior behavior = new Behavior();
            behavior.setClassPattern(item.getClassPattern());
            behavior.setMethodPatterns(StringUtils.split(item.getMethods(), ","));
            behavior.setIncludeSubClasses(false);

            if (behavior.getClassPattern().startsWith("java")) {
                behavior.setIncludeBootstrapClasses(true);
            } else {
                behavior.setIncludeBootstrapClasses(false);
            }

            behaviorList.add(behavior);
        });

        return behaviorList;
    }
}
