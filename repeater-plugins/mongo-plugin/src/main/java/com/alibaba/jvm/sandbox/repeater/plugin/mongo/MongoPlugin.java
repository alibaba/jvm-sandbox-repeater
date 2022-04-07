package com.alibaba.jvm.sandbox.repeater.plugin.mongo;

import java.util.List;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;

import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;



/**
 * <p>
 *
 * @author wangyeran
 */
@MetaInfServices(InvokePlugin.class)
public class MongoPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern mp1 = EnhanceModel.MethodPattern.builder()
                .methodName("find")
                .parameterType(new String[]{"org.springframework.data.mongodb.core.query.Query","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp2 = EnhanceModel.MethodPattern.builder()
                .methodName("findOne")
                .parameterType(new String[]{"org.springframework.data.mongodb.core.query.Query","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp3 = EnhanceModel.MethodPattern.builder()
                .methodName("findById")
                .parameterType(new String[]{"java.lang.Object","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp4 = EnhanceModel.MethodPattern.builder()
                .methodName("findAll")
                .parameterType(new String[]{"java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp5 = EnhanceModel.MethodPattern.builder()
                .methodName("findAndModify")
                .parameterType(new String[]{"org.springframework.data.mongodb.core.query.Query","org.springframework.data.mongodb.core.query.Update","org.springframework.data.mongodb.core.FindAndModifyOptions","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp6 = EnhanceModel.MethodPattern.builder()
                .methodName("count")
                .parameterType(new String[]{"org.springframework.data.mongodb.core.query.Query","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp7 = EnhanceModel.MethodPattern.builder()
                .methodName("save")
                .parameterType(new String[]{"java.lang.Object","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp8 = EnhanceModel.MethodPattern.builder()
                .methodName("insertAll")
                .parameterType(new String[]{"java.util.Collection"})
                .build();
        EnhanceModel.MethodPattern mp9 = EnhanceModel.MethodPattern.builder()
                .methodName("insert")
                .parameterType(new String[]{"java.lang.Object","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp10 = EnhanceModel.MethodPattern.builder()
                .methodName("insert")
                .parameterType(new String[]{"java.util.Collection","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp11 = EnhanceModel.MethodPattern.builder()
                .methodName("insert")
                .parameterType(new String[]{"java.util.Collection","java.lang.Class"})
                .build();
        EnhanceModel.MethodPattern mp12 = EnhanceModel.MethodPattern.builder()
                .methodName("remove")
                .parameterType(new String[]{"org.springframework.data.mongodb.core.query.Query","java.lang.Class","java.lang.String"})
                .build();
        EnhanceModel.MethodPattern mp13 = EnhanceModel.MethodPattern.builder()
                .methodName("aggregate")
                .parameterType(new String[]{"org.springframework.data.mongodb.core.aggregation.Aggregation","java.lang.String","java.lang.Class","org.springframework.data.mongodb.core.aggregation.AggregationOperationContext"})
                .build();
        EnhanceModel.MethodPattern mp14 = EnhanceModel.MethodPattern.builder()
                .methodName("doUpdate")
                .parameterType(new String[]{"java.lang.String","org.springframework.data.mongodb.core.query.Query","org.springframework.data.mongodb.core.query.Update","org.springframework.data.mongodb.core.FindAndModifyOptions","java.lang.Class","boolean","boolean"})
                .build();
      EnhanceModel em = EnhanceModel.builder()
                .classPattern("org.springframework.data.mongodb.core.MongoTemplate")
                .methodPatterns(new EnhanceModel.MethodPattern[]{mp1,mp2,mp3,mp4,mp5,mp6,mp7,mp8,mp9,mp10,mp11,mp12,mp13,mp14})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MongoProcessor(getType());
    }



    @Override
    public InvokeType getType() {
        return InvokeType.MONGO;
    }

    @Override
    public String identity() {
        return "mongo";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

}
