package com.linor.singer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SingerDaoTests {
	@Autowired
	private SingerDao singerDao;
	
	@Before
	public void setUp() {
		//singerDao = new SingerDaoImpl();
	}
	
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
		
		Singer singer = new Singer();
		singer.setFirstName("길동");
		singer.setLastName("홍");
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
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
		Singer singer = new Singer();
		singer.setId(1);
		singer.setFirstName("종서");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao.update(singer);
		Singer singerNewSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}
}