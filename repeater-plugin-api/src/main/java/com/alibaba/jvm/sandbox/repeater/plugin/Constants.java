package com.alibaba.jvm.sandbox.repeater.plugin;

/**
 * {@link Constants} 通用一些关键字
 * <p>
 *
 * @author zhaoyb1990
 */
public class Constants {

    /**
     * console与module通信数据传输字段
     */
    public static final String DATA_TRANSPORT_IDENTIFY = "_data";

    /**
     * 默认数据源地址
     */
    public static final String DEFAULT_REPEAT_DATASOURCE = "repeat.record.url";

    /**
     * 默认配置拉取地址
     */
    public static final String DEFAULT_CONFIG_DATASOURCE = "repeat.config.url";

    /**
     * 默认回放消息投递地址
     */
    public static final String DEFAULT_REPEAT_BROADCASTER = "broadcaster.repeat.url";

    /**
     * 默认录制消息投递地址
     */
    public static final String DEFAULT_RECORD_BROADCASTER = "broadcaster.record.url";

    /**
     * 是否开启单机工作模式
     */
    public static final String REPEAT_STANDALONE_MODE = "repeat.standalone.mode";

    /**
     * 是否开启spring advice的拦截
     */
    public static final String REPEAT_SPRING_ADVICE_SWITCH = "repeat.spring.advice.switch";

    /**
     * 模块心跳地址
     */
    public static final String REPEAT_HEARTBEAT_URL= "repeat.heartbeat.url";

    /**
     * 插件自有类正则
     */
    public static final String[] PLUGIN_CLASS_PATTERN = new String[]{
            "^com.alibaba.jvm.sandbox.repeater.plugin.core..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.api..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.spi..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.domain..*",
            "^com.alibaba.jvm.sandbox.repeater.plugin.exception..*",
            "^org.slf4j..*",
            "^ch.qos.logback..*",
            "^org.apache.commons..*"
    };

    /**
     * servlet-api路由（目前sandbox还不支持启动module路由，所以在插件层面进行路由，保证插件使用容器的servlet-api）
     */
    public static final String SERVLET_API_NAME = "javax.servlet.http.HttpServlet";

    /**
     * 透传给下游的traceId；需要利用traceId串联回放流程
     */
    public static final String HEADER_TRACE_ID = "Repeat-TraceId";

    /**
     * 透传给下游的traceId；跟{@code HEADER_TRACE_ID}的差异在于，{@code HEADER_TRACE_ID_X}表示一次回放请求；需要进行Mock
     */
    public static final String HEADER_TRACE_ID_X = "Repeat-TraceId-X";
}
