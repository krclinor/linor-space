package com.linor.singer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain.CamelCaseMap;
import com.linor.singer.domain.Singer;

@Mapper
public interface SingerDao {
	List<CamelCaseMap> findAll();
	List<CamelCaseMap> findAllByNativeQuery();

	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(CamelCaseMap singer);
	List<Singer> findAllWithAlbums();

	List<CamelCaseMap> findAlbumsBySinger(CamelCaseMap singer);
	List<CamelCaseMap> findAlbumsByTitle(String title);

	String findNameById(Integer id);
	String findFirstNameById(Integer id);

	CamelCaseMap findById(Integer id);
	void insert(CamelCaseMap singer);
	void update(CamelCaseMap singer);
	void delete(Integer singerId);
	
	void insertWithAlbum(CamelCaseMap singer);

	void insertInstrument(CamelCaseMap instrument);

	public List<CamelCaseMap> listAllSingersSummary();
}
