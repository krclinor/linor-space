package com.linor.singer;

import java.time.LocalDate;
import java.util.List;

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
	
	@Test
	public void testAuditSinger() {
		List<Singer> singers = singerDao.findAll();
		listSingers(singers);
		log.info("새 가수 추가");
		Singer singer = new Singer();
		singer.setFirstName("BB");
		singer.setLastName("King");
		singer.setBirthDate(LocalDate.parse("1970-12-09"));
		singerDao.save(singer);
		
		singers = singerDao.findAll();
		listSingers(singers);
		
		singer = singerDao.findById(2);
		log.info("가수  내역: " + singer.toString());
		
		singer.setFirstName("John Clayton");
		singerDao.save(singer);
		
		singer = singerDao.findById(2);
		singer.setFirstName("Riley B.");
		singerDao.save(singer);

		singers = singerDao.findAll();
		listSingers(singers);
	}
	
	private void listSingers(List<Singer> singers) {
		singers.forEach(singer -> {
			log.info(singer.toString());
			log.info("Audit: {}, {}, {}, {}" 
					, singer.getCreatedBy()
					, singer.getCreatedDate()
					, singer.getLastModifiedBy()
					, singer.getLastModifiedDate());
		});
	}
}
