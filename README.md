# smart-cloud-learn
当你走过原始的xml配置方式的Spring项目，又一步一步玩完了SpringBoot的项目，此时又准备跌跌撞撞迎接新的SpringCloud架构项目时，不要担心，不要气馁兄die（跌），因为SpringCloud很简单，你过去的项目架构经历都是最好的铺垫，当你用SpringCloud作为你的新的项目架构时，你会惊叹，哇，好简单！并且Cloud的确完善了很多曾经的架构上的不足，它可以让你的项目架构更加稳定的运行，所以开始吧；


## 推荐几个不错的Cloud资料

现有的SpringCloud网上资料很多很多，建议去看相关资料的时候结合去看，并且记得一定要结合曾经的知识去理解，这样你将会发现，
Cloud的框架的确很简单，很舒服；如果你现有的SOA服务需要做结构升级，Cloud的方式的确是最佳的选择；（当然，建议评估下风险预算后再做升级哦）

1、https://github.com/macrozheng/springcloud-learning

2、https://github.com/forezp/SpringCloudLearning

3、https://github.com/ityouknow/spring-cloud-examples

4、https://spring.io/blog

如果觉得以上几个项目较为抽象，那么也可以参考下本项目来一探究竟，因为本项目就是基于以上几个项目整合后的结果;

## 项目各层级说明：

* [smart-eureka-server](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-eureka-server) 
(port：8001，replica1：8002，replica2：8003)实现了eureka注册中心的多节点互为主备验证,以及当前eurekaServer添加Security认证的方式

* [smart-eureka-client](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-eureka-client) 
(port：8101)用于验证eureka-client于server端的注册

* [smart-cloud-platform/smart-user-service](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-cloud-platform/smart-user-service) 
(port：8201，replica1：8202) user manager service，多个user_service实例的目的是为了验证Ribbon，Feign负载均衡的调用能力

* [smart-cloud-platform/smart-ribbon](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-cloud-platform/smart-ribbon)
(port：8301) 采用ribbon的方式调用user-service服务接口，包含对负载均衡的服务端调用方式说明（随机，轮训，响应时间等）

* [smart-cloud-platform/smart-hystrix-user](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-cloud-platform/smart-hystrix-user)
(port：8302，replica1:8303) ribbon的方式调用user-service服务接口，并包含了一系列的熔断操作（请求缓存，请求合并，服务熔断等）

* [smart-hystrix-turbine](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-hystrix-turbine)
(port：8102) 用来聚合smart-hystrix-user的监控信息，多Hystrix集群监控时使用；

* [smart-hystrix-dashboard](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-hystrix-dashboard)
(port：8103) 单Hystrix实例监控及多Hystrix集群状态监控 [(监控页各展示内容详情可看当前项目下的README说明)](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-hystrix-dashboard)；

* [smart-cloud-platform/smart-feign-user](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-cloud-platform/smart-feign-user) （port：8303）
基于Feign的方式调用user-service服务接口实现了负载均衡的服务调用方式及熔断说明（Feign是基于ribbon实现的客户端服务调用时的负载均衡组件）

* [smart-zuul-proxy](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-zuul-proxy)(port：8401)
基于zuul的代理网关，实现了反向代理，负载均衡及拦截器和熔断功能；

* [smart-nacos](https://github.com/DearZh/smart-cloud-learn/tree/master/smart-nacos) 实现了基于Alibaba Nacos的配置中心和注册中心功能；

**除了上述对各层级的简单说明外，各项目目录下也都有添加针对当前项目的详细说明，详情可查看各项目目录下的README文件；**

## 结束
当前所负责的产品的确存在现有的SOA项目结构向SpringCloud分布式升级的需求，所以smart-cloud-learn也便是作为本人对SpringCloud的探索而存在；
当前项目结构由于是学习使用，相对而言结构并不复杂，为了保持单一性，避免再添加更多的文件，导致项目结构越来越繁杂，所以当前的smart-cloud-learn后续将不再进行大量提交；

不过后续会将已有的项目升级完后的Cloud框架抽出来提交为一个新的项目 [点击我](https://github.com/DearZh/smart-boot)；
但在此之前如果有需要了解真实场景下Cloud使用方式的同学可以先参考GitHub的这个项目：[一款基于电商商城的Cloud项目](https://github.com/paascloud/paascloud-master)
除了启动方式没有使用Docker以外，整体项目结构还是蛮规范的，仅供参考 :octocat: :zap: 
