# Spring Security 表单登录 2

## 前后端分离

前后端分离的架构下，交互都是通过JSON来进行，不会存在什么URL跳转。

因此登录成功或失败后只需返回JSON格式的相应的数据即可。

登录成功的处理需要调用`successHandler()`:

successHandler 方法的参数是一个 AuthenticationSuccessHandler 对象，这个对象中我们要实现的方法是 onAuthenticationSuccess。

onAuthenticationSuccess 方法有三个参数，分别是：

- HttpServletRequest
- HttpServletResponse
- Authentication

可以定义成一个类：

```java
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(principal));
        out.flush();
        out.close();
    }
}
```

然后再SecurityConfig内注入后到`loginSuccessHandler`后，调用`successHandler(loginSuccessHandler)`即可。

也可以简写成lambda表达式：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            // ***
            .successHandler(((req, resp, authentication) -> { 
                Object principal = authentication.getPrincipal();
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                out.write(new ObjectMapper().writeValueAsString(principal));
                out.flush();
                out.close();
            }));
    }
}
```

同理，登录失败需要调用`failureHandler()`：

```java
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(e.getMessage());
        out.flush();
        out.close();
    }
}
```

注销需要调用`logoutSuccessHandler()`:

```java
@Component
public class LogoutHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("success");
        out.flush();
        out.close();
    }
}
```

未认证处理需要调用`authenticationEntryPoint()`:

```java
@Component
public class NoLoginHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write("尚未登录，请先登录");
        out.flush();
        out.close();
    }
}
```

## 权限验证

在 Spring Security 的 configure(HttpSecurity http) 方法中配置权限验证的规则即可：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // ***
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
            // *****
            ;
    }
}
```

这里的匹配规则采用了 Ant 风格的路径匹配符，它的匹配规则：

| 通配符 | 含义             |
| :----- | :--------------- |
| **     | 匹配多层路径     |
| *      | 匹配一层路径     |
| ?      | 匹配任意单个字符 |

antMatchers必须在anyRequest之前；

**角色继承**：如果要实现角色继承，例如所有 user 能够访问的资源，admin 都能够访问，则需要在 SecurityConfig 中添加如下代码来配置角色继承关系即可：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }
}
```

## 添加数据库

Spring Security 支持多种不同的数据源，这些不同的数据源最终都将被封装成 UserDetailsService 的实例，可以创建一个类实现UserDetailsService 接口，也可以直接使用系统默认提供的 UserDetailsService 实例。

 UserDetailsService的实现类中除了InMemoryUserDetailsManager外，还可以使用JdbcUserDetailsManager，通过JDBC的方式将让Spring Security使用数据库。

JdbcUserDetailsManager 自己提供了一个数据库模型，这个数据库模型保存在如下位置：

`org/springframework/security/core/userdetails/jdbc/users.ddl`；

```sql
create table users(username varchar_ignorecase(50) not null primary key,password varchar_ignorecase(500) not null,enabled boolean not null);
create table authorities (username varchar_ignorecase(50) not null,authority varchar_ignorecase(50) not null,constraint fk_authorities_users foreign key(username) references users(username));
create unique index ix_auth_username on authorities (username,authority);
```

但是MySQL需要改写一下：

```sql
create table users
(
    username varchar(50)  not null
        primary key,
    password varchar(500) not null,
    enabled  tinyint(1)   not null
);
create table authorities
(
    username  varchar(50) not null,
    authority varchar(50) not null,
    constraint ix_auth_username
        unique (username, authority),
    constraint fk_authorities_users
        foreign key (username) references users (username)
);
```

执行完 SQL 脚本后，一共创建了两张表：users 和 authorities。

- users 表中保存用户的基本信息，包括用户名、用户密码以及账户是否可用。
- authorities 中保存了用户的角色。

配置完成后，实现JdbcUserDetailsManager：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    DataSource dataSource;
    
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        if (!manager.userExists("tom")) {
            manager.createUser(User.withUsername("tom").password("tom").roles("user").build());
        }
        if (!manager.userExists("admin")) {
            manager.createUser(User.withUsername("admin").password("xxx").roles("admin").build());
        }
        return manager;
    }
}
```

* 首先生成一个JdbcUserDetailsManager
* 然后设置其DataSource即可
* 创建user与使用InMemoryUserDetailsManager时一样

也可以通过编写api实现来添加用户、修改权限等功能；

