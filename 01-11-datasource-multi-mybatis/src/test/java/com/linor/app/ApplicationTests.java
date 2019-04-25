package com.linor.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.linor.app.db1.dao.Db1Dao;
import com.linor.app.db2.dao.Db2Dao;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {
	
	@Autowired 
	private Db1Dao db1Dao;
	
	@Autowired
	private Db2Dao db2Dao;
	
	@Test
	public void db1Test() {
		String message = db1Dao.selectData();
		assertEquals(message, "데이타베이스1");
		log.info("데이타베이스 : {}", message);
	}

	@Test
	public void db2Test() {
		String message = db2Dao.selectData();
		assertEquals(message, "데이타베이스2");
		log.info("데이타베이스 : {}", message);
	}

}
