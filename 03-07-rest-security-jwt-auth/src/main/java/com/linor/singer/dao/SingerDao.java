package com.linor.singer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain.Singer;

@Mapper
public interface SingerDao {
	List<Singer> findAll();
	List<Singer> findByFirstName(String firstName);
	String findNameById(Integer id);
	Singer findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer singer);
	void update(Singer singer);
	void delete(Integer singerId);
	List<Singer> findAllWithAlbums();
	void insertWithAlbum(Singer singer);
}
