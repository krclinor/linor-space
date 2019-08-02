package com.linor.singer.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class SingerDaoImpl implements SingerDao {
	@PersistenceContext
	EntityManager entityManager;

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
				.createQuery("from Singer")
				.list();
	}

	@Override
	public List<Singer> findByFirstName(String firstName) {
		Session session = getCurrentSession();
		return session
				.getNamedQuery("Singer.findByFirstName")
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
		
		return session.getNamedQuery("Singer.findAllWithAlbum").list();
	}

	@Override
	public void insertWithAlbum(Singer singer) {
		insert(singer);
	}
}
