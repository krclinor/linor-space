package com.linor.singer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.linor.singer.domain.Singer;
import com.linor.singer.service.SingerService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/singers")
public class SingerController {

	@Autowired
	private SingerService singerService;
	
	@PostMapping
	public ResponseEntity<Mono<Singer>> save(@RequestBody Singer singer){
		HttpStatus status = (singer.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
		Mono<Singer> mono = singerService.save(singer);
		return new ResponseEntity<Mono<Singer>>(mono, status);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Mono<Singer>> findById(@PathVariable("id") Integer id){
		Mono<Singer> mono = singerService.findById(id);
		HttpStatus status = mono != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
		return new ResponseEntity<Mono<Singer>>(mono, status);
	}
	
	@GetMapping
	public ResponseEntity<Flux<Singer>> findAll(){
		Flux<Singer> flux = singerService.findAll();
		
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_STREAM_JSON)
				.body(flux.publishOn(Schedulers.elastic()).subscribeOn(Schedulers.elastic()));

	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteById(@PathVariable("id") Integer id){
		singerService.deleteById(id).subscribe();
	}
}