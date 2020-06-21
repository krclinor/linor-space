package com.linor.singer.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

import lombok.extern.slf4j.Slf4j;
@Repository
@Transactional
@Slf4j
public class SingerDaoImpl implements SingerDao {
	
	@Autowired
	private SingerRepository singerRepository;
	
	@Autowired
	private AlbumRespository albumRepository;

	@Autowired
	private InstrumentRepository instrumentRepository;

	@Override
	public List<Singer> findAll() {
		return singerRepository.findAll();
	}

	@Override
	public List<Singer> findAllByNativeQuery() {
		return singerRepository.findAllByNativeQuery();
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		return singerRepository.findByFirstName(firstName);
	}

	@Override
	public List<Singer> findByFirstNameAndLastName(Singer singer) {
		return singerRepository.findByFirstNameAndLastName(singer.getFirstName(), singer.getLastName());
	}

	@Override
	public List<Album> findAlbumsBySinger(Singer singer) {
		return albumRepository.findBySinger(singer);
	}

	@Override
	public List<Album> findAlbumsByTitle(String title) {
		//return albumRepository.findByTitle(title);
		return albumRepository.findByTitleContaining(title);
	}

	@Override
	public String findNameById(Integer id) {
		Singer singer = singerRepository.findById(id).get();
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer findById(Integer id) {
		return singerRepository.findById(id).get();
	}

	@Override
	public String findFirstNameById(Integer id) {
		return singerRepository.findById(id).get().getFirstName();
	}

	@Override
	public void insert(Singer singer) {
		singerRepository.save(singer);
	}

	@Override
	public void update(Singer singer) {
		singerRepository.save(singer);
	}

	@Override
	public void delete(Integer singerId) {
		singerRepository.deleteById(singerId);
	}

	@Override
	public void insertWithAlbum(Singer singer) {
		singerRepository.save(singer);
	}

	
	@Override
	public List<Singer> findAllWithAlbums() {
		return singerRepository.findAllWithAlbum();
	}

	@Override
	public List<SingerSummary> listAllSingersSummary() {
		return singerRepository.listAllSingersSummary();
	}

	@Override
	public void insertInstrument(Instrument instrument) {
		instrumentRepository.save(instrument);
	}
}
