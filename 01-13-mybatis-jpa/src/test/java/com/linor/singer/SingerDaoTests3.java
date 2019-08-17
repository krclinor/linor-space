package com.linor.singer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import com.linor.singer.domain2.SingerSummary2;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SingerDaoTests3 {
	@Autowired
	private SingerDao1 singerDao1;

	@Autowired
	private SingerDao2 singerDao2;
	
	@Test
	public void test101FindAll(){
		log.info("테스트101");
		List<Singer1> singers = singerDao1.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		listSingers1(singers);
	}
	
	@Test
	public void test201FindAll(){
		log.info("테스트201");
		List<Singer2> singers = singerDao2.findAll();
		assertNotNull(singers);
		log.info("가수목록");
		listSingers2(singers);
		assertTrue(singers.size() == 3);
	}
	
	private void listSingers1(List<Singer1> singers){
		for(Singer1 singer: singers){
			log.info(singer.toString());
		}
	}

	private void listSingers2(List<Singer2> singers){
		for(Singer2 singer: singers){
			log.info(singer.toString());
		}
	}
	
//	@Test
//	public void test102FindAllByNativeQuery() {
//		log.info("테스트102");
//		log.info("네이티브 쿼리 실행 결과");
//		listSingers1(singerDao1.findAllByNativeQuery());
//	}
	
	@Test
	public void test202FindAllByNativeQuery() {
		log.info("테스트202");
		log.info("네이티브 쿼리 실행 결과");
		listSingers2(singerDao2.findAllByNativeQuery());
	}
	
	@Test
	public void test103FindAllWidthAlbums() {
		log.info("테스트103");
		List<Singer1> singers = singerDao1.findAllWithAlbums();
		assertTrue(singers.size() == 4);
		singers.forEach(singer -> {
			log.info(singer.toString());
			singer.getAlbums().forEach(album -> {
				log.info("앨범 >>> " + album.toString());
			});
		});
	}
	
	@Test
	public void test203FindAllWidthAlbums() {
		log.info("테스트203");
		List<Singer2> singers = singerDao2.findAllWithAlbums();
		assertTrue(singers.size() == 3);
		singers.forEach(singer -> {
			log.info(singer.toString());
			singer.getAlbums().forEach(album -> {
				log.info("앨범 >>> " + album.toString());
			});
		});
	}

	@Test
	public void test104FindbyId() {
		log.info("테스트104");
		Singer1 singer = singerDao1.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
	}
	
	@Test
	public void test204FindbyId() {
		log.info("테스트204");
		Singer2 singer = singerDao2.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
		singer.getAlbums().forEach(album -> {
			log.info("앨범 >>> " + album.toString());
		});
	}

	@Test
	public void test105FindByFirstName() {
		log.info("테스트105");
		List<Singer1> singers = singerDao1.findByFirstName("1종서");
		assertTrue(singers.size() == 1);
		listSingers1(singers);
	}

	@Test
	public void test205FindByFirstName() {
		log.info("테스트205");
		List<Singer2> singers = singerDao2.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		listSingers2(singers);
	}

//	@Test
//	public void test106ListSingersSummary() {
//		log.info("테스트106");
//		List<SingerSummary1> singers = singerDao1.listAllSingersSummary();
//		listSingerSummary1(singers);
//		assertEquals(2, singers.size());
//		
//	}
	
	@Test
	public void test206ListSingersSummary() {
		log.info("테스트206");
		List<SingerSummary2> singers = singerDao2.listAllSingersSummary();
		listSingerSummary2(singers);
		assertEquals(2, singers.size());
		
	}

//	private void listSingerSummary1(List<SingerSummary1> singers) {
//		log.info("--- 가수 요약 리스트 : ");
//		for(SingerSummary1 singer : singers) {
//			log.info(singer.toString());
//		}
//	}

	private void listSingerSummary2(List<SingerSummary2> singers) {
		log.info("--- 가수 요약 리스트 : ");
		for(SingerSummary2 singer : singers) {
			log.info(singer.toString());
		}
	}

	@Test
	public void test107InsertSinger() {
		log.info("테스트107");
		Singer1 singer = new Singer1();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao1.insert(singer);
		List<Singer1> singers = singerDao1.findAll();
		log.info(">>> 김조한 추가후");
		listSingers1(singers);
	}
	
	@Test
	@Transactional("transactionManager2")
	public void test207InsertSinger() {
		log.info("테스트207");
		Singer2 singer = new Singer2();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao2.insert(singer);
		List<Singer2> singers = singerDao2.findAll();
		log.info(">>> 김조한 추가후");
		listSingers2(singers);
	}
	@Test
	public void test108UpdateSinger() {
		log.info("테스트108");
		Singer1 singerOldSinger = singerDao1.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		singerOldSinger.setFirstName("종서");
		singerOldSinger.setLastName("김");
		singerOldSinger.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao1.update(singerOldSinger);
		Singer1 singerNewSinger = singerDao1.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}

	@Test
	@Transactional("transactionManager2")
	public void test208UpdateSinger() {
		log.info("테스트208");
		Singer2 singerOldSinger = singerDao2.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		singerOldSinger.setFirstName("종서");
		singerOldSinger.setLastName("김");
		singerOldSinger.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao2.update(singerOldSinger);
		Singer2 singerNewSinger = singerDao2.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}
	@Test
	public void test109DeleteSinger() {
		log.info("테스트109");
		singerDao1.delete(3);
		List<Singer1> singers = singerDao1.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers1(singers);
	}

	@Test
	@Transactional("transactionManager2")
	public void test209DeleteSinger() {
		log.info("테스트209");
		singerDao2.delete(3);
		List<Singer2> singers = singerDao2.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers2(singers);
	}
	@Test
	public void test110InsertSingerWithAlbum() {
		log.info("테스트110");
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
		log.info("가수 및 앨범 추가 후 >>>");
		listSingers1(singers);
	}
	
	@Test
	@Transactional("transactionManager2")
	public void test210InsertSingerWithAlbum() {
		log.info("테스트210");
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
		log.info("가수 및 앨범 추가 후 >>>");
		listSingers2(singers);
	}
}
