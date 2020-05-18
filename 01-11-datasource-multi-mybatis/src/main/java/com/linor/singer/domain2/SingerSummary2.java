package com.linor.singer.domain2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingerSummary2{
	private String firstName;
	private String lastName;
	private String lastAlbum;
}
