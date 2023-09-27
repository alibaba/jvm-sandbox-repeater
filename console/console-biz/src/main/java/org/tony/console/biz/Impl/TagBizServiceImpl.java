package org.tony.console.biz.Impl;

import com.alibaba.fastjson2.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.RecordBizService;
import org.tony.console.biz.TagBizService;
import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.db.model.Record;
import org.tony.console.common.domain.Tag;
import org.tony.console.service.TagConfigService;
import org.tony.console.service.convert.RecordDetailConverter;
import org.tony.console.service.model.TagConfigDTO;
import org.tony.console.service.model.enums.TagConfigScope;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签计算服务
 * @author peng.hu1
 * @Date 2023/3/17 13:51
 */
@Slf4j
@Component
public class TagBizServiceImpl implements TagBizService {

    @Resource
    RecordBizService recordBizService;

    @Resource
    TagConfigService tagConfigService;

    @Resource
    RecordDetailConverter recordDetailConverter;

    @Override
    public List<Tag> compute(String appName, String traceId) {
        RecordDetailBO recordDetailBO = recordBizService.get(appName, traceId);
        if (recordDetailBO == null) {
            return new ArrayList<>(0);
        }

        return compute(recordDetailBO);
    }

    @Override
    public List<Tag> compute(Record record) {
        if (record == null) {
            return new ArrayList<>(0);
        }
        RecordDetailBO recordDetailBO = recordDetailConverter.convert(record);
        return compute(recordDetailBO);
    }



    private List<Tag> compute(RecordDetailBO recordDetailBO) {
        List<TagConfigDTO> tagConfigDTOS = tagConfigService.queryByAppName(recordDetailBO.getAppName());
        if (CollectionUtils.isEmpty(tagConfigDTOS)) {
            return new ArrayList<>(0);
        }

        List<TagConfigDTO> allScopeTagList = tagConfigDTOS.stream().filter(item->item.getScope().equals(TagConfigScope.ALL)).collect(Collectors.toList());

        List<Tag> res = new ArrayList<>();

        //先计算全局的
        for (TagConfigDTO tagConfigDTO : allScopeTagList) {
            execute(tagConfigDTO, recordDetailBO, res);
        }

        //再计算私域的
        for (TagConfigDTO tagConfigDTO : tagConfigDTOS) {
            if (tagConfigDTO.getScope().equals(TagConfigScope.ALL)) {
                continue;
            }

            if (!tagConfigDTO.getIdentity().equalsIgnoreCase(recordDetailBO.getEntranceDesc())) {
                continue;
            }

            execute(tagConfigDTO, recordDetailBO, res);
        }

        return res;
    }

    private void execute(TagConfigDTO tagConfigDTO, Object object, List<Tag> res) {

        Optional findOne = res.stream().filter(item->item.getId().equals(tagConfigDTO.getName())).findFirst();
        //有同名id的，不重复计算
        if (findOne.isPresent()) {
            return;
        }

        if (object==null) {
            return;
        }
        Object o = JSONPath.eval(object, tagConfigDTO.getJsonpath());
        if (o==null) {
            return;
        }

        res.add(new Tag(tagConfigDTO.getName(), tagConfigDTO.getNickName(), String.valueOf(o)));
    }
}
