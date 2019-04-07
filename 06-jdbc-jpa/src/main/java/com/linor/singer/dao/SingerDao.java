package com.linor.singer.dao;

import java.util.List;

import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

public interface SingerDao {
	List<Singer> findAll();
	List<Singer> findAllByNativeQuery();
	List<Singer> findByFirstName(String firstName);
	List<Singer> findAllWithAlbums();
	String findNameById(Integer id);
	Singer findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer singer);
	void update(Singer singer);
	void delete(Integer singerId);
	void insertWithAlbum(Singer singer);
	public List<SingerSummary> listAllSingersSummary();
}
