package com.linor.singer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.CamelCaseMap;
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
	public void testFindAll(){
		log.info("testFindAll---->>");
		List<CamelCaseMap> singers = singerDao.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}
	
	private void listSingers(List<Singer> singers){
		for(Singer singer: singers){
			log.info(singer.toString());
		}
	}

	@Test
	public void testFindAllByNativeQuery() {
		log.info("testFindAllByNativeQuery---->>");
		List<CamelCaseMap> singers = singerDao.findAllByNativeQuery();
		log.info("네이티브 쿼리 실행 결과");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers.size() == 4);
	}

	@Test
	public void testFindAllWidthAlbums() {
		log.info("testFindAllWidthAlbums---->>");
		List<Singer> singers = singerDao.findAllWithAlbums();
		assertTrue(singers.size() == 4);
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void testFindbyId() {
		log.info("testFindbyId---->>");
		CamelCaseMap singer = singerDao.findById(1);
		log.info(singer.toString());
		assertEquals("종서", singer.get("firstName"));
	}
	
	@Test
	public void testFindByFirstName() {
		log.info("testFindByFirstName---->>");
		List<Singer> singers = singerDao.findByFirstName("종서");
		listSingers(singers);
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByFirstNameAndLastName() {
		log.info("findByFirstNameAndLastName----");
		
		CamelCaseMap singer = new CamelCaseMap();
		singer.cput("firstName", "종서");
		singer.cput("lastName", "김");
		List<Singer> singers = singerDao.findByFirstNameAndLastName(singer);
		singers.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByTitle() {
		log.info("testFindByTitle----");
		List<CamelCaseMap> albums = singerDao.findAlbumsByTitle("황혼의");
		assertTrue(albums.size() > 0);
		albums.forEach(a -> log.info(a.toString()));
		assertEquals(1, albums.size());
	}
	
	@Test
	public void testInsertSinger() {
		log.info("testInsertSinger---->>");
		List<Singer> oldSingers = singerDao.findAllWithAlbums();
		log.info(">>> 김조한 추가전");
		listSingers(oldSingers);
		CamelCaseMap singer = new CamelCaseMap();
		singer.cput("firstName", "조한");
		singer.cput("lastName", "김");
		singer.cput("birthDate", LocalDate.parse("1990-10-16"));
		singerDao.insert(singer);
		List<Singer> newSingers = singerDao.findAllWithAlbums();
		log.info(">>> 김조한 추가후");
		listSingers(newSingers);
		assertEquals(oldSingers.size() + 1 , newSingers.size());
	}
	
	@Test
	public void testUpdateSinger() {
		log.info("testUpdateSinger---->>");
		CamelCaseMap singerOldSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		
		CamelCaseMap singer = new CamelCaseMap();
		singer.cput("id", 1);
		singer.cput("firstName", "종서");
		singer.cput("lastName", "김");
		singer.cput("birthDate", LocalDate.parse("1977-10-16"));
		singerDao.update(singer);
		CamelCaseMap singerNewSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
		assertEquals(LocalDate.parse("1977-10-16"), ((Date)singerNewSinger.get("birthDate")).toLocalDate());
	}

	@Test
	public void testDeleteSinger() {
		log.info("testDeleteSinger---->>");
		List<Singer> oldSingers = singerDao.findAllWithAlbums();
		singerDao.delete(3);
		List<Singer> singers = singerDao.findAllWithAlbums();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers(singers);
		assertEquals(oldSingers.size() - 1, singers.size());
	}

	@Test
	public void testInsertSingerWithAlbum() {
		log.info("testInsertSingerWithAlbum---->>");
		List<Singer> oldSingers = singerDao.findAllWithAlbums();
		CamelCaseMap singer = new CamelCaseMap();
		singer.cput("firstName", "태원");
		singer.cput("lastName", "김");
		singer.cput("birthDate", LocalDate.parse("1965-04-12"));
		
		Set<CamelCaseMap> ablums = new HashSet<CamelCaseMap>();
		CamelCaseMap album = new CamelCaseMap();
		album.cput("title", "Never Ending Story");
		album.cput("releaseDate", LocalDate.parse("2001-08-31"));
		ablums.add(album);
		album = new CamelCaseMap();
		album.cput("title", "생각이나");
		album.cput("releaseDate", LocalDate.parse("2009-08-14"));
		ablums.add(album);
		album = new CamelCaseMap();
		album.cput("title", "사랑할수록");
		album.cput("releaseDate", LocalDate.parse("1993-11-01"));
		ablums.add(album);
		singer.cput("albums", ablums);
		
		singerDao.insertWithAlbum(singer);
		List<Singer> singers = singerDao.findAllWithAlbums();
		listSingers(singers);
		assertEquals(oldSingers.size() + 1, singers.size());
	}

	@Test
	public void testListSingersSummary() {
		log.info("testListSingersSummary---->>");
		List<CamelCaseMap> singers = singerDao.listAllSingersSummary();
		log.info("--- 가수 요약 리스트 : ");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertEquals(2, singers.size());
	}
}
