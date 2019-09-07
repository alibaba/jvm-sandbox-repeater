package com.alibaba.jvm.sandbox.repeater.plugin.mybatis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
class MybatisProcessor extends DefaultInvocationProcessor {

    MybatisProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object mapperMethod = event.target;
        // SqlCommand = MapperMethod.command
        Field field = FieldUtils.getDeclaredField(mapperMethod.getClass(), "command", true);
        if (field == null) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
        try {
            Object command = field.get(mapperMethod);
            Object name = MethodUtils.invokeMethod(command, "getName");
            Object type = MethodUtils.invokeMethod(command, "getType");
            return new Identity(InvokeType.MYBATIS.name(), type.toString(), name.toString(), new HashMap<String, String>(1));
        } catch (Exception e) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        // MapperMethod#execute(SqlSession sqlSession, Object[] args)
        // args可能存在不可序序列化异常（例如使用tk.mybatis)

        /**
         * org.apache.ibatis.binding.MapperMethod中可以通过这个方法获取MappedStatement
         * 获取方法： MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(command.getName());
         */
        Object mapperMethod = event.target;
        Field field = FieldUtils.getDeclaredField(mapperMethod.getClass(), "command", true);
        Object isUseGeneratedKeys = false;
        // keyProperties 的默认值是"id"
        String[] defaultKeyProperties = new String[]{"id"};
        Object keyProperties = null;
        Object sqlSession = event.argumentArray[0];

        // 作为request的第二个参数保存
        JSONObject mapperConfig = new JSONObject();

        try {
            // 获取从方法的调用实例中获取到一些变量信息
            Object command = field.get(mapperMethod);
            Object config = MethodUtils.invokeMethod(sqlSession,"getConfiguration");
            Object name = MethodUtils.invokeMethod(command, "getName");
            Object mappedStatement =  MethodUtils.invokeMethod(config, "getMappedStatement", name);

            // 判断isUseGeneratedKeys： 从mappedStatement中的keyGenerator的类型来判断
            Object keyGenerator =  MethodUtils.invokeMethod(mappedStatement, "getKeyGenerator");
            isUseGeneratedKeys = getIsUseGeneratedKeysByKeyGeneratorType(keyGenerator);

            // 从mappedStatement中的获取keyProperties ，获取需要替换id的属性名列表
            Object keyPropertiesFromObject =  MethodUtils.invokeMethod(mappedStatement, "getKeyProperties");
            LogUtil.debug("mybatis get keyPropertiesFromObject={}", JSONObject.toJSONString(keyPropertiesFromObject));
            keyProperties = (keyPropertiesFromObject != null && keyPropertiesFromObject instanceof String[]) ? keyPropertiesFromObject: defaultKeyProperties;
            LogUtil.debug("mybatis get keyProperties={}", JSONObject.toJSONString(keyProperties));
        } catch (Exception e) {
            LogUtil.error("Get mybatis statement useGeneratedKeys configuration fail, use default value: false", e);
        }

        mapperConfig.put("isUseGeneratedKeys", isUseGeneratedKeys);
        mapperConfig.put("keyProperties", keyProperties);

        return new Object[]{event.argumentArray[1], mapperConfig};
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }

    /**
     * 判断isUseGeneratedKeys： 从mappedStatement中的keyGenerator的类型来判断
     * 只有当isUseGeneratedKeys为true时 keyGenerator为org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator
     * @param keyGenerator
     * @return
     */

    private Boolean getIsUseGeneratedKeysByKeyGeneratorType(Object keyGenerator){
        return  (keyGenerator != null && "org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator".equals(keyGenerator.getClass().getName()));

    }
}
