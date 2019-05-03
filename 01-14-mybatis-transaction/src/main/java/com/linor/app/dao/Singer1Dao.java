package com.linor.app.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.app.domain.Singer1;

@Mapper
public interface Singer1Dao {
	List<Singer1> findAll();
	List<Singer1> findByFirstName(String firstName);
	List<Singer1> findByFirstNameAndLastName(String firstName, String lastName);

	String findNameById(Integer id);
	Singer1 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer1 singer) throws Exception;
	void update(Singer1 singer) throws Exception;
	void delete(Integer singerId) throws Exception;
}
