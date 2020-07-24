package com.linor.singer.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return singerRepository.save(singer);
	}

	@Override
	public Mono<Singer> findById(Integer id) {
		return singerRepository.findById(id);
	}

	@Override
	public Flux<Singer> findAllFlux() {
		return singerRepository.findAll();
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		return singerRepository.deleteById(id);
	}

}
