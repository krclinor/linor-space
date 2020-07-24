package com.linor.singer.service;

import java.util.List;

import com.linor.singer.domain.Singer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SingerService {
	Mono<Singer> save(Singer singer);
	Mono<Singer> findById(Integer id);
	List<Singer> findAllList();
	Flux<Singer> findAllFlux();
	Mono<Void> deleteById(Integer id);
}
