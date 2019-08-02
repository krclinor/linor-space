package com.linor.singer.dao1;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.linor.singer.domain1.Singer1;

@Repository
@Mapper
public interface SingerDao1 {
	List<Singer1> findAll();
	List<Singer1> findByFirstName(String firstName);
	String findNameById(Integer id);
	Singer1 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer1 singer);
	void update(Singer1 singer);
	void delete(Integer singerId);
	List<Singer1> findAllWithAlbums();
	void insertWithAlbum(Singer1 singer);
}
