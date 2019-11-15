package com.linor.singer.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linor.singer.model.EnumValue;
import com.linor.singer.model.Gender;

@RestController
public class EnumApi {
	@GetMapping("/enum")
	public List<EnumValue> listGender() {
		return Arrays.stream(Gender.values())
				.map(EnumValue::new)
				.collect(Collectors.toList());
	}
}
