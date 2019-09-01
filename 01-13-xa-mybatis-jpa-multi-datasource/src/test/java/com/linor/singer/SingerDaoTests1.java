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

import com.linor.singer.dao1.SingerDao1;
import com.linor.singer.domain1.Album1;
import com.linor.singer.domain1.Singer1;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class SingerDaoTests1 {
	@Autowired
	private SingerDao1 singerDao1;

	private void listSingers1(List<Singer1> singers){
		for(Singer1 singer: singers){
			log.info(singer.toString());
		}
	}
	
	@Test
	public void testFindAll1(){
		List<Singer1> singers = singerDao1.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		listSingers1(singers);
	}


	@Test
	public void testFindNameById1() {
		String name = singerDao1.findNameById(1);
		assertTrue("1종서 김".equals(name));
	}

	@Test
	public void testDeleteSinger1() {
		singerDao1.delete(3);
		List<Singer1> singers = singerDao1.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers1(singers);
		
	}


	@Test
	public void testFindAllWidthAlbums1() {
		List<Singer1> singers = singerDao1.findAllWithAlbums();
		log.info("테스트 testFindAllWidthAlbums1 >>>");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers.size() == 4);
	}

	@Test
	public void testFindbyId1() {
		Singer1 singer = singerDao1.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
	}

	@Test
	public void testFindByFirstName1() {
		List<Singer1> singers = singerDao1.findByFirstName("1종서");
		assertTrue(singers.size() == 1);
		listSingers1(singers);
	}

	@Test
	public void testInsertSinger1() {
		Singer1 singer = new Singer1();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao1.insert(singer);
		log.info(">>> 김조한 추가후1");
		log.info(singer.toString());
		List<Singer1> singers = singerDao1.findAll();
		log.info(">>> 김조한 추가후2");
		listSingers1(singers);
	}

	@Test
	public void testUpdateSinger1() {
		Singer1 singerOldSinger = singerDao1.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer1 singer = new Singer1();
		singer.setId(1);
		singer.setFirstName("종서");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao1.update(singer);
		Singer1 singerNewSinger = singerDao1.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}
	
	@Test
	public void testInsertSingerWithAlbum1() {
		Singer1 singer = new Singer1();
		singer.setFirstName("태원");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1965-04-12"));
		
		Album1 album = new Album1();
		album.setTitle("Never Ending Story");
		album.setReleaseDate(LocalDate.parse("2001-08-31"));
		singer.addAlbum(album);
		
		album = new Album1();
		album.setTitle("생각이나");
		album.setReleaseDate(LocalDate.parse("2009-08-14"));
		singer.addAlbum(album);
		
		album = new Album1();
		album.setTitle("사랑할수록");
		album.setReleaseDate(LocalDate.parse("1993-11-01"));
		singer.addAlbum(album);
		
		singerDao1.insertWithAlbum(singer);
		List<Singer1> singers = singerDao1.findAllWithAlbums();
		listSingers1(singers);
	}
}
