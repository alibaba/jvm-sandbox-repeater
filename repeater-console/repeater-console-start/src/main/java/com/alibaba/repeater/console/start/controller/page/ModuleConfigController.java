package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.params.ModuleConfigParams;
import com.alibaba.repeater.console.service.ModuleConfigService;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import com.alibaba.repeater.console.start.controller.vo.PagerAdapter;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * {@link ModuleConfigController}
 * <p>
 * 配置管理页面
 *
 * @author zhaoyb1990
 */
@RequestMapping("/config")
@Controller
public class ModuleConfigController {

    @Resource
    private ModuleConfigService moduleConfigService;

    @RequestMapping("list.htm")
    public String list(@ModelAttribute("requestParams") ModuleConfigParams params, Model model) {
        PageResult<ModuleConfigBO> result = moduleConfigService.list(params);
        PagerAdapter.transform0(result, model);
        return "config/list";
    }

    @RequestMapping("detail.htm")
    public String detail(@ModelAttribute("requestParams") ModuleConfigParams params, Model model) {
        RepeaterResult<ModuleConfigBO> result = moduleConfigService.query(params);
        if (!result.isSuccess()) {
            return "/error/404";
        }
        model.addAttribute("config", result.getData().getConfig());
        return "config/detail";
    }

    @RequestMapping("edit.htm")
    public String edit(@ModelAttribute("requestParams") ModuleConfigParams params, Model model) {
        RepeaterResult<ModuleConfigBO> result = moduleConfigService.query(params);
        if (!result.isSuccess()) {
            return "/error/404";
        }
        model.addAttribute("config", result.getData().getConfig());
        return "config/edit";
    }

    @RequestMapping("add.htm")
    public String add(Model model) {
        RepeaterConfig defaultConf = new RepeaterConfig();
        List<Behavior> behaviors = Lists.newArrayList();
        defaultConf.setPluginIdentities(Lists.newArrayList( "http", "java-entrance", "java-subInvoke"));
        defaultConf.setRepeatIdentities(Lists.newArrayList("java", "http"));
        defaultConf.setUseTtl(true);
        defaultConf.setHttpEntrancePatterns(Lists.newArrayList("^/regress/.*$"));
        behaviors.add(new Behavior("com.alibaba.repeater.console.service.impl.RegressServiceImpl", "getRegress"));
        defaultConf.setJavaEntranceBehaviors(behaviors);
        List<Behavior> subBehaviors = Lists.newArrayList();
        subBehaviors.add(new Behavior("com.alibaba.repeater.console.service.impl.RegressServiceImpl", "getRegressInner", "findPartner","slogan"));
        defaultConf.setJavaSubInvokeBehaviors(subBehaviors);
        try {
            model.addAttribute("config", JacksonUtil.serialize(defaultConf));
        } catch (SerializeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/error/404";
        }
        return "config/add";
    }

    @RequestMapping("saveOrUpdate.json")
    @ResponseBody
    public RepeaterResult<ModuleConfigBO> doAdd(@ModelAttribute("requestParams") ModuleConfigParams params) {
        return moduleConfigService.saveOrUpdate(params);
    }

    @RequestMapping("push.json")
    @ResponseBody
    public RepeaterResult<ModuleConfigBO> push(@ModelAttribute("requestParams") ModuleConfigParams params) {
        return moduleConfigService.push(params);
    }

}
