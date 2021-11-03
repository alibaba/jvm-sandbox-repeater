package com.alibaba.jvm.sandbox.repeater.plugin.esrhl;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * @Author: luwenrong@zhongan.com
 * @Title:  elasticsearch RestHighLevelClient 插件
 * @Description: 拦截{@code org.elasticsearch.client}包下面的RestHighLevelClient实现类
 * @Date: 2021/10/19
 */
@MetaInfServices(InvokePlugin.class)
public class ElasticsearchRestHighLevelPlugin extends AbstractInvokePluginAdapter {
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("org.elasticsearch.client.RestHighLevelClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "bulk",
                        "bulkAsync",
                        "reindex",
                        "submitReindexTask",
                        "reindexAsync",
                        "updateByQuery",
                        "submitUpdateByQueryTask",
                        "updateByQueryAsync",
                        "deleteByQuery",
                        "submitDeleteByQueryTask",
                        "deleteByQueryAsync",
                        "deleteByQueryRethrottle",
                        "updateByQueryRethrottle",
                        "updateByQueryRethrottleAsync",
                        "reindexRethrottle",
                        "ping",
                        "info",
                        "get",
                        "getAsync",
                        "multiGet",
                        "multiGetAsync",
                        "mgetAsync",
                        "exists",
                        "existsAsync",
                        "existsSource",
                        "existsSourceAsync",
                        "existsSource",
                        "existsSourceAsync",
                        "getSource",
                        "getSourceAsync",
                        "index",
                        "indexAsync",
                        "count",
                        "countAsync",
                        "update",
                        "updateAsync",
                        "delete",
                        "deleteAsync",
                        "search",
                        "searchAsync",
                        "multiSearch",
                        "multiSearchAsync",
                        "msearchAsync",
                        "searchScroll",
                        "scroll",
                        "searchScrollAsync",
                        "scrollAsync",
                        "clearScroll",
                        "clearScrollAsync",
                        "searchTemplate",
                        "searchTemplateAsync",
                        "explain",
                        "explainAsync",
                        "termvectors",
                        "termvectorsAsync",
                        "mtermvectors",
                        "mtermvectorsAsync",
                        "rankEval",
                        "msearchTemplate",
                        "msearchTemplateAsync",
                        "rankEvalAsync",
                        "fieldCaps",
                        "getScript",
                        "getScriptAsync",
                        "deleteScript",
                        "deleteScriptAsync",
                        "putScript",
                        "putScriptAsync",
                        "fieldCapsAsync"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new ElasticsearchRestHighLevelProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.ESRHL;
    }

    @Override
    public String identity() {
        return "elasticsearch-rest-high-level";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }


}
