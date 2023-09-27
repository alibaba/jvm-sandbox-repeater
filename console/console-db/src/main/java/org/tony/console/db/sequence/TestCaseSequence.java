package org.tony.console.db.sequence;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 17:09
 */
@Component
public class TestCaseSequence {

    @Resource
    MysqlSequence mysqlSequence;

    public List<Long> nextVal(int num) throws SequenceException {
        List<Long> valList = new ArrayList<>(num);
        for (int i=0; i<num; i++) {
            valList.add(mysqlSequence.nextVal("TEST_CASE"));
        }

        return valList;
    }
}
