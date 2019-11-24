package com.smart.cloud.smartfeignuser.service;

import com.smart.cloud.smartfeignuser.domain.User;
import com.smart.cloud.smartfeignuser.service.impl.UserFallbackService;
import com.smart.cloud.smartplatformcommon.utils.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用远程的 user-service 模块接口
 *
 * @author Arnold.zhao <a href="mailto:13949123615@163.com"/>
 * @create 2019-11-21
 */
@FeignClient(value = "user-service",fallback = UserFallbackService.class)
public interface UserService {

    @PostMapping("/user/create")
    CommonResult create(@RequestBody User user);

    @GetMapping("/user/{id}")
    CommonResult<User> getUser(@PathVariable(value = "id") Long id);

    @GetMapping("/user/getByUsername")
    CommonResult<User> getByUsername(@RequestParam("username") String username);

    @PostMapping("/user/update")
    CommonResult update(@RequestBody User user);

    @PostMapping("/user/delete/{id}")
    CommonResult delete(@PathVariable(value = "id") Long id);
}
