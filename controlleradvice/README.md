# ControllerAdvice

异常的统一处理一般使用`@ControllerAdvice`或者`@RestControllerAdvice`。第一个返回html，第二个返回json；

而这两者不仅只适用于异常处理，而且适用于全局数据绑定和数据预处理；

这三个功能分别由三个注解实现：

* `@ExceptionHandler`：用于处理全局异常，其异常也可以是自定义异常；
* `@ModelAttribute`：用于全局数据绑定
* `@InitBinder`：用于数据预处理

## 处理全局异常

自定义异常：

```java
public class CustomException extends RuntimeException {
    private String msg;

    public CustomException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
```

捕获全局异常：

* 使用`@RestControllerAdvice`或`@ControllerAdvice`注解
* 定义处理特定异常的方法

```java
@RestControllerAdvice
public class MyExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error(e.getMessage());
        return new Result(500, e.getLocalizedMessage());
    }

    @ExceptionHandler(CustomException.class)
    public Result handlerCustomeException(CustomException e) {
        logger.error(e.getMessage());
        return new Result(500, e.getMsg());
    }
}
```

## 全局数据绑定

定义全局数据：

```java
@RestControllerAdvice
public class AttributeHandler {
    @ModelAttribute(name = "test")
    public Map<String, Object> data() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "chun");
        map.put("age", 18);
        return map;
    }
}
```

获取数据：通过 @ModelAttribute 注解的 name 属性去重新指定 key。

```java
 @RestController
 public class TestController {
    @RequestMapping("/attr")
    public Map<String, Object> attr(Model model) {
        return (Map<String, Object>) model.getAttribute("test");
    }
}
```

## 数据预处理

定义模型：

```java
public class User {
    private String name;
    private String email;
}
```

定义预处理的类：（`@InitBinder`也可以在controller中定义，但是那个只能作用在那一个controller之中）

```java
@RestControllerAdvice
public class InitHandler {
    @InitBinder("user")
    public void init(WebDataBinder dataBinder){
        dataBinder.setFieldDefaultPrefix("user."); // 定义该参数的前缀为user.
    }
}
```

controller中使用：

```java
@RestController
public class TestController {
    @PostMapping("/user")
    public Result setUser(@ModelAttribute("user") User user) {
        System.out.println(user);
        return new Result(200, user.getName() + " : " + user.getEmail());
    }
}
```

