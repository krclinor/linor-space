package com.linor.singer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain.Singer;

@Mapper
public interface SingerDao {
	public void insert(Singer singer);
	public List<Singer> findAll();
}
