package com.linor.singer.model;

public enum Gender implements EnumModel{
	MALE("남"),
	FEMALE("여");
	
	private String value;
	
	Gender(String value){
		this.value = value;
	}
	
	@Override
	public String getKey() {
		return name();
	}
	
	@Override
	public String getValue() {
		return value;
	}
}
