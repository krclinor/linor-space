package com.linor.singer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linor.singer.domain.Singer;

@Repository
public interface SingerRepository extends JpaRepository<Singer, Integer> {

}
