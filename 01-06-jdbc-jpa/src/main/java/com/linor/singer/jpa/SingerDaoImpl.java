package com.linor.singer.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Repository
@Slf4j
public class SingerDaoImpl implements SingerDao {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	@Transactional(readOnly=true)
	public List<Singer> findAll() {
		return entityManager.createQuery("from Singer s", Singer.class).getResultList();
	}

	private static final String ALL_SINGER_NATIVE_SQL =
			"select id, first_name, last_name, birth_date, version from singer";
	@Override
	public List<Singer> findAllByNativeQuery() {
		return entityManager.createNativeQuery(ALL_SINGER_NATIVE_SQL, Singer.class)
				.getResultList();
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		return entityManager.createNamedQuery("Singer.findByFirstName", Singer.class)
				.setParameter("firstName", firstName)
				.getResultList();
	}

	@Override
	public String findNameById(Integer id) {
		Singer singer = findById(id);
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer findById(Integer id) {
		return entityManager.createNamedQuery("Singer.findById",Singer.class).
				setParameter("id", id).getSingleResult();
	}

	@Override
	public String findFirstNameById(Integer id) {
		Singer singer = findById(id);
		return singer.getFirstName();
	}
	
	@Override
	public List<SingerSummary> listAllSingersSummary() {
		List<SingerSummary> result = entityManager.createQuery("select \n"
				+ "new com.linor.singer.domain.SingerSummary(\n"
				+ "s.firstName, s.lastName, a.title) from Singer s\n"
				+ "left join s.albums a\n"
				+ "where a.releaseDate=(select max(a2.releaseDate)\n"
				+ "from Album a2 where a2.singer.id = s.id)", SingerSummary.class)
				.getResultList();
		return result;
	}
	
	@Override
	public void insert(Singer singer) {
		entityManager.persist(singer);
	}

	@Override
	public void update(Singer singer) {
		entityManager.merge(singer);
	}

	@Override
	public void delete(Integer singerId) {
		Singer singer = entityManager.createNamedQuery("Singer.findById",Singer.class).
				setParameter("id", singerId).getSingleResult();
		if(singer != null) {
			entityManager.remove(singer);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Singer> findAllWithAlbums() {
		return entityManager.createNamedQuery("Singer.findAllWithAlbum",Singer.class).getResultList();
	}

	@Override
	public void insertWithAlbum(Singer singer) {
		insert(singer);
	}

	@Override
	public void insert(Instrument instrument) {
		entityManager.persist(instrument);
	}

	
}
