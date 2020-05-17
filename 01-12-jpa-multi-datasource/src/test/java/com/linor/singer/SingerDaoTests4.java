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

import com.linor.singer.dao1.SingerDao1;
import com.linor.singer.dao2.SingerDao2;
import com.linor.singer.domain1.Album1;
import com.linor.singer.domain1.Singer1;
import com.linor.singer.domain1.SingerSummary1;
import com.linor.singer.domain2.Album2;
import com.linor.singer.domain2.Singer2;
import com.linor.singer.domain2.SingerSummary2;

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
	public void test101FindAll(){
		log.info("테스트101");
		List<Singer1> singers1 = singerDao1.findAll();
		assertNotNull(singers1);
		assertTrue(singers1.size() == 3);
		listSingers1(singers1);

		log.info("테스트201");
		List<Singer2> singers2 = singerDao2.findAll();
		assertNotNull(singers2);
		log.info("가수목록");
		listSingers2(singers2);
		assertTrue(singers2.size() == 3);
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
	
	@Test
	public void test102FindAllByNativeQuery() {
		log.info("테스트102");
		log.info("네이티브 쿼리 실행 결과");
		listSingers1(singerDao1.findAllByNativeQuery());

		log.info("테스트202");
		log.info("네이티브 쿼리 실행 결과");
		listSingers2(singerDao2.findAllByNativeQuery());
	}
	
	@Test
	public void test103FindAllWidthAlbums() {
		log.info("테스트103");
		List<Singer1> singers1 = singerDao1.findAllWithAlbums();
		assertTrue(singers1.size() == 3);
		singers1.forEach(singer1 -> {
			log.info(singer1.toString());
			singer1.getAlbums().forEach(album1 -> {
				log.info("앨범 >>> " + album1.toString());
			});
		});

		log.info("테스트203");
		List<Singer2> singers2 = singerDao2.findAllWithAlbums();
		assertTrue(singers2.size() == 3);
		singers2.forEach(singer2 -> {
			log.info(singer2.toString());
			singer2.getAlbums().forEach(album2 -> {
				log.info("앨범 >>> " + album2.toString());
			});
		});
	}
	
	@Test
	public void test104FindbyId() {
		log.info("테스트104");
		Singer1 singer1 = singerDao1.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer1.toString());
		singer1.getAlbums().forEach(album1 -> {
			log.info("앨범 >>> " + album1.toString());
		});

		log.info("테스트204");
		Singer2 singer2 = singerDao2.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer2.toString());
		singer2.getAlbums().forEach(album2 -> {
			log.info("앨범 >>> " + album2.toString());
		});
	}
	
	@Test
	public void test105FindByFirstName() {
		log.info("테스트105");
		List<Singer1> singers1 = singerDao1.findByFirstName("종서");
		assertTrue(singers1.size() == 1);
		listSingers1(singers1);

		log.info("테스트205");
		List<Singer2> singers2 = singerDao2.findByFirstName("종서");
		assertTrue(singers2.size() == 1);
		listSingers2(singers2);
	}

	@Test
	public void test106ListSingersSummary() {
		log.info("테스트106");
		List<SingerSummary1> singers1 = singerDao1.listAllSingersSummary();
		listSingerSummary1(singers1);
		assertEquals(2, singers1.size());
		
		log.info("테스트206");
		List<SingerSummary2> singers2 = singerDao2.listAllSingersSummary();
		listSingerSummary2(singers2);
		assertEquals(2, singers2.size());
	}
	
	private void listSingerSummary1(List<SingerSummary1> singers) {
		log.info("--- 가수 요약 리스트 : ");
		for(SingerSummary1 singer : singers) {
			log.info(singer.toString());
		}
	}

	private void listSingerSummary2(List<SingerSummary2> singers) {
		log.info("--- 가수 요약 리스트 : ");
		for(SingerSummary2 singer : singers) {
			log.info(singer.toString());
		}
	}

	@Test
	public void test107InsertSinger() {
		log.info("테스트107");
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("조한");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao1.insert(singer1);
		List<Singer1> singers1 = singerDao1.findAll();
		log.info(">>> 김조한 추가후");
		listSingers1(singers1);

		log.info("테스트207");
		Singer2 singer2 = new Singer2();
		singer2.setFirstName("조한");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao2.insert(singer2);
		List<Singer2> singers2 = singerDao2.findAll();
		log.info(">>> 김조한 추가후");
		listSingers2(singers2);
	}
	
	@Test
	public void test108UpdateSinger() {
		log.info("테스트108");
		Singer1 singerOldSinger1 = singerDao1.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger1.toString());
		singerOldSinger1.setFirstName("종서");
		singerOldSinger1.setLastName("김");
		singerOldSinger1.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao1.update(singerOldSinger1);
		Singer1 singerNewSinger1 = singerDao1.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger1.toString());

		log.info("테스트208");
		Singer2 singerOldSinger2 = singerDao2.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger2.toString());
		singerOldSinger2.setFirstName("종서");
		singerOldSinger2.setLastName("김");
		singerOldSinger2.setBirthDate(LocalDate.parse("1977-10-16"));
		singerDao2.update(singerOldSinger2);
		Singer2 singerNewSinger2 = singerDao2.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger2.toString());
	}

	@Test
	public void test109DeleteSinger() {
		log.info("테스트109");
		singerDao1.delete(3);
		List<Singer1> singers1 = singerDao1.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers1(singers1);

		log.info("테스트209");
		singerDao2.delete(3);
		List<Singer2> singers2 = singerDao2.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers2(singers2);
	}

	@Test
	public void test110InsertSingerWithAlbum() {
		log.info("테스트110");
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
		log.info("가수 및 앨범 추가 후 >>>");
		listSingers1(singers1);

		log.info("테스트210");
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
		log.info("가수 및 앨범 추가 후 >>>");
		listSingers2(singers2);
	}
}
