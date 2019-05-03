package com.linor.app;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.linor.app.dao.Singer1Dao;
import com.linor.app.dao.Singer2Dao;
import com.linor.app.domain.Singer1;
import com.linor.app.domain.Singer2;

import lombok.extern.slf4j.Slf4j;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {
	@Autowired
	private Singer1Dao singer1Dao;
	
	@Autowired
	private Singer2Dao singer2Dao;
	
	@Test
	public void test11FindAll() {
		List<Singer1> singers = singer1Dao.findAll();
		assertTrue(singers.size() >= 3);
		log.info("테스트 findAll----");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void test12FindByFirstName() {
		List<Singer1> singers = singer1Dao.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		log.info("테스트 : findByFirstName----");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}
	
	@Test
	public void test13FindByFirstNameAndLastName() {
		List<Singer1> singers = singer1Dao.findByFirstNameAndLastName("종서", "김");
		assertTrue(singers.size() == 1);
		log.info("테스트 findByFirstNameAndLastName----");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void test14FindbyId() {
		Singer1 singer = singer1Dao.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
	}
	

	@Test
	public void test15InsertSinger() {
		Singer1 singer = new Singer1();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		try {
			singer1Dao.insert(singer);
		} catch (Exception e) {
		}
		List<Singer1> singers = singer1Dao.findAll();
		log.info(">>> 김조한 추가후");
		singers.forEach(s -> {
			log.info(s.toString());
		});
	}
	
	@Test
	public void test16UpdateSinger() {
		Singer1 singerOldSinger = singer1Dao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer1 singer = new Singer1();
		singer.setId(1);
		singer.setFirstName("종서");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
		try {
			singer1Dao.update(singer);
		} catch (Exception e) {
		}
		Singer1 singerNewSinger = singer1Dao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}

	@Test
	public void test17DeleteSinger() {
		try {
			singer1Dao.delete(3);
		} catch (Exception e) {
		}
		List<Singer1> singers = singer1Dao.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		singers.forEach(s -> {
			log.info(s.toString());
		});
	}

	
	@Test
	public void test21FindAll() {
		List<Singer2> singers = singer2Dao.findAll();
		assertTrue(singers.size() >= 3);
		log.info("테스트 findAll----");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void tes22tFindByFirstName() {
		List<Singer2> singers = singer2Dao.findByFirstName("종서");
		assertTrue(singers.size() == 1);
		log.info("테스트 : findByFirstName----");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}
	
	@Test
	public void test23FindByFirstNameAndLastName() {
		List<Singer2> singers = singer2Dao.findByFirstNameAndLastName("종서", "김");
		assertTrue(singers.size() == 1);
		log.info("테스트 findByFirstNameAndLastName----");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}

	@Test
	public void test24FindbyId() {
		Singer2 singer = singer2Dao.findById(1);
		log.info("주키 검색 결과>>>");
		log.info(singer.toString());
	}
	

	@Test
	public void test25InsertSinger() {
		log.info("테스트 25");
		Singer2 singer = new Singer2();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		try {
			singer2Dao.insert(singer);
		} catch (Exception e) {
			// TODO: handle exception
		}
		List<Singer2> singers = singer2Dao.findAll();
		log.info(">>> 김조한 추가후");
		singers.forEach(s -> {
			log.info(s.toString());
		});
	}
	
	@Test
	public void test26UpdateSinger() {
		Singer2 singerOldSinger = singer2Dao.findById(1);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singerOldSinger.toString());
		Singer2 singer = new Singer2();
		singer.setId(1);
		singer.setFirstName("종서");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1977-10-16"));
		try {
			singer2Dao.update(singer);
		} catch (Exception e) {
			// TODO: handle exception
		}
		Singer2 singerNewSinger = singer2Dao.findById(1);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(singerNewSinger.toString());
	}

	@Test
	public void test27DeleteSinger() {
		try {
			singer2Dao.delete(3);
		} catch (Exception e) {
			// TODO: handle exception
		}
		List<Singer2> singers = singer2Dao.findAll();
		log.info("가수 삭제 후 가수 목록>>>");
		singers.forEach(s -> {
			log.info(s.toString());
		});
	}
	
}
