package com.linor.app.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.app.mybatis.domain.Singer2;

@Mapper
public interface Singer2Dao {
	List<Singer2> findAll();
	List<Singer2> findByFirstName(String firstName);
	List<Singer2> findByFirstNameAndLastName(String firstName, String lastName);

	String findNameById(Integer id);
	Singer2 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer2 singer);
	void update(Singer2 singer);
	void delete(Integer singerId);
}
