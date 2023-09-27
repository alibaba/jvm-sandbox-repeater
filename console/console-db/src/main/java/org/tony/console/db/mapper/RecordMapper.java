package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tony.console.db.model.Record;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper {

    int insertRecord(Record object);

    int updateRecord(Record object);

    List<Record> queryRecord(Map<String, Object> params);

    long count(Map<String, Object> params);

    Record queryRecordLimit1(Record object);

    Record queryById(long id);

    int batchUpdateRecord(List<Record> records);

    int batchRemove(List<String> traceIdList);

    int removeById(Long id);

    @Delete("delete from record where app_name = #{app} limit 10000")
    int removeBatch(@Param("app") String appName);
}
