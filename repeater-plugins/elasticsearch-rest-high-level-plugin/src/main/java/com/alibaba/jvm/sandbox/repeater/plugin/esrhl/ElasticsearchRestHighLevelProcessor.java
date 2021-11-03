package com.alibaba.jvm.sandbox.repeater.plugin.esrhl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * @Author: luwenrong@zhongan.com
 * @Title:  ElasticsearchRestHighLevelProcessor
 * @Date: 2021/10/19
 */
class ElasticsearchRestHighLevelProcessor extends DefaultInvocationProcessor {
    ElasticsearchRestHighLevelProcessor(InvokeType type) {
        super(type);
    }
}
