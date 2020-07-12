package com.linor.security.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyUser {
	private String id;
	private String name;
	private String email;
	private String password;
	private boolean enabled;
	private Date lastPasswordResetDate;
	private List<Role> roles;
}
