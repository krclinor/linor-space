package com.linor.app.jpa.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.app.jpa.dao.Singer1Dao;
import com.linor.app.jpa.domain.Singer1;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class Singer1DaoImpl implements Singer1Dao {
	
	@Autowired
	private Singer1Repository singer1Repository;
	
	
	@Override
	@Transactional(readOnly=true)
	public List<Singer1> findAll() {
		return singer1Repository.findAll();
	}

	@Override
	public List<Singer1> findByFirstName(String firstName) {
		return singer1Repository.findByFirstName(firstName);
	}

	@Override
	public List<Singer1> findByFirstNameAndLastName(String firstName, String lastName) {
		return singer1Repository.findByFirstNameAndLastName(firstName, lastName);
	}


	@Override
	public String findNameById(Integer id) {
		log.debug("가수 ID: {}", id);
		Singer1 singer = singer1Repository.findById(id).get();
		log.debug("가수: {}", singer);
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer1 findById(Integer id) {
		return singer1Repository.findById(id).get();
	}

	@Override
	public String findFirstNameById(Integer id) {
		return singer1Repository.findById(id).get().getFirstName();
	}

	@Override
	public void insert(Singer1 singer) {
		singer1Repository.save(singer);
	}

	@Override
	public void update(Singer1 singer) {
		singer1Repository.save(singer);
	}

	@Override
	public void delete(Integer singerId) {
		singer1Repository.deleteById(singerId);
	}

}
