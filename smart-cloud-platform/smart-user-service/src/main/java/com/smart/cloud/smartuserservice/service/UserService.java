package com.smart.cloud.smartuserservice.service;

import com.smart.cloud.smartuserservice.domain.User;

import java.util.List;

/**
 * @author arnold.zhao
 * @date 2019/11/21 14:31
 */
public interface UserService {
    void create(User user);

    User getUser(Long id);

    void update(User user);

    void delete(Long id);

    User getByUsername(String username);

    List<User> getUserByIds(List<Long> ids);
}
