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
import com.linor.singer.dao2.SingerDao2;
import com.linor.singer.domain1.Album1;
import com.linor.singer.domain1.Singer1;
import com.linor.singer.domain2.Album2;
import com.linor.singer.domain2.Singer2;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class SingerDaoTests4 {
	@Autowired
	private SingerDao1 singerDao1;

	@Autowired
	private SingerDao2 singerDao2;
	
	@Test
	public void testFindAll1(){
		List<Singer1> singers1 = singerDao1.findAll();
		assertNotNull(singers1);
		assertTrue(singers1.size() == 4);
		log.info("DB1 가수목록");
		for(Singer1 singer: singers1){
			log.info(singer.toString());
		}	

		List<Singer2> singers2 = singerDao2.findAll();
		assertNotNull(singers2);
		assertTrue(singers2.size() == 4);
		log.info("DB2 가수목록");
		for(Singer2 singer: singers2){
			log.info(singer.toString());
		}	
	}
	
	@Test
	public void testFindNameById() {
		String name = singerDao1.findNameById(1);
		log.info("DB1 종서 김: {}", name );
		assertTrue("1종서 김".equals(name));

		name = singerDao2.findNameById(1);
		log.info("DB2 종서 김: {}", name );
		assertTrue("2종서 김".equals(name));
	}

		
	@Test
	public void testDeleteSinger() {
		singerDao1.delete(3);
		List<Singer1> singers1 = singerDao1.findAll();
		log.info("DB1 가수 삭제 후 가수 목록>>>");
		for(Singer1 singer: singers1){
			log.info(singer.toString());
		}	
		
		singerDao2.delete(3);
		List<Singer2> singers2 = singerDao2.findAll();
		log.info("DB2 가수 삭제 후 가수 목록>>>");
		for(Singer2 singer: singers2){
			log.info(singer.toString());
		}	
	}


	@Test
	public void testFindAllWidthAlbums() {
		List<Singer1> singers1 = singerDao1.findAllWithAlbums();
		log.info("DB1 testFindAllWidthAlbums 가수목록");
		singers1.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers1.size() == 4);
		
		List<Singer2> singers2 = singerDao2.findAllWithAlbums();
		log.info("DB2 testFindAllWidthAlbums 가수목록");
		singers2.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers2.size() == 4);
	}

	@Test
	public void testFindbyId() {
		Singer1 singer1 = singerDao1.findById(1);
		log.info("DB1 주키 검색 결과>>>");
		log.info(singer1.toString());

		Singer2 singer2 = singerDao2.findById(1);
		log.info("DB2 주키 검색 결과>>>");
		log.info(singer2.toString());
	}
	
	@Test
	public void testFindByFirstName() {
		List<Singer1> singers1 = singerDao1.findByFirstName("1종서");
		log.info("DB1 testFindByFirstName 가수목록");
		singers1.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers1.size() == 1);

		List<Singer2> singers2 = singerDao2.findByFirstName("2종서");
		log.info("DB2 testFindByFirstName 가수목록");
		singers2.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers2.size() == 1);
	}

	@Test
	public void testInsertSinger() {
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("조한");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao1.insert(singer1);
		log.info(">>> DB1 김조한 추가후 추가한 가수 내역");
		log.info(singer1.toString());
		List<Singer1> singers1 = singerDao1.findAll();
		log.info(">>> DB1 김조한 추가후 가수목록");
		singers1.forEach(s -> {
			log.info(s.toString());
		});

		Singer2 singer2 = new Singer2();
		singer2.setFirstName("조한");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao2.insert(singer2);
		log.info(">>> DB2 김조한 추가후 추가한 가수 내역");
		log.info(singer2.toString());
		List<Singer2> singers2 = singerDao2.findAll();
		log.info(">>> DB2 김조한 추가후 가수목록");
		singers2.forEach(s -> {
			log.info(s.toString());
		});
	}
	
	@Test
	public void testUpdateSinger() {
		Singer1 singerOldSinger1 = singerDao1.findById(1);
		log.info(">>> DB1 김종서 수정 전 >>>");
		log.info(singerOldSinger1.toString());
		singerOldSinger1.setFirstName("종서");
		singerOldSinger1.setLastName("김");
		singerOldSinger1.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao1.update(singerOldSinger1);
		Singer1 singerNewSinger1 = singerDao1.findById(1);
		log.info(">>> DB1 김종서 수정 후 >>>");
		log.info(singerNewSinger1.toString());

		Singer2 singerOldSinger2 = singerDao2.findById(1);
		log.info(">>> DB2 김종서 수정 전 >>>");
		log.info(singerOldSinger2.toString());
		singerOldSinger2.setFirstName("종서");
		singerOldSinger2.setLastName("김");
		singerOldSinger2.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao2.update(singerOldSinger2);
		Singer2 singerNewSinger2 = singerDao2.findById(1);
		log.info(">>> DB2 김종서 수정 후 >>>");
		log.info(singerNewSinger2.toString());
	}
	
	@Test
	public void testInsertSingerWithAlbum() {
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("태원");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1965-04-12"));
		
		Album1 album1 = new Album1();
		album1.setTitle("Never Ending Story");
		album1.setReleaseDate(LocalDate.parse("2001-08-31"));
		singer1.addAlbum(album1);
		
		album1 = new Album1();
		album1.setTitle("생각이나");
		album1.setReleaseDate(LocalDate.parse("2009-08-14"));
		singer1.addAlbum(album1);
		
		album1 = new Album1();
		album1.setTitle("사랑할수록");
		album1.setReleaseDate(LocalDate.parse("1993-11-01"));
		singer1.addAlbum(album1);
		
		singerDao1.insertWithAlbum(singer1);
		List<Singer1> singers1 = singerDao1.findAllWithAlbums();
		log.info(">>> DB1 김태원 추가후 가수목록");
		singers1.forEach(s -> {
			log.info(s.toString());
		});
	
		Singer2 singer2 = new Singer2();
		singer2.setFirstName("태원");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1965-04-12"));
		
		Album2 album2 = new Album2();
		album2.setTitle("Never Ending Story");
		album2.setReleaseDate(LocalDate.parse("2001-08-31"));
		singer2.addAlbum(album2);
		
		album2 = new Album2();
		album2.setTitle("생각이나");
		album2.setReleaseDate(LocalDate.parse("2009-08-14"));
		singer2.addAlbum(album2);
		
		album2 = new Album2();
		album2.setTitle("사랑할수록");
		album2.setReleaseDate(LocalDate.parse("1993-11-01"));
		singer2.addAlbum(album2);
		
		singerDao2.insertWithAlbum(singer2);
		List<Singer2> singers2 = singerDao2.findAllWithAlbums();
		log.info(">>> DB2 김태원 추가후 가수목록");
		singers2.forEach(s -> {
			log.info(s.toString());
		});
	}

}
