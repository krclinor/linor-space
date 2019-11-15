package com.linor.singer.model;

import lombok.Data;

@Data
public class EnumValue {
	private String key;
	private String value;
	
	public EnumValue(EnumModel enumModel) {
		key = enumModel.getKey();
		value = enumModel.getValue();
	}
}
