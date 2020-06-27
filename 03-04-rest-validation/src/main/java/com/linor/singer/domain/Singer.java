package com.linor.singer.domain;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Singer {
	private Integer id;

	@Size(min = 2, max = 10, message = "{error.name}")
	private String firstName;

	@NotEmpty(message = "{error.lastName}")
	private String lastName;

	@Past
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;
	
	private Set<Album> albums;
	private Set<Instrument> instruments;
}
