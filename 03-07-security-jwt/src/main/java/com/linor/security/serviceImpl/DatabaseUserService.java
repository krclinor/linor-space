package com.linor.security.serviceImpl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.linor.security.dao.UserDao;
import com.linor.security.model.MyUser;
import com.linor.security.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseUserService implements UserService {
	private final UserDao userDao;

	@Override
	public Optional<MyUser> getByUsername(String username) {
		MyUser user = userDao.findById(username);
		user.setRoles(userDao.listRolesByUser(user));
		return Optional.of(user);
	}
}
