package com.linor.singer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
@Slf4j
@Transactional
public class SingerDaoTests3 {
	@Autowired
	private SingerDao1 singerDao1;

	@Autowired
	private SingerDao2 singerDao2;

	@Test
	public void testFindAll1(){
		log.info("testFindAll1---->>");
		List<Singer1> singers = singerDao1.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		listSingers1(singers);
	}
	
	private void listSingers1(List<Singer1> singers){
		for(Singer1 singer: singers){
			log.info(singer.toString());
		}
	}

	@Test
	public void testFindAll2(){
		log.info("testFindAll2---->>");
		List<Singer2> singers = singerDao2.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		listSingers2(singers);
	}
	
	private void listSingers2(List<Singer2> singers){
		for(Singer2 singer: singers){
			log.info(singer.toString());
		}
	}

	@Test
	public void testFindAllByNativeQuery1() {
		log.info("testFindAllByNativeQuery1---->>");
		List<Singer1> singers = singerDao1.findAllByNativeQuery();
		log.info("네이티브 쿼리 실행 결과");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers.size() == 4);
	}

	@Test
	public void testFindAllByNativeQuery2() {
		log.info("testFindAllByNativeQuery2---->>");
		List<Singer2> singers = singerDao2.findAllByNativeQuery();
		log.info("네이티브 쿼리 실행 결과");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers.size() == 4);
	}

	@Test
	public void testFindAllWidthAlbums1() {
		log.info("testFindAllWidthAlbums1---->>");
		List<Singer1> singers = singerDao1.findAllWithAlbums();
		assertTrue(singers.size() == 4);
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void testFindAllWidthAlbums2() {
		log.info("testFindAllWidthAlbums2---->>");
		List<Singer2> singers = singerDao2.findAllWithAlbums();
		assertTrue(singers.size() == 4);
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void testFindbyId1() {
		log.info("testFindbyId1---->>");
		Singer1 singer = singerDao1.findById(1);
		log.info(singer.toString());
		assertEquals("종서", singer.getFirstName());
	}

	@Test
	public void testFindbyId2() {
		log.info("testFindbyId2---->>");
		Singer2 singer = singerDao2.findById(1);
		log.info(singer.toString());
		assertEquals("종서", singer.getFirstName());
	}

	@Test
	public void testFindByFirstName1() {
		log.info("testFindByFirstName1---->>");
		List<Singer1> singers = singerDao1.findByFirstName("종서");
		listSingers1(singers);
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByFirstName2() {
		log.info("testFindByFirstName2---->>");
		List<Singer2> singers = singerDao2.findByFirstName("종서");
		listSingers2(singers);
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByFirstNameAndLastName1() {
		log.info("findByFirstNameAndLastName1---->>");
		Singer1 singer = new Singer1();
		singer.setFirstName("종서");
		singer.setLastName("김");
		List<Singer1> singers = singerDao1.findByFirstNameAndLastName(singer);
		singers.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByFirstNameAndLastName2() {
		log.info("findByFirstNameAndLastName2---->>");
		Singer2 singer = new Singer2();
		singer.setFirstName("종서");
		singer.setLastName("김");
		List<Singer2> singers = singerDao2.findByFirstNameAndLastName(singer);
		singers.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByTitle1() {
		log.info("testFindByTitle1---->>");
		List<Album1> albums = singerDao1.findAlbumsByTitle("황혼의");
		assertTrue(albums.size() > 0);
		albums.forEach(a -> log.info(a.toString() + ", Singer: " + a.getSinger().toString()));
		assertEquals(1, albums.size());
	}
	
	@Test
	public void testFindByTitle2() {
		log.info("testFindByTitle2---->>");
		List<Album2> albums = singerDao2.findAlbumsByTitle("황혼의");
		assertTrue(albums.size() > 0);
		albums.forEach(a -> log.info(a.toString() + ", Singer: " + a.getSinger().toString()));
		assertEquals(1, albums.size());
	}

	@Test
	public void testInsertSinger1() {
		log.info("testInsertSinger1---->>");
		List<Singer1> oldSingers = singerDao1.findAllWithAlbums();
		log.info(">>> 김조한 추가전");
		listSingers1(oldSingers);
		
		Singer1 singer = Singer1.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		singerDao1.insert(singer);
		List<Singer1> newSingers = singerDao1.findAllWithAlbums();
		log.info(">>> 김조한 추가후");
		listSingers1(newSingers);
		assertEquals(oldSingers.size() + 1 , newSingers.size());
	}
	
	@Test
	public void testInsertSinger2() {
		log.info("testInsertSinger2---->>");
		List<Singer2> oldSingers = singerDao2.findAllWithAlbums();
		log.info(">>> 김조한 추가전");
		listSingers2(oldSingers);
		
		Singer2 singer = Singer2.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		singerDao2.insert(singer);
		List<Singer2> newSingers = singerDao2.findAllWithAlbums();
		log.info(">>> 김조한 추가후");
		listSingers2(newSingers);
		assertEquals(oldSingers.size() + 1 , newSingers.size());
	}

	@Test
	public void testUpdateSinger1() {
		log.info("testUpdateSinger1---->>");
		Singer1 singerOldSinger = singerDao1.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer1 singer = Singer1.builder()
				.id(1)
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1977-10-16"))
				.build();
		singerDao1.update(singer);
		Singer1 singerNewSinger = singerDao1.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
		assertEquals(LocalDate.parse("1977-10-16"), singerNewSinger.getBirthDate());
	}

	@Test
	public void testUpdateSinger2() {
		log.info("testUpdateSinger2---->>");
		Singer2 singerOldSinger = singerDao2.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer2 singer = Singer2.builder()
				.id(1)
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1977-10-16"))
				.build();
		singerDao2.update(singer);
		Singer2 singerNewSinger = singerDao2.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
		assertEquals(LocalDate.parse("1977-10-16"), singerNewSinger.getBirthDate());
	}

	@Test
	public void testDeleteSinger1() {
		log.info("testDeleteSinger1---->>");
		List<Singer1> oldSingers = singerDao1.findAllWithAlbums();
		singerDao1.delete(3);
		List<Singer1> singers = singerDao1.findAllWithAlbums();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers1(singers);
		assertEquals(oldSingers.size() - 1, singers.size());
	}
	
	@Test
	public void testDeleteSinger2() {
		log.info("testDeleteSinger2---->>");
		List<Singer2> oldSingers = singerDao2.findAllWithAlbums();
		singerDao2.delete(3);
		List<Singer2> singers = singerDao2.findAllWithAlbums();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers2(singers);
		assertEquals(oldSingers.size() - 1, singers.size());
	}

	@Test
	public void testInsertSingerWithAlbum1() {
		log.info("testInsertSingerWithAlbum1---->>");
		List<Singer1> oldSingers = singerDao1.findAllWithAlbums();
		Singer1 singer = Singer1.builder()
				.firstName("태원")
				.lastName("김")
				.birthDate(LocalDate.parse("1965-04-12"))
				.albums(new HashSet<Album1>())
				.build();
		Set<Album1> ablums = singer.getAlbums();
				ablums.add(Album1.builder()
						.title("Never Ending Story")
						.releaseDate(LocalDate.parse("2001-08-31"))
						.build()
						);
				ablums.add(Album1.builder()
						.title("생각이나")
						.releaseDate(LocalDate.parse("2009-08-14"))
						.build()
						);
				ablums.add(Album1.builder()
						.title("사랑할수록")
						.releaseDate(LocalDate.parse("1993-11-01"))
						.build()
						);
		singerDao1.insertWithAlbum(singer);
		List<Singer1> singers = singerDao1.findAllWithAlbums();
		listSingers1(singers);
		assertEquals(oldSingers.size() + 1, singers.size());
	}
	
	@Test
	public void testInsertSingerWithAlbum2() {
		log.info("testInsertSingerWithAlbum2---->>");
		List<Singer2> oldSingers = singerDao2.findAllWithAlbums();
		Singer2 singer = Singer2.builder()
				.firstName("태원")
				.lastName("김")
				.birthDate(LocalDate.parse("1965-04-12"))
				.albums(new HashSet<Album2>())
				.build();
		Set<Album2> ablums = singer.getAlbums();
				ablums.add(Album2.builder()
						.title("Never Ending Story")
						.releaseDate(LocalDate.parse("2001-08-31"))
						.build()
						);
				ablums.add(Album2.builder()
						.title("생각이나")
						.releaseDate(LocalDate.parse("2009-08-14"))
						.build()
						);
				ablums.add(Album2.builder()
						.title("사랑할수록")
						.releaseDate(LocalDate.parse("1993-11-01"))
						.build()
						);
		singerDao2.insertWithAlbum(singer);
		List<Singer2> singers = singerDao2.findAllWithAlbums();
		listSingers2(singers);
		assertEquals(oldSingers.size() + 1, singers.size());
	}

	@Test
	public void testListSingersSummary1() {
		log.info("testListSingersSummary1---->>");
		List<SingerSummary1> singers = singerDao1.listAllSingersSummary();
		listSingerSummary1(singers);
		assertEquals(2, singers.size());
	}
	
	private void listSingerSummary1(List<SingerSummary1> singers) {
		log.info("--- 가수 요약 리스트 : ");
		for(SingerSummary1 singer : singers) {
			log.info(singer.toString());
		}
	}

	@Test
	public void testListSingersSummary2() {
		log.info("testListSingersSummary2---->>");
		List<SingerSummary2> singers = singerDao2.listAllSingersSummary();
		listSingerSummary2(singers);
		assertEquals(2, singers.size());
	}
	
	private void listSingerSummary2(List<SingerSummary2> singers) {
		log.info("--- 가수 요약 리스트 : ");
		for(SingerSummary2 singer : singers) {
			log.info(singer.toString());
		}
	}

}
