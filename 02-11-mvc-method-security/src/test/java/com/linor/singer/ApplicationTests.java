package com.linor.singer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {
	@Autowired
	private PasswordEncoder encoder;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testPassword() {
		log.info("linor -> 인코딩된 암호: " + encoder.encode("linor"));
		log.info("user -> 인코딩된 암호: " + encoder.encode("user"));
	}
}
