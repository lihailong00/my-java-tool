package com.lhl.tool.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageDTO {
    @NotNull(message = "当前页为必传项")
    @Min(value = 1, message = "当前页最小为1")
    @Max(value = 2000L, message = "当前页最大为2000")
    private Integer currentPage;

    @NotNull(message = "页大小为必传项")
    @Min(value = 1L, message = "页大小最小为1")
    @Max(value = 3000L, message = "页大小最大为3000")
    private Integer perPage;

    private Integer total;

    public PageDTO() {
    }

    private PageDTO(Builder builder) {
        this.setCurrentPage(builder.currentPage);
        this.setPerPage(builder.perPage);
        this.setTotal(builder.total);
    }


    @Override
    public String toString() {
        return "PageDTO{" +
                "currentPage=" + currentPage +
                ", perPage=" + perPage +
                ", total=" + total +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer currentPage;
        private Integer perPage;
        private Integer total;

        public Builder() {
        }

        public Builder currentPage(Integer val) {
            this.currentPage = val;
            return this;
        }

        public Builder perPage(Integer val) {
            this.perPage = val;
            return this;
        }

        public Builder total(Integer val) {
            this.total = val;
            return this;
        }

        public PageDTO build() {
            return new PageDTO(this);
        }
    }
}
