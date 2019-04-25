package com.linor.singer.domain;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyUser {
	private String id;
	private String name;
	private String email;
	private String password;
	private boolean enabled;
	private Timestamp lastPasswordResetDate;
}
