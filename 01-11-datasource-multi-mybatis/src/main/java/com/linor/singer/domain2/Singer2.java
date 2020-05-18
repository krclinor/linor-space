package com.linor.singer.domain2;

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
public class Singer2 {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	
	private Set<Album2> albums;
	private Set<Instrument2> instruments;
}
