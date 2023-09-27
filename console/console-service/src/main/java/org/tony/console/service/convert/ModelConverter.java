package org.tony.console.service.convert;

import java.util.List;

/**
 * {@link ModelConverter}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ModelConverter<S, T> {

    /**
     * 模型转换
     *
     * @param source 源模型
     * @return 目标模型
     */
    T convert(S source);

    List<T> convert(List<S> sList);

    List<S> reconvertList(List<T> sList);

    /**
     * 模型恢复
     *
     * @param target 目标模型
     * @return 源模型
     */
    S reconvert(T target);
}
