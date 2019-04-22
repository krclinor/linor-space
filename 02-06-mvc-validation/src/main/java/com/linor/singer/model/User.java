package com.linor.singer.model;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import lombok.Data;

@Data
public class User {
	@Size(min = 2, max = 10, message = "{error.name}")
	private String name;
	
	@NotEmpty(message = "{error.lastName}")
	private String lastName;
	
	@Email(message = "{error.email}")
	private String email;
	
	@Pattern(regexp = "^[a-zA-Z]\\w{3,14}$")
	private String password;
	
	private String detail;
	
	@Past
	@DateTimeFormat(pattern="yyyy.MM.dd")
	private LocalDate birthDate;
	
	private Gender gender;
	private String country;
	private boolean nonSmoking;
	
	@NumberFormat(pattern="#,##0")
	@Min(value = 100)
	private long salary;
}
