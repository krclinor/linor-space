package com.linor.app.db1.dao;

import java.util.List;

import com.linor.app.db1.domain.Singer1;

public interface Singer1Dao {
	List<Singer1> findAll();
	List<Singer1> findByFirstName(String firstName);
	List<Singer1> findByFirstNameAndLastName(String firstName, String lastName);

	String findNameById(Integer id);
	Singer1 findById(Integer id);
	String findFirstNameById(Integer id);
	void insert(Singer1 singer);
	void update(Singer1 singer);
	void delete(Integer singerId);
}
