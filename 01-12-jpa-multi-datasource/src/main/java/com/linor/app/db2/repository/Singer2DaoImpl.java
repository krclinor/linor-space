package com.linor.app.db2.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.app.db2.dao.Singer2Dao;
import com.linor.app.db2.domain.Singer2;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class Singer2DaoImpl implements Singer2Dao {
	
	@Autowired
	private Singer2Repository singer2Repository;
	
	
	@Override
	@Transactional(readOnly=true)
	public List<Singer2> findAll() {
		return singer2Repository.findAll();
	}

	@Override
	public List<Singer2> findByFirstName(String firstName) {
		return singer2Repository.findByFirstName(firstName);
	}

	@Override
	public List<Singer2> findByFirstNameAndLastName(String firstName, String lastName) {
		return singer2Repository.findByFirstNameAndLastName(firstName, lastName);
	}


	@Override
	public String findNameById(Integer id) {
		log.debug("가수 ID: {}", id);
		Singer2 singer = singer2Repository.findById(id).get();
		log.debug("가수: {}", singer);
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer2 findById(Integer id) {
		return singer2Repository.findById(id).get();
	}

	@Override
	public String findFirstNameById(Integer id) {
		return singer2Repository.findById(id).get().getFirstName();
	}

	@Override
	public void insert(Singer2 singer) {
		singer2Repository.save(singer);
	}

	@Override
	public void update(Singer2 singer) {
		singer2Repository.save(singer);
	}

	@Override
	public void delete(Integer singerId) {
		singer2Repository.deleteById(singerId);
	}

}
