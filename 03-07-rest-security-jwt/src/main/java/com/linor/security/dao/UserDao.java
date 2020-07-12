package com.linor.security.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.linor.security.model.MyUser;
import com.linor.security.model.Role;

@Mapper
@Repository
public interface UserDao {
	MyUser findById(String id);
	List<Role> listRolesByUser(MyUser user);
	void updatePassword(MyUser user);
}
