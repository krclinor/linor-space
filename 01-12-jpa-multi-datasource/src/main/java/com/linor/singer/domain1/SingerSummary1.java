package com.linor.singer.domain1;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingerSummary1 implements Serializable {
	private String firstName;
	private String lastName;
	private String lastAlbum;
}
