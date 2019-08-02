package com.linor.singer.domain2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Singer2 {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private List<Album2> albums;

	public void addAlbum(Album2 album) {
		if(albums == null) {
			albums = new ArrayList<>();
		}
		albums.add(album);
	}
}
