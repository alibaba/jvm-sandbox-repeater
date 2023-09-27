package org.tony.console.common;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {

    List<T> data;

    long total=0;

    int pageSize;

    int pageNo;

    public static <T> Page<T> build(List<T> data, long total) {
        Page<T> r = new Page<T>();
        r.setData(data);
        r.setTotal(total);
        return r;
    }

    public boolean hasContent() {
        if (total <= 0) {
            return false;
        }

        return true;
    }
}
