package com.linor.app.jpa.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.linor.app.jpa.domain.Singer1;

public interface Singer1Repository extends CrudRepository<Singer1, Integer> {
	List<Singer1> findAll();
	List<Singer1> findByFirstName(String firstName);
	List<Singer1> findByFirstNameAndLastName(String firstName, String lastName);
}
