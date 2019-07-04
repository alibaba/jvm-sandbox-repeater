package com.alibaba.repeater.console.start;

import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RegressTest} 集成测试；依赖于通过bootstrap.sh启动console启动
 * <p>
 *
 * @author zhaoyb1990
 */
public class RegressTest {

    @Test
    public void testSlogan() {
        try {
            String traceId = TraceGenerator.generate();
            // record
            HttpUtil.Resp record = executeRecord("/slogan", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Thread.sleep(100);
            // repeat
            HttpUtil.Resp repeat = executeRepeat("/slogan", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Assert.assertEquals(record.getBody(), repeat.getBody());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartner() {
        try {
            String traceId = TraceGenerator.generate();
            // record
            HttpUtil.Resp record = executeRecord("/partner/李雷", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Thread.sleep(100);
            // repeat
            HttpUtil.Resp repeat = executeRepeat("/partner/李雷", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Assert.assertEquals(record.getBody(), repeat.getBody());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetSingle() {
        try {
            String traceId = TraceGenerator.generate();
            // record
            HttpUtil.Resp record = executeRecord("/get/single", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Thread.sleep(100);
            // repeat
            HttpUtil.Resp repeat = executeRepeat("/get/single", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Assert.assertEquals(record.getBody(), repeat.getBody());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetMultiple() {
        try {
            String traceId = TraceGenerator.generate();
            // record
            HttpUtil.Resp record = executeRecord("/get/multiple/5", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Thread.sleep(100);
            // repeat
            HttpUtil.Resp repeat = executeRepeat("/get/multiple/5", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Assert.assertEquals(record.getBody(), repeat.getBody());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAsync() {
        try {
            String traceId = TraceGenerator.generate();
            // record
            HttpUtil.Resp record = executeRecord("/getAsync/async", traceId, null);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isSuccess());
            Thread.sleep(100);
            // repeat
            HttpUtil.Resp repeat = executeRepeat("/getAsync/async", traceId, null);
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
