package com.smart.cloud.smarthystrixuser.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.smart.cloud.smarthystrixuser.domain.User;
import com.smart.cloud.smartplatformcommon.utils.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * https://juejin.im/post/5d822d27e51d45621479ad92
 *
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-12-18
 */
@Service
public class UserService {

    private Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service-url.user-service}")
    private String userServiceUrl;

    //*************************************** Hystrix 服务降级的操作 ************************************

    /**
     * @description: @HystrixCommand 服务降级的方法（当前getUser()接口异常，则降级访问getDefaultUser()方法）
     * @param: [id]
     * @return: com.smart.cloud.smartplatformcommon.utils.CommonResult
     * @author: Arnold.zhao
     * @date: 2019/12/18
     */
    @HystrixCommand(fallbackMethod = "getDefaultUser")
    public CommonResult getUser(Long id) {
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", CommonResult.class, id);
    }

    public CommonResult getDefaultUser(@PathVariable Long id) {
        User defaultUser = new User(-1L, "defaultUser", "123456");
        return new CommonResult<>(defaultUser);
    }

    /**
     * @description: 服务降级的方法（ignoreExceptions忽略某些异常，当前发生NullPointerException异常时，不发生服务降级的操作）
     * @param: [id]
     * @return: com.smart.cloud.smartplatformcommon.utils.CommonResult
     * @author: Arnold.zhao
     * @date: 2019/12/18
     */
    @HystrixCommand(fallbackMethod = "getDefaultUser2", ignoreExceptions = {NullPointerException.class})
    public CommonResult getUserException(Long id) {
        if (id == 1) {
            throw new IndexOutOfBoundsException();
        } else if (id == 2) {
            //当ID = 2时，此时则抛出NullPointer异常，服务不做降级处理，直接抛出对应的异常
            throw new NullPointerException();
        }
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", CommonResult.class, id);
    }

    public CommonResult getDefaultUser2(@PathVariable Long id, Throwable e) {
        LOGGER.error("getDefaultUser2 id:{},throwable class:{}", id, e.getClass());
        User defaultUser = new User(-2L, "defaultUser2", "123456");
        return new CommonResult<>(defaultUser);
    }

    /**
     * @description: commandKey：命令名称，用于区分不同的命令，
     * groupKey：分组名称，Hystrix将会根据不同的分组用于统计信息（Hystrix Dashboard时将会体现出来）
     * threadPoolKey：线程池名称，用于划分线程池
     * @author: Arnold.zhao
     * @date: 2019/12/18
     */
    @HystrixCommand(fallbackMethod = "getDefaultUser",
            commandKey = "getUserCommand",
            groupKey = "getUserGroup",
            threadPoolKey = "getUserThreadPool")
    public CommonResult getUserCommand(@PathVariable Long id) {
        LOGGER.info("getUserCommand id:{}", id);
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", CommonResult.class, id);
    }

    //********************************************** Hystrix 请求缓存的操作 *********************************

    /**
     * 这里简单说明一下：
     * 为什么如下的getUserCache()方法，在被UserHystrixController的testCache()方法调用时，第一次在调用：userService.getUserCache(id)时，
     * getUserCache()方法会被触发 LOGGER.info("getUserCache id:{}", id);  的日志，而后续的第二次及第三次的调用，则都不再触发该getUserCache()的日志输出，
     * 原因则是对应的@CacheResult缓存的作用；但是缓存所存在的有效时间是多长时间呢？在何时访问时会不再触发缓存？此处便和对应的Filter中所添加的HystrixRequestContext的初始化有关；
     * HystrixRequestContext的初始化在此处是伴随着一次请求所进行的初始化，简单查看下所对应的 HystrixRequestContext.initializeContext()的代码，发现也的确是将所初始化后的对象，set到了
     * 对应的ThreadLocal里面，所以此处的 熔断器的缓存操作，或许也的确和当前一次请求的线程有关，根据当前ThreadLocal所取出来的HystrixRequestContext对象，以此来将所对应的缓存数据进行对应的关联
     * 等到 HystrixRequestContext.close()后，则缓存数据结束；（而此处所设置的 HystrixRequestContext的close()的时机，则也是对应的web请求dofilter()执行结束后，进行close的操作；
     * 所以与所对应的判断相一致（后续可以再debug更进下底层的代码）
     */

    /**
     * @description: 在一次客户端的request请求当中，访问了多次getUserCache()方法，且ID都相同的情况下，则后续的访问方法，则直接被拦截，不再进行服务的restTemplate调用，直接返回对应的结果
     * @param: [id]
     * @return: com.smart.cloud.smartplatformcommon.utils.CommonResult
     * @author: Arnold.zhao
     * @date: 2019/12/18
     */
    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(fallbackMethod = "getDefaultUser", commandKey = "getUserCache")
    public CommonResult getUserCache(Long id) {
        LOGGER.info("getUserCache id:{}", id);
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", CommonResult.class, id);
    }

    /**
     * 为缓存生成key的方法
     */
    public String getCacheKey(Long id) {
        return String.valueOf(id);
    }

    @CacheRemove(commandKey = "getUserCache", cacheKeyMethod = "getCacheKey")
    @HystrixCommand
    public CommonResult removeCache(Long id) {
        LOGGER.info("removeCache id:{}", id);
        return restTemplate.postForObject(userServiceUrl + "/user/delete/{1}", null, CommonResult.class, id);
    }

    //***************************************** Hystrix 请求合并的操作 *****************************************


    @HystrixCollapser(batchMethod = "getUserByIds", collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "100")
    })
    public Future<User> getUserFuture(Long id) {
        return new AsyncResult<User>() {
            @Override
            public User invoke() {
                CommonResult commonResult = restTemplate.getForObject(userServiceUrl + "/user/{1}", CommonResult.class, id);
                Map data = (Map) commonResult.getData();
                User user = BeanUtil.mapToBean(data, User.class, true);
                LOGGER.info("getUserById username:{}", user.getUsername());
                return user;
            }
        };
    }

    @HystrixCommand
    public List<User> getUserByIds(List<Long> ids) {
        LOGGER.info("getUserByIds:{}", ids);
        CommonResult commonResult = restTemplate.getForObject(userServiceUrl + "/user/getUserByIds?ids={1}", CommonResult.class, CollUtil.join(ids, ","));
        return (List<User>) commonResult.getData();
    }
}
