package com.linor.singer.respository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class SingerDaoImpl implements SingerDao {
	
	@Autowired
	private SingerRepository singerRepository;
	
	@Autowired
	private AlbumRespository albumRepository;
	
	@Override
	@Transactional(readOnly=true)
	public List<Singer> findAll() {
		return singerRepository.findAll();
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		return singerRepository.findByFirstName(firstName);
	}

	@Override
	public List<Singer> findByFirstNameAndLastName(String firstName, String lastName) {
		return singerRepository.findByFirstNameAndLastName(firstName, lastName);
	}

	@Override
	public Singer findById(int id) {
		return singerRepository.findById(id).get();
	}
	@Override
	public List<Album> findBySinger(Singer singer) {
		return albumRepository.findBySinger(singer);
	}

	@Override
	public List<Album> findByTitle(String title) {
		return albumRepository.findByTitle(title);
	}
	
	public void save(Singer singer) {
		singerRepository.save(singer);
	}
}
