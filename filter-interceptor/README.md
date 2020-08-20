# Filter 和 Interceptor

Filter和Interceptor是Web中两种常见的功能，Filter属于servlet的一个接口，而Interceptor属于AOP的一种实现方式

相关的执行顺序为：

```
filter --> Interceptor --> Aspect --> Controller
```

## Filter

实现一个Filter的话需要实现`java.servlet.Filter`接口：

```java
@Component
@Order(1)
@WebFilter(urlPatterns = {"/index/*"})
public class TimeFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Filter 1 启动！");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.info("---------------------------------------");
        LOGGER.info("执行Filter 1中......");
        long start = new Date().getTime();
        filterChain.doFilter(servletRequest, servletResponse);
        LOGGER.info("Filter 1耗时：" + ((new Date().getTime()) - start));
        LOGGER.info("Filter 1结束");
        LOGGER.info("---------------------------------------");
    }

    @Override
    public void destroy() {
        LOGGER.error("Filter 1 销毁了！");
    }
}
```

* `@Order()`可以设定优先级，值越小拥有越高的优先级。
* 可以通过`@WebFilter(urlPatterns = {})`来指定过滤的URL
* `@Component`注解让`TimeFilter`成为Spring上下文中的一个Bean

除了用`@Component`注解外，还可以使用`FilterRegistrationBean`来注册过滤器。

先编写一个Filter：

```java
public class ParamsFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Filter 2 启动！");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.info("执行Filter 2中......");
        servletRequest.setAttribute("name", "chun");
        servletRequest.setAttribute("pwd", "xxxx");
        String type = (String) servletRequest.getParameter("type");
        LOGGER.info(type);
        filterChain.doFilter(servletRequest, servletResponse);
        servletResponse.setContentType("application/json");
        LOGGER.info("Filter 2结束");
    }

    @Override
    public void destroy() {
        LOGGER.error("Filter 2 销毁了！");
    }
}
```

然后注册：

```java
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ParamsFilter> paramsFilter() {
        FilterRegistrationBean<ParamsFilter> filterRegistrationBean = new FilterRegistrationBean<ParamsFilter>();
        ParamsFilter paramsFilter = new ParamsFilter();
        filterRegistrationBean.setFilter(paramsFilter);
        filterRegistrationBean.setOrder(2);
        List<String> urlList = new ArrayList<>();
        urlList.add("/index/*");
        filterRegistrationBean.setUrlPatterns(urlList);
        return filterRegistrationBean;
    }
}
```

## Interceptor

Interceptor需要实现`org.springframework.web.servlet.HandlerInterceptor`接口：

```java
@Component
public class TimeInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.info(this.getClass() + "拦截之前");
        LOGGER.info(((HandlerMethod) handler).getMethod().getName());
        request.setAttribute("startTime", new Date().getTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOGGER.info(this.getClass() + "开始处理拦截");
        long start = (long) request.getAttribute("startTime");
        LOGGER.info(this.getClass() + "耗时" + (new Date().getTime() - start));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOGGER.info(this.getClass() + "处理拦截后");
        long start = (long) request.getAttribute("startTime");
        LOGGER.info(this.getClass() + "耗时" + (new Date().getTime() - start));
        assert ex != null;
        LOGGER.info("异常信息：" + ex.getMessage());
    }
}
```

`preHandle`方法在处理拦截之前执行，`postHandle`只有当被拦截的方法没有抛出异常成功时才会处理，`afterCompletion`方法无论被拦截的方法抛出异常与否都会执行。

`@Component`只能将Interceptor注册为Bean，要使其生效的话，还需要在WebMvcConfigurer里进行注册：

```java
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final TimeInterceptor timeInterceptor;

    public InterceptorConfig(TimeInterceptor timeInterceptor) {
        this.timeInterceptor = timeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeInterceptor);
    }
}
```

