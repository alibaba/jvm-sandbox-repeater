package com.alibaba.jvm.sandbox.repeater.plugin.thrift.wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 *
 * @author wangyeran/fanxiuping
 */
public class ThriftWrapperTransModel {
    /**
     * thrift code
     */
    private final String thriftCode;
    /**
     * thrift_parameter_types
     */
    private final String thriftParameterTypes;
    /**
     * thrift_parameter_values
     */
    private final String thriftProtocol;
    public static String RECEIVE = "receiveBase";
    public static String SEND = "sendBase";
    protected static Logger log = LoggerFactory.getLogger(ThriftWrapperTransModel.class);

    private ThriftWrapperTransModel(String thriftCode, String thriftParameterTypes, String thriftProtocol) {
        this.thriftCode = thriftCode;
        this.thriftParameterTypes = thriftParameterTypes;
        this.thriftProtocol = thriftProtocol;
    }

    public static ThriftWrapperTransModel build(BeforeEvent event) {
        try {
            Object[] argumentArray = (Object[]) event.argumentArray;
            String thriftCode = "";
            String thriftParameterTypes = "";
            String thriftProtocol = "";

            Object thriftObj = event.target;
            if (thriftObj != null) {
                Object inprotocol = MethodUtils.invokeMethod(thriftObj, "getInputProtocol");
                thriftProtocol = inprotocol.toString();
            }
            if (event.argumentArray != null && argumentArray.length >= 2) {
                if (RECEIVE.equals(event.javaMethodName)) {
                    thriftCode = RECEIVE + "_" + argumentArray[1].toString();
                    thriftParameterTypes = argumentArray[0].toString();
                } else {
                    thriftCode = SEND + "_" + argumentArray[0].toString();
                    thriftParameterTypes = JSON.toJSONString(argumentArray[1]);
                }
            }
            return new ThriftWrapperTransModel(
                    thriftCode,
                    thriftParameterTypes,
                    thriftProtocol

            );
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(" thrift Wrapper error {},object:{},event arg:{},func:{}",
                    e, event.target, event.argumentArray, event.javaClassName + event.javaMethodName);

        }
        return null;
    }

    public String getThriftCode() {
        return thriftCode;
    }

    public String getThriftParameterTypes() {
        return thriftParameterTypes;
    }

    public String getThriftProtocol() {
        return this.thriftProtocol;
    }
}
