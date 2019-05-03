package com.linor.app.service;

import com.linor.app.domain.Singer1;
import com.linor.app.domain.Singer2;
import com.linor.app.exception.BizException;

public interface SingerService {
	public void insertSinger1(Singer1 singer1) throws BizException;
	public void updateSinger1(Singer1 singer1) throws BizException;
	public void deleteSinger1(Integer id) throws BizException;
	
	public void insertSinger2(Singer2 singer1) throws BizException;
	public void updateSinger2(Singer2 singer1) throws BizException;
	public void deleteSinger2(Integer id) throws BizException;

	public void insertScenario(Singer1 singer1, Singer2 singer2) throws BizException;
	public void updateScenario(Singer1 singer1, Singer2 singer2) throws BizException;
	public void deleteScenario(Singer1 singer1, Singer2 singer2) throws BizException;
}
