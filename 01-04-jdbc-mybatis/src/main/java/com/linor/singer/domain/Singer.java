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
	private List<Instrument> instruments;

	public void addAlbum(Album album) {
		if(albums == null) {
			albums = new ArrayList<>();
		}
		albums.add(album);
	}
	
	public void removeAlbum(Album album) {
		albums.remove(album);
	}
	
	public void addInstrument(Instrument instrument) {
		if(instruments == null) {
			instruments = new ArrayList<>();
		}
		instruments.add(instrument);
	}
	
	public void removeInstrument(Instrument instrument) {
		instruments.remove(instrument);
	}
}
