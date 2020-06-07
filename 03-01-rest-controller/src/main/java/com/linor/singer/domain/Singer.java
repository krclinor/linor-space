package com.linor.singer.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Singer {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private List<Album> albums;
}
