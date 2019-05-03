package com.linor.app.respository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.linor.app.domain.Singer1;

public interface Singer1Repository extends CrudRepository<Singer1, Integer> {
	public List<Singer1> findAll();
}
