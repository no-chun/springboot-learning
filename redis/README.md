# Redis

Redis属于NoSQL，Redis主要的应用场景是缓存、分布式会话、分布式锁、排行榜等。

## 使用Redis实现缓存

引入依赖：

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
```

配置Redis：

```properties
# Redis数据库索引（默认为0）
spring.redis.database=0
# Redis服务器地址
spring.redis.host=localhost
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接池最大连接数（使用负值表示没有限制） 默认 8
spring.redis.lettuce.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
spring.redis.lettuce.pool.max-wait=-1
# 连接池中的最大空闲连接 默认 8
spring.redis.lettuce.pool.max-idle=8
# 连接池中的最小空闲连接 默认 0
spring.redis.lettuce.pool.min-idle=0
```

添加 cache 的配置类

```java
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(o.getClass().getName());
                stringBuilder.append(method.getName());
                for(Object object:objects){
                    stringBuilder.append(object.toString());
                }
                return stringBuilder.toString();
            }
        };
    }
}
```

KeyGenerator实际上就是生成Key的，使用@EnableCaching来开启缓存。

测试redis：

```java
@SpringBootTest
class RedisApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSet() {
        stringRedisTemplate.opsForValue().set("a", "1");
        assertEquals("1", stringRedisTemplate.opsForValue().get("a"));
    }

    @Test
    public void testUser() throws InterruptedException {
        User user = new User("test@xxx.com", "chun", "xxxxxx", "chun", "123");
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        operations.set("com.chun", user);
        operations.set("com.xxx", user, 1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        assertEquals("chun", operations.get("com.xxx").getUserName());
    }

}
```

根据方法生成缓存：

```java
@RestController
public class UserController {

    @RequestMapping("/user")
    @Cacheable(value = "user-key")
    public User getUser(){
        User user = new User("test@xxx.com", "chun", "xxxxxx", "chun", "123");
        System.out.println("只会出现一次，缓存后不会出现");
        return user;
    }
}
```

## 共享Session

分布式系统中，Session 共享有很多的解决方案，其中托管到缓存中应该是最常用的方案之一，Spring Session则是默认采用redis来存储session数据。

引入依赖：

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

进行配置：

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400*30)
public class SessionConfig{
}
```

其中maxInactiveIntervalInSeconds设置 Session 失效时间。

然后在Controller中测试：

```java
@RestController
public class TestController {
    @RequestMapping("/uid")
    String uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return session.getId();
    }
}
```

访问一次http://localhost:8080/uid后，然后就可以在Redis中查询到了。

