# smart-zuul-proxxy
## 简介
Spring Cloud Zuul 是Spring Cloud Netflix 子项目的核心组件之一，可以作为微服务架构中的API网关使用，支持动态路由与过滤功能

API网关为微服务架构中的服务提供了统一的访问入口，客户端通过API网关访问相关服务。API网关的定义类似于设计模式中的门面模式，
它相当于整个微服务架构中的门面，所有客户端的访问都通过它来进行路由及过滤。
它实现了请求路由、负载均衡、校验过滤、服务容错、服务聚合等功能。

在Spring Cloud体系中， Spring Cloud Zuul就是提供负载均衡、反向代理、权限认证的一个API gateway。

## why？
那么问题来了？前面我们已经具备了eureka的注册以及Feign&Ribbon的服务负载调用和hystrix的容错能力，
那么此时这个zuul的负载均衡和方向代理的目的又是为了什么呢？
## answer
我们微服务的内部可以通过注册到eureka上进行各个服务组件的互相调用及各种支持的负载能力，但是对于外部的应用呢？
可以理解为我们的前端应用，或者APP客户端应用，以及外部的第三方服务应用，只要是非我们已有的微服务内的组件服务，如果
要调用我们的接口能力，总不可能每次都要一会访问8001的user-server模块端口，一会访问8002的warnWord-server接口，这样对于外部
的其它服务的调用是很不友好的，所以此时，zuul便应然而生；

所以，此时再看上述的关于zuul的简介，是否便清晰了许多；

Spring Cloud Zuul就是提供负载均衡、反向代理、权限认证的一个API gateway。
Zuul是Netflix出品的一个基于JVM路由和服务端的负载均衡器。

##
此处smart-zuul-proxy共实现（反向代理，负载均衡，及拦截器和熔断功能）
````
常用配置
zuul:
  routes: #给服务配置路由
    user-service:
      path: /userService/**
    feign-service:
      path: /feignService/**
  ignored-services: user-service,feign-service #关闭默认路由配置
  prefix: /proxy #给网关路由添加前缀
  sensitive-headers: Cookie,Set-Cookie,Authorization #配置过滤敏感的请求头信息，设置为空就不会过滤
  add-host-header: true #设置为true重定向是会添加host请求头
  retryable: true # 关闭重试机制
  PreLogFilter:
    pre:
      disable: false #控制是否启用过滤器
hystrix:
  command: #用于控制HystrixCommand的行为
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 1000 #配置HystrixCommand执行的超时时间，执行超过该时间会进行服务降级处理
ribbon: #全局配置
  ConnectTimeout: 1000 #服务请求连接超时时间（毫秒）
  ReadTimeout: 3000 #服务请求处理超时时间（毫秒）
````

可参考链接：
https://juejin.im/post/5d9f2dea6fb9a04e3e724067
http://www.ityouknow.com/springcloud/2017/06/01/gateway-service-zuul.html