package com.linor.singer.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Singer {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private List<Album> albums;

	public void addAlbum(Album album) {
		if(albums == null) {
			albums = new ArrayList<>();
		}
		albums.add(album);
	}
}
