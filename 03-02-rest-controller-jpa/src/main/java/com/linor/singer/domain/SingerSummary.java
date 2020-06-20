package com.linor.singer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingerSummary{
	private String firstName;
	private String lastName;
	private String lastAlbum;
}
