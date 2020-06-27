package com.linor.singer.domain;

import java.util.List;

import lombok.Data;

@Data
public class ErrorDetail {
	private int errorCode;
	private String errorMessage;
	private String devErrorMessage;

	private List<FieldErrorMessage> fieldErrors;
}
