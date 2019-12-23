package com.smart.cloud.smarthystrixuser.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import com.smart.cloud.smarthystrixuser.domain.User;
import com.smart.cloud.smarthystrixuser.service.UserService;
import com.smart.cloud.smartplatformcommon.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-12-18
 */
@RestController
@RequestMapping("/user")
public class UserHystrixController {

    @Autowired
    private UserService userService;

    //*************************************** Hystrix 服务降级的操作 ************************************

    @GetMapping("/testFallback/{id}")
    public CommonResult testFallback(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/testException/{id}")
    public CommonResult testException(@PathVariable Long id) {
        return userService.getUserException(id);
    }

    @GetMapping("/testCommand/{id}")
    public CommonResult testCommand(@PathVariable Long id) {
        return userService.getUserCommand(id);
    }

    //********************************************** Hystrix 请求缓存的操作 *********************************

    @GetMapping("/testCache/{id}")
    public CommonResult testCache(@PathVariable Long id) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", userService.getUserCache(id));
        jsonObject.put("key2", userService.getUserCache(id));
        jsonObject.put("key3", userService.getUserCache(id));

        System.out.println("testCache：>>> " + jsonObject);
        return new CommonResult("操作成功", 200);
    }

    @GetMapping("/testCache2/{id}")
    public CommonResult testCache2(@PathVariable Long id) {
        
        return userService.getUserCache(id);
    }


    @GetMapping("/testRemoveCache/{id}")
    public CommonResult testRemoveCache(@PathVariable Long id) {
        userService.getUserCache(id);
        userService.removeCache(id);
        userService.getUserCache(id);
        return new CommonResult("操作成功", 200);
    }

    //***************************************** Hystrix 请求合并的操作 *****************************************

    @GetMapping("/testCollapser")
    public CommonResult testCollapser() throws ExecutionException, InterruptedException {
        Future<User> future1 = userService.getUserFuture(1L);
        Future<User> future2 = userService.getUserFuture(2L);
        future1.get();
        future2.get();
        ThreadUtil.safeSleep(200);
        Future<User> future3 = userService.getUserFuture(3L);
        future3.get();
        return new CommonResult("操作成功", 200);
    }
}
