package com.linor.singer.service;

import java.util.List;

import com.linor.singer.domain.Singer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SingerService {
	Mono<Singer> save(Singer singer);
	Mono<Singer> findById(Integer id);
	Flux<Singer> findAllFlux();
	List<Singer> findAllList();
	Mono<Void> deleteById(Integer id);
}
