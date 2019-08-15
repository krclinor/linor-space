package com.linor.singer.jpa1;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao1.SingerDao1;
import com.linor.singer.domain1.Instrument1;
import com.linor.singer.domain1.Singer1;
import com.linor.singer.domain1.SingerSummary1;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class SingerDao1Impl implements SingerDao1 {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	@Transactional(readOnly=true)
	public List<Singer1> findAll() {
		return entityManager.createQuery("from Singer1 s", Singer1.class).getResultList();
	}

	private static final String ALL_SINGER_NATIVE_SQL =
			"select id, first_name, last_name, birth_date, version from singer1";
	@Override
	public List<Singer1> findAllByNativeQuery() {
		return entityManager.createNativeQuery(ALL_SINGER_NATIVE_SQL, Singer1.class)
				.getResultList();
	}

	@Override
	public List<Singer1> findByFirstName(String firstName) {
		return entityManager.createNamedQuery("Singer1.findByFirstName", Singer1.class)
				.setParameter("firstName", firstName)
				.getResultList();
	}

	@Override
	public String findNameById(Integer id) {
		Singer1 singer = findById(id);
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer1 findById(Integer id) {
		return entityManager.createNamedQuery("Singer1.findById",Singer1.class).
				setParameter("id", id).getSingleResult();
	}

	@Override
	public String findFirstNameById(Integer id) {
		Singer1 singer = findById(id);
		return singer.getFirstName();
	}
	
	@Override
	public List<SingerSummary1> listAllSingersSummary() {
		List<SingerSummary1> result = entityManager.createQuery("select \n"
				+ "new com.linor.singer.domain1.SingerSummary1(\n"
				+ "s.firstName, s.lastName, a.title) from Singer1 s\n"
				+ "left join s.albums a\n"
				+ "where a.releaseDate=(select max(a2.releaseDate)\n"
				+ "from Album1 a2 where a2.singer.id = s.id)", SingerSummary1.class)
				.getResultList();
		return result;
	}
	
	@Override
	public void insert(Singer1 singer) {
		entityManager.persist(singer);
	}

	@Override
	public void update(Singer1 singer) {
		entityManager.merge(singer);
	}

	@Override
	public void delete(Integer singerId) {
		Singer1 singer = entityManager.createNamedQuery("Singer1.findById",Singer1.class).
				setParameter("id", singerId).getSingleResult();
		if(singer != null) {
			entityManager.remove(singer);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Singer1> findAllWithAlbums() {
		return entityManager.createNamedQuery("Singer1.findAllWithAlbum",Singer1.class).getResultList();
	}

	@Override
	public void insertWithAlbum(Singer1 singer) {
		insert(singer);
	}

	@Override
	public void insert(Instrument1 instrument) {
		entityManager.persist(instrument);
	}

	
}
