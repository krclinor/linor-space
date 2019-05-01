package com.linor.app.db2.dao;

import java.util.List;

import com.linor.app.db2.domain.Singer2;


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
