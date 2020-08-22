# OAuth2

OAuth2的作用在于第三方应用在用户授权的情况下，通过认证服务器，访问相关资源。

主要有以下几个概念：

1. **Third-party application**: 第三方应用
2. **Resource Owner**: 资源持有者
3. **Authorization server**: 认证服务器
4. **Resource server**: 资源服务器
5. **User Agent**: 用户代理
6. **Http Service**: 服务提供者

协议流程：

```
     +--------+                               +---------------+
     |        |--(A)- Authorization Request ->|   Resource    |
     |        |                               |     Owner     |
     |        |<-(B)-- Authorization Grant ---|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(C)-- Authorization Grant -->| Authorization |
     | Client |                               |     Server    |
     |        |<-(D)----- Access Token -------|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(E)----- Access Token ------>|    Resource   |
     |        |                               |     Server    |
     |        |<-(F)--- Protected Resource ---|               |
     +--------+                               +---------------+

                     Figure 1: Abstract Protocol Flow
```

* 客户端向资源所有者请求授权
* 客户机收到的是一个授权许可，它是一个代表资源所有者授权的凭证。
* 客户机通过授权许可，申请访问令牌。
* 授权服务器对客户端进行认证，并验证授权许可，如果有效，则发出一个访问令牌。
* 客户机向资源方请求保护的资源服务器，并通过出示访问令牌进行验证。
* 资源服务器验证访问令牌，如果有效，则服务于该请求。

RFC6749中有4种取得授权许可的方式：

1. 授权码模式
2. 简化模式
3. 密码模式
4. 客户端模式

最常用的是授权码模式和密码模式；

## 授权码模式

```
     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
          ^
          |
         (B)
     +----|-----+          Client Identifier      +---------------+
     |         -+----(A)-- & Redirection URI ---->|               |
     |  User-   |                                 | Authorization |
     |  Agent  -+----(B)-- User authenticates --->|     Server    |
     |          |                                 |               |
     |         -+----(C)-- Authorization Code ---<|               |
     +-|----|---+                                 +---------------+
       |    |                                         ^      v
      (A)  (C)                                        |      |
       |    |                                         |      |
       ^    v                                         |      |
     +---------+                                      |      |
     |         |>---(D)-- Authorization Code ---------'      |
     |  Client |          & Redirection URI                  |
     |         |                                             |
     |         |<---(E)----- Access Token -------------------'
     +---------+       (w/ Optional Refresh Token)

   Note: The lines illustrating steps (A), (B), and (C) are broken into
   two parts as they pass through the user-agent.

                     Figure 3: Authorization Code Flow
```

* 重定位至认证服务器；
* 用户决定是否给客户端授权；
* 同意授权则认证服务器将用户重定位至客户端提供的URL，并附上授权码；
* 客户端通过重定向URL和授权码到认证服务器换取令牌；
* 认证服务器校验无误后发放令牌；

## 密码模式

```
     +----------+
     | Resource |
     |  Owner   |
     |          |
     +----------+
          v
          |    Resource Owner
         (A) Password Credentials
          |
          v
     +---------+                                  +---------------+
     |         |>--(B)---- Resource Owner ------->|               |
     |         |         Password Credentials     | Authorization |
     | Client  |                                  |     Server    |
     |         |<--(C)---- Access Token ---------<|               |
     |         |    (w/ Optional Refresh Token)   |               |
     +---------+                                  +---------------+

            Figure 5: Resource Owner Password Credentials Flow
```

* 资源拥有者向客户端提供密码凭证；
* 客户端通过密码凭证向认证服务器换取令牌；
* 验证通过则向客户端发放令牌。

## Demo

引入依赖：

```xml
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-oauth2</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
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

然后实现与[JPA](../spring-security-jpa)的过程一样，完成相关数据的实现。

先创建认证服务器：

```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

创建认证服务器很简单，只需要在Spring Security的配置类上使用`@EnableAuthorizationServer`注解标注即可。

然后在properties里配置client-id和client-secret（不配置会随机生成）：

```properties
security.oauth2.client.client-id=chun
security.oauth2.client.client-secret=chun_chun
```

然后又要定义重定位的URI：

```properties
security.oauth2.client.registered-redirect-uri=http://localhost:8080/token
```

为了方便获取Token，编写一个Controller返回JSON：

```java
@RestController
public class TokenController {
    @RequestMapping("/token")
    public Map<String, String> get(@RequestParam("code") String code, @RequestParam("state") String state) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("state", state);
        return map;
    }
}
```

这样就完成了授权服务器的搭建。

### 授权码模式

请求授权码的地址：http://localhost:8080/oauth/authorize?response_type=code&client_id=chun&redirect_uri=http://localhost:8080/token&scope=all&state=hello

访问后会先跳转登录界面，登录成功后会跳转到授权页面，在授权页面进行授权后，会重定位到上面定义的URI，然后返回类似下面的JSON：

```json
{
    "code":"ZT7Wii",
    "state":"hello"
}
```

然后就可以利用该授权码申请获取Token了，直接POST相应请求即可：

```bash
curl -X POST \
  'http://localhost:8080/oauth/token' \
  -H 'Authorization: Basic Y2h1bjpjaHVuX2NodW4=' \
  -H 'Content-Type: application/x-www-form-urlencoded; charset=utf-8' \
  -d 'grant_type=password&code=hzCFlG&client_id=chun&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Ftoken&scope=all'
```

### 密码模式

密码模式只需直接POST相应请求即可：

```bash
curl -X POST \
  'http://localhost:8080/oauth/token' \
  -H 'Authorization: Basic Y2h1bjpjaHVuX2NodW4=' \
  -H 'Content-Type: application/x-www-form-urlencoded; charset=utf-8' \
  -d 'username=chun&password=123456&grant_type=password&scope=all'
```

利用这两种方式获取完Token后，就可以利用Token去访问资源了。

然后必须配置资源服务器，客户端就可以通过合法的令牌来获取资源：

```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig  {
}
```

