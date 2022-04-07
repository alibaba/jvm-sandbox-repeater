package com.alibaba.jvm.sandbox.repeater.plugin.mongo.wrapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * @author wangyeran
 */
public class MongoWrapperTransModel {
    /**
     * 数据库dbname
     */
    private String dbName;
    /**
     * 表名称
     */
    private String  tableName;
    /**
     * mongomethod
     */
    private String  methodName;
    /**
     * ParamString
     */
    private String  paramString;

    private MongoWrapperTransModel(String dbName,String tableName,String methodName,String paramString) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.methodName = methodName;
        this.paramString = paramString;
    }

    public static MongoWrapperTransModel build(BeforeEvent event) {
        try {
            String methodName = ((BeforeEvent) event).javaMethodName;
            Object params = event.argumentArray[0];
            String paramString = JSONObject.toJSONString(params);
            String tableName = "";
            String dbName = "";
            try {
                Object mongotemplate = event.target;

                Field fieldtable = FieldUtils.getDeclaredField(mongotemplate.getClass(), "mongoDbFactory", true);
                if (fieldtable != null) {
                    Object dbfactory = fieldtable.get(mongotemplate);
                    Object dbs = MethodUtils.invokeMethod(dbfactory, "getDb");
                    if (dbs != null) {
                        dbName = MethodUtils.invokeMethod(dbs, "getName").toString();
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            switch(methodName) {
                case "find": tableName = event.argumentArray[2].toString();break;
                case "findOne": tableName = event.argumentArray[2].toString();break;
                case "findById": tableName = event.argumentArray[2].toString();break;
                case "findAll": tableName = event.argumentArray[1].toString();break;
                case "findAndModify": tableName = event.argumentArray[4].toString();break;
                case "count": tableName = event.argumentArray[2].toString();break;
                case "save": tableName = event.argumentArray[2].toString();break;
                case "insertAll": tableName = event.argumentArray[0].getClass().getName();break;
                case "insert": tableName = event.argumentArray[2].toString();break;
                case "remove": tableName = event.argumentArray[2].toString();break;
                case "aggregate": tableName = event.argumentArray[1].toString();break;
                case "doUpdate": tableName = event.argumentArray[0].toString();break;
                default: break;
            }

            switch(dbName){
                case "ugc_audit_production": dbName = "audit";break;
                case "ugc_audit": dbName = "audit";break;
                case "audit_shortvideo": dbName = "audit_shortvideo_test";break;
                case "ai_prod": dbName = "ai_test";break;
                default:break;
            }

            return new MongoWrapperTransModel(
                    dbName,
                    tableName,
                    methodName,
                    paramString
            );


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String getDbName() {
        return dbName;
    }

    public String gettableName() {
        return tableName;
    }

    public String getmethodName() {
        return methodName;
    }

    public String getparamString() {
        return paramString;
    }

}
