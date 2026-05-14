package com.compliance.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Pagination response wrapper.
 *
 * <p><b>Fixes from original:</b>
 * <ul>
 *   <li><b>Replaced {@code @Data} with {@code @Getter/@Setter}.</b>
 *       {@code @Data} generates {@code equals()} and {@code hashCode()} over all
 *       fields including the generic {@code content} list. When two pages with
 *       different content happen to have the same page/size/total metadata,
 *       they would compare as equal. Page wrappers are not value objects —
 *       identity-based equality (the default from {@code Object}) is correct
 *       here.</li>
 *   <li><b>Removed {@code @EqualsAndHashCode(callSuper = true)}.</b>
 *       Calling super chains to {@link BaseDto}'s equals which includes audit
 *       timestamps ({@code createdAt}, {@code updatedAt}) — meaning two
 *       identical pages fetched one second apart would not be equal. Pages
 *       are not domain objects; they don't need content-based equality at all.</li>
 *   <li><b>Removed {@code extends BaseDto}.</b>
 *       {@code PageResponseDto} is an API transport wrapper for paginated
 *       results — it is not a domain entity record and has no business
 *       carrying audit timestamps ({@code createdAt}, {@code updatedAt},
 *       {@code createdBy}, {@code updatedBy}) which BaseDto adds to every
 *       response. Including them confuses API consumers.</li>
 *   <li><b>Added a {@link #from(Page)} factory method</b> to avoid boilerplate
 *       when converting a Spring Data {@link Page} in service/controller code.</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponseDto<T> {

    private List<T> content;
    private int     page;
    private int     size;
    private long    totalElements;
    private int     totalPages;
    private boolean first;
    private boolean last;

    /**
     * Convenience factory — converts a Spring Data {@link Page} to this DTO.
     *
     * <p>Usage:
     * <pre>{@code
     *   Page<EntityResponse> page = entityService.getAllEntities(pageable);
     *   return ok(PageResponseDto.from(page));
     * }</pre>
     *
     * @param page the Spring Data page to wrap
     * @param <T>  element type
     * @return populated DTO ready for serialization
     */
    public static <T> PageResponseDto<T> from(Page<T> page) {
        PageResponseDto<T> dto = new PageResponseDto<>();
        dto.setContent(page.getContent());
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setFirst(page.isFirst());
        dto.setLast(page.isLast());
        return dto;
    }
}
