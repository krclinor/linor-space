package com.linor.app.domain;

import java.util.HashMap;

public class CamelCaseMap extends HashMap<String, Object>{
	
	private String toProperCase(String source, boolean isCapital) {
		String result = "";
		if(isCapital) {
			result = source.substring(0,1).toUpperCase() + source.substring(1).toLowerCase();
		}else {
			result = source.toLowerCase();
		}
		return result;
	}
	
	private String toCamelCase(String source) {
		String[] parts = source.split("_");
		StringBuilder camelCaseString = new StringBuilder();
		for(int i = 0; i < parts.length; i++){
			String part = parts[i];
			camelCaseString.append(toProperCase(part, (i != 0 ? true: false)));
		}
		return camelCaseString.toString();
	}

	@Override
	public Object put(String key, Object value) {
		return super.put(toCamelCase(key), value);
	}
}
