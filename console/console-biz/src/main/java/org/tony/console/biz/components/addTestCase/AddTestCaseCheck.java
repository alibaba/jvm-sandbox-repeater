package org.tony.console.biz.components.addTestCase;

import org.springframework.stereotype.Component;
import org.tony.console.biz.AppBizService;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.AddTestCaseRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.TestSuitService;
import org.tony.console.service.model.TestSuitDTO;
import org.tony.console.service.model.enums.TestSuitType;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2022/12/16 15:27
 */
@Order(1)
@Component
public class AddTestCaseCheck implements AddTestCaseBizComponent {

    @Resource
    TestSuitService testSuitService;

    @Resource
    AppBizService appBizService;

    @Override
    public void execute(AddTestCaseRequest request) throws BizException {

        TestSuitDTO testSuitDTO = testSuitService.queryById(request.getSuitId());
        if (testSuitDTO == null) {
            throw BizException.build("测试套件不存在");
        }

        if (!TestSuitType.Task.equals(testSuitDTO.getType())) {
            throw BizException.build("不允许在非任务套件上添加用例");
        }

        request.setAppName(testSuitDTO.getAppName());

        //加一个权限管理
        appBizService.checkAuth(testSuitDTO.getAppName(), request.getUser());

    }

    @Override
    public boolean isSupport(AddTestCaseRequest request) {
        return true;
    }



}
