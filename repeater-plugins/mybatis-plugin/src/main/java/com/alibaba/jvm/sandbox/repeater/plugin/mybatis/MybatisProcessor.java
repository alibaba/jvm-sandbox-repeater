package com.alibaba.jvm.sandbox.repeater.plugin.mybatis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

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
        return new Object[]{event.argumentArray[1]};
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }

    //支持keyGenerator
    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        try {
            Object mapperMethod = event.target;
            // SqlCommand = MapperMethod.command
            Field field = FieldUtils.getDeclaredField(mapperMethod.getClass(), "command", true);
            Object command = field.get(mapperMethod);
            String name = (String) MethodUtils.invokeMethod(command, "getName");

            Configuration configuration = (Configuration) MethodUtils.invokeMethod(event.argumentArray[0], "getConfiguration");
            MappedStatement mappedStatement = configuration.getMappedStatement(name);
            if (mappedStatement.getKeyGenerator()!=null && mappedStatement.getKeyProperties()!=null &&mappedStatement.getKeyProperties().length==1) {

                String[] keyProperties = mappedStatement.getKeyProperties();
                if (keyProperties!=null && keyProperties.length==1) {
                    String keyFieldName = keyProperties[0];
                    if (invocation.getRequest()!=null && invocation.getRequest().length==1) {
                        Object[] mockParam = (Object[]) invocation.getRequest()[0];

                        Object[] param = (Object[]) event.argumentArray[1];

                        Field keyField = FieldUtils.getDeclaredField(mockParam[0].getClass(), keyFieldName, true);
                        if (keyField != null) {
                            Object v = keyField.get(mockParam[0]);
                            FieldUtils.writeDeclaredField(param[0], keyFieldName, v, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("mybatis plugin error", e);
        }

        return invocation.getResponse();
    }

    @Override
    protected boolean skipMock(BeforeEvent event, Boolean entrance, RepeatContext context) {
        //优先继承父类
        boolean superSKip = super.skipMock(event, entrance, context);
        if (superSKip) {
            return true;
        }

        DynamicConfig dynamicConfig = ApplicationModel.instance().getDynamicConfig();
        Set<String> skipMockIdentities = dynamicConfig.getSkipMockIdentities2();
        Identity identity = assembleIdentity(event);
        if (skipMockIdentities.contains(identity.getUri())) {
            return true;
        }

        return false;
    }
}
