package com.linor.singer.domain2;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Album2 {
	private Integer id;
	private Integer singerId;
	private String title;
	private LocalDate releaseDate;
}
