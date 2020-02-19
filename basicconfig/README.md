# spring boot 配置

## 定制banner

在src/main/resources目录下放置ASCII的banner.txt文件即可修改

## 全局配置文件

在src/main/resources目录下的application.properties为全局配置文件，里面可以修改spring boot的一些默认配置：[application.properties中可配置所有官方属性](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

如：

```properties
# 修改默认端口
server.port=8000 
```

### 自定义配置

自定义属性：

```properties
project.name = chun
project.version = 1.0
```

自定义一个bean，然后通过@Value("${属性名}")来加载配置文件中的属性值：

```java
@Component
public class property {
    @Value("${project.name}")
    private String name;

    @Value("${project.version}")
    private float version;
}
```

然后在用`@Autowired`获取bean即可使用；

通过注解`@ConfigurationProperties(prefix="xxx")`即可指明属性的通用前缀，通用前缀加属性名和配置文件的属性名一一对应，方便进一步细分；

在application.properties配置文件中，各个属性可以相互引用，如下

```properties
project.title = project : ${project.name} versiom : ${project.version}
```


## 自定义配置文件

可以在src/main/resources自定义一个配置文件如，test.properties:

```properties
test.name=chun
test.version=2.0
```

注解`@PropertySource("classpath:test.properties")`指明了使用哪个配置文件。

在启动类中使用注解`@EnableConfigurationProperties({TestConfigBean.class})`来启用该配置。或者使用`@Configuration`即可启用配置；