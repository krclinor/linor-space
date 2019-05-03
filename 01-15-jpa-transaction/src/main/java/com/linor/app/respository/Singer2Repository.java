package com.linor.app.respository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.linor.app.domain.Singer2;

public interface Singer2Repository extends CrudRepository<Singer2, Integer> {
	List<Singer2> findAll();
}
