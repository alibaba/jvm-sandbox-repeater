package org.tony.console.db.sequence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.MysqlSequenceMapper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author peng.hu1
 * @Date 2022/12/15 16:37
 */
@Slf4j
public class MysqlSequenceHolder {

    private final Lock lock                = new ReentrantLock();

    /** seqName */
    private String seqName;

    /** sequenceDao */
    private MysqlSequenceMapper sequenceDAO;

    private MysqlSequenceBO sequenceBo;
    /**  */
    private SequenceRange sequenceRange;
    /** 是否初始化 */
    private volatile boolean       isInitialize      = false;
    /** sequence初始化重试次数 */
    private int initRetryNum;
    /** sequence获取重试次数 */
    private int getRetryNum;

    /**
     * <p> 构造方法 </p>
     * @Title MysqlSequenceHolder
     * @param sequenceDAO
     * @param sequenceBo
     * @param initRetryNum 初始化时，数据库更新失败后重试次数
     * @param getRetryNum 获取nextVal时，数据库更新失败后重试次数
     * @return
     * @author coderzl
     */
    public MysqlSequenceHolder(MysqlSequenceMapper sequenceDAO, MysqlSequenceBO sequenceBo,int initRetryNum,int getRetryNum) {
        this.sequenceDAO = sequenceDAO;
        this.sequenceBo = sequenceBo;
        this.initRetryNum = initRetryNum;
        this.getRetryNum = getRetryNum;
        if(sequenceBo != null)
            this.seqName = sequenceBo.getSeqName();
    }

    /**
     * <p> 初始化 </p>
     * @Title init
     * @param
     * @return void
     * @author coderzl
     */
    public void init(){
        if (isInitialize == true) {
            return;
        }
        // 初始化该sequence
        try {
            initSequenceRecord(sequenceBo);
        } catch (SequenceException e) {
            log.error("sequence error", e);
        }
        isInitialize = true;
    }

    /**
     * <p> 获取下一个序列号 </p>
     * @Title getNextVal
     * @param
     * @return long
     * @author coderzl
     */
    public long getNextVal() throws SequenceException {
        long curValue = sequenceRange.getAndIncrement();

        if(curValue == -1){
            try{
                lock.lock();
                curValue = sequenceRange.getAndIncrement();
                if(curValue != -1){
                    return curValue;
                }
                sequenceRange = retryRange();
                curValue = sequenceRange.getAndIncrement();
            }finally {
                lock.unlock();
            }
        }
        return curValue;
    }

    /**
     * <p> 初始化当前这条记录 </p>
     * @Title initSequenceRecord
     * @Description
     * @param sequenceBo
     * @return void
     * @author coderzl
     */
    private void initSequenceRecord(MysqlSequenceBO sequenceBo) throws SequenceException {
        //在限定次数内，乐观锁更新数据库记录
        for(int i = 1; i < initRetryNum; i++){
            //查询bo
            MysqlSequenceBO curBo = sequenceDAO.getSequence(sequenceBo.getSeqName());
            if(curBo == null){
                throw new SequenceException("[" + seqName + "] the current sequenceBo is null");
            }
            if (!curBo.validate()){
                throw new SequenceException("[" + seqName + "] the current sequenceBo validate fail");
            }
            //改变当前值
            long newValue = curBo.getSeqValue()+curBo.getStep();
            //检查当前值
            if(!checkCurrentValue(newValue,curBo)){
                newValue = resetCurrentValue(curBo);
            }
            int result = sequenceDAO.updSequence(sequenceBo.getSeqName(),curBo.getSeqValue(),newValue);
            if(result > 0){
                sequenceRange = new SequenceRange(curBo.getSeqValue(),newValue - 1);
                curBo.setSeqValue(newValue);
                this.sequenceBo = curBo;
                return;
            }else{
                continue;
            }
        }
        //限定次数内，更新失败，抛出异常
        throw new SequenceException("[" + seqName + "]  sequenceBo update error");
    }

    /**
     * <p> 检查新值是否合法 新的当前值是否在最大最小值之间</p>
     * @param curValue
     * @param curBo
     * @return boolean
     * @author coderzl
     */
    private boolean checkCurrentValue(long curValue,MysqlSequenceBO curBo){
        if(curValue > curBo.getMinValue() && curValue <= curBo.getMaxValue()){
            return true;
        }
        return false;
    }

    /**
     * <p> 重置sequence当前值 ：当前sequence达到最大值时，重新从最小值开始 </p>
     * @Title resetCurrentValue
     * @param curBo
     * @return long
     * @author coderzl
     */
    private long resetCurrentValue(MysqlSequenceBO curBo){
        return curBo.getMinValue();
    }

    /**
     * <p> 缓存区间使用完毕时，重新读取数据库记录，缓存新序列段 </p>
     * @Title retryRange
     * @author coderzl
     */
    private SequenceRange retryRange() throws SequenceException {
        for(int i = 1; i < getRetryNum; i++){
            //查询bo
            MysqlSequenceBO curBo = sequenceDAO.getSequence(sequenceBo.getSeqName());

            //改变当前值
            long newValue = curBo.getSeqValue()+curBo.getStep();
            //检查当前值
            if(!checkCurrentValue(newValue,curBo)){
                newValue = resetCurrentValue(curBo);
            }
            int result = sequenceDAO.updSequence(sequenceBo.getSeqName(),curBo.getSeqValue(),newValue);
            if(result > 0){
                sequenceRange = new SequenceRange(curBo.getSeqValue(),newValue - 1);
                curBo.setSeqValue(newValue);
                this.sequenceBo = curBo;
                return sequenceRange;
            }else{
                continue;
            }
        }
        throw new SequenceException("[" + seqName + "]  sequenceBo update error");

    }
}
