#smart-feign-user
实现了基于Feign的请求调用方式，Feign的jar依赖中已经依赖了hystrix熔断，所以此处无需再引入hystrix的包

当使用Feign时，Spring Cloud集成了Ribbon和Eureka以提供负载均衡的服务调用及基于Hystrix的服务容错保护功能
（smart-feign-user此处共说明了feign的负载均衡调用能力，及hystrix的熔断调用能力，以及Feign的http请求细节日志输出）


可参考链接：

https://juejin.im/post/5d9c85c3e51d45782c23fab6