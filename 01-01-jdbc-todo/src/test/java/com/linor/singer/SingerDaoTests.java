package com.linor.singer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class SingerDaoTests {
	@Autowired
	private SingerDao singerDao;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testFindNameById() {
		String name = singerDao.findNameById(1);
		assertTrue("종서 김".equals(name));
	}

	@Test
	public void testFindAll(){
		List<Singer> singers = singerDao.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		listSingers(singers);
		
		Singer singer = 
				Singer.builder()
				.firstName("길동")
				.lastName("홍")
				.birthDate(LocalDate.parse("1991-01-11"))
				.build();

		singerDao.insert(singer);
		
		singers = singerDao.findAll();
		assertTrue(singers.size() == 5);
		log.info("가수 추가 후 가수 목록");
		listSingers(singers);
		
		singerDao.delete(singer.getId());
		singers = singerDao.findAll();
		assertTrue(singers.size() == 4);
		log.info("가수 삭제 후 가수 목록");
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
		assertTrue(singers.size() == 4);
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}
	
	@Test
	public void testFindByFirstName() {
		List<Singer> singers = singerDao.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		listSingers(singers);
	}

	@Test
	public void testFindbyId() {
		Singer singer = singerDao.findById(1);
		log.info("주키로 1개 레코드 검색 결과>>>");
		log.info(singer.toString());
	}
	
	@Test
	public void testSingerUpdate() {
		Singer singerOldSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer singer = Singer.builder()
				.id(1)
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1977-10-16"))
				.build();
		singerDao.update(singer);
		Singer singerNewSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}
	
	@Test
	public void testInsertSinger() {
		Singer singer = Singer.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		singerDao.insert(singer);
		List<Singer> singers = singerDao.findAll();
		log.info(">>> 김조한 추가후");
		listSingers(singers);
	}
	
	@Test
	public void testInsertSingerWithAlbum() {
		Singer singer = Singer.builder()
				.firstName("태원")
				.lastName("김")
				.birthDate(LocalDate.parse("1965-04-12"))
				.album(Album.builder()
						.title("Never Ending Story")
						.releaseDate(LocalDate.parse("2001-08-31"))
						.build()
						)
				.album(Album.builder()
						.title("생각이나")
						.releaseDate(LocalDate.parse("2009-08-14"))
						.build()
						)
				.album(Album.builder()
						.title("사랑할수록")
						.releaseDate(LocalDate.parse("1993-11-01"))
						.build()
						)
				.build();
		singerDao.insertWithAlbum(singer);
		List<Singer> singers = singerDao.findAllWithAlbums();
		listSingers(singers);
	}
}
