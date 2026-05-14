package com.compliance.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkCreateUserRequestDto {
	
    private List<CreateUserRequestDto> users;
    
	
	
	

}
