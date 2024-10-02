package com.lhl.tool.model.bo;

import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaginationBO {
    /**
     * 页面大小
     */
    private int pageSize;
    /**
     * 页码，从0开始
     */
    private int pageIndex;

    /**
     * 指定offset开始查找
     */
    private Integer offset;

    public PaginationBO(int pageSize, int pageIndex) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public int getOffset() {
        return Objects.nonNull(offset) ? offset : pageSize * pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}