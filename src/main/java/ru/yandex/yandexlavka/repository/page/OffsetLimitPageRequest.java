package ru.yandex.yandexlavka.repository.page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetLimitPageRequest implements Pageable {
    private final int limit;
    private final int offset;
    private final Sort sort;

    public OffsetLimitPageRequest(int limit, int offset) {
        this(limit, offset, Sort.unsorted());
    }

    public OffsetLimitPageRequest(int limit, int offset, Sort sort) {
        if (limit < 1) {
            throw new IllegalArgumentException("Page limit must not be less than one");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Page offset must not be less than zero");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageRequest(limit, offset + limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? new OffsetLimitPageRequest(limit, offset - limit, sort) : this;
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageRequest(limit, 0, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageRequest(limit, limit * pageNumber, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
