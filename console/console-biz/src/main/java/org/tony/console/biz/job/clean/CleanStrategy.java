package org.tony.console.biz.job.clean;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/4/8 10:41
 */
public interface CleanStrategy<T> {

    /**
     * 获取数据
     * @param startIndex 起始位置
     * @param size 分片大小
     * @return
     */
    public List<T> getData(long startIndex, Integer size);

    /**
     * 是否需要标记起始位置
     * @return
     */
    public boolean needStoreStartIndex();

    /**
     * 批量检索的数据量大小
     * @return
     */
    public Integer batchSize();

    /**
     * 是否可以清除
     * @param item
     * @return
     */
    public boolean canClean(T item);

    /**
     * 清除指定的数据
     * @param itemList
     */
    public void clean(List<T> itemList);

    /**
     * 名称
     * @return
     */
    public String getName();
}
