package com.linor.singer.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.linor.singer.domain.Album;
import com.linor.singer.domain.Singer;

public interface AlbumRespository extends JpaRepository<Album, Integer> {

	List<Album> findBySinger(Singer singer);
	
	@Query("from Album a where a.title like %:title%")
	List<Album> findByTitle(@Param("title") String t);
}
