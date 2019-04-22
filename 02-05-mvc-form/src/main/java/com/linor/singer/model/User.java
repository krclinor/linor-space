package com.linor.singer.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import lombok.Data;

@Data
public class User {
	private String name;
	private String lastName;
	private String email;
	private String password;
	private String detail;
	
	@DateTimeFormat(pattern="yyyy.MM.dd")
	private LocalDate birthDate;
	
	private Gender gender;
	private String country;
	private boolean nonSmoking;
	
	@NumberFormat(pattern="#,##0")
	private long salary;
}
