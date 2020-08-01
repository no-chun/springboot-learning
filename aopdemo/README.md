# Aop 

Aop的一个demo

Aop即面向切面编程，可以进一步降低业务中组件之间的耦合度。

主要功能：日志记录，性能统计，安全控制，事务处理，异常处理等等。

以实现一个日志记录为例：

引入依赖：

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
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
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

定义日志的模型：

```java
public class LogMessage {
    private Long id;
    private String username;
    private String operation;
    private Long time;
    private String method;
    private String params;
    private String ip;
    private Date createTime;
}
```

数据库初始化的文件：

```sql
drop table if exists log_message;

create table log_message
(
	id bigint auto_increment,
	username varchar(50) null,
	operation varchar(50) null,
	time numeric(11) null,
	method varchar(100) null,
	params varchar(200) null,
	ip varchar(64) null,
	create_time date null,
	constraint log_message_pk
		primary key (id)
);
```

定义一个自己的注解`@log`,用于标注需要监控的方法：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value() default "";
}
```

使用jpa来操作数据库，具体略；

定义切面和切点：

```java
@Aspect
@Component
public class LogAspect {
    private final LogService logService;

    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("@annotation(com.chun.aopdemo.annotation.Log)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint point) {
        long beginTime = System.currentTimeMillis();
        try {
            point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - beginTime;
        saveLog(point, time);
    }

    private void saveLog(ProceedingJoinPoint point, Long time) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        LogMessage logMessage = new LogMessage();
        Log log = method.getAnnotation(Log.class);
        if (log != null) {
            logMessage.setOperation(log.value());
        }
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        logMessage.setMethod(className + "." + methodName + "()");
        Object[] args = point.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                params.append("  ").append(paramNames[i]).append(": ").append(args[i]);
            }
            logMessage.setParams(params.toString());
        }
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        logMessage.setIp(IPUtils.getIpAddr(request));
        logMessage.setUsername("admin");
        logMessage.setTime(time);
        logMessage.setCreateTime(new Date());
        logService.saveLog(logMessage);
    }

}
```

定义一个切面的类，使用`@Pointcut`定义切点，切点为使用@Log注解标注的方法。然后实现aop的`@After`、`@Before`、`@Around`注解，其中`@Before`是在所拦截方法执行之前执行一段逻辑。`@After `是在所拦截方法执行之后执行一段逻辑。`@Around`是可以同时在所拦截方法的前后执行一段逻辑。

然后进行测试即可；