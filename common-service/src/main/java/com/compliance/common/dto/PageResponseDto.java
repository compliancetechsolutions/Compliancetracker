package com.compliance.common.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponseDto<T> extends BaseDto {
	private List<T> content;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
