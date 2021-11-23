package com.alibaba.jvm.sandbox.repeater.plugin.rpc.wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *
 * @author qiyi-wangyeran/fanxiuping
 */
public class RpcWrapperTransModel {

    /**
     * rpc code
     */
    private String rpcCode;
    /**
     * thrift_parameter_types
     */
    private String  thriftParameterTypes;
    /**
     * thrift_parameter_values
     */
    private  String thriftProtocol;
    public static String RECEIVE = "receiveBase";
    public static String SEND = "sendBase";
    protected static Logger log = LoggerFactory.getLogger(RpcWrapperTransModel.class);




    private RpcWrapperTransModel(String rpcCode,String thriftParameterTypes,String  thriftProtocol) {
        this.rpcCode=rpcCode;
        this.thriftParameterTypes=thriftParameterTypes;
        this.thriftProtocol = thriftProtocol;
    }


    public static RpcWrapperTransModel build(BeforeEvent event) {
        try {
            Object[] argumentArray=(Object[])event.argumentArray;
            String rpcCode="";
            String thriftParameterTypes="";
            String thriftProtocol="";
            Object rpcObj = event.target;
            if (rpcObj != null){
                Object inprotocol = MethodUtils.invokeMethod(rpcObj, "getInputProtocol");
                thriftProtocol = inprotocol.toString();
            }
            if(event.argumentArray!= null && argumentArray.length>=2){
                if (RECEIVE.equals(event.javaMethodName)){
                    rpcCode= RECEIVE + "_" + argumentArray[1].toString();
                    thriftParameterTypes = argumentArray[0].toString();
                }else{
                    rpcCode= SEND + "_" + argumentArray[0].toString();
                    thriftParameterTypes = JSON.toJSONString(argumentArray[1]);
                }
            }
            return new RpcWrapperTransModel(
                    rpcCode,
                    thriftParameterTypes,
                    thriftProtocol
            );
        }catch(Exception e){
            log.error("rpc thrift Wrapper error {},object:{},event arg:{},func:{}", e,event.target,event.argumentArray,event.javaClassName+event.javaMethodName);

        }
        return null;
    }
    public String getRpcCode() {
        return rpcCode;
    }

    public String getthriftParameterTypes() {
        return thriftParameterTypes;
    }

    public  String getThriftProtocol(){
        return this.thriftProtocol;
    }
}
