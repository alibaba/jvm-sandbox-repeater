package org.tony.console.db.sequence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.MysqlSequenceMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author peng.hu1
 * @Date 2022/12/15 16:35
 */
@Component
public class MysqlSequenceFactory {

    private final Lock lock = new ReentrantLock();

    /**  */
    private Map<String,MysqlSequenceHolder> holderMap = new ConcurrentHashMap<>();

    @Resource
    private MysqlSequenceMapper mysqlSequenceMapper;
    /** 单个sequence初始化乐观锁更新失败重试次数 */
    @Value("${seq.init.retry:5}")
    private int initRetryNum;
    /** 单个sequence更新序列区间乐观锁更新失败重试次数 */
    @Value("${seq.get.retry:20}")
    private int getRetryNum;

    @PostConstruct
    private void init(){
        //初始化所有sequence
        initAll();
    }


    /**
     * <p> 加载表中所有sequence，完成初始化 </p>
     * @return void
     * @author coderzl
     */
    private void initAll(){
        try {
            lock.lock();
            List<MysqlSequenceBO> boList = mysqlSequenceMapper.getAll();
            if (boList == null) {
                throw new IllegalArgumentException("The sequenceRecord is null!");
            }
            for (MysqlSequenceBO bo : boList) {
                MysqlSequenceHolder holder = new MysqlSequenceHolder(mysqlSequenceMapper, bo,initRetryNum,getRetryNum);
                holder.init();
                holderMap.put(bo.getSeqName(), holder);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * <p>  </p>
     * @param seqName
     * @return long
     * @author coderzl
     */
    public long getNextVal(String seqName) throws SequenceException {
        MysqlSequenceHolder holder = holderMap.get(seqName);
        if (holder == null) {
            try {
                lock.lock();
                holder = holderMap.get(seqName);
                if (holder != null){
                    return holder.getNextVal();
                }
                MysqlSequenceBO bo = mysqlSequenceMapper.getSequence(seqName);
                holder = new MysqlSequenceHolder(mysqlSequenceMapper, bo,initRetryNum,getRetryNum);
                holder.init();
                holderMap.put(seqName, holder);
            }finally {
                lock.unlock();
            }
        }
        return holder.getNextVal();
    }

}

