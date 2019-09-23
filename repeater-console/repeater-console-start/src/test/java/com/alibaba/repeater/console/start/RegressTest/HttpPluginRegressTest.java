package com.alibaba.repeater.console.start.RegressTest;

import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.repeater.console.start.DataProvider.HttpPluginTestDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link HttpPluginRegressTest} 集成测试；依赖于通过bootstrap.sh启动console启动
 * <p>
 *
 * @author zhaoyb1990
 */
public class HttpPluginRegressTest {

    @Test(description = "Used to test httpPlugin' record and repeat ",
            dataProvider = "HttpPluginTestDataProvider",
            dataProviderClass = HttpPluginTestDataProvider.class
    )
    public void httpPluginRecordRepeateTest(String url){
        try {
            String traceId = TraceGenerator.generate();
            // record
            HttpUtil.Resp record = executeRecord(url, traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Thread.sleep(100);
            // repeat
            HttpUtil.Resp repeat = executeRepeat(url, traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Assert.assertEquals(record.getBody(), repeat.getBody());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private HttpUtil.Resp executeRecord(String uri, String traceId, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>(2);
        }
        params.put("Repeat-TraceId", traceId);
        return HttpUtil.doGet("http://127.0.0.1:8001/regress" + uri, params);
    }

    private HttpUtil.Resp executeRepeat(String uri, String traceId, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>(2);
        }
        params.put("Repeat-TraceId-X", traceId);
        return HttpUtil.doGet("http://127.0.0.1:8001/regress" + uri, params);
    }
}
