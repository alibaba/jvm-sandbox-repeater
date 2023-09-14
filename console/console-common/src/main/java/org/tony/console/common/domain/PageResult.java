package org.tony.console.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * {@link PageResult}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class PageResult<T> implements java.io.Serializable {

    private List<T> data;

    private boolean success;

    private String message;

    private Long count;
    private Integer totalPage;
    private Integer pageSize;
    private Integer pageIndex;

    public boolean hasPrevious() {
        return pageIndex > 1;
    }

    public boolean hasNext() {
        return pageIndex < totalPage;
    }

    public static <T> PageResult buildSuccess(List<T> data, Long count) {
        PageResult<T> result = new PageResult<>();
        result.setSuccess(true);
        result.setCount(count);
        result.setData(data);
        return result;
    }

    public static PageResult buildFail(String message) {
        PageResult result = new PageResult();
        result.setSuccess(false);
        result.setMessage(message);

        return result;
    }
}
