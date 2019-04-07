package com.linor.singer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional
public class SingerDaoTests {
	@Autowired
	private SingerDao singerDao;
	
	@Test
	public void testFindAll() {
		List<Singer> singers = singerDao.findAll();
		assertTrue(singers.size() >= 3);
		log.info("테스트 findAll----");
		singers.forEach(singer -> {
			log.info(singer.toString());
			singer.getAlbums().forEach(album ->{
				log.info(album.toString());
			});
			singer.getInstruments().forEach(inst ->{
				log.info(inst.toString());
			});
		});
	}

	@Test
	public void testFindByFirstName() {
		List<Singer> singers = singerDao.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		log.info("테스트 : findByFirstName----");
		singers.forEach(singer -> {
			log.info(singer.toString());
			singer.getAlbums().forEach(album ->{
				log.info(album.toString());
			});
			singer.getInstruments().forEach(inst ->{
				log.info(inst.toString());
			});
		});
	}
	
	@Test
	public void testFindByFirstNameAndLastName() {
		List<Singer> singers = singerDao.findByFirstNameAndLastName("종서", "김");
		assertTrue(singers.size() == 1);
		log.info("테스트 findByFirstNameAndLastName----");
		singers.forEach(singer -> {
			log.info(singer.toString());
			singer.getAlbums().forEach(album ->{
				log.info(album.toString());
			});
			singer.getInstruments().forEach(inst ->{
				log.info(inst.toString());
			});
		});
	}

	@Test
	public void testFindByTitle() {
		List<Album> albums = singerDao.findByTitle("황혼의");
		assertTrue(albums.size() > 0);
		assertEquals(1, albums.size());
		log.info("테스트 findByTitle----");
		albums.forEach(a -> log.info(a.toString() + ", Singer: " + a.getSinger().toString()));
	}

	@Test
	public void testAuditSinger() {
		List<Singer> singers = singerDao.findAll();
		listSingers(singers);
		log.info("새 가수 추가");
		Singer singer = new Singer();
		singer.setFirstName("BB");
		singer.setLastName("King");
		singer.setBirthDate(LocalDate.parse("1970-12-09"));
		singerDao.save(singer);
		
		singers = singerDao.findAll();
		listSingers(singers);
		
		singer = singerDao.findById(1);
		log.info("가수  내역: " + singer.toString());
		
		singer.setFirstName("John Clayton");
		singerDao.save(singer);
		
		singer = singerDao.findById(1);
		singer.setFirstName("Riley B.");
		singerDao.save(singer);

		singers = singerDao.findAll();
		listSingers(singers);
	}
	
	private void listSingers(List<Singer> singers) {
		singers.forEach(singer -> {
			log.info(singer.toString());
			log.info("Audit: {}, {}, {}, {}" 
					, singer.getCreatedBy()
					, singer.getCreatedDate()
					, singer.getLastModifiedBy()
					, singer.getLastModifiedDate());
			singer.getAlbums().forEach(album ->{
				log.info(album.toString());
			});
			singer.getInstruments().forEach(inst ->{
				log.info(inst.toString());
			});
		});

	}
}
