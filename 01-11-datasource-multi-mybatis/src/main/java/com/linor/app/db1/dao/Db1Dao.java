package com.linor.app.db1.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Db1Dao {
	public String selectData();
}
