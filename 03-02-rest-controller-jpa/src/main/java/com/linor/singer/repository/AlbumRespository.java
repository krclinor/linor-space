package com.linor.singer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;

public interface AlbumRespository extends JpaRepository<Album, Integer> {

	List<Album> findBySinger(Singer singer);
	
//	@Query("from Album a where a.title like %:title%")
//	List<Album> findByTitle(@Param("title") String t);
	List<Album> findByTitleContaining(String title);
}
