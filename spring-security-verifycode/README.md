# Spring Security 验证码

自定义一个验证码的话需要实现AuthenticationProvider，AuthenticationProvider 定义了Spring Security中的验证逻辑：

```java
public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication) throws AuthenticationException;
    boolean supports(Class<?> authentication);
}
```

* authenticate 方法用来进行用户验证。
* supports 则用来判断当前的AuthenticationProvider 是否支持对应的 Authentication。

Authentication 则是Spring Security中一个非常重要的对象，可以在任何地方注入 Authentication 进而获取到当前登录用户信息，Authentication 本身是一个接口，它实际上对java.security.Principal做的进一步封装：

```java
public interface Authentication extends Principal, Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
    Object getCredentials();
    Object getDetails();
    Object getPrincipal();
    boolean isAuthenticated();
    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
}
```

1. getAuthorities 方法用来获取用户的权限。
2. getCredentials 方法用来获取用户凭证。
3. getDetails 方法用来获取用户携带的详细信息。
4. getPrincipal 方法用来获取当前用户。
5. isAuthenticated 当前用户是否认证成功。

Authentication有许多的实现类，最常用的是UsernamePasswordAuthenticationToken类，而每一个Authentication都有适合它的AuthenticationProvider去处理校验。例如处理UsernamePasswordAuthenticationToken的AuthenticationProvider是 DaoAuthenticationProvider。

在一次完整的认证中，可能包含多个AuthenticationProvider，而这多个 AuthenticationProvider则由ProviderManager进行统一管理。

使用用户名/密码登录的时候，就是使用DaoAuthenticationProvider进行校验。DaoAuthenticationProvider的父类是 AbstractUserDetailsAuthenticationProvider，由于AbstractUserDetailsAuthenticationProvider已经实现了authenticate和 supports方法，所以自定义一个验证码只需重写additionalAuthenticationChecks 方法即可。

## 思路

* 登录请求是调用 AbstractUserDetailsAuthenticationProvider#authenticate 方法进行认证的
* 然后又会调用到 DaoAuthenticationProvider#additionalAuthenticationChecks 方法做进一步的校验，去校验用户登录密码。
* 可以自定义一个AuthenticationProvider代替DaoAuthenticationProvider，并重写它里边的additionalAuthenticationChecks方法，在重写的过程中，加入验证码的校验逻辑即可。

引入依赖：

```xml
	<dependencies>
		<dependency>
			<groupId>com.github.penggle</groupId>
			<artifactId>kaptcha</artifactId>
			<version>2.3.2</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
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
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

配置验证码：

```java
@Configuration
public class VerifyCodeConfig {
    @Bean
    Producer verifyCode() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
```

添加获取验证码的接口：

```java
@RestController
public class VerifyCodeController {
    private final Producer producer;

    public VerifyCodeController(Producer producer) {
        this.producer = producer;
    }

    @GetMapping("/verifycode")
    public void getCode(HttpServletResponse response, HttpSession session) {
        response.setContentType("image/jpeg");
        String text = producer.createText();
        session.setAttribute("verify_code", text); // 放入session中，以便验证
        BufferedImage image = producer.createImage(text);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

自定义一个 MyAuthenticationProvider 继承自 DaoAuthenticationProvider，并重写 additionalAuthenticationChecks 方法：

```java
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String code = request.getParameter("code"); // 获取到输入的验证码
        String verifyCode = (String) request.getSession().getAttribute("verify_code"); // 从session中获取验证码
        if (code == null || verifyCode == null || !code.equals(verifyCode)) {
            throw new AuthenticationServiceException("验证码错误");
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
```

在 additionalAuthenticationChecks 方法中：

1. 首先获取当前请求
2. 从当前请求中拿到 code 参数，也就是用户传来的验证码。
3. 从 session 中获取生成的验证码字符串。
4. 两者进行比较，如果验证码输入错误，则直接抛出异常。
5. 最后通过 super 调用父类方法，也就是 DaoAuthenticationProvider 的 additionalAuthenticationChecks 方法，该方法中主要做密码的校验。

然后在SecurityConfig内配置到ProviderManager：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 其他略
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(myAuthenticationProvider()));
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/verifycode").permitAll() // 将验证码的url放行
            	// ....
            ;
    }

    @Bean
    MyAuthenticationProvider myAuthenticationProvider() {
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder()); // 
        myAuthenticationProvider.setUserDetailsService(userDetailsService()); // 
        return myAuthenticationProvider;
    }
}
```

