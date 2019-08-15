package com.linor.singer.jpa2;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao2.SingerDao2;
import com.linor.singer.domain2.Instrument2;
import com.linor.singer.domain2.Singer2;
import com.linor.singer.domain2.SingerSummary2;

import lombok.extern.slf4j.Slf4j;

@Transactional("transactionManager2")
@Repository
@Slf4j
public class SingerDao2Impl implements SingerDao2 {
	
	@PersistenceContext(unitName = "db2")
	private EntityManager entityManager;
	
	@Override
	@Transactional(readOnly=true)
	public List<Singer2> findAll() {
		return entityManager.createQuery("from Singer2 s", Singer2.class).getResultList();
	}

	private static final String ALL_SINGER_NATIVE_SQL =
			"select id, first_name, last_name, birth_date, version from singer2";
	@Override
	public List<Singer2> findAllByNativeQuery() {
		return entityManager.createNativeQuery(ALL_SINGER_NATIVE_SQL, Singer2.class)
				.getResultList();
	}

	@Override
	public List<Singer2> findByFirstName(String firstName) {
		return entityManager.createNamedQuery("Singer2.findByFirstName", Singer2.class)
				.setParameter("firstName", firstName)
				.getResultList();
	}

	@Override
	public String findNameById(Integer id) {
		Singer2 singer = findById(id);
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer2 findById(Integer id) {
		return entityManager.createNamedQuery("Singer2.findById",Singer2.class).
				setParameter("id", id).getSingleResult();
	}

	@Override
	public String findFirstNameById(Integer id) {
		Singer2 singer = findById(id);
		return singer.getFirstName();
	}
	
	@Override
	public List<SingerSummary2> listAllSingersSummary() {
		List<SingerSummary2> result = entityManager.createQuery("select \n"
				+ "new com.linor.singer.domain2.SingerSummary2(\n"
				+ "s.firstName, s.lastName, a.title) from Singer2 s\n"
				+ "left join s.albums a\n"
				+ "where a.releaseDate=(select max(a2.releaseDate)\n"
				+ "from Album2 a2 where a2.singer.id = s.id)", SingerSummary2.class)
				.getResultList();
		return result;
	}
	
	@Override
	public void insert(Singer2 singer) {
		entityManager.persist(singer);
	}

	@Override
	public void update(Singer2 singer) {
		entityManager.merge(singer);
	}

	@Override
	public void delete(Integer singerId) {
		Singer2 singer = entityManager.createNamedQuery("Singer2.findById",Singer2.class).
				setParameter("id", singerId).getSingleResult();
		if(singer != null) {
			entityManager.remove(singer);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Singer2> findAllWithAlbums() {
		return entityManager.createNamedQuery("Singer2.findAllWithAlbum",Singer2.class).getResultList();
	}

	@Override
	public void insertWithAlbum(Singer2 singer) {
		insert(singer);
	}

	@Override
	public void insert(Instrument2 instrument) {
		entityManager.persist(instrument);
	}

}
