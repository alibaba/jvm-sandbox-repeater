package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.StrategyProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockInterceptor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MetaInfServices(MockInterceptor.class)
public class MybatisMockInterceptor implements MockInterceptor {

    private final static String INSERT_TYPE = "INSERT";

    @Override
    public void beforeSelect(final MockRequest request) {
        RepeatMeta meta = request.getMeta();
        JSONObject mapperConfig = (JSONObject) request.getArgumentArray()[1];
        SelectResult selectResult = StrategyProvider.instance().provide(meta.getStrategyType()).select(request,false);
        Invocation selectInvocation = selectResult.getInvocation();
        if (selectInvocation == null) {
            LogUtil.error("MybatisMockInterceptor set id to insertObject beforeSelect fail:  can not find any invocation from record");
            return;
        }
        // org.apache.ibatis.binding.MapperMethod.execute(SqlSession sqlSession, Object[] args)
        // 这里只兼容insert单个对象的操作，所以取的是入参的第一个值
        Object recordArgument = ((Object[]) selectInvocation.getRequest()[0])[0];
        Object mockArgument = ((Object[]) request.getArgumentArray()[0])[0];

        if(!recordArgument.getClass().equals(mockArgument.getClass())){
            LogUtil.error("MybatisMockInterceptor set id to insertObject beforeSelect fail:  type of record first arguments is different than type of mock first argument");
            return;
        }

        // 非集合直接设置id
        if(!(mockArgument instanceof Collection)){
            setKeyPropertiesValueToMockRequest(recordArgument, mockArgument, mapperConfig);
            return;
        }


        List recordArgumentList = new ArrayList((Collection) recordArgument);
        List mockArgumentList = new ArrayList((Collection) mockArgument);

        // 如果集合长度不同，直接不处理设值以免出现数组越界
        if(recordArgumentList.size() != mockArgumentList.size()){
            LogUtil.error("MybatisMockInterceptor set id to insertObject beforeSelect fail: size of record arguments list is different than size of  type of mock first argument list");
            return;
        }

        for(int i=0; i<mockArgumentList.size();i++){
            setKeyPropertiesValueToMockRequest(recordArgumentList.get(i), mockArgumentList.get(i), mapperConfig);
        }

    }

    @Override
    public void beforeReturn(final MockRequest request, final MockResponse response) {
        // do nothing
    }

    @Override
    public boolean matchingSelect(final MockRequest request) {
        // 仅在调用类型为mybatis 且 操作类型是插入 且 插入方法的入参 有且只有一个 且 keyProperties只有一个 的情况下，才进一步判断isUserGenerateKey，否则一律不作处理
        if (!request.getType().equals(InvokeType.MYBATIS)) {
            return false;
        }

        if (!INSERT_TYPE.equals(request.getIdentity().getLocation())) {
            return false;
        }

        // org.apache.ibatis.binding.MapperMethod.execute(SqlSession sqlSession, Object[] args)
        Object[] insertParam = (Object[]) request.getArgumentArray()[0];
        if (insertParam.length != 1) {
            return false;
        }

        JSONObject mapperConfig = (JSONObject) request.getArgumentArray()[1];

        // 当keyProperties的长度大于1时，不做处理；不兼容批量插入的场景
        JSONArray keyProperties = mapperConfig.getJSONArray("keyProperties");
        if (keyProperties.size() != 1) {
            return false;
        }

        Boolean isUseGeneratedKeys = mapperConfig.getBoolean("isUseGeneratedKeys");
        return isUseGeneratedKeys.booleanValue();
    }

    @Override
    public boolean matchingReturn(final MockRequest request, final MockResponse response) {
        return false;
    }


    /**
     * 将keyProperties设定的值，从录制记录中获取，并设置到mock的request中
     *
     * @param recordArgument
     * @param mockArgument
     * @param mapperConfig
     */
    private void setKeyPropertiesValueToMockRequest(Object recordArgument, Object mockArgument, JSONObject mapperConfig) {

        String[] keyProperties = (String[]) mapperConfig.get("keyProperties");
        // 只取第一个keyProperties，将id设定到对应的字段
        Field field = FieldUtils.getDeclaredField(recordArgument.getClass(), keyProperties[0], true);
        try {
            field.set(mockArgument, field.get(recordArgument));
        } catch (IllegalAccessException e) {
            LogUtil.error("MybatisMockInterceptor set id to insertObject beforeSelect fail", e);
        } catch (NullPointerException e) {
            LogUtil.error("MybatisMockInterceptor set id to insertObject beforeSelect fail：field [{}] not declared in record", keyProperties[0], e);
        }

    }

}
