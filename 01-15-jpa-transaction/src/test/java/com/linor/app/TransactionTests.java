package com.linor.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

import com.linor.app.domain.Singer1;
import com.linor.app.domain.Singer2;
import com.linor.app.respository.Singer1Repository;
import com.linor.app.respository.Singer2Repository;
import com.linor.app.service.SingerService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionTests {
	@Autowired
	private Singer1Repository singer1Dao;
	
	@Autowired
	private Singer2Repository singer2Dao;
	
	@Autowired
	private SingerService singerService;

	@Test
	@Transactional
	public void test11InsertSinger() {
		log.info("테스트1-1(정상)시작--->>>");
		List<Singer1> singers1 = singer1Dao.findAll();
		int singersCount = singers1.size();
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("조한");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1990-10-16"));
		
		Singer2 singer2 = new Singer2();
		singer2.setFirstName("조한");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1990-10-16"));

		singerService.insertScenario(singer1, singer2);
		
		singers1 = singer1Dao.findAll();
		log.info(">>> 김조한 추가후");
		singers1.forEach(s -> {
			log.info(s.toString());
		});

		List<Singer2> singers2 = singer2Dao.findAll();
		singers2.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers1.size() == (singersCount + 1));
		assertTrue(singers2.size() == (singersCount + 1));
	}

	@Test
	public void test12InsertSinger() {
		log.info("테스트1-2시작--->>>");
		List<Singer1> singers1 = singer1Dao.findAll();
		int singersCount = singers1.size();
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("조한");
		singer1.setBirthDate(LocalDate.parse("1990-10-16"));

		Singer2 singer2 = new Singer2();
		singer2.setFirstName("조한");
		singer2.setLastName("김");
		singer2.setBirthDate(LocalDate.parse("1990-10-16"));
		try {
			singerService.insertScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		singers1 = singer1Dao.findAll();
		log.info(">>> 김조한 추가후");
		singers1.forEach(s -> {
			log.info(s.toString());
		});

		List<Singer2> singers2 = singer2Dao.findAll();
		singers2.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers1.size() == singersCount);
		assertTrue(singers2.size() == singersCount);
	}

	@Test
	public void test13InsertSinger() {
		log.info("테스트1-3시작--->>>");
		List<Singer1> singers1 = singer1Dao.findAll();
		int singersCount = singers1.size();
		Singer1 singer1 = new Singer1();
		singer1.setFirstName("조한");
		singer1.setLastName("김");
		singer1.setBirthDate(LocalDate.parse("1990-10-16"));

		Singer2 singer2 = new Singer2();
		singer2.setFirstName("조한");
		singer2.setBirthDate(LocalDate.parse("1990-10-16"));

		try {
			singerService.insertScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		
		singers1 = singer1Dao.findAll();
		log.info(">>> 김조한 추가후");
		singers1.forEach(s -> {
			log.info(s.toString());
		});

		List<Singer2> singers2 = singer2Dao.findAll();
		singers2.forEach(s -> {
			log.info(s.toString());
		});
		assertTrue(singers1.size() == singersCount);
		assertTrue(singers2.size() == singersCount);
	}
	
	@Test
	@Transactional
	public void test21UpdateSinger() {
		log.info("테스트2-1시작(정상)--->>>");
		Singer1 singer1 = singer1Dao.findById(1).get();
		Singer2 singer2 = singer2Dao.findById(1).get();
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singer1.toString());
		log.info(singer2.toString());
		singer1.setBirthDate(LocalDate.parse("1977-10-16"));
		singer2.setBirthDate(LocalDate.parse("1977-10-16"));
		try {
			singerService.updateScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		Singer1 newSinger1 = singer1Dao.findById(1).get();
		Singer2 newSinger2 = singer2Dao.findById(1).get();
		log.info(">>> 김종서 수정 후 >>>");
		log.info(newSinger1.toString());
		log.info(newSinger2.toString());
		assertEquals(singer1.getBirthDate(), newSinger1.getBirthDate());
		assertEquals(singer2.getBirthDate(), newSinger2.getBirthDate());
	}

	@Test
	public void test22UpdateSinger() {
		log.info("테스트2-2시작--->>>");
		Singer1 singer1 = singer1Dao.findById(1).get();
		Singer2 singer2 = singer2Dao.findById(1).get();
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singer1.toString());
		singer1.setLastName(null);
		singer1.setBirthDate(LocalDate.parse("1977-10-16"));
		singer2.setBirthDate(LocalDate.parse("1977-10-16"));
		try {
			singerService.updateScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		Singer1 newSinger1 = singer1Dao.findById(1).get();
		Singer2 newSinger2 = singer2Dao.findById(1).get();
		log.info(">>> 김종서 수정 후 >>>");
		log.info(newSinger1.toString());
		log.info(newSinger2.toString());
		assertNotEquals(singer1.getBirthDate(), newSinger1.getBirthDate());
		assertNotEquals(singer2.getBirthDate(), newSinger2.getBirthDate());
	}

	@Test
	public void test23UpdateSinger() {
		log.info("테스트2-3시작--->>>");
		Singer1 singer1 = singer1Dao.findById(1).get();
		Singer2 singer2 = singer2Dao.findById(1).get();
		log.info(">>> 김종서 수정 전 >>>");
		log.info(singer1.toString());
		singer2.setLastName(null);
		singer1.setBirthDate(LocalDate.parse("1977-10-16"));
		singer2.setBirthDate(LocalDate.parse("1977-10-16"));
		try {
			singerService.updateScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}

		Singer1 newSinger1 = singer1Dao.findById(1).get();
		Singer2 newSinger2 = singer2Dao.findById(1).get();
		log.info(">>> 김종서 수정 후 >>>");
		log.info(newSinger1.toString());
		log.info(newSinger2.toString());
		assertNotEquals(singer1.getBirthDate(), newSinger1.getBirthDate());
		assertNotEquals(singer2.getBirthDate(), newSinger2.getBirthDate());
	}

	@Test
	@Transactional
	public void test31DeleteSinger() {
		log.info("테스트3-1시작--->>>");
		Singer1 singer1 = singer1Dao.findById(1).orElse(null);
		Singer2 singer2 = singer2Dao.findById(1).orElse(null);
		try {
			singerService.deleteScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		Singer1 newSinger1 = singer1Dao.findById(1).orElse(null);
		Singer2 newSinger2 = singer2Dao.findById(1).orElse(null);
		log.info(">>> 김종서 삭제 후 >>>");
		assertNull(newSinger1);
		assertNull(newSinger2);
	}

	@Test
	public void test32DeleteSinger() {
		log.info("테스트3-2시작--->>>");
		Singer1 singer1 = singer1Dao.findById(10).orElse(null);
		Singer2 singer2 = singer2Dao.findById(1).orElse(null);
		try {
			singerService.deleteScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		Singer1 newSinger1 = singer1Dao.findById(1).orElse(null);
		Singer2 newSinger2 = singer2Dao.findById(1).orElse(null);
		log.info(">>> 김종서 삭제 후 >>>");
		assertNotNull(newSinger1);
		assertNotNull(newSinger2);
	}
	
	@Test
	public void test33DeleteSinger() {
		log.info("테스트3-3시작--->>>");
		Singer1 singer1 = singer1Dao.findById(1).orElse(null);
		Singer2 singer2 = singer2Dao.findById(10).orElse(null);
		try {
			singerService.deleteScenario(singer1, singer2);
		}catch (Exception e) {
			log.error("에러 발생해야 함");
		}
		Singer1 newSinger1 = singer1Dao.findById(1).orElse(null);
		Singer2 newSinger2 = singer2Dao.findById(1).orElse(null);
		log.info(">>> 김종서 삭제 후 >>>");
		assertNotNull(newSinger1);
		assertNotNull(newSinger2);
	}
}
