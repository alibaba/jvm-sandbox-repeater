package org.tony.console.biz.model.convert;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.Constant;
import org.tony.console.biz.model.TestCaseExecResultVO;
import org.tony.console.service.model.TaskItemDTO;
import org.tony.console.service.model.TestCaseDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author peng.hu1
 * @Date 2023/1/6 09:52
 */
@Component
public class TestCaseExecResultVOConvert {

    public List<TestCaseExecResultVO> convert(List<TaskItemDTO> taskItemDTOList, List<TestCaseDTO> testCaseDTOList) {
        if (CollectionUtils.isEmpty(taskItemDTOList)) {
            return new ArrayList<>(0);
        }

        List<TestCaseExecResultVO> list = new LinkedList<>();

        for (TaskItemDTO taskItemDTO : taskItemDTOList) {
            TestCaseExecResultVO vo = new TestCaseExecResultVO();
            vo.setCaseId(taskItemDTO.getName());
            vo.setId(taskItemDTO.getId());
            vo.setStatus(taskItemDTO.getStatus().name());
            vo.setDuration("-");
            vo.setTaskId(taskItemDTO.getTaskId());

            if (taskItemDTO.getExtend()!=null) {
                vo.setRepeatId(taskItemDTO.getExtend().getString(Constant.REPEAT_ID));
                vo.setDuration(taskItemDTO.getExtend().getString(Constant.REPEAT_COST));
                vo.setIp(taskItemDTO.getExtend().getString(Constant.REPEAT_IP));
            }

            Optional<TestCaseDTO> testCaseDTOOptional = testCaseDTOList.stream().filter(item->item.getCaseId().equals(vo.getCaseId())).findFirst();
            if (testCaseDTOOptional.isPresent()) {
                TestCaseDTO testCaseDTO = testCaseDTOOptional.get();
                vo.setCaseName(testCaseDTO.getCaseName());
                vo.setEntrance(testCaseDTO.getEntranceDesc());
                vo.setInvokeType(testCaseDTO.getRecordType().name());
                vo.setAppName(testCaseDTO.getAppName());
            }

            list.add(vo);
        }

        return list;
    }
}
