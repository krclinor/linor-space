package com.linor.singer.dao2;

import java.util.List;

import com.linor.singer.domain2.Instrument2;
import com.linor.singer.domain2.Singer2;
import com.linor.singer.domain2.SingerSummary2;

public interface SingerDao2 {
	List<Singer2> findAll();
	List<Singer2> findAllByNativeQuery();
	List<Singer2> findByFirstName(String firstName);
	List<Singer2> findAllWithAlbums();
	String findNameById(Integer id);
	Singer2 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer2 singer);
	void update(Singer2 singer);
	void delete(Integer singerId);
	void insertWithAlbum(Singer2 singer);
	public List<SingerSummary2> listAllSingersSummary();
	
	void insert(Instrument2 instrument);
}
