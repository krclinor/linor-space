package com.linor.singer.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.linor.singer.model.User;

@Component
public class UserValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User)target;
		String email = user.getEmail();
		if ("linor@gmail.com".equals(email)) {
			errors.rejectValue("email",
					"email.exists",
					new Object[] {email},
					email + "은(는) 이미 사용중입니다.");
		}
	}
}
