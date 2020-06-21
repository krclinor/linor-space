package com.linor.singer.dao;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

public interface SingerDao {
	List<Singer> findAll();
	List<Singer> findAllByNativeQuery();

	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(Singer singer);
	List<Singer> findAllWithAlbums();
	List<Album> findAlbumsBySinger(Singer singer);
	
	List<Album> findAlbumsByTitle(@Param("title") String t);

	String findNameById(Integer id);
	Singer findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer singer);
	void update(Singer singer);
	void delete(Integer singerId);
	void insertWithAlbum(Singer singer);
	public List<SingerSummary> listAllSingersSummary();

	void insertInstrument(Instrument instrument);
}
