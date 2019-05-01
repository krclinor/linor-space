package com.linor.app.mybatis.domain;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Singer2 implements Serializable{
	private Integer id;
	
	private String firstName;
	
	private String lastName;
	
	private LocalDate birthDate;
	
}
