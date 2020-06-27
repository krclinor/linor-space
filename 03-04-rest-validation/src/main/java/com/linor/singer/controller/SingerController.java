package com.linor.singer.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;
import com.linor.singer.exception.BizException;
import com.linor.singer.exception.DataAccessException;
import com.linor.singer.exception.ResourceNotFoundException;
import com.linor.singer.exception.ValidException;

@RestController
@RequestMapping("/rest/singer")
public class SingerController {
	@Autowired
	private SingerDao singerDao;
	
	@GetMapping
	public List<Singer> getSingers(){
		return singerDao.findAllWithAlbums();
	}
	
	@GetMapping(value = "/{id}")
	public Singer getSinger(@PathVariable("id") int id) {
		Singer singer = singerDao.findById(id);
		if(singer == null)
			throw new ResourceNotFoundException();
		
		return singer;
	}
	
	@PostMapping
	public void addSinger(@Valid @RequestBody Singer singer, BindingResult result) {
		if(result.hasErrors()) {
			throw new ValidException("유효성 체크 오류", result);
		}
		if(null != singer.getId())
			throw new BizException("비즈니스 오류 발생");
		singerDao.insertWithAlbum(singer);
	}
	
	@PutMapping(value="/{id}")
	public void updateSinger(@PathVariable("id") int id, @Valid @RequestBody Singer singer,
			BindingResult result) {
		if(result.hasErrors()) {
			throw new ValidException("유효성 체크 오류", result);
		}
		singerDao.update(singer);
	}
	
	@DeleteMapping(value = "/{id}")
	public void deleteSinger(@PathVariable("id") int id) throws DataAccessException{
		Singer singer = singerDao.findById(id);
		if (singer == null)
			throw new DataAccessException("데이타가 존재하지 않음");
		singerDao.delete(id);
	}
}
