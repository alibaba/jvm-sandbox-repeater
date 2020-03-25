package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.Regress;

import java.util.List;

/**
 * {@link RegressService} 回归示例服务
 * <p>
 *
 * @author zhaoyb1990
 */
public interface RegressService {

    /**
     * 获取regress对象
     *
     * @param name 名字
     * @return regress对象
     */
    RepeaterResult<Regress> getRegress(String name);

    /**
     * 获取多个 regress对象
     *
     * @param name  名字
     * @param count 个数
     * @return regress对象集合
     */
    RepeaterResult<List<Regress>> getRegress(String name, int count);


    /**
     * 找到你的小伙伴
     *
     * @param name 名字
     * @return
     */
    RepeaterResult<String> findPartner(String name);

    /**
     * 喊口号
     * @return
     */
    String slogan();

    /**
     * 从缓存中获取regress对象
     * @param name 名字
     * @return regress对象
     */
    RepeaterResult<Regress> getRegressWithCache(String name);
}
