package com.linor.singer.domain;


import java.util.List;

import lombok.Data;

@Data
public class Role {
	private String id;
	private String name;
	private List<MyUser> users;
	
}
