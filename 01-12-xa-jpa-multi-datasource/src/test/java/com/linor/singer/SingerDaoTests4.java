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
public class SingerDaoTests4 {
	@Autowired
	private SingerDao1 singerDao1;

	@Autowired
	private SingerDao2 singerDao2;

	@Test
	public void testFindAll(){
		log.info("testFindAll1---->>");
		List<Singer1> singers1 = singerDao1.findAll();
		assertNotNull(singers1);
		assertTrue(singers1.size() == 4);
		log.info("가수목록");
		listSingers1(singers1);

		log.info("testFindAll2---->>");
		List<Singer2> singers2 = singerDao2.findAll();
		assertNotNull(singers2);
		assertTrue(singers2.size() == 4);
		log.info("가수목록");
		listSingers2(singers2);
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
	public void testFindAllByNativeQuery() {
		log.info("testFindAllByNativeQuery1---->>");
		List<Singer1> singers1 = singerDao1.findAllByNativeQuery();
		log.info("네이티브 쿼리 실행 결과");
		singers1.forEach(singer1 -> {
			log.info(singer1.toString());
		});
		assertTrue(singers1.size() == 4);

		log.info("testFindAllByNativeQuery2---->>");
		List<Singer2> singers2 = singerDao2.findAllByNativeQuery();
		log.info("네이티브 쿼리 실행 결과");
		singers2.forEach(singer2 -> {
			log.info(singer2.toString());
		});
		assertTrue(singers2.size() == 4);
	}

	@Test
	public void testFindAllWidthAlbums() {
		log.info("testFindAllWidthAlbums1---->>");
		List<Singer1> singers1 = singerDao1.findAllWithAlbums();
		assertTrue(singers1.size() == 4);
		singers1.forEach(singer1 -> {
			log.info(singer1.toString());
		});

		log.info("testFindAllWidthAlbums2---->>");
		List<Singer2> singers2 = singerDao2.findAllWithAlbums();
		assertTrue(singers2.size() == 4);
		singers2.forEach(singer2 -> {
			log.info(singer2.toString());
		});
	}

	@Test
	public void testFindbyId() {
		log.info("testFindbyId1---->>");
		Singer1 singer1 = singerDao1.findById(1);
		log.info(singer1.toString());
		assertEquals("종서", singer1.getFirstName());

		log.info("testFindbyId2---->>");
		Singer2 singer2 = singerDao2.findById(1);
		log.info(singer2.toString());
		assertEquals("종서", singer2.getFirstName());
	}

	@Test
	public void testFindByFirstName() {
		log.info("testFindByFirstName1---->>");
		List<Singer1> singers1 = singerDao1.findByFirstName("종서");
		listSingers1(singers1);
		assertTrue(singers1.size() == 1);
		
		log.info("testFindByFirstName2---->>");
		List<Singer2> singers2 = singerDao2.findByFirstName("종서");
		listSingers2(singers2);
		assertTrue(singers2.size() == 1);
	}

	@Test
	public void testFindByFirstNameAndLastName() {
		log.info("findByFirstNameAndLastName1---->>");
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("종서");
		singer1.setLastName("김");
		List<Singer1> singers1 = singerDao1.findByFirstNameAndLastName(singer1);
		singers1.forEach(s1 -> {
			log.info(s1.toString());
		});
		assertTrue(singers1.size() == 1);
		
		log.info("findByFirstNameAndLastName2---->>");
		Singer2 singer2 = new Singer2();
		singer2.setFirstName("종서");
		singer2.setLastName("김");
		List<Singer2> singers2 = singerDao2.findByFirstNameAndLastName(singer2);
		singers2.forEach(s2 -> {
			log.info(s2.toString());
		});
		assertTrue(singers2.size() == 1);
	}

	@Test
	public void testFindByTitle() {
		log.info("testFindByTitle1---->>");
		List<Album1> albums1 = singerDao1.findAlbumsByTitle("황혼의");
		assertTrue(albums1.size() > 0);
		albums1.forEach(a1 -> log.info(a1.toString() + ", Singer: " + a1.getSinger().toString()));
		assertEquals(1, albums1.size());
		
		log.info("testFindByTitle2---->>");
		List<Album2> albums2 = singerDao2.findAlbumsByTitle("황혼의");
		assertTrue(albums2.size() > 0);
		albums2.forEach(a2 -> log.info(a2.toString() + ", Singer: " + a2.getSinger().toString()));
		assertEquals(1, albums2.size());
	}
	
	@Test
	public void testInsertSinger() {
		log.info("testInsertSinger1---->>");
		List<Singer1> oldSingers1 = singerDao1.findAllWithAlbums();
		log.info(">>> 김조한 추가전");
		listSingers1(oldSingers1);
		
		Singer1 singer1 = Singer1.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		singerDao1.insert(singer1);
		List<Singer1> newSingers1 = singerDao1.findAllWithAlbums();
		log.info(">>> 김조한 추가후");
		listSingers1(newSingers1);
		assertEquals(oldSingers1.size() + 1 , newSingers1.size());
		
		log.info("testInsertSinger2---->>");
		List<Singer2> oldSingers2 = singerDao2.findAllWithAlbums();
		log.info(">>> 김조한 추가전");
		listSingers2(oldSingers2);
		
		Singer2 singer2 = Singer2.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		singerDao2.insert(singer2);
		List<Singer2> newSingers2 = singerDao2.findAllWithAlbums();
		log.info(">>> 김조한 추가후");
		listSingers2(newSingers2);
		assertEquals(oldSingers2.size() + 1 , newSingers2.size());
	}

	@Test
	public void testUpdateSinger() {
		log.info("testUpdateSinger1---->>");
		Singer1 singerOldSinger1 = singerDao1.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger1.toString());
		Singer1 singer1 = Singer1.builder()
				.id(1)
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1977-10-16"))
				.build();
		singerDao1.update(singer1);
		Singer1 singerNewSinger1 = singerDao1.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger1.toString());
		assertEquals(LocalDate.parse("1977-10-16"), singerNewSinger1.getBirthDate());
		
		log.info("testUpdateSinger2---->>");
		Singer2 singerOldSinger2 = singerDao2.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger2.toString());
		Singer2 singer2 = Singer2.builder()
				.id(1)
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1977-10-16"))
				.build();
		singerDao2.update(singer2);
		Singer2 singerNewSinger2 = singerDao2.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger2.toString());
		assertEquals(LocalDate.parse("1977-10-16"), singerNewSinger2.getBirthDate());
	}

	@Test
	public void testDeleteSinger() {
		log.info("testDeleteSinger1---->>");
		List<Singer1> oldSingers1 = singerDao1.findAllWithAlbums();
		singerDao1.delete(3);
		List<Singer1> singers1 = singerDao1.findAllWithAlbums();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers1(singers1);
		assertEquals(oldSingers1.size() - 1, singers1.size());
		
		log.info("testDeleteSinger2---->>");
		List<Singer2> oldSingers2 = singerDao2.findAllWithAlbums();
		singerDao2.delete(3);
		List<Singer2> singers2 = singerDao2.findAllWithAlbums();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers2(singers2);
		assertEquals(oldSingers2.size() - 1, singers2.size());
	}
	
	@Test
	public void testInsertSingerWithAlbum() {
		log.info("testInsertSingerWithAlbum1---->>");
		List<Singer1> oldSingers1 = singerDao1.findAllWithAlbums();
		Singer1 singer1 = Singer1.builder()
				.firstName("태원")
				.lastName("김")
				.birthDate(LocalDate.parse("1965-04-12"))
				.albums(new HashSet<Album1>())
				.build();
		Set<Album1> ablums1 = singer1.getAlbums();
				ablums1.add(Album1.builder()
						.title("Never Ending Story")
						.releaseDate(LocalDate.parse("2001-08-31"))
						.build()
						);
				ablums1.add(Album1.builder()
						.title("생각이나")
						.releaseDate(LocalDate.parse("2009-08-14"))
						.build()
						);
				ablums1.add(Album1.builder()
						.title("사랑할수록")
						.releaseDate(LocalDate.parse("1993-11-01"))
						.build()
						);
		singerDao1.insertWithAlbum(singer1);
		List<Singer1> singers1 = singerDao1.findAllWithAlbums();
		listSingers1(singers1);
		assertEquals(oldSingers1.size() + 1, singers1.size());
		
		log.info("testInsertSingerWithAlbum2---->>");
		List<Singer2> oldSingers2 = singerDao2.findAllWithAlbums();
		Singer2 singer2 = Singer2.builder()
				.firstName("태원")
				.lastName("김")
				.birthDate(LocalDate.parse("1965-04-12"))
				.albums(new HashSet<Album2>())
				.build();
		Set<Album2> ablums2 = singer2.getAlbums();
				ablums2.add(Album2.builder()
						.title("Never Ending Story")
						.releaseDate(LocalDate.parse("2001-08-31"))
						.build()
						);
				ablums2.add(Album2.builder()
						.title("생각이나")
						.releaseDate(LocalDate.parse("2009-08-14"))
						.build()
						);
				ablums2.add(Album2.builder()
						.title("사랑할수록")
						.releaseDate(LocalDate.parse("1993-11-01"))
						.build()
						);
		singerDao2.insertWithAlbum(singer2);
		List<Singer2> singers2 = singerDao2.findAllWithAlbums();
		listSingers2(singers2);
		assertEquals(oldSingers2.size() + 1, singers2.size());
	}

	@Test
	public void testListSingersSummary() {
		log.info("testListSingersSummary1---->>");
		List<SingerSummary1> singers1 = singerDao1.listAllSingersSummary();
		listSingerSummary1(singers1);
		assertEquals(2, singers1.size());
		
		log.info("testListSingersSummary2---->>");
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

}
