package com.linor.singer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

@Mapper
public interface SingerDao {
	List<Singer> findAll();
	List<Singer> findAllByNativeQuery();

	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(Singer singer);
	List<Singer> findAllWithAlbums();

	List<Album> findAlbumsBySinger(Singer singer);
	List<Album> findAlbumsByTitle(String title);

	String findNameById(Integer id);
	String findFirstNameById(Integer id);

	Singer findById(Integer id);
	void insert(Singer singer);
	void update(Singer singer);
	void delete(Integer singerId);
	
	void insertWithAlbum(Singer singer);

	void insertInstrument(Instrument instrument);

	public List<SingerSummary> listAllSingersSummary();
}
