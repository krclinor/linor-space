package com.linor.app.db2.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.linor.app.db2.domain.Singer2;


public interface Singer2Repository extends CrudRepository<Singer2, Integer> {
	List<Singer2> findAll();
	List<Singer2> findByFirstName(String firstName);
	List<Singer2> findByFirstNameAndLastName(String firstName, String lastName);
}
