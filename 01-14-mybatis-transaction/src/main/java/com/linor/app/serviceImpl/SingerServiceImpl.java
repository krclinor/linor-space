package com.linor.app.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linor.app.dao.Singer1Dao;
import com.linor.app.dao.Singer2Dao;
import com.linor.app.domain.Singer1;
import com.linor.app.domain.Singer2;
import com.linor.app.exception.BizException;
import com.linor.app.service.SingerService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SingerServiceImpl implements SingerService {
	@Autowired
	private Singer1Dao dao1;

	@Autowired
	private Singer2Dao dao2;
	
	@Override
	public void insertSinger1(Singer1 singer1) throws BizException {
		log.info("가수1 추가: {}", singer1);
		try {
			dao1.insert(singer1);
		}catch (Exception e) {
			log.error("가수 1 추가시 오류 발생: {}\n{}", singer1, e.getMessage());;
			throw new BizException("가수1 추가시 오류 발생!!", e);
		}
		
	}

	@Override
	public void updateSinger1(Singer1 singer1) throws BizException {
		log.info("가수1 수정: {}", singer1);
		try {
			dao1.update(singer1);
		}catch (Exception e) {
			log.error("가수 1 수정시 오류 발생: {}\n{}", singer1, e.getMessage());;
			throw new BizException("가수1 수정시 오류 발생!!", e);
		}
	}

	@Override
	public void deleteSinger1(Integer id) throws BizException {
		log.info("가수1 삭제: {}", id);
		try {
			dao1.delete(id);
		}catch (Exception e) {
			log.error("가수 1 삭제시 오류 발생: {}\n{}", id, e.getMessage());;
			throw new BizException("가수1 삭제시 오류 발생!!", e);
		}
	}

	@Override
	public void insertSinger2(Singer2 singer1) throws BizException {
		log.info("가수2 추가: {}", singer1);
		try {
			dao2.insert(singer1);
		}catch (Exception e) {
			log.error("가수 2 추가시 오류 발생: {}\n{}", singer1, e.getMessage());;
			throw new BizException("가수2 추가시 오류 발생!!", e);
		}
	}

	@Override
	public void updateSinger2(Singer2 singer1) throws BizException {
		log.info("가수2 수정: {}", singer1);
		try {
			dao2.update(singer1);
		}catch (Exception e) {
			log.error("가수 2 수정시 오류 발생: {}\n{}", singer1, e.getMessage());;
			throw new BizException("가수2 수정시 오류 발생!!", e);
		}
	}

	@Override
	public void deleteSinger2(Integer id) throws BizException {
		log.info("가수2 삭제: {}", id);
		try {
			dao2.delete(id);
		}catch (Exception e) {
			log.error("가수 2 삭제시 오류 발생: {}\n{}", id, e.getMessage());;
			throw new BizException("가수 삭제시 오류 발생!!", e);
		}
	}

	@Override
	public void insertScenario(Singer1 singer1, Singer2 singer2) throws BizException {
		this.insertSinger1(singer1);
		this.insertSinger2(singer2);
	}

	@Override
	public void updateScenario(Singer1 singer1, Singer2 singer2) throws BizException {
		this.updateSinger1(singer1);
		this.updateSinger2(singer2);
	}

	@Override
	public void deleteScenario(Singer1 singer1, Singer2 singer2) throws BizException {
		this.deleteSinger1(singer1.getId());
		this.deleteSinger2(singer2.getId());
	}

}
