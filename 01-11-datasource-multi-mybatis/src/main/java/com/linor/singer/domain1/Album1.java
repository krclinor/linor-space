package com.linor.singer.domain1;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Album1 {
	private Integer id;
	private Integer singerId;
	private String title;
	private LocalDate releaseDate;
}
