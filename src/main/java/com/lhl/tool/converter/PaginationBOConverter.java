package com.lhl.tool.converter;

import com.lhl.tool.model.bo.PaginationBO;
import com.lhl.tool.model.dto.PageDTO;

import java.util.Objects;

public class PaginationBOConverter {
    public static PaginationBO toPaginationBO(PageDTO page) {
        if (Objects.isNull(page)) {
            return null;
        }
        PaginationBO pagination = new PaginationBO();
        pagination.setPageIndex(page.getCurrentPage() - 1);
        pagination.setPageSize(page.getPerPage());
        return pagination;
    }

    public static PaginationBO toPaginationBO(int pageIndex, int size) {
        PaginationBO pagination = new PaginationBO();
        pagination.setPageIndex(pageIndex);
        pagination.setPageSize(size);
        return pagination;
    }
}