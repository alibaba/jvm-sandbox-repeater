package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;

/**
 * {@link MockStrategy} 作为Mock回放的选择策略；负责在N个子调用中选择正确的子调用作为Mock返回
 * <p>
 * 回放策略来讲开放的实现比较少；用户可以自己定义和实现更多的策略
 * </p>
 *
 * @author zhaoyb1990
 */
public interface MockStrategy {

    /**
     * 回放策略的类型
     *
     * @return 回放策略
     */
    StrategyType type();

    /**
     * 执行策略
     *
     * @param request mock请求对象
     * @return mock返回结果
     */
    MockResponse execute(final MockRequest request);



    /**
     *  选择出回放的invocation 并删除命中的invocation
     *  一般情况下，为了保证回放匹配效率，在回放流程中默认是true，而在其他步骤调用则按需要选择即可
     *
     * @param request mock回放请求
     * @return 选择结果
     */
    SelectResult select(final MockRequest request);


    /**
     *  选择出回放的invocation 并根据需要看是否要删除命中的invocation
     *  一般情况下，为了保证回放匹配效率，在回放流程中默认是true，而在其他步骤调用则按需要选择即可
     *
     * @param request mock回放请求
     * @Param removeFromSubInvocationIfFound 找到后是否需要删除子调用中的命中的invocation
     * @return 选择结果
     */
    SelectResult select(final MockRequest request, Boolean removeFromSubInvocationIfFound);


    enum StrategyType {
        /**
         * 参数相似度对比
         */
        PARAMETER_MATCH("parameter_match"),

        /**
         * 默认回放策略，阻断所有子调用
         */
        DEFAULT("default"),

        /**
         * 对象对比
         */
        OBJECT_DFF("object_dff")
        ;

        private String type;

        StrategyType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
