package com.linor.singer;

import static org.junit.Assert.assertEquals;
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

import com.linor.singer.dao2.SingerDao2;
import com.linor.singer.domain2.Album2;
import com.linor.singer.domain2.Singer2;
import com.linor.singer.domain2.SingerSummary2;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional("transactionManager2")
public class SingerDaoTests2 {
	@Autowired
	private SingerDao2 singerDao;
	
	@Test
	public void testFindAll(){
		List<Singer2> singers = singerDao.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 3);
		log.info("가수목록");
		listSingers(singers);
	}
	
	
	private void listSingers(List<Singer2> singers){
		for(Singer2 singer: singers){
			log.info(singer.toString());
		}
	}
	
	@Test
	public void testFindAllByNativeQuery() {
		log.info("네이티브 쿼리 실행 결과");
		listSingers(singerDao.findAllByNativeQuery());
	}
	
	@Test
	public void testFindAllWidthAlbums() {
		List<Singer2> singers = singerDao.findAllWithAlbums();
		assertTrue(singers.size() == 3);
		singers.forEach(singer -> {
			log.info(singer.toString());
			singer.getAlbums().forEach(album -> {
				log.info("앨범 >>> " + album.toString());
			});
		});
	}

	@Test
	public void testFindbyId() {
		Singer2 singer = singerDao.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
		singer.getAlbums().forEach(album -> {
			log.info("앨범 >>> " + album.toString());
		});
	}
	
	@Test
	public void testFindByFirstName() {
		List<Singer2> singers = singerDao.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		listSingers(singers);
	}

	@Test
	public void testListSingersSummary() {
		List<SingerSummary2> singers = singerDao.listAllSingersSummary();
		listSingerSummary(singers);
		assertEquals(2, singers.size());
		
	}
	
	private void listSingerSummary(List<SingerSummary2> singers) {
		log.info("--- 가수 요약 리스트 : ");
		for(SingerSummary2 singer : singers) {
			log.info(singer.toString());
		}
	}

	@Test
	public void testInsertSinger() {
		Singer2 singer = new Singer2();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao.insert(singer);
		List<Singer2> singers = singerDao.findAll();
		log.info(">>> 김조한 추가후");
		listSingers(singers);
	}
	
	@Test
	public void testUpdateSinger() {
		Singer2 singerOldSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		singerOldSinger.setFirstName("종서");
		singerOldSinger.setLastName("김");
		singerOldSinger.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao.update(singerOldSinger);
		Singer2 singerNewSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}

	@Test
	public void testDeleteSinger() {
		singerDao.delete(3);
		List<Singer2> singers = singerDao.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers(singers);
	}

	@Test
	public void testInsertSingerWithAlbum() {
		Singer2 singer = new Singer2();
		singer.setFirstName("태원");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1965-04-12"));
		
		Album2 album = new Album2();
		album.setTitle("Never Ending Story");
		album.setReleaseDate(LocalDate.parse("2001-08-31"));
		singer.addAlbum(album);
		
		album = new Album2();
		album.setTitle("생각이나");
		album.setReleaseDate(LocalDate.parse("2009-08-14"));
		singer.addAlbum(album);
		
		album = new Album2();
		album.setTitle("사랑할수록");
		album.setReleaseDate(LocalDate.parse("1993-11-01"));
		singer.addAlbum(album);
		
		singerDao.insertWithAlbum(singer);
		List<Singer2> singers = singerDao.findAllWithAlbums();
		log.info("가수 및 앨범 추가 후 >>>");
		listSingers(singers);
	}
}
