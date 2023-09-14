package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Param;
import org.tony.console.db.sequence.MysqlSequenceBO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 16:13
 */
public interface MysqlSequenceMapper {

    public int createSequence(MysqlSequenceBO bo);

    public int updSequence(@Param("seqName") String seqName, @Param("oldValue") long oldValue , @Param("newValue") long newValue);

    public int delSequence(@Param("seqName") String seqName);

    public MysqlSequenceBO getSequence(@Param("seqName") String seqName);

    public List<MysqlSequenceBO> getAll();
}
