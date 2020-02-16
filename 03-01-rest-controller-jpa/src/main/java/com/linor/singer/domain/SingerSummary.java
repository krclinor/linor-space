package com.linor.singer.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingerSummary implements Serializable {
	private String firstName;
	private String lastName;
	private String lastAlbum;
}
