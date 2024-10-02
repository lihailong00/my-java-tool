package com.lhl.tool.model.dto;

import lombok.Data;

@Data
public class FieldSortDTO {
    private String field;

    /**
     * asc或desc
     */
    private String direction;

    @Override
    public String toString() {
        return "FieldSortDTO{" +
                "field='" + field + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
