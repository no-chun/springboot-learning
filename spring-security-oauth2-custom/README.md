# OAuth2自定义Token获取方式

与[OAuth2 的demo](../spring-security-oauth2)一致，完成了基础的配置之后，可以自定义用用户名密码就可以获取Token；

首先在资源服务器上加上一些Spring Security的配置：

```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final MyAuthenticationSuccessHandler authenticationSuccessHandler;

    private final MyAuthenticationFailureHandler authenticationFailureHandler;

    public ResourceServerConfig(MyAuthenticationSuccessHandler authenticationSuccessHandler,
                                MyAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    public void configure(HttpSecurity http)  {
        http.formLogin()
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();
    }

}
```

自定义的登录成功的handler：

```java
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ClientDetailsService clientDetailsService;

    private final AuthorizationServerTokenServices authorizationServerTokenServices;
    private final ObjectMapper mapper;

    public MyAuthenticationSuccessHandler(ClientDetailsService clientDetailsService,
                                          @Qualifier("defaultAuthorizationServerTokenServices")
                                                  AuthorizationServerTokenServices
                                                  authorizationServerTokenServices,
                                          ObjectMapper mapper) {
        this.clientDetailsService = clientDetailsService;
        this.authorizationServerTokenServices = authorizationServerTokenServices;
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }
        String[] token = getToken(header);
        String clientId = token[0];
        String clientSecret = token[1];
        TokenRequest tokenRequest;
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId:" + clientId + "对应的信息不存在");
        } else if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            throw new UnapprovedClientAuthenticationException("clientSecret不正确");
        } else {
            tokenRequest = new TokenRequest(new HashMap<>(), clientId, clientDetails.getScope(), "custom");
        }
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        OAuth2AccessToken tokens = authorizationServerTokenServices.createAccessToken(auth2Authentication);
        LOGGER.info("登录成功");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(mapper.writeValueAsString(new Result(200, "Success", tokens)));
    }

    private String[] getToken(String header) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
        String token = new String(decoded, StandardCharsets.UTF_8);
        int splitIndex = token.indexOf(":");
        if (splitIndex == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, splitIndex), token.substring(splitIndex + 1)};
        }
    }
}
```

* 首先，通过用户名和密码校验后，从Header中提取出`ClientId`和`ClientSecret`
* 然后通过`ClientDetailsService`获取`ClientDetails`，校验`ClientId`和`ClientSecret`的正确性
* 然后通过`createOAuth2Request`方法获取`OAuth2Request`，通过`Authentication`和 `OAuth2Request`构造出 `OAuth2Authentication`
* 然后通过`AuthorizationServerTokenServices `生成 `OAuth2AccessToken`
* 最后返回Token即可

自定义失败的Handler：

```java
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper;

    public MyAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException e)
            throws IOException, ServletException {
        LOGGER.warn("登录失败: " + e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=utf-8");
        Result result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
```

这样一个简单的自定义获取Token的方式就实现了。

获取Token的方式：

```shell script
curl -X POST \
  'http://localhost:8080/login' \
  -H 'Authorization: Basic Y2h1bjpjaHVuX2NodW4=' \
  -H 'Content-Type: application/x-www-form-urlencoded; charset=utf-8' \
  -d 'username=chun&password=123456'
```

