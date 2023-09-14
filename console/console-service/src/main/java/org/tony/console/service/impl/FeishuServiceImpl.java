package org.tony.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.tony.console.service.FeishuService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/3/2 21:02
 */
@Slf4j
@Component
public class FeishuServiceImpl implements FeishuService {

    private final static String baseUrl = "https://open.feishu.cn/open-apis/bot/v2/hook/";

    @Override
    public void send(String webhook, String template, Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        String content = parser.parseExpression(template,parserContext).getValue(params, String.class);


        ApacheHttpClient apacheHttpClient = ApacheHttpClient.getInstance();
        String url = baseUrl + webhook;

        HashMap<String,String> headerMap = new HashMap<>();
        headerMap.put("content-type", "application/json");
        try {
            CloseableHttpResponse response = apacheHttpClient.post(url, content, headerMap);
            String respContent = apacheHttpClient.getResponseString(response);
            int code = response.getStatusLine().getStatusCode();

            log.info("success send to feishu, url={} code={} content={}", url, code, respContent);

        } catch (IOException e) {
            log.error("system error", e);
        }
    }

    public static void main(String args[]) {
        String smsTemplate = "验证码:#{[code]},您正在登录管理后台，5分钟内输入有效。";
        Map<String, Object> params = new HashMap<>();
        params.put("code", 12345);;

        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        String content = parser.parseExpression(smsTemplate,parserContext).getValue(params, String.class);

    }
}
