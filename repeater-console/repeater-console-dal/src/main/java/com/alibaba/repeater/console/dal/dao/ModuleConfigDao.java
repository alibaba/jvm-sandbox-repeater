package com.alibaba.repeater.console.dal.dao;

import com.alibaba.repeater.console.common.params.ModuleConfigParams;
import com.alibaba.repeater.console.dal.model.ModuleConfig;
import com.alibaba.repeater.console.dal.repository.ModuleConfigRepository;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("moduleConfigDao")
public class ModuleConfigDao {

    @Resource
    private ModuleConfigRepository moduleConfigRepository;

    public Page<ModuleConfig> selectByParams(@NotNull final ModuleConfigParams params) {
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getSize(), new Sort(Sort.Direction.DESC, "id"));
        return moduleConfigRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = Lists.newArrayList();
                    if (params.getAppName() != null && !params.getAppName().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("appName"), params.getAppName()));
                    }
                    if (params.getEnvironment() != null && !params.getEnvironment().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("environment"), params.getEnvironment()));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );
    }

    public ModuleConfig query(ModuleConfigParams params) {
        return moduleConfigRepository.findByAppNameAndEnvironment(params.getAppName(), params.getEnvironment());
    }

    public ModuleConfig saveOrUpdate(ModuleConfig moduleConfig) {
        return moduleConfigRepository.saveAndFlush(moduleConfig);
    }
}
