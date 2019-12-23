#smart-ribbon
1、使用Ribbon的方式，作为Client端来调用对应的smart-user-service服务层接口数据，除此之外，主要还验证下对应的
Ribbon的负载均衡能力；

2、smart-ribbon，对接完成user-service后，（分别启动eureka-server，user-server,user-server-replica1，smart-ribbon）即可；
然后分别多次访问当前链接：http://localhost:8202/user/1 
可以看到，每次的日志输出分别来自于两个不同的user-server的实例中，则说明请求的分发效果成功



可参考链接：

https://juejin.im/post/5d7f9006f265da03951a260c

Ribbon定义负载均衡策略的几种方式

https://blog.csdn.net/qq_33404395/article/details/80899375

Ribbon负载均衡的策略原理

https://blog.csdn.net/wudiyong22/article/details/80829808

Feign的远程调用实现及负载方式（Feign集成了Ribbon的技术，所以负载的配置方式于Ribbon相同）

https://blog.csdn.net/qq_33404395/article/details/80910996
