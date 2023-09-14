package org.tony.console.biz.Impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tony.console.biz.AppBizService;
import org.tony.console.biz.TagConfigBizService;
import org.tony.console.biz.request.AddTagBizRequest;
import org.tony.console.biz.request.tag.DeleteTagRequest;
import org.tony.console.biz.request.tag.UpdateTagRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.AppAuthService;
import org.tony.console.service.TagConfigService;
import org.tony.console.service.model.TagConfigDTO;
import org.tony.console.service.model.enums.TagConfigScope;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/17 11:54
 */
@Component
public class TagConfigBizServiceImpl implements TagConfigBizService {

    @Resource
    TagConfigService tagConfigService;

    @Resource
    AppBizService appBizService;

    @Resource
    AppAuthService appAuthService;

    @Override
    public void addTag(AddTagBizRequest request) throws BizException {
        request.check();

        //鉴权
        appBizService.checkAuth(request.getAppName(), request.getUser());

        //添加
        tagConfigService.addTag(build(request));
    }

    @Override
    public List<TagConfigDTO> queryTagList(String appName) {
        return tagConfigService.queryByAppName(appName);
    }

    @Override
    public void updateTag(UpdateTagRequest request) throws BizException {
        request.check();

        TagConfigDTO tagConfigDTO = tagConfigService.queryById(request.getId());
        if (tagConfigDTO ==null) {
            throw BizException.build("配置不存在");
        }

        appAuthService.checkAuth(tagConfigDTO.getAppName(), request.getOperator());

        TagConfigScope scope = tagConfigDTO.getScope();
        if (TagConfigScope.ONLY.equals(scope) && StringUtils.isEmpty(request.getIdentity())) {
            throw BizException.build("identity必填");
        }

        tagConfigDTO.setNickName(request.getNickName().trim());
        tagConfigDTO.setJsonpath(request.getJsonpath().trim());
        tagConfigDTO.setName(request.getName().trim());

        if ( StringUtils.isNotEmpty(request.getIdentity())) {
            tagConfigDTO.setIdentity(request.getIdentity().trim());
        }

        tagConfigService.update(tagConfigDTO);
    }

    @Override
    public void removeTag(DeleteTagRequest request) throws BizException {
        request.check();
        TagConfigDTO tagConfigDTO = tagConfigService.queryById(request.getId());
        if (tagConfigDTO ==null) {
            throw BizException.build("配置不存在");
        }

        appAuthService.checkAuth(tagConfigDTO.getAppName(), request.getOperator());

        tagConfigService.remove(request.getId());
    }

    private TagConfigDTO build(AddTagBizRequest request) {
        TagConfigDTO tagConfigDTO = new TagConfigDTO();

        tagConfigDTO.setName(request.getName());
        tagConfigDTO.setJsonpath(request.getJsonPath());
        tagConfigDTO.setScope(TagConfigScope.getByCode(request.getScope()));
        tagConfigDTO.setAppName(request.getAppName());
        tagConfigDTO.setIdentity(request.getIdentity());
        tagConfigDTO.setNickName(request.getNickName());

        return tagConfigDTO;
    }
}
