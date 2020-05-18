package com.linor.singer.dao2;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain2.Album2;
import com.linor.singer.domain2.Instrument2;
import com.linor.singer.domain2.Singer2;
import com.linor.singer.domain2.SingerSummary2;

@Mapper
public interface SingerDao2 {
	List<Singer2> findAll();
	List<Singer2> findAllByNativeQuery();

	List<Singer2> findByFirstName(String firstName);
	List<Singer2> findByFirstNameAndLastName(Singer2 singer);
	List<Singer2> findAllWithAlbums();

	List<Album2> findAlbumsBySinger(Singer2 singer);
	List<Album2> findAlbumsByTitle(String title);

	String findNameById(Integer id);
	String findFirstNameById(Integer id);

	Singer2 findById(Integer id);
	void insert(Singer2 singer);
	void update(Singer2 singer);
	void delete(Integer singerId);
	
	void insertWithAlbum(Singer2 singer);

	void insertInstrument(Instrument2 instrument);

	public List<SingerSummary2> listAllSingersSummary();
}
