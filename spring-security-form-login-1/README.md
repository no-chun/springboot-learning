# Spring Security 表单登录

Spring Security 是一个功能强大且高度可自定义的身份验证和访问控制框架,侧重于为 Java 应用程序提供身份验证和授权。

这是一个简单的表单登录的demo，不与数据库进行交互；

首先引入依赖：

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

然后随便定义一个controller:

```java
@RestController
public class TestController {
    @RequestMapping("/")
    public String rout() {
        return "root";
    }
}
```

然后无需配置，直接运行时控制台会生成一个密码，然后访问的时候就会跳转到spring security的默认登录页面，默认的用户名是user，密码就是控制台打印的密码；

若向自定义用户名密码就可以进行配置，在application.properties里进行配置：

```properties
spring.security.user.name=xxxx
spring.security.user.password=xxxx
```

这个配置最终会通过SecurityProperties这个类注入到整个程序里；

除了在properties里进行配置，还可以在配置类中进行配置：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("chun")
                .password("12345678")
                .roles("admin");
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("name")
                .passwordParameter("pwd")
                .defaultSuccessUrl("/")
                .successForwardUrl("/")
                .failureForwardUrl("/failure")
                .failureUrl("/fail")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/")
                .deleteCookies()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .csrf().disable();
    }
}
```

`WebSecurityConfigurerAdapter`是配置WebSecurity的一个适配器，首先需要提供了一个 PasswordEncoder 的实例，就直接提供一个不加密的实例即可；

configure方法即可配置：

```java
// 配置用户名、密码和权限
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("chun")
                .password("12345678")
                .roles("admin");
}
```

如果需要配置多个用户，直接用`and()`进行连接即可；

这样就可以自定义用户名与密码了；

如果需要进一步自定义登录页面，就需要进一步配置，继续重写它的 `configure(WebSecurity web)`和`configure(HttpSecurity http)`方法：

```java
// 用于配置不拦截的url，一般是静态文件的地址
public void configure(WebSecurity webSecurity) {
    webSecurity.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
}

// 用户进行配置HTTP请求
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/login.html")
            .loginProcessingUrl("/login")
            .usernameParameter("name")
            .passwordParameter("pwd")
            .defaultSuccessUrl("/")
            .successForwardUrl("/")
            .failureForwardUrl("/failure")
            .failureUrl("/fail")
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
            .logoutSuccessUrl("/")
            .deleteCookies()
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .permitAll()
            .and()
            .csrf().disable();
}
```

* authorizeRequests() 定义开始请求进行权限配置

* anyRequest().authenticated() ：定义任何地址都需要验证过，任何地址是因为anyRequest()未进行配置，anyRequest()可以进行特定url的配置；

* formLogin().loginPage("/login.html")：formLogin()指定了身份验证的方式，formLogin()和httpBasic()是其两种方式。loginPage("/login.html")则定义其登录的页面是login.html文件，一般默认放在static文件夹下；

* loginProcessingUrl("/login")：指定了登录表单的数据发送的url地址，方式为POST；

* usernameParameter("name")和passwordParameter("pwd")则指定了表单内的用户名和密码的参数的名字，不使用这个进行配置的话，则默认未username和password；

* defaultSuccessUrl("/")则设置的是默认的登录成功的跳转地址，如果是直接在浏览器中输入的登录地址，登录成功后，就直接跳转到 `/`，如果是在其他地址，例如`/users`内，应为未登录，则会跳转到登录页面，登录成功后就会返回到之前的地址；successForwardUrl("/")则是登录成功后直接跳转到`/`。这两个一般配置一个即可。

* failureForwardUrl是登录失败之后会发生服务端跳转，failureUrl 则在登录失败之后，会发生重定向。同样一般配置一个即可。

* permitAll()则将上述的关于验证或注销等相关的url配置为不需要进行身份验证的url；

* logout()则开启登出的接口，默认注销的 URL 是 `/logout`，是一个 GET 请求，可以通过 logoutUrl 方法来修改默认的注销 URL。

* logoutRequestMatcher()则是定义注销的RequestMatcher，RequestMatcher是一个匹配HTTP请求的interface，spring security内置了几个实现的类：

  | 实现类                        | 介绍                                     |
  | ----------------------------- | ---------------------------------------- |
  | `AnyRequestMatcher`           | 匹配任何请求                             |
  | `AntPathRequestMatcher`       | 使用`ant`风格的路径匹配模板匹配请求      |
  | `ELRequestMatcher`            | 使用`EL`表达式匹配请求                   |
  | `IpAddressMatcher`            | 基于`IP`地址匹配请求，支持`IPv4`和`IPv6` |
  | `MediaTypeRequestMatcher`     | 基于`MediaType`匹配请求                  |
  | `RegexRequestMatcher`         | 基于正则表达式匹配请求                   |
  | `RequestHeaderRequestMatcher` | 基于头部值比较匹配请求                   |
  | `AndRequestMatcher`           | `and`组合多个`RequestMatcher`            |
  | `OrRequestMatcher`            | `or`组合多个`RequestMatcher`             |
  | `NegatedRequestMatcher`       | `not`操作一个`RequestMatcher`            |

  AntPathRequestMatcher("/logout", "POST")则新建了一个匹配`/logout`且方法是POST的HTTP请求；

* logoutSuccessUrl("/")则定义了注销成功后要跳转的url；

* deleteCookies()则设置注销后清除cookie；

* clearAuthentication 和 invalidateHttpSession 分别表示清除认证信息和使 HttpSession 失效，默认可以不用配置，默认就会清除。

编写的用户登录的表单：

```html
<div class="col-sm">
    <form action="/login" method="post">
        <div class="form-group">
            <label for="name">用户名</label>
            <input class="form-control" type="text" name="name" id="name">
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input class="form-control" type="password" name="pwd" id="password">
        </div>
        <button type="submit" class="btn btn-primary">登录</button>
    </form>
</div>
```

