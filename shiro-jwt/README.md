# Shiro中使用JWT

首先引入依赖：

```xml
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.auth0</groupId>
			<artifactId>java-jwt</artifactId>
			<version>3.10.3</version>
		</dependency>
		<dependency>
			<groupId>org.apache.shiro</groupId>
			<artifactId>shiro-spring</artifactId>
			<version>1.5.3</version>
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

然后，与[Shiro demo](../shirodemo)一样搭建好基于角色的访问控制（Role-Based Access Control ，RBAC）的模型。

然后实现一个简单的JWT的工具类：

```java
public class JwtUtil {
    private static final long EXPIRE_TIME = 10 * 60 * 1000;

    public static boolean verify(String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static String sign(String username, String secret) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    public static String extract(String token) {
        if (token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return token;
    }
}
```

实现完成后，就需要配置Shiro：

* 首先实现一个**JWTToken**，它是进行验证的凭证，要传入Realm里进行JWT验证的，所以需要实现AuthenticationToken接口：

  ```java
  public class JWTToken implements AuthenticationToken {
      private String token;
      public JWTToken(String token) {
          this.token = token;
      }
      @Override
      public Object getPrincipal() {
          return token;
      }
      @Override
      public Object getCredentials() {
          return token;
      }
      public String getToken() {
          return token;
      }
      public void setToken(String token) {
          this.token = token;
      }
  }
  ```

* 然后就实现Realm：

  ```java
  public class MyShiroRealm extends AuthorizingRealm {
      @Resource
      private UserInfoService userInfoService;
  
      @Override
      public boolean supports(AuthenticationToken token) {
          return token instanceof JWTToken;
      }
      
      @Override
      protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
          System.out.println("权限验证");
          String username = JwtUtil.getUsername(principalCollection.toString());
          UserInfo userInfo = userInfoService.findByUsername(username);
          SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
          for (SysRole role : userInfo.getRoleList()) {
              authorizationInfo.addRole(role.getRole());
              for (SysPermission permission : role.getPermissions()) {
                  authorizationInfo.addStringPermission(permission.getPermission());
              }
          }
          return authorizationInfo;
      }
  
      @Override
      protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
          System.out.println("Token验证");
          String token = (String) authenticationToken.getCredentials();
          String username = JwtUtil.getUsername(token);
          if (username == null) {
              throw new AuthenticationException("token invalid");
          }
          UserInfo userInfo = userInfoService.findByUsername(username);
          if (userInfo == null) {
              throw new AuthenticationException("User didn't existed!");
          }
          if (!JwtUtil.verify(token, userInfo.getUsername(), userInfo.getPassword())) {
              throw new AuthenticationException("Username or password error");
          }
          System.out.println("验证成功");
          return new SimpleAuthenticationInfo(token, token, getName());
      }
  }
  ```

* 实现Realm后，需要实现一个Filter，实现Token的生成和拦截各种URL的功能：

  ```java
  public class JWTFilter extends BasicHttpAuthenticationFilter {
      @Override
      protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
          HttpServletRequest req = (HttpServletRequest) request;
          String authorization = req.getHeader("Authorization");
          return authorization != null;
      }
  
      @Override
      protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
          HttpServletRequest httpServletRequest = (HttpServletRequest) request;
          String authorization = httpServletRequest.getHeader("Authorization");
          JWTToken token = new JWTToken(JwtUtil.extract(authorization));
          getSubject(request, response).login(token);
          return true;
      }
  
      @Override
      protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
          if (isLoginAttempt(request, response)) {
              try {
                  executeLogin(request, response);
              } catch (Exception ignored) {
              }
          }
          return true;
      }
  }
  ```

  所有的请求都会先经过 `Filter`，所以继承官方的 `BasicHttpAuthenticationFilter` ，并且重写鉴权的方法。

  代码的执行流程 `preHandle` -> `isAccessAllowed` -> `isLoginAttempt` -> `executeLogin` 。

  首先利用`isLoginAttempt`判断是否想要登入，即检测Header里面是否包含Authorization字段，若是尝试登入，则执行`executeLogin`，`executeLogin`则是获取到Header里的Authorization字段的值，然后就生成一个JWTToken，提交给realm进行登入，如果realm出现错误则会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true。

* 实现完JWTFilter后就开始配置Shiro了：

  ```java
  @Configuration
  public class ShiroConfig {
      @Bean
      public MyShiroRealm myShiroRealm() {
          return new MyShiroRealm();
      }
  
      @Bean
      public SecurityManager securityManager() {
          DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
          securityManager.setRealm(myShiroRealm());
          // 关闭session
          DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
          DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
          defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
          subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
          securityManager.setSubjectDAO(subjectDAO);
          return securityManager;
      }
  
      @Bean
      public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
          System.out.println("ShiroConfiguration.shirFilter()");
          ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
          Map<String, Filter> filterMap = new HashMap<>();
          filterMap.put("jwt", new JWTFilter());
          factoryBean.setFilters(filterMap);
          factoryBean.setSecurityManager(securityManager);
          factoryBean.setUnauthorizedUrl("/403");
          Map<String, String> filterRuleMap = new HashMap<>();
          filterRuleMap.put("/**", "jwt");
          filterRuleMap.put("/403", "anon");
          factoryBean.setFilterChainDefinitionMap(filterRuleMap);
          return factoryBean;
      }
  
      @Bean
      public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
          AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
          authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
          return authorizationAttributeSourceAdvisor;
      }
  
      @Bean(name = "simpleMappingExceptionResolver")
      public SimpleMappingExceptionResolver
      createSimpleMappingExceptionResolver() {
          SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
          Properties mappings = new Properties();
          mappings.setProperty("DatabaseException", "databaseError");
          mappings.setProperty("UnauthorizedException", "403");
          r.setExceptionMappings(mappings);
          r.setDefaultErrorView("error");
          r.setExceptionAttribute("ex");
          return r;
      }
  }
  ```

  最重要的就是将Filter添加到配置当中，然后配置相关路由的规则；