package com.linor.app.db2.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Db2Dao {
	public String selectData();
}
