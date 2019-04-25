package com.linor.singer.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldErrorMessage {
	private String resource;
	private String field;
	private String code;
	private String message;
}
