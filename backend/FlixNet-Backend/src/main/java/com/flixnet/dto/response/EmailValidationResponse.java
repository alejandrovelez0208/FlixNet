package com.flixnet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailValidationResponse {

	private boolean exits;
	private boolean available;
}
