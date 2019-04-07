package com.linor.singer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
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
	
	@Before
	public void setUp() {
		//singerDao = new SingerDaoImpl();
	}
	
	@Test
	public void testFindAll(){
		List<Singer> singers = singerDao.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 3);
		log.info("가수목록");
		listSingers(singers);
	}
	
	
	private void listSingers(List<Singer> singers){
		for(Singer singer: singers){
			log.info(singer.toString());
		}
	}

	@Test
	public void testFindAllWidthAlbums() {
		List<Singer> singers = singerDao.findAllWithAlbums();
		assertTrue(singers.size() == 3);
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void testFindbyId() {
		Singer singer = singerDao.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
	}
	
	@Test
	public void testFindByFirstName() {
		List<Singer> singers = singerDao.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		listSingers(singers);
	}

	@Test
	public void testInsertSinger() {
		Singer singer = new Singer();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao.insert(singer);
		List<Singer> singers = singerDao.findAll();
		log.info(">>> 김조한 추가후");
		listSingers(singers);
	}
	
	@Test
	public void testUpdateSinger() {
		Singer singer = singerDao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singer.toString());
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao.update(singer);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singer.toString());
	}

	@Test
	public void testDeleteSinger() {
		singerDao.delete(3);
		List<Singer> singers = singerDao.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers(singers);
	}

	@Test
	public void testInsertSingerWithAlbum() {
		Singer singer = new Singer();
		singer.setFirstName("태원");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1965-04-12"));
		
		Album album = new Album();
		album.setTitle("Never Ending Story");
		album.setReleaseDate(LocalDate.parse("2001-08-31"));
		singer.addAlbum(album);
		
		album = new Album();
		album.setTitle("생각이나");
		album.setReleaseDate(LocalDate.parse("2009-08-14"));
		singer.addAlbum(album);
		
		album = new Album();
		album.setTitle("사랑할수록");
		album.setReleaseDate(LocalDate.parse("1993-11-01"));
		singer.addAlbum(album);
		
		singerDao.insertWithAlbum(singer);
		List<Singer> singers = singerDao.findAllWithAlbums();
		log.info("가수 및 앨범 추가 후 >>>");
		listSingers(singers);
	}
}
