package com.linor.singer.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Album {
	private Integer id;
	private Integer singerId;
	private String title;
	private LocalDate releaseDate;
	
	private Singer singer;
}
