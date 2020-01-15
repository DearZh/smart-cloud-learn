# smart-nacos-user-ribbon
smart-nacos-user-ribbon 代码全部拷贝自smart-cloud-platform/smart-nacos-user-server下，唯一变更的是application.yml中注册中心的配置代码，由最初的Eureka的注册方式变更为了Nacos的配置

## 目的
smart-nacos-user-ribbon作为smart-nacos-user-server的调用方而存在，目的是为了说明user-ribbon于user-server同时注册在Nacos后，Ribbon的服务发现及负载调用能力；

此处smart-nacos-user-server启动多个实例后，再启动smart-nacos-user-ribbon的服务，访问：localhost:9104/user/1 链接将会看到
服务的调用日志分别来自于user-server的多个实例中，此时则说明Ribbon的负载调用及通过Nacos的服务发现功能OK；
