package com.linor.singer.domain;

import lombok.Data;

@Data
public class ErrorDetail {
	private int errorCode;
	private String errorMessage;
	private String devErrorMessage;
}
