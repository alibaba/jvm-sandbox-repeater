package com.alibaba.repeater.console.start.controller.vo;

import com.alibaba.repeater.console.common.domain.PageResult;
import com.google.common.collect.Lists;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 分页适配器，将后端分页转换为前端显示分页
 *
 * @author yuebing.zyb@alibaba-inc.com 2018/11/23 15:49.
 */
public class PagerAdapter<T> {
    private long total;
    private long currentPage;
    private long previous;
    private long next;
    private long count;
    private boolean hasPrevious;
    private boolean hasNext;
    private String url;
    private List<T> content;
    private List<Integer> pages;

    public static <T> PagerAdapter<T> transform(@NotNull PageResult<T> result) {
        PagerAdapter<T> pager = new PagerAdapter<>();
        pager.setTotal(result.getTotalPage());
        pager.setContent(result.getData());
        pager.setHasNext(result.hasNext());
        pager.setHasPrevious(result.hasPrevious());
        pager.setCurrentPage(result.getPageIndex());
        pager.setPages(buildPageNo(result.getPageIndex().intValue(), result.getTotalPage().intValue()));
        pager.setUrl(buildUrl("page"));
        return pager;
    }

    /**
     * 配合前端VM分页插件使用
     *
     * @param result 分页结果
     * @param model  spring模型
     * @param <T>    泛型
     */
    public static <T> void transform0(PageResult<T> result, Model model) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return;
        }
        model.addAttribute("pagerWrapper", transform(result));
    }

    private static List<Integer> buildPageNo(int currentPage, int totalPage) {
        List<Integer> pages = Lists.newArrayList();
        // 10页以下全部展示,或者当前页小于7,都是展示10页
        if (totalPage <= 10 || currentPage < 7) {
            for (int i = 1; i <= (totalPage > 10 ? 10 : totalPage); i++) {
                pages.add(i);
            }
        } else {
            // 10页以上动态展示
            for (int i = currentPage - 5; i <= ((currentPage + 4) > totalPage ? totalPage : (currentPage + 4)); i++) {
                pages.add(i);
            }
        }
        return pages;
    }

    private static String buildUrl(String key) {
        StringBuilder builder = new StringBuilder(getServletReq().getRequestURI());
        if (getServletReq().getQueryString() == null) {
            builder.append("?tk=_repeater&");
        } else {
            builder.append("?");
        }
        Map<String, String[]> paramMap = getServletReq().getParameterMap();
        for (Map.Entry<String, String[]> entry :
                paramMap.entrySet()) {
            if (entry.getKey().equals(key)) {
                continue;
            }
            for (String value :
                    entry.getValue()) {
                builder.append(entry.getKey())
                        .append("=")
                        .append(value)
                        .append("&");
            }
        }
        return builder.substring(0, builder.length() - 1);
    }

    private static HttpServletRequest getServletReq() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public long getPrevious() {
        return currentPage - 1;
    }

    public void setPrevious(long previous) {
        this.previous = previous;
    }

    public long getNext() {
        return currentPage + 1;
    }

    public void setNext(long next) {
        this.next = next;
    }

    public boolean isHasPrevious() {
        return currentPage > 1;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }
}
