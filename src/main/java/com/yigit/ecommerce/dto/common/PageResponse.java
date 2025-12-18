package com.yigit.ecommerce.dto.common;

import java.util.List;

public class PageResponse<T> {
    private List<T> items;
    private PaginationMeta meta;

    public PageResponse() {}

    public PageResponse(List<T> items, PaginationMeta meta) {
        this.items = items;
        this.meta = meta;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public PaginationMeta getMeta() {
        return meta;
    }

    public void setMeta(PaginationMeta meta) {
        this.meta = meta;
    }
}
