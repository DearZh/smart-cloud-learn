# smart-nacos-user-server
smart-nacos-user-server 代码全部拷贝自smart-cloud-platform/smart-nacos-user-server下，唯一变更的是application.yml中注册中心的配置代码，由最初的Eureka的注册方式变更为了Nacos的配置

## 目的
smart-nacos-user-server作为smart-nacos-user-ribbon的服务端而存在，目的是为了说明user-ribbon于user-server同时注册在Nacos后，Ribbon的服务发现及负载调用能力
