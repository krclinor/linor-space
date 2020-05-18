package com.linor.singer.dao1;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain1.Album1;
import com.linor.singer.domain1.Instrument1;
import com.linor.singer.domain1.Singer1;
import com.linor.singer.domain1.SingerSummary1;

@Mapper
public interface SingerDao1 {
	List<Singer1> findAll();
	List<Singer1> findAllByNativeQuery();

	List<Singer1> findByFirstName(String firstName);
	List<Singer1> findByFirstNameAndLastName(Singer1 singer);
	List<Singer1> findAllWithAlbums();

	List<Album1> findAlbumsBySinger(Singer1 singer);
	List<Album1> findAlbumsByTitle(String title);

	String findNameById(Integer id);
	String findFirstNameById(Integer id);

	Singer1 findById(Integer id);
	void insert(Singer1 singer);
	void update(Singer1 singer);
	void delete(Integer singerId);
	
	void insertWithAlbum(Singer1 singer);

	void insertInstrument(Instrument1 instrument);

	public List<SingerSummary1> listAllSingersSummary();
}
