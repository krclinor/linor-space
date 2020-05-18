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

import com.linor.singer.dao2.SingerDao2;
import com.linor.singer.domain2.Album2;
import com.linor.singer.domain2.Singer2;
import com.linor.singer.domain2.SingerSummary2;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional("txManager2")
public class SingerDaoTests2 {
	@Autowired
	private SingerDao2 singerDao;
	
	@Test
	public void testFindAll(){
		log.info("testFindAll---->>");
		List<Singer2> singers = singerDao.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
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
		log.info("testFindAllByNativeQuery---->>");
		List<Singer2> singers = singerDao.findAllByNativeQuery();
		log.info("네이티브 쿼리 실행 결과");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
		assertTrue(singers.size() == 4);
	}

	@Test
	public void testFindAllWidthAlbums() {
		log.info("testFindAllWidthAlbums---->>");
		List<Singer2> singers = singerDao.findAllWithAlbums();
		assertTrue(singers.size() == 4);
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void testFindbyId() {
		log.info("testFindbyId---->>");
		Singer2 singer = singerDao.findById(1);
		log.info(singer.toString());
		assertEquals("종서", singer.getFirstName());
	}
	
	@Test
	public void testFindByFirstName() {
		log.info("testFindByFirstName---->>");
		List<Singer2> singers = singerDao.findByFirstName("종서");
		listSingers(singers);
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByFirstNameAndLastName() {
		log.info("findByFirstNameAndLastName----");
		Singer2 singer = new Singer2();
		singer.setFirstName("종서");
		singer.setLastName("김");
		List<Singer2> singers = singerDao.findByFirstNameAndLastName(singer);
		singers.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers.size() == 1);
	}

	@Test
	public void testFindByTitle() {
		log.info("testFindByTitle----");
		List<Album2> albums = singerDao.findAlbumsByTitle("황혼의");
		assertTrue(albums.size() > 0);
		albums.forEach(a -> log.info(a.toString() + ", Singer: " + a.getSinger().toString()));
		assertEquals(1, albums.size());
	}
	
	@Test
	public void testInsertSinger() {
		log.info("testInsertSinger---->>");
		List<Singer2> oldSingers = singerDao.findAllWithAlbums();
		log.info(">>> 김조한 추가전");
		listSingers(oldSingers);
		
		Singer2 singer = Singer2.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		singerDao.insert(singer);
		List<Singer2> newSingers = singerDao.findAllWithAlbums();
		log.info(">>> 김조한 추가후");
		listSingers(newSingers);
		assertEquals(oldSingers.size() + 1 , newSingers.size());
	}
	
	@Test
	public void testUpdateSinger() {
		log.info("testUpdateSinger---->>");
		Singer2 singerOldSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer2 singer = Singer2.builder()
				.id(1)
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1977-10-16"))
				.build();
		singerDao.update(singer);
		Singer2 singerNewSinger = singerDao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
		assertEquals(LocalDate.parse("1977-10-16"), singerNewSinger.getBirthDate());
	}

	@Test
	public void testDeleteSinger() {
		log.info("testDeleteSinger---->>");
		List<Singer2> oldSingers = singerDao.findAllWithAlbums();
		singerDao.delete(3);
		List<Singer2> singers = singerDao.findAllWithAlbums();
		log.info("가수 삭제 후 가수 목록>>>");
		listSingers(singers);
		assertEquals(oldSingers.size() - 1, singers.size());
	}

	@Test
	public void testInsertSingerWithAlbum() {
		log.info("testInsertSingerWithAlbum---->>");
		List<Singer2> oldSingers = singerDao.findAllWithAlbums();
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
		singerDao.insertWithAlbum(singer);
		List<Singer2> singers = singerDao.findAllWithAlbums();
		listSingers(singers);
		assertEquals(oldSingers.size() + 1, singers.size());
	}

	@Test
	public void testListSingersSummary() {
		log.info("testListSingersSummary---->>");
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

}
