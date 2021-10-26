package com.ms.resources.user.services.services;

import com.ms.resources.user.services.entity.User;

import java.util.List;

public interface UserService {

    public List<User> findAllUser();
    public User createUser(User user);
    public User findUserByUsername(String username);
}
