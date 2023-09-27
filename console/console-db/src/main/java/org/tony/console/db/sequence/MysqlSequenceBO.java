package org.tony.console.db.sequence;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/15 16:27
 */
@Data
public class MysqlSequenceBO {

    /**
     * seq名
     */
    private String seqName;
    /**
     * 当前值
     */
    private Long seqValue;
    /**
     * 最小值
     */
    private Long minValue;
    /**
     * 最大值
     */
    private Long maxValue;
    /**
     * 每次取值的数量
     */
    private Long step;
    /**  */
    private Date tmCreate;
    /**  */
    private Date tmSmp;

    public boolean validate(){
        //一些简单的校验。如当前值必须在最大最小值之间。step值不能大于max与min的差
        if ((seqName==null) || minValue < 0 || maxValue <= 0 || step <= 0 || minValue >= maxValue || maxValue - minValue <= step ||seqValue < minValue || seqValue > maxValue ) {
            return false;
        }
        return true;
    }
}
