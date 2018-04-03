package com.test.org.Service;

import com.test.org.model.User;

import java.util.List;

public interface UserService {

    int addUser(User user);

    List<User> findAllUser(int pageNum, int pageSize);

    int insertListUser(List<User> user);
}