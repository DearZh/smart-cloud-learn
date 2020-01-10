#smart-ribbon
1、使用Ribbon的方式，作为Client端来调用对应的smart-user-service服务层接口数据，除此之外，主要还验证下对应的
Ribbon的负载均衡能力；

2、smart-ribbon，对接完成user-service后，（分别启动eureka-server，user-server,user-server-replica1，smart-ribbon）即可；
然后分别多次访问当前链接：http://localhost:8202/user/1 
可以看到，每次的日志输出分别来自于两个不同的user-server的实例中，则说明请求的分发效果成功

3、此处说明一下：
Ribbon和Feign都是SpringCloud中实现服务调用的负载均衡的组件；是的，Ribbon和Feign都是客户端实现负载均衡的组件，
而对于具体服务调用时的细节，则是SpringWeb模块中所提供的RestTemplate进行的实现，（RestTemplate是一个基于Rest规范所提供的Http请求的工具）

一般我们在注册@Bean RestTemplate类时，都会使用到@LoadBalanced注解，那么此时该注解的作用便是，在RestTemplate进行服务调用时会进行一层
对应的拦截器，及此处的Ribbon实现；RestTemplate在进行对应的服务调用时，Ribbon会先拦截后进行负载的判断，然后将对应的请求再指向具体的服务
进行发送（可参考：https://blog.csdn.net/m0_37179470/article/details/84677180）

而Feign和Ribbon的关系则是，Feign是基于Ribbon的接口上进行的一次改进，无需再使用原生的RestTemplate进行数据的发送，而是直接采用接口的方式进行了
负载均衡的调用实现（可参考：https://www.leiue.com/difference-between-feign-and-ribbon）


可参考链接：

https://juejin.im/post/5d7f9006f265da03951a260c

Ribbon定义负载均衡策略的几种方式

https://blog.csdn.net/qq_33404395/article/details/80899375

Ribbon负载均衡的策略原理

https://blog.csdn.net/wudiyong22/article/details/80829808

Feign的远程调用实现及负载方式（Feign集成了Ribbon的技术，所以负载的配置方式于Ribbon相同）

https://blog.csdn.net/qq_33404395/article/details/80910996
