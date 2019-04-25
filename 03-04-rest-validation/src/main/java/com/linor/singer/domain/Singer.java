package com.linor.singer.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Singer {
	private Integer id;
	
	@Size(min=2, max = 15)
	private String firstName;
	
	@NotEmpty(message = "성은 필수입력입니다.")
	private String lastName;
	
	@JsonFormat(pattern = "yyyy.MM.dd")
	@Past(message = "과거 날짜로 입력해 주세요")
	private LocalDate birthDate;
	
	private List<Album> albums;

	public void addAlbum(Album album) {
		if(albums == null) {
			albums = new ArrayList<>();
		}
		albums.add(album);
	}
}
