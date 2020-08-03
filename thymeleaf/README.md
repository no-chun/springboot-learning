# Thymeleaf

Thymeleaf是一个模板引擎，是Spring推荐使用的模板引擎。

有以下几个优点：

* 有网络和无网络的环境下皆可运行

* 开箱即用

* 提供 Spring 标准方言和一个与 SpringMVC 完美集成的可选模块，可以快速的实现表单绑定、属性编辑器、国际化等功能。

引入依赖：

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
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
    </dependencies>
```

Thymeleaf的默认配置：

```properties
#开启模板缓存（默认值：true）
spring.thymeleaf.cache=true 
#Check that the template exists before rendering it.
spring.thymeleaf.check-template=true 
#检查模板位置是否正确（默认值:true）
spring.thymeleaf.check-template-location=true
#Content-Type的值（默认值：text/html）
spring.thymeleaf.content-type=text/html
#开启MVC Thymeleaf视图解析（默认值：true）
spring.thymeleaf.enabled=true
#模板编码
spring.thymeleaf.encoding=UTF-8
#要被排除在解析之外的视图名称列表，用逗号分隔
spring.thymeleaf.excluded-view-names=
#要运用于模板之上的模板模式。另见StandardTemplate-ModeHandlers(默认值：HTML5)
spring.thymeleaf.mode=HTML5
#在构建URL时添加到视图名称前的前缀（默认值：classpath:/templates/）
spring.thymeleaf.prefix=classpath:/templates/
#在构建URL时添加到视图名称后的后缀（默认值：.html）
spring.thymeleaf.suffix=.html
#Thymeleaf模板解析器在解析器链中的顺序。默认情况下，它排第一位。顺序从1开始，只有在定义了额外的TemplateResolver Bean时才需要设置这个属性。
spring.thymeleaf.template-resolver-order=
#可解析的视图名称列表，用逗号分隔
spring.thymeleaf.view-names=
```

一般开发时会将模板缓存关掉，置为false，便于边修改边观察页面；

定义一个简单的模型：

```java
public class User {
    private String name;
    private int age;
}
```

编写Controller：

```java
@Controller
public class TestController {

    private List<User> users = new ArrayList<User>();

    @RequestMapping("/users")
    public String getUser(Model model) {
        model.addAttribute("userList", users);
        return "Users";
    }

    @RequestMapping("/form")
    public String form(Model model) {
        model.addAttribute("user", new User());
        return "Add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute(value = "user") User user) {
        users.add(user);
        return "redirect:/users";
    }
}
```

当未使用Restful进行前后端分离时，一般Controller采用`@Controller`。

一般从后台传入数据使用addAttribute，它是Spring中Model的一个方法。

Users.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" th:href="@{/css/style.css}" type="text/css">
</head>
<body>
<h1 th:text="${#dates.createNow()}">Time:</h1>
<table>
    <tr>
        <th>id</th>
        <th>name</th>
        <th>age</th>
    </tr>
    <tr th:each="user, stat : ${userList}">
        <td th:text="${stat.count}">1</td>
        <td th:text="${user.name}">chun</td>
        <td th:text="${user.age}">22</td>
    </tr>
</table>
</body>
</html>
```

Add.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
</head>
<body>
<div>
    <form th:action="@{/add}" th:object="${user}" method="post">
        <input type="text" th:field="*{name}"/>
        <input type="text" th:field="*{age}"/>
        <input type="submit"/>
    </form>
</div>
</body>
</html>
```

Thymeleaf的标准表达式分为四类：

1. 变量表达式

2. 选择表达式

3. 文字国际化表达式

4. url表达式

## 变量表达式

变量表达式即 OGNL 表达式或 Spring EL 表达式(在 Spring 术语中也叫 model attributes)。

它们将以HTML标签的一个属性来表示：

```html
<tr th:each="user, stat : ${userList}">
    <td th:text="${stat.count}">1</td>
    <td th:text="${user.name}">chun</td>
    <td th:text="${user.age}">22</td>
</tr>
```

## 选择表达式

选择表达式很像变量表达式，不过它们用一个预先选择的对象来代替上下文变量容器(map)来执行，如下：*{name}

被指定的 object 由 th:object 属性定义：

```html
<form th:action="@{/add}" th:object="${user}" method="post">
    <input type="text" th:field="*{name}"/>
    <input type="text" th:field="*{age}"/>
    <input type="submit"/>
</form>
```

## 文字国际化表达式

文字国际化表达式允许我们从一个外部文件获取区域文字信息(.properties)，用 Key 索引 Value。

## URL 表达式

URL 表达式指的是把一个有用的上下文或回话信息添加到 URL，这个过程经常被叫做 URL 重写：`@{/user/list}`

URL还可以设置参数：`@{/user/details(id=${userId})}`

相对路径：`@{../list}`

## 常用th标签

| 关键字      | 功能介绍                                     | 案例                                                         |
| :---------- | :------------------------------------------- | :----------------------------------------------------------- |
| th:id       | 替换id                                       | `<input th:id="'xxx' + ${collect.id}"/>`                     |
| th:text     | 文本替换                                     | `<p th:text="${collect.description}">description</p>`        |
| th:utext    | 支持html的文本替换                           | `<p th:utext="${htmlcontent}">conten</p>`                    |
| th:object   | 替换对象                                     | `<div th:object="${session.user}"> `                         |
| th:value    | 属性赋值                                     | `<input th:value="${user.name}" /> `                         |
| th:with     | 变量赋值运算                                 | `<div th:with="isEven=${prodStat.count}%2==0"></div> `       |
| th:style    | 设置样式                                     | `th:style="'display:' + @{(${sitrue} ? 'none' : 'inline-block')} + ''" ` |
| th:onclick  | 点击事件                                     | `th:onclick="'getCollect()'" `                               |
| th:each     | 属性赋值                                     | `tr th:each="user,userStat:${users}"> `                      |
| th:if       | 判断条件                                     | ` <a th:if="${userId == collect.userId}" > `                 |
| th:unless   | 和th:if判断相反                              | `<a th:href="@{/login}" th:unless=${session.user != null}>Login</a> ` |
| th:href     | 链接地址                                     | `<a th:href="@{/login}" th:unless=${session.user != null}>Login</a> /> ` |
| th:switch   | 多路选择 配合th:case 使用                    | `<div th:switch="${user.role}"> `                            |
| th:case     | th:switch的一个分支                          | `<p th:case="'admin'">User is an administrator</p>`          |
| th:fragment | 布局标签，定义一个代码片段，方便其它地方引用 | `<div th:fragment="alert">`                                  |
| th:include  | 布局标签，替换内容到引入的文件               | `<head th:include="layout :: htmlhead" th:with="title='xx'"></head> /> ` |
| th:replace  | 布局标签，替换整个标签到引入的文件           | `<div th:replace="fragments/header :: title"></div> `        |
| th:selected | selected选择框 选中                          | `th:selected="(${xxx.id} == ${configObj.dd})"`               |
| th:src      | 图片类地址引入                               | `<img class="img-responsive" alt="App Logo" th:src="@{/img/logo.png}" /> ` |
| th:inline   | 定义js脚本可以使用变量                       | `<script type="text/javascript" th:inline="javascript">`     |
| th:action   | 表单提交的地址                               | `<form action="subscribe.html" th:action="@{/subscribe}">`   |
| th:remove   | 删除某个属性                                 | `<tr th:remove="all"> 1.all:删除包含标签和所有的孩子。2.body:不包含标记删除,但删除其所有的孩子。3.tag:包含标记的删除,但不删除它的孩子。4.all-but-first:删除所有包含标签的孩子,除了第一个。5.none:什么也不做。这个值是有用的动态评估。` |

为了模板更加易用，Thymeleaf 还提供了一系列 Utility 对象（内置于 Context 中），可以通过 # 直接访问：

* dates ： java.util.Date的功能方法类。

* calendars : 类似#dates，面向java.util.Calendar

* numbers : 格式化数字的功能方法类

* strings : 字符串对象的功能类，contains,startWiths,prepending/appending等等。

* objects: 对objects的功能类操作。

* bools: 对布尔值求值的功能方法。

* arrays：对数组的功能类方法。

* lists: 对lists功能类方法

* sets：对set功能类方法

* maps：对Map功能类方法

