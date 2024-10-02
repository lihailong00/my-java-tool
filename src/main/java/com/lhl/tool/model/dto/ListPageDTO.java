package com.lhl.tool.model.dto;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ListPageDTO<T> {
    List<T> data;

    private PageDTO pageDTO;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PageDTO getPageDTO() {
        return pageDTO;
    }

    public void setPageDTO(PageDTO pageDTO) {
        this.pageDTO = pageDTO;
    }
}