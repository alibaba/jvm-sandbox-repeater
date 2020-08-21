package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.params.ModuleInfoParams;
import com.alibaba.repeater.console.service.impl.ModuleInfoServiceImpl;
import org.aspectj.weaver.patterns.HasMemberTypePattern;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ModuleInfoController}
 * <p>
 * 在线模块页面
 *
 * @author zhaoyb1990
 */
@RequestMapping("/module")
@RestController
public class ModuleInfoController {

    @Resource
    private ModuleInfoServiceImpl moduleInfoService;

    @RequestMapping("/list")
    public List<ModuleInfoBO> list(Long configId) {
        List<ModuleInfoBO> result = moduleInfoService.query(configId);
        return result;
    }

    @RequestMapping("/update")
    public Object update(Long id, String ip, String port, String username, String password, String privateRsaFile, String preCommand, Long moduleConfigId) {
        moduleInfoService.update(id, ip, port, username, password, privateRsaFile, preCommand, moduleConfigId);
        return RepeaterResult.builder().success(true).build();
    }

    @RequestMapping("/delete")
    public Object delete(Long id) {
        moduleInfoService.delete(id);
        return RepeaterResult.builder().success(true).build();
    }

    @RequestMapping("/getStatus")
    public Object getStatus(Long id) {
        return moduleInfoService.refreshStatus(id).getStatus();
    }

    @RequestMapping("/install")
    public Object install(Long id) {
        boolean success = moduleInfoService.install(id);
        Map result = new HashMap<>();
        result.put("success", success);
        result.put("status", moduleInfoService.refreshStatus(id).getStatus());
        return result;
    }

    @RequestMapping("/attach")
    public Object attach(Long id) {
        boolean success = moduleInfoService.attach(id);
        Map result = new HashMap<>();
        result.put("success", success);
        result.put("status", moduleInfoService.refreshStatus(id).getStatus());
        return result;
    }

    @RequestMapping("/detach")
    public Object detach(Long id) {
        boolean success = moduleInfoService.detach(id);
        Map result = new HashMap<>();
        result.put("success", success);
        result.put("status", moduleInfoService.refreshStatus(id).getStatus());
        return result;
    }

//    @ResponseBody
//    @RequestMapping("/byName.json")
//    public RepeaterResult<List<ModuleInfoBO>> list(@RequestParam("appName") String appName) {
//        return moduleInfoService.query(appName);
//    }

    @ResponseBody
    @RequestMapping("/report.json")
    public RepeaterResult<ModuleInfoBO> list(@ModelAttribute("requestParams") ModuleInfoBO params) {
        return moduleInfoService.report(params);
    }

    @ResponseBody
    @RequestMapping("/active.json")
    public RepeaterResult<ModuleInfoBO> active(@ModelAttribute("requestParams") ModuleInfoParams params) {
        return moduleInfoService.active(params);
    }

    @ResponseBody
    @RequestMapping("/frozen.json")
    public RepeaterResult<ModuleInfoBO> frozen(@ModelAttribute("requestParams") ModuleInfoParams params) {
        return moduleInfoService.frozen(params);
    }

    @ResponseBody
    @RequestMapping("/install.json")
    public RepeaterResult<String> install(@ModelAttribute("requestParams") ModuleInfoParams params) {
        return moduleInfoService.install(params);
    }

    @ResponseBody
    @RequestMapping("/reload.json")
    public RepeaterResult<String> reload(@ModelAttribute("requestParams") ModuleInfoParams params) {
        return moduleInfoService.reload(params);
    }
}
