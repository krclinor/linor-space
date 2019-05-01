package com.linor.app.jpa.dao;

import java.util.List;

import com.linor.app.jpa.domain.Singer1;

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
