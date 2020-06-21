package com.linor.singer.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorDetail {
	private int errorCode;
	private String errorMessage;
	private String devErrorMessage;
	private Map<String, Object> addtionalData = new HashMap<>();
}
