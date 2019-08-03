package com.linor.app;

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

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional("txManager2")
@Slf4j
public class SingerDaoTests2 {
	@Autowired
	private SingerDao2 singerDao2;
	
	private void listSingers2(List<Singer2> singers){
		for(Singer2 singer: singers){
			log.info(singer.toString());
		}
	}

	@Test
	public void testFindAll2(){
		List<Singer2> singers = singerDao2.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		listSingers2(singers);
	}
	
	@Test
	public void testFindNameById2() {
		String name = singerDao2.findNameById(1);
		log.info("종서 이름: {}",name);
		assertTrue("2종서 김".equals(name));
	}
		
	@Test
	public void testDeleteSinger2() {
		singerDao2.delete(3);
		List<Singer2> singers = singerDao2.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers2(singers);
		
	}

	@Test
	public void testFindAllWidthAlbums2() {
		List<Singer2> singers = singerDao2.findAllWithAlbums();
		log.info("testFindAllWidthAlbums2 >>>");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers.size() == 4);
	}

	@Test
	public void testFindbyId2() {
		Singer2 singer = singerDao2.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
	}

	@Test
	public void testFindByFirstName2() {
		List<Singer2> singers = singerDao2.findByFirstName("2종서");
		assertTrue(singers.size() == 1);
		listSingers2(singers);
	}

	@Test
	public void testInsertSinger2() {
		Singer2 singer = new Singer2();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao2.insert(singer);
		log.info(">>> 김조한 추가후1");
		log.info(singer.toString());
		List<Singer2> singers = singerDao2.findAll();
		log.info(">>> 김조한 추가후2");
		listSingers2(singers);
	}

	@Test
	public void testUpdateSinger2() {
		Singer2 singerOldSinger = singerDao2.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer2 singer = new Singer2();
		singer.setId(1);
		singer.setFirstName("종서");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao2.update(singer);
		Singer2 singerNewSinger = singerDao2.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}

	@Test
	public void testInsertSingerWithAlbum2() {
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
		
		singerDao2.insertWithAlbum(singer);
		List<Singer2> singers = singerDao2.findAllWithAlbums();
		listSingers2(singers);
	}
}
