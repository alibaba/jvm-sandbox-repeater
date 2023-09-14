package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.TrxMsgDO;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrxMessageMapper {

    public void insert(TrxMsgDO msg);

    public List<TrxMsgDO> query(Map<String, Object> params);

    public int update(TrxMsgDO msg);

    public int deleteById(Long id);
}
