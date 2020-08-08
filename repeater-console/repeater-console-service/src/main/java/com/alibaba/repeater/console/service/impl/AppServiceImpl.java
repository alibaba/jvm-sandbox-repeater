package com.alibaba.repeater.console.service.impl;

import com.alibaba.repeater.console.common.domain.AppBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.dal.model.App;
import com.alibaba.repeater.console.dal.repository.AppRepository;
import com.alibaba.repeater.console.service.convert.AppConverter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link }
 * <p>
 *
 * @author Flag
 */
@Service
public class AppServiceImpl {
    @Resource
    private AppConverter appConverter;
    @Resource
    private AppRepository appRepository;

    public PageResult<AppBO> list(String keyword, int page, int size) {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.DESC, "id"));
        Page<App> pageData = appRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = Lists.newArrayList();
                    if (StringUtils.isNotBlank(keyword)) {
                        predicates.add(cb.like(root.<String>get("name"), keyword));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );

        PageResult<AppBO> result = new PageResult<>();
        if (pageData.hasContent()) {
            result.setSuccess(true);
            result.setPageIndex(page);
            result.setCount(pageData.getTotalElements());
            result.setTotalPage(pageData.getTotalPages());
            result.setPageSize(size);
            result.setData(pageData.getContent().stream().map(appConverter::convert).collect(Collectors.toList()));
        }
        return result;
    }
}
