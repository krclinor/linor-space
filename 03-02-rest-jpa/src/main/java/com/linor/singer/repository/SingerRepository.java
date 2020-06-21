package com.linor.singer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

public interface SingerRepository extends JpaRepository<Singer, Integer> {
	List<Singer> findAll();
	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(String firstName, String lastName);
	List<Singer> findAllWithAlbum();
	
	@Query("select \n"
				+ "new com.linor.singer.domain.SingerSummary(\n"
				+ "s.firstName, s.lastName, a.title) from Singer s\n"
				+ "left join s.albums a\n"
				+ "where a.releaseDate=(select max(a2.releaseDate)\n"
				+ "from Album a2 where a2.singer.id = s.id)")
	public List<SingerSummary> listAllSingersSummary();
	
	@Query(value = "select id, first_name, last_name, birth_date, version from singer", nativeQuery = true)
	public List<Singer> findAllByNativeQuery();
}
