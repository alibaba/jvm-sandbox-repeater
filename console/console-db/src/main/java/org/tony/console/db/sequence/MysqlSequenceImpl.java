package org.tony.console.db.sequence;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2022/12/15 16:34
 */
@Component
public class MysqlSequenceImpl implements MysqlSequence {

    @Resource
    private MysqlSequenceFactory mysqlSequenceFactory;

    @Override
    public long nextVal(String seqName) throws SequenceException {
        return mysqlSequenceFactory.getNextVal(seqName);
    }

}
