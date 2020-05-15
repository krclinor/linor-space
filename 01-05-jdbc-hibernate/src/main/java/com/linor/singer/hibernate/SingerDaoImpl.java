package com.linor.singer.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Album;
import com.linor.singer.domain.Instrument;
import com.linor.singer.domain.Singer;
import com.linor.singer.domain.SingerSummary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class SingerDaoImpl implements SingerDao {
	@PersistenceContext
	private final EntityManager entityManager;

	protected Session getCurrentSession()  {
		return entityManager.unwrap(Session.class);
	}
	
//	@Autowired
//	private EntityManagerFactory entityManagerFactory;
//
//	protected Session getCurrentSession()  {
//		return entityManagerFactory.unwrap(SessionFactory.class).getCurrentSession();
//	}
	
	@Override
	public List<Singer> findAll() {
		Session session = getCurrentSession();
		return session
				.createQuery("from Singer", Singer.class)
				.list();
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		Session session = getCurrentSession();
		return session
				.createNamedQuery("Singer.findByFirstName", Singer.class)
				.setParameter("firstName", firstName)
				.list();
	}

	@Override
	public String findNameById(Integer id) {
		Singer singer = findById(id);
		return singer.getFirstName() + " " + singer.getLastName();
	}

	@Override
	public Singer findById(Integer id) {
		Session session = getCurrentSession();
		return (Singer)session
				.getNamedQuery("Singer.findById")
				.setParameter("id", id)
				.uniqueResult();
	}

	@Override
	public String findFirstNameById(Integer id) {
		Singer singer = findById(id);
		return singer.getFirstName();
	}

	@Override
	public void insert(Singer singer) {
		Session session = getCurrentSession();
		session.saveOrUpdate(singer);
		log.info("저장된 가수 ID: " + singer.getId());
	}

	@Override
	public void update(Singer singer) {
		Session session = getCurrentSession();
		session.update(singer);
	}

	@Override
	public void delete(Integer singerId) {
		Session session = getCurrentSession();
		Singer singer = (Singer)session
				.getNamedQuery("Singer.findById")
				.setParameter("id", singerId)
				.uniqueResult();
		if(singer != null) {
			session.remove(singer);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Singer> findAllWithAlbums() {
		Session session = getCurrentSession();
		
		return session.createNamedQuery("Singer.findAllWithAlbum", Singer.class).list();
	}

	@Override
	public void insertWithAlbum(Singer singer) {
		insert(singer);
	}

	@Override
	public void insertInstrument(Instrument instrument) {
		Session session = getCurrentSession();
		session.saveOrUpdate(instrument);
	}

	private static final String ALL_SINGER_NATIVE_SQL =
			"select id, first_name, last_name, birth_date, version from singer";
	@Override
	public List<Singer> findAllByNativeQuery() {
		Session session = getCurrentSession();
		return session
				.createNativeQuery(ALL_SINGER_NATIVE_SQL, Singer.class)
				.list();
	}

	@Override
	public List<Singer> findByFirstNameAndLastName(Singer singer) {
		Session session = getCurrentSession();
		return session
				.createNamedQuery("Singer.findByFirstNameAndLastName", Singer.class)
				.setParameter("firstName", singer.getFirstName())
				.setParameter("lastName", singer.getLastName())
				.list();
	}
	
	@Override
	public List<Album> findAlbumsBySinger(Singer singer) {
		Session session = getCurrentSession();
		return session
				.createNamedQuery("Album.findAlbumsBySinger", Album.class)
				.setParameter("singer_id", singer.getId())
				.list();
	}

	@Override
	public List<Album> findAlbumsByTitle(String title) {
		Session session = getCurrentSession();
		return session
				.createNamedQuery("Album.findByTitle", Album.class)
				.setParameter("title", title)
				.list();
	}

	@Override
	public List<SingerSummary> listAllSingersSummary() {
		Session session = getCurrentSession();
		return session.createQuery("select \n"
				+ "new com.linor.singer.domain.SingerSummary(\n"
				+ "s.firstName, s.lastName, a.title) from Singer s\n"
				+ "left join s.albums a\n"
				+ "where a.releaseDate=(select max(a2.releaseDate)\n"
				+ "from Album a2 where a2.singer.id = s.id)", SingerSummary.class)
				.list();
	}
	
	
}
