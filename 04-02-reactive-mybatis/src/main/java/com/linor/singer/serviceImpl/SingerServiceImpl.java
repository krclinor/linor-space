package com.linor.singer.serviceImpl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;
import com.linor.singer.service.SingerService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
public class SingerServiceImpl implements SingerService {
	@Autowired
	private SingerDao singerDao;
	
	@Override
	public Mono<Singer> save(Singer singer) {
		if(singer.getId() == null) {
			singerDao.insert(singer);
			return Mono.just(singer);
		}else {
			singerDao.update(singer);
			return Mono.just(singer);
		}
	}

	@Override
	public Mono<Singer> findById(Integer id) {
		Singer singer = singerDao.findById(id);
		if (singer == null) {
			throw new NoSuchElementException();
		}
		return Mono.just(singer);
	}

	
	@Override
	public List<Singer> findAllList() {
		return singerDao.findAllLIst();
	}

	@Override
	public Flux<Singer> findAllFlux() {
		Flux<Singer> flux = Flux.push(fluxSink -> {
			singerDao.findAllFlux(resultContext -> fluxSink.next(resultContext.getResultObject()));
			fluxSink.complete();
		});
		return flux.publishOn(Schedulers.elastic()).subscribeOn(Schedulers.elastic());
	}

	@Override
	public Mono<Void> deleteById(Integer id){
		Singer singer = singerDao.findById(id);
		singerDao.deleteById(singer.getId());
		return Mono.empty();
	}

}
