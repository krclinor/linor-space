package com.linor.app.domain;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Singer2 implements Serializable{
	private Integer id;
	
	private String firstName;
	
	private String lastName;
	
	private LocalDate birthDate;
	
}
