package com.smart.cloud.smartfeignuser.service.impl;

import com.smart.cloud.smartfeignuser.domain.User;
import com.smart.cloud.smartfeignuser.service.UserService;
import com.smart.cloud.smartplatformcommon.utils.CommonResult;
import org.springframework.stereotype.Service;

/**
 * 远程 Feign 接口调用的熔断后行为 hystrix
 *
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-11-21
 */
@Service
public class UserFallbackService implements UserService {

    @Override
    public CommonResult create(User user) {
        User defaultUser = new User(-1L, "defaultUser", "123456");
        return new CommonResult<>(defaultUser);
    }

    @Override
    public CommonResult<User> getUser(Long id) {
        User defaultUser = new User(-1L, "defaultUser", "123456");
        return new CommonResult<>(defaultUser);
    }

    @Override
    public CommonResult<User> getByUsername(String username) {
        User defaultUser = new User(-1L, "defaultUser", "123456");
        return new CommonResult<>(defaultUser);
    }

    @Override
    public CommonResult update(User user) {
        return new CommonResult<>("服务降级，调用失败", 500);
    }

    @Override
    public CommonResult delete(Long id) {
        return new CommonResult<>("服务降级，调用失败", 500);
    }
}
