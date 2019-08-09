package com.linor.singer.dao;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;

public interface SingerDao {
	List<Singer> findAll();
	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(String firstName, String lastName);
	Singer findById(int id);
	public void save(Singer singer);
	
	List<Album> findBySinger(Singer singer);
	
	
	List<Album> findByTitle(@Param("title") String t);

	void insert(Instrument instrument);
}
