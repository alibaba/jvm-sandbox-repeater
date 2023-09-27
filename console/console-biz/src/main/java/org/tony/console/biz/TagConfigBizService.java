package org.tony.console.biz;

import org.tony.console.biz.request.AddTagBizRequest;
import org.tony.console.biz.request.tag.DeleteTagRequest;
import org.tony.console.biz.request.tag.UpdateTagRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.model.TagConfigDTO;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/17 11:19
 */
public interface TagConfigBizService {

    public void addTag(AddTagBizRequest request) throws BizException;

    public List<TagConfigDTO> queryTagList(String appName);

    public void updateTag(UpdateTagRequest request) throws BizException;

    public void removeTag(DeleteTagRequest request) throws BizException;
}
