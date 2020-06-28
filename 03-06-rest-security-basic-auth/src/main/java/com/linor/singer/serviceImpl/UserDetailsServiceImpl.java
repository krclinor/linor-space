package com.linor.singer.serviceImpl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.linor.singer.dao.UserDao;
import com.linor.singer.domain.MyUser;
import com.linor.singer.domain.Role;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private  UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		MyUser myUser = userDao.findById(userId);
		if(myUser == null) {
			throw new UsernameNotFoundException("사용자ID " + userId + "은(는) 존재하지 않습니다.");
		}
		return new User(myUser.getId(), myUser.getPassword(), getAuthorities(myUser));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(MyUser myUser){
		List<Role> roles = userDao.listRolesByUser(myUser);
		String[] userRoles = new String[roles.size()];
		for(int i= 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			userRoles[i] = role.getId(); 
		}
		Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
		return authorities;
	}
}
