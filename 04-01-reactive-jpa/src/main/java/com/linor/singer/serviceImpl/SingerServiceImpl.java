package com.linor.singer.serviceImpl;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linor.singer.domain.Singer;
import com.linor.singer.repository.SingerRepository;
import com.linor.singer.service.SingerService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class SingerServiceImpl implements SingerService {
	@Autowired
	private SingerRepository singerRepository;
	
	@Override
	public Mono<Singer> save(Singer singer) {
		return Mono.just(singerRepository.save(singer));
	}

	@Override
	public Mono<Singer> findById(Integer id) {
		return Mono.just(singerRepository.findById(id).orElseThrow(NoSuchElementException::new));
	}

	@Override
	public Flux<Singer> findAll() {
		return Flux.fromIterable(singerRepository.findAll());
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		singerRepository.deleteById(id);
		return Mono.empty();
	}

}
