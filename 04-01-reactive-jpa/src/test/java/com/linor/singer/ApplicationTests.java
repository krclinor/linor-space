package com.linor.singer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ApplicationTests {
	@Autowired
	private WebTestClient webTestClient;
	
	@LocalServerPort
	private int port;
	
	@Test
	public void test1DeleteSingerFailure() {
		log.info("test1DeleteSingerFailure--->");
		webTestClient.delete().uri("/singers/{id}", 999)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().is5xxServerError();
	}
	
	@Test
	public void test2AddSinger() {
		log.info("test2AddSinger--->");
		Singer newSinger = Singer.builder()
				.firstName("조한")
				.lastName("김")
				.birthDate(LocalDate.parse("1990-10-16"))
				.build();
		WebClient webClient = WebClient.create("http://localhost:"+port);
		
		Singer singer = webClient.post().uri("/singers")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(newSinger))
				.retrieve()
				.bodyToMono(Singer.class)
				.block();
		Integer id = singer.getId();
		log.info("추가된 가수 ID : {}", id);
		assertThat(singer.getFirstName(), is(newSinger.getFirstName()));
	}
	
	@Test
	public void test3FindSinger() {
		log.info("test3FindSinger--->");
		Integer id = 1;
		webTestClient.get().uri("/singers/{id}",id)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectBody()
			.jsonPath("$.firstName")
			.isEqualTo("종서");
	}
	
	@Test
	public void test4FindAllSinger() {
		log.info("test4FindAllSinger--->");
		Flux<Singer> singers = webTestClient.get().uri("/singers")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(Singer.class).getResponseBody();
		
		StepVerifier.create(singers)
			.expectNextCount(4)
			.verifyComplete();
	}
	
	@Test
	public void test5DeleteSinger() {
		log.info("test5DeleteSinger--->");
		webTestClient.delete().uri("/singers/{id}", 1)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();
	}
	
	@Test
	public void test6FindSingerFailure() {
		log.info("test6FindSingerFailure--->");
		webTestClient.get().uri("/singers/{id}", 1)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().is5xxServerError();
	}
}
