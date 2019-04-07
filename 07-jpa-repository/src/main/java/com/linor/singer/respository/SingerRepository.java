package com.linor.singer.respository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

public interface SingerRepository extends CrudRepository<Singer, Integer> {
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
}
