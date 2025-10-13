package pl.rezerveo.booking.common.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        PageableResponse pageable,
        boolean last,
        long totalElements,
        int totalPages,
        int size,
        int number,
        SortResponse sort,
        boolean first,
        int numberOfElements,
        boolean empty
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                PageableResponse.of(page.getPageable()),
                page.isLast(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                SortResponse.of(page.getSort()),
                page.isFirst(),
                page.getNumberOfElements(),
                page.isEmpty()
        );
    }

    public record PageableResponse(
            int pageNumber,
            int pageSize,
            SortResponse sort,
            long offset,
            boolean paged,
            boolean unpaged
    ) {
        public static PageableResponse of(Pageable pageable) {
            return new PageableResponse(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    SortResponse.of(pageable.getSort()),
                    pageable.getOffset(),
                    pageable.isPaged(),
                    pageable.isUnpaged()
            );
        }
    }

    public record SortResponse(
            boolean empty,
            boolean sorted,
            boolean unsorted
    ) {
        public static SortResponse of(Sort sort) {
            return new SortResponse(
                    sort.isEmpty(),
                    sort.isSorted(),
                    sort.isUnsorted()
            );
        }
    }
}