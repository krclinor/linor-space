package com.linor.singer.domain2;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingerSummary2 implements Serializable {
	private String firstName;
	private String lastName;
	private String lastAlbum;
}
