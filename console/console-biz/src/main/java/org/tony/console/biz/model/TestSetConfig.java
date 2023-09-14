package org.tony.console.biz.model;

import lombok.Data;
import org.tony.console.service.model.TestSuitDTO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/2 15:38
 */
@Data
public class TestSetConfig {

    private Boolean open;

    private List<TestSuitDTO> testSuitDTOList;

    private List<String> feishuWebHooks;

    /**
     * 失败重试次数
     */
    private Integer failRetryTime;
}
