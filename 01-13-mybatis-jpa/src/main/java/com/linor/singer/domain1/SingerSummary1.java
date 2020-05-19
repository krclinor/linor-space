package com.linor.singer.domain1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingerSummary1{
	private String firstName;
	private String lastName;
	private String lastAlbum;
}
