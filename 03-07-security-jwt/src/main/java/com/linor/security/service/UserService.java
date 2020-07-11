package com.linor.security.service;

import java.util.Optional;

import com.linor.security.model.MyUser;

public interface UserService {
    public Optional<MyUser> getByUsername(String username);
}
