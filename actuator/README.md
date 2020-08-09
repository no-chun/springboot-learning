# Actuator

Actuator是用来监控Spring Boot应用的一个框架，可以通过REST接口、远程shell等方式获取信息；

## Actuator REST接口

| HTTP 方法 | 路径            | 描述                                                         |
| --------- | --------------- | ------------------------------------------------------------ |
| GET       | /autoconfig     | 提供了一份自动配置报告，记录哪些自动配置条件通过了，哪些没通过 |
| GET       | /configprops    | 描述配置属性(包含默认值)如何注入Bean                         |
| GET       | /beans          | 描述应用程序上下文里全部的Bean，以及它们的关系               |
| GET       | /dump           | 获取线程活动的快照                                           |
| GET       | /env            | 获取全部环境属性                                             |
| GET       | /env/{name}     | 根据名称获取特定的环境属性值                                 |
| GET       | /health         | 报告应用程序的健康指标，这些值由HealthIndicator的实现类提供  |
| GET       | /info           | 获取应用程序的定制信息，这些信息由info打头的属性提供         |
| GET       | /mappings       | 描述全部的URI路径，以及它们和控制器(包含Actuator端点)的映射关系 |
| GET       | /metrics        | 报告各种应用程序度量信息，比如内存用量和HTTP请求计数         |
| GET       | /metrics/{name} | 报告指定名称的应用程序度量值                                 |
| POST      | /shutdown       | 关闭应用程序，要求endpoints.shutdown.enabled设置为true       |
| GET       | /trace          | 提供基本的HTTP请求跟踪信息(时间戳、HTTP头等)                 |

## 自定义Actuator

自定义Actuator在properties里配置即可：

```properties
info.app.name=spring-boot-actuator
info.app.version= 1.0.0
info.app.test=test

management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoints.web.base-path=/monitor

management.endpoint.shutdown.enabled=true
```

* 修改接口 ID：

  每个Actuator 接口都有一个ID用来决定接口的路径，比方说，/beans接口的默认ID就是beans。比如要修改 /beans 为 /instances，则设置如下：

  ```properties
  endpoints.beans.id = instances
  ```

* 启用和禁用接口：

  默认情况下，所有接口(除 了/shutdown)都启用，需要禁用直接设置为false即可。

  ```properties
  endpoints.enabled = false
  endpoints.metrics.enabled = true
  ```

* 添加自定义度量信息

  Actuator 自动配置有两个实例 CounterService 和 GaugeService 可以用来计数使用，我们所要做的就是把它们的实例注入所需的 bean 然后调用相应的方法。除此之外，我们还可以实现 PublicMetrics 接口，提供自己需要的度量信息。

* 创建自定义跟踪仓库

  默认情况下，/trace 接口报告的跟踪信息都存储在内存仓库里，100个条目封顶。一旦仓库满了，就开始移除老的条目，给新的条目腾出空间。在开发阶段这没什么问题，但在生产环境中，大流量会造成跟踪信息还没来得及看就被丢弃。我们可以将那些跟踪条目存储在其他地方——既不消耗内存，又能长久保存的地方。只需实现Spring Boot的TraceRepository接口即可。

* 插入自定义的健康指示器:

  实现 HealthIndicator 接口则可以实现自定义的健康指示器。

* 保护 Actuator 接口：

  很多Actuator端点发布的信息都可能涉及敏感数据，还有一些端点，(比如/shutdown)非常危险，可以用来关闭应用程序。因此，保护这些端点尤为重要,能访问它们的只能是那些经过授权的客户端。