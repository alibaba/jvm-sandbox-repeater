package com.alibaba.jvm.sandbox.repeater.plugin.core.model;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder.IBuildingForClass;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;

/**
 * {@link EnhanceModel}
 * 插件增加类型的基础抽象，{@link com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter}使用进行插件增强
 * <p>
 *
 * @author zhaoyb1990
 */
public class EnhanceModel {

    /**
     * 增强类表达式，支持通配符
     *
     * @see com.alibaba.jvm.sandbox.api.util.GaStringUtils#matching
     */
    private String classPattern;

    /**
     * 增强方法表达式，，支持通配符
     */
    private MethodPattern[] methodPatterns;

    /**
     * 观察的事件
     * <p>
     * 一般情况需要关注 BEFORE/RETURN/THROW 事件，构成一个方法的around，完成方法入参/返回值/异常的录制
     * <p>
     * 如果基于回调的情况，例如onRequest/onResponse，则只需关注BEFORE事件，通过两个BEFORE去组装，但注意这种情况需要重写{@link
     * com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener#isEntranceFinish(Event)}方法
     */
    private Type[] watchTypes;

    /**
     * 是否包含子类
     *
     * @see IBuildingForClass#includeSubClasses()
     */
    private boolean includeSubClasses;

    private boolean includeBootstrap = false;

    private String[] parameterTypes;

    @ConstructorProperties({"classPattern", "methodPatterns", "watchTypes", "includeSubClasses", "includeBootstrap"})
    EnhanceModel(String classPattern, MethodPattern[] methodPatterns, Type[] watchTypes, boolean includeSubClasses, boolean includeBootstrap) {
        this.classPattern = classPattern;
        this.methodPatterns = methodPatterns;
        this.watchTypes = watchTypes;
        this.includeSubClasses = includeSubClasses;
        this.includeBootstrap = includeBootstrap;
    }

    public static EnhanceModelBuilder builder() {
        return new EnhanceModelBuilder();
    }

    /**
     * 行为转换
     *
     * @param behavior 行为模型
     * @return 增强类模型
     */
    public static EnhanceModel convert(Behavior behavior) {
        return EnhanceModel.builder()
                .classPattern(behavior.getClassPattern())
                .methodPatterns(MethodPattern.transform(behavior.getMethodPatterns()))
                .includeSubClasses(behavior.isIncludeSubClasses())
                .includeBootstrap(behavior.isIncludeBootstrapClasses())
                .watchTypes(Type.BEFORE, Type.RETURN, Type.THROWS)
                .build();
    }

    public String getClassPattern() {
        return this.classPattern;
    }

    public MethodPattern[] getMethodPatterns() {
        return this.methodPatterns;
    }

    public Type[] getWatchTypes() {
        return this.watchTypes;
    }

    public boolean isIncludeSubClasses() {
        return this.includeSubClasses;
    }

    public boolean isIncludeBootstrap() {
        return includeBootstrap;
    }

    public void setIncludeBootstrap(boolean includeBootstrap) {
        this.includeBootstrap = includeBootstrap;
    }

    public static class EnhanceModelBuilder {
        private String classPattern;
        private MethodPattern[] methodPatterns;
        private Type[] watchTypes;
        private boolean includeSubClasses;
        private boolean includeBootstrap;

        EnhanceModelBuilder() {
        }

        public EnhanceModelBuilder classPattern(String classPattern) {
            this.classPattern = classPattern;
            return this;
        }

        public EnhanceModelBuilder methodPatterns(MethodPattern[] methodPatterns) {
            this.methodPatterns = methodPatterns;
            return this;
        }

        public EnhanceModelBuilder watchTypes(Type... watchTypes) {
            this.watchTypes = watchTypes;
            return this;
        }

        public EnhanceModelBuilder includeSubClasses(boolean includeSubClasses) {
            this.includeSubClasses = includeSubClasses;
            return this;
        }

        public EnhanceModelBuilder includeBootstrap(boolean includeBootstrap) {
            this.includeBootstrap = includeBootstrap;
            return this;
        }

        public EnhanceModel build() {
            return new EnhanceModel(this.classPattern, this.methodPatterns, this.watchTypes, this.includeSubClasses, this.includeBootstrap);
        }

        @Override
        public String toString() {
            return "EnhanceModel.EnhanceModelBuilder(classPattern=" + this.classPattern + ", methodPatterns=" + Arrays.deepToString(this.methodPatterns) + ", watchTypes=" + Arrays.deepToString(this.watchTypes) + ", includeSubClasses=" + this.includeSubClasses + ")";
        }
    }

    public static class MethodPattern {

        String methodName;
        String[] parameterType;
        String[] annotationTypes;

        private Boolean emptyParameter;

        @ConstructorProperties({"methodName", "parameterType", "annotationTypes"})
        public MethodPattern(String methodName, String[] parameterType, String[] annotationTypes) {
            this.methodName = methodName;
            this.parameterType = parameterType;
            this.annotationTypes = annotationTypes;
        }

        public MethodPattern(String methodName, String[] parameterType, String[] annotationTypes, Boolean emptyParameter) {
            this.methodName = methodName;
            this.parameterType = parameterType;
            this.annotationTypes = annotationTypes;
            this.emptyParameter = emptyParameter;
        }

        public static MethodPattern[] transform(String... methodNames) {
            if (ArrayUtils.isEmpty(methodNames)) {
                return null;
            }
            List<MethodPattern> methodPatterns = Lists.newArrayList();
            for (String methodName : methodNames) {
                methodPatterns.add(MethodPattern.builder().methodName(methodName).build());
            }
            return methodPatterns.toArray(new MethodPattern[0]);
        }

        public static MethodPatternBuilder builder() {
            return new MethodPatternBuilder();
        }

        public String getMethodName() {
            return this.methodName;
        }

        public String[] getParameterType() {
            return this.parameterType;
        }

        public String[] getAnnotationTypes() {
            return this.annotationTypes;
        }

        public Boolean getEmptyParameter() {
            return emptyParameter;
        }

        public static class MethodPatternBuilder {
            private String methodName;
            private String[] parameterType;
            private String[] annotationTypes;

            MethodPatternBuilder() {
            }

            public MethodPatternBuilder methodName(String methodName) {
                this.methodName = methodName;
                return this;
            }

            public MethodPatternBuilder parameterType(String[] parameterType) {
                this.parameterType = parameterType;
                return this;
            }

            public MethodPatternBuilder annotationTypes(String[] annotationTypes) {
                this.annotationTypes = annotationTypes;
                return this;
            }

            public MethodPattern build() {
                return new MethodPattern(this.methodName, this.parameterType, this.annotationTypes);
            }

            @Override
            public String toString() {
                return "EnhanceModel.MethodPattern.MethodPatternBuilder(methodName=" + this.methodName + ", parameterType=" + Arrays.deepToString(this.parameterType) + ", annotationTypes=" + Arrays.deepToString(this.annotationTypes) + ")";
            }
        }
    }
}
