package com.linor.singer.domain1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Singer1 {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private List<Album1> albums;

	public void addAlbum(Album1 album) {
		if(albums == null) {
			albums = new ArrayList<>();
		}
		albums.add(album);
	}
}
