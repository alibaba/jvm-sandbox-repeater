package org.tony.console.db.sequence;

/**
 * @author peng.hu1
 * @Date 2022/12/15 16:27
 */
public interface MysqlSequence {

    public long nextVal(String seqName) throws SequenceException;
}
