package com.linor.singer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.linor.singer.domain.Singer;

public interface SingerRepository extends ReactiveCrudRepository<Singer, Integer> {
}