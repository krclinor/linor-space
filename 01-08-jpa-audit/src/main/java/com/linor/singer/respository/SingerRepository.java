package com.linor.singer.respository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.linor.singer.domain.Singer;

public interface SingerRepository extends CrudRepository<Singer, Integer> {
	List<Singer> findAll();
	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(String firstName, String lastName);
}
