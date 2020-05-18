package com.linor.singer.domain1;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album1 {
	private Integer id;
	private Integer singerId;
	private String title;
	private LocalDate releaseDate;
	
	private Singer1 singer;
}
