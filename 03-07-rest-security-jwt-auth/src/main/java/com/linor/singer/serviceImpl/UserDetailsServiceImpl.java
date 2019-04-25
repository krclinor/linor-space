package com.linor.singer.serviceImpl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linor.singer.dao.UserDao;
import com.linor.singer.domain.MyUser;
import com.linor.singer.domain.Role;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private  UserDao userDao;
	
//	@Autowired
//	private PasswordEncoder passwordEncoder;
	
//	@Resource(name = "custumAuthenticationManager")
//	private AuthenticationManager authenticationManager;
	
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
	
//	public void changePassword(String oldPassword, String newPassword) {
//		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
//		String username = currentUser.getName();
//		
//		if(authenticationManager != null) {
//			log.info("비밀번호 변경을 위해 사용자 {} 재인증 처리", username);
//			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
//		}else {
//			log.info("인증 관리자가 없어서 비밀번호를 수정할 수 없습니다.");
//			return;
//		}
//		
//		MyUser myUser = MyUser.builder()
//				.id(username)
//				.password(passwordEncoder.encode(newPassword))
//				.build();
//		userDao.updatePassword(myUser);
//	}
}
