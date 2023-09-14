package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.Replay;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReplayMapper {

    int insertReplay(Replay object);

    int updateReplay(Replay object);

    List<Replay> queryReplay(Replay object);

    Replay queryReplayLimit1(Map<String, Object> params);

    List<Replay> queryReplayWithSize(int size);

    int delete(Replay replay);
}
