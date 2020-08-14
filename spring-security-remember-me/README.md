# Spring Security 使用Remember me

大题框架与[之前](../spring-security-jpa)一致，开启Remember me的功能很简单，只需要在 Spring Security 的配置中，添加如下代码即可：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            	// ....
                .rememberMe().key("chun")
                .and()
            	// ...
        ;
    }
}
```

key 默认值是一个 UUID 字符串，每次服务端重启， key 会重新设置为一个新的UUID字符串，就导致之前派发出去的所有 remember-me 自动登录令牌失效，所以可以指定这个 key为一个特定的值。

POST请求所发的表单也多了一个remember-me的属性：

```html
<div class="form-group">
    <label for="name">用户名</label>
    <input class="form-control" type="text" name="username" id="name">
</div>
<div class="form-group">
    <label for="password">密码</label>
    <input class="form-control" type="password" name="password" id="password">
</div>
<div class="form-group">
    <label for="remember">记住我</label>
    <input type="checkbox" name="remember-me" id="remember">
</div>
```

这样就开启了Remember me的功能了。

如果希望令牌实现持久化，那么就需要将其存入数据库，持久化令牌就是在基本的自动登录功能基础上，又增加了新的校验参数，来提高系统的安全性，这一些都是由开发者在后台完成的，对于用户来说，登录体验和普通的自动登录体验是一样的。

在持久化令牌中，新增了两个经过 MD5 散列函数计算的校验参数，一个是 series，另一个是 token。其中，series 只有当用户在使用用户名/密码登录时，才会生成或者更新，而 token 只要有新的会话，就会重新生成，这样就可以避免一个用户同时在多端登录。

持久化令牌的具体处理类在 PersistentTokenBasedRememberMeServices 中,而用来保存令牌的处理类则是 PersistentRememberMeToken：

```java
public class PersistentRememberMeToken {
    private final String username;
    private final String series;
    private final String tokenValue;
    private final Date date;
    // ...
}
```

根据这个类的定义在数据库中（MySQL为例）新建一个表：

```sql
create table persistent_logins
(
    username  varchar(64)                         not null,
    series    varchar(64)                         not null
        primary key,
    token     varchar(64)                         not null,
    last_used timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    collate = utf8mb4_unicode_ci;
```

然后在SecurityConfig内进行配置：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            	// formlogin 之后
                .and()
                .rememberMe().key("chun").tokenRepository(jdbcTokenRepository())
                .and()
            	// logout 之前
        ;
    }
}
```

这样就实现了持久化。

如果要进一步细分权限的化：

* 类似` .anyRequest().authenticated()`，只需验证通过即可，包含自动登录。
* 类似`.antMatchers("/**").rememberMe()`需要 **rememberMe** 才能访问。
* 类似`.antMatchers("/**").fullyAuthenticated()`必须通过用户名和密码验证，不能通过自动登录。