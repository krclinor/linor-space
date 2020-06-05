package com.linor.singer.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.linor.singer.domain.MyUser;
import com.linor.singer.domain.Role;

@Mapper
public interface UserDao {
	MyUser findById(String id);
	List<Role> listRolesByUser(MyUser user);
}
