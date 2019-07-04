package com.alibaba.repeater.console.dal.mapper;


import com.alibaba.repeater.console.dal.model.Record;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@Mapper
public interface RecordMapper {

    /**
     * 根据应用名和traceId找到录制记录
     *
     * @param appName 应用名
     * @param traceId traceId
     */
    @Select("select * from record where app_name = #{appName} and trace_id = #{traceId}")
    @Results({
            @Result(property = "appName", column = "app_name"),
            @Result(property = "traceId", column = "trace_id"),
            @Result(property = "wrapperRecord", column = "wrapper_record"),
            @Result(property = "gmtRecord", column = "gmt_record"),
            @Result(property = "gmtCreate", column = "gmt_create")
    })
    Record selectByAppNameAndTraceId(@Param("appName") String appName,
                                     @Param("traceId") String traceId);

    /**
     * 插入录制结果
     *
     * @param record 录制结果
     */
    @Insert("insert into record(gmt_create,gmt_record,app_name,environment,host,trace_id,wrapper_record) " +
            "VALUES (#{gmtCreate},#{gmtRecord},#{appName},#{environment},#{host},#{traceId},#{wrapperRecord})")
    void insert(Record record);

    /**
     * 删除记录
     *
     * @param id
     */
    @Delete("delete from record where id = #{id}")
    void deleteById(@Param("id") Long id);


    /**
     * 根据应用名和traceId删除记录
     *
     * @param appName 应用名
     * @param traceId traceId
     */
    @Delete("delete from record where app_name = #{appName} and trace_id = #{traceId}")
    void deleteByAppNameAndTraceId(@Param("appName") String appName,
                                   @Param("traceId") String traceId);
}
