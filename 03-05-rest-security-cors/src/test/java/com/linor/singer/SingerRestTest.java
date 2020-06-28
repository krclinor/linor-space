package com.linor.singer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SingerRestTest {
	private static final String ROOT_URL = "http://localhost:";
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${local.server.port:8080}")
	private int port;
	
	@Test
	public void test01ListSingers() {
		ResponseEntity<Singer[]> responseEntity = restTemplate.getForEntity(ROOT_URL + port + "/rest/singer", Singer[].class);
		List<Singer> singers = Arrays.asList(responseEntity.getBody());
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		listSingers(singers);
	}
	
	@Test
	public void test02GetSingerById() {
		Singer singer = restTemplate.getForObject(ROOT_URL + port + "/rest/singer/1", Singer.class);
		assertNotNull(singer);
		log.info("가수 1 : {}", singer);
	}
	
	@Test
	public void test03InsertSinger() {
		ResponseEntity<Singer[]> responseEntity = restTemplate.getForEntity(ROOT_URL + port + "/rest/singer", Singer[].class);
		List<Singer> oldSingers = Arrays.asList(responseEntity.getBody());
		Singer singer = Singer.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		restTemplate.postForLocation(ROOT_URL + port + "/rest/singer", singer);

		responseEntity = restTemplate.getForEntity(ROOT_URL + port + "/rest/singer", Singer[].class);
		List<Singer> newSingers = Arrays.asList(responseEntity.getBody());
		assertNotNull(newSingers);
		assertEquals((oldSingers.size() + 1), newSingers.size());
		log.info("추가후 가수목록");
		listSingers(newSingers);
	}
	
	@Test
	public void test04UpdateSinger() {
		Singer oldSinger = restTemplate.getForObject(ROOT_URL + port + "/rest/singer/1", Singer.class);
		log.info(">>> 김종서 수정 전 >>>");
		log.info(oldSinger.toString());
		oldSinger.setBirthDate(LocalDate.parse("1978-06-28"));
		restTemplate.put(ROOT_URL + port + "/rest/singer/1", oldSinger);
		Singer newSinger = restTemplate.getForObject(ROOT_URL + port + "/rest/singer/1", Singer.class);
		log.info(">>> 김종서 수정 후 >>>");
		log.info(newSinger.toString());
		assertEquals(oldSinger.getBirthDate(), newSinger.getBirthDate());
	}
	
	@Test
	public void test05InsertSingerWithAlbum() {
		ResponseEntity<Singer[]> responseEntity = restTemplate.getForEntity(ROOT_URL + port + "/rest/singer", Singer[].class);
		List<Singer> singers = Arrays.asList(responseEntity.getBody());
		int singerCount = singers.size();
		
		Singer singer = Singer.builder()
				.firstName("태원")
				.lastName("김")
				.birthDate(LocalDate.parse("1965-04-12"))
				.albums(new HashSet<Album>())
				.build();
		Set<Album> ablums = singer.getAlbums();
				ablums.add(Album.builder()
						.title("Never Ending Story")
						.releaseDate(LocalDate.parse("2001-08-31"))
						.build()
						);
				ablums.add(Album.builder()
						.title("생각이나")
						.releaseDate(LocalDate.parse("2009-08-14"))
						.build()
						);
				ablums.add(Album.builder()
						.title("사랑할수록")
						.releaseDate(LocalDate.parse("1993-11-01"))
						.build()
						);
		
		restTemplate.postForLocation(ROOT_URL + port + "/rest/singer", singer);

		responseEntity = restTemplate.getForEntity(ROOT_URL + port + "/rest/singer", Singer[].class);
		singers = Arrays.asList(responseEntity.getBody());
		assertNotNull(singers);
		assertTrue(singers.size() == (singerCount+1));
		log.info("추가후 가수목록");
		listSingers(singers);
	}
	
	@Test
	public void test06DeleteSinger() {
		restTemplate.delete(ROOT_URL + port + "/rest/singer/1");
		try {
			Singer singer = restTemplate.getForObject(ROOT_URL + port +"/rest/singer/1", Singer.class);
			log.info("삭제되지 않은 경우 가수: {}", singer);
		}catch(final HttpClientErrorException e) {
			assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
			log.info(e.getMessage());
		}
	}
	
	private void listSingers(List<Singer> singers){
		for(Singer singer: singers){
			log.info(singer.toString());
		}
	}
}
