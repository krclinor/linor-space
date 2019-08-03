package com.linor.singer.dao2;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.domain2.Singer2;

@Repository
@Transactional("txManager2")
@Mapper
public interface SingerDao2 {
	List<Singer2> findAll();
	List<Singer2> findByFirstName(String firstName);
	String findNameById(Integer id);
	Singer2 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer2 singer);
	void update(Singer2 singer);
	void delete(Integer singerId);
	List<Singer2> findAllWithAlbums();
	void insertWithAlbum(Singer2 singer);
}
