package com.linor.singer.dao1;

import java.util.List;

import com.linor.singer.domain1.Instrument1;
import com.linor.singer.domain1.Singer1;
import com.linor.singer.domain1.SingerSummary1;

public interface SingerDao1 {
	List<Singer1> findAll();
	List<Singer1> findAllByNativeQuery();
	List<Singer1> findByFirstName(String firstName);
	List<Singer1> findAllWithAlbums();
	String findNameById(Integer id);
	Singer1 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer1 singer);
	void update(Singer1 singer);
	void delete(Integer singerId);
	void insertWithAlbum(Singer1 singer);
	public List<SingerSummary1> listAllSingersSummary();
	
	void insert(Instrument1 instrument);
}
