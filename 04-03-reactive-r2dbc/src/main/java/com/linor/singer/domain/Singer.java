package com.linor.singer.domain;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Singer{
	@Id
	private Integer id;
	
	private String firstName;
	
	private String lastName;
	
	@JsonFormat(pattern = "yyyy.MM.dd")
	private LocalDate birthDate;
	
}
