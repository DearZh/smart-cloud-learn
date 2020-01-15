# smart-nacos
Nacos是SpringCloudAlibaba的核心组件，主要用于注册中心及配置中心使用，[关于Nacos的官方介绍可点击我查看](https://nacos.io/zh-cn/docs/what-is-nacos.html)
## Nacos的优势和Eureka的区别
* SpringCloud是一个分布式的服务组件的整合，其中大多数的组件都可以采用其它的外部服务组件进行替代，在服务发现的软件当中，Eureka只是其中之一，
除此之外，Consul，Nacos等组件都可以替代Eureka的服务发现的方案（由于Eureka2.X版本目前已经停止开发，所以对应的各种服务发现的组件也都应允而生）

* Nacos是纯国产的阿里巴巴所开发的组件，并且Nacos除了具备服务发现的功能外，还具备实时配置中心的功能；
假设当前应用无需使用MQ的情况下，那么一个Nacos可以等于 SpringConfig + SpringCloudBus + Eureka
所以，不香吗？ 最重要的另外一点就是，Nacos是阿里在一直迭代维护的产品，而Eureka2.X目前已经不在维护了，相信这可能也才是很多应用服务都升级为Nacos的重要原因之一
## Nacos使用
Nacos的服务端需要单独下载部署使用，所以项目中使用Nacos的方式都是作为Nacos的client端来使用的；下载地址：https://github.com/alibaba/nacos/releases；；

此处下载完对应的Nacos后，直接startup.cmd进行启动即可，默认的URL访问地址为：http://localhost:8848/nacos/ 默认账号密码为：nacos/nacos

## Nacos服务注册发现于配置管理功能

1、[smart-nacos-client](smart-nacos-client)：实现了Nacos的配置管理于配置更新的功能；

2、smart-nacos-server，smart-nacos-ribbon：实现了Nacos的服务注册与发现功能


## 可参考链接：

https://nacos.io/zh-cn/docs/quick-start-spring-cloud.html

https://juejin.im/post/5dcbf7bc5188250d1f5a78ea

http://www.ityouknow.com/springcloud/2018/07/20/spring-cloud-consul.html
