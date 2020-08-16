# Spring Security 使用JWT

JWT，全称是Json Web Token， 是一种JSON风格的轻量级的授权和身份认证规范，可实现无状态、分布式的Web应用授权。

JWT包含三部分数据：

1. Header：头部，通常头部有两部分信息：
   * 声明类型
   * 加密算法，自定义
2. Payload：载荷，即有效数据，里面可以放置一些信息，例如：
   * iss (issuer)：表示签发人
   * exp (expiration time)：表示token过期时间
   * sub (subject)：主题
   * aud (audience)：受众
   * nbf (Not Before)：生效时间
   * iat (Issued At)：签发时间
   * jti (JWT ID)：编号

3. Signature：签名，服务器用来进行验证。

一般使用JWT进行验证的流程：

1. 用户端向服务端请求授权
2. 服务端返回访问的token
3. 客户端使用带token来访问保护的资源

## demo

引入依赖：

```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.2</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.2</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.11.2</version>
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

编写一些简单的JWT的相关静态方法：

```java
public class JwtUtil {
    public static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public static String generateToken(String username, String authorities) {
        return Jwts.builder().claim("authorities", authorities)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(secretKey)
                .compact();
    }
}

```

创建一个简单的实现了UserDetails的User对象：

```java
public class User implements UserDetails {
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
```

然后创建一个测试用的Controller：

```java
@RestController
public class TestController {
    @RequestMapping("/index") // 任何人都可以访问
    public Result index() {
        return new Result(200, "ok", null);
    }

    @RequestMapping("/admin/info") // admin角色访问
    public Result admin() {
        return new Result(200, "ok", "admin");
    }

    @RequestMapping("/user/info") // user角色访问
    public Result user() {
        return new Result(200, "ok", "user");
    }

}
```

然后配置JWT的相关过滤器：

```java
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    public JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException, IOException, ServletException {
        User user = new ObjectMapper().readValue(req.getInputStream(), User.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        StringBuilder auths = new StringBuilder();
        for (GrantedAuthority authority : authorities) {
            auths.append(authority.getAuthority())
                    .append(",");
        }
        String jwt = JwtUtil.generateToken(authResult.getName(), auths.toString());
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(new Result(200, "success", jwt)));
        out.flush();
        out.close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed) throws IOException, ServletException {
        resp.setContentType("application/json;charset=utf-8");
        resp.setStatus(400);
        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(new Result(400, "fail", null)));
        out.flush();
        out.close();
    }
}
```

这个自定义的JwtLoginFilter类用来进行授权：

*  类继承自 AbstractAuthenticationProcessingFilter，并实现其中的三个默认方法
* attemptAuthentication方法是从登录参数中提取出用户名密码，然后调用AuthenticationManager.authenticate()方法去进行自动校验。
* 校验成功就会调用successfulAuthentication，生成JWT返回给客户端
* 校验失败就会调用unsuccessfulAuthentication，返回错误信息

```java
public class JwtFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = req.getHeader("authorization");
        System.out.println(jwt);
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.replace("Bearer ", "");
        }
        try {
            Claims claims = JwtUtil.getClaimsFromToken(jwt);
            String username = claims.getSubject();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(token);
            filterChain.doFilter(req, response);
        } catch (Exception e) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(400);
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(new Result(400, "Token error", null)));
            out.flush();
            out.close();
        }
    }
}
```

这个自定义的JwtFilter类用来进行验证：

* 这个类继承了GenericFilterBean，属于过滤器，首先从请求头中提取出authorization字段，这个字段对应的值就是用户的JWT token。
* 将JWT token转换为一个Claims对象，然后再从Claims对象中提取出当前用户名和角色，创建一个UsernamePasswordAuthenticationToken放到当前的上下文中，然后执行过滤链使请求继续执行下去。这一步还可以进行token过期等验证。

紧接着就是在SecurityConfig内添加这两个Filter：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin")
                .password("123").roles("admin")
                .and()
                .withUser("sang")
                .password("456")
                .roles("user");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/index/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }
}
```

配置规则：

* 路径：`/index`无需进行JWT的验证
* 路径：`/admin/**`需具有admin 角色才能访问，且需要验证JWT
* 路径：`/user/**`需具有user角色才能访问，且需要验证JWT

addFilterAt(A, B.class)为将A拦截器添加到B拦截器的位置，不覆盖该filter，addFilterBefore(A,B.class)为将A拦截器添加到B拦截器之前，addFilterAfter(A,B.class)为将A拦截器添加到B拦截器之后。

到此配置完拦截器后就完成了JWT的demo；