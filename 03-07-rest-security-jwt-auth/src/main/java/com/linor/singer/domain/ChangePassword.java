package com.linor.singer.domain;

import lombok.Data;

@Data
public class ChangePassword {
	private String oldPassword;
	private String newPassword;
}
