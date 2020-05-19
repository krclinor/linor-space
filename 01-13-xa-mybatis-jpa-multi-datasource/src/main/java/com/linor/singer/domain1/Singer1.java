package com.linor.singer.domain1;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Singer1 {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	
	private Set<Album1> albums;
	private Set<Instrument1> instruments;
}
