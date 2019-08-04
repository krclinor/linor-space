package com.linor.singer.domain;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Singer {
	@Builder.Default
	private Integer id = null;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
}
