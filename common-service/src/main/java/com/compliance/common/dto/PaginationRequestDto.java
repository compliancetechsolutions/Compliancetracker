package com.compliance.common.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequestDto {

	private Integer page = 0;
	private Integer size = 20;
	private List<String> sort;
}