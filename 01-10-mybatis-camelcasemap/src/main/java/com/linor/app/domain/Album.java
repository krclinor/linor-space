package com.linor.app.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Album {
	private Integer id;
	private Integer singerId;
	private String title;
	private LocalDate releaseDate;
}
