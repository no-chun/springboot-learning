# Spring Boot单元测试

Spring Boot进行测试引入的依赖是：

```xml
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
```

引入的是JUnit5，标准的测试结构如下：

```java
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

常用注解：

| Annotation               | Description                                                  |
| :----------------------- | :----------------------------------------------------------- |
| `@Test`                  | Denotes that a method is a test method. Unlike JUnit 4’s `@Test` annotation, this annotation does not declare any attributes, since test extensions in JUnit Jupiter operate based on their own dedicated annotations. Such methods are *inherited* unless they are *overridden*. |
| `@ParameterizedTest`     | Denotes that a method is a [parameterized test](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests). Such methods are *inherited* unless they are *overridden*. |
| `@RepeatedTest`          | Denotes that a method is a test template for a [repeated test](https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests). Such methods are *inherited* unless they are *overridden*. |
| `@TestFactory`           | Denotes that a method is a test factory for [dynamic tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests). Such methods are *inherited* unless they are *overridden*. |
| `@TestTemplate`          | Denotes that a method is a [template for test cases](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-templates) designed to be invoked multiple times depending on the number of invocation contexts returned by the registered [providers](https://junit.org/junit5/docs/current/user-guide/#extensions-test-templates). Such methods are *inherited* unless they are *overridden*. |
| `@TestMethodOrder`       | Used to configure the [test method execution order](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order) for the annotated test class; similar to JUnit 4’s `@FixMethodOrder`. Such annotations are *inherited*. |
| `@TestInstance`          | Used to configure the [test instance lifecycle](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle) for the annotated test class. Such annotations are *inherited*. |
| `@DisplayName`           | Declares a custom [display name](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-names) for the test class or test method. Such annotations are not *inherited*. |
| `@DisplayNameGeneration` | Declares a custom [display name generator](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-name-generator) for the test class. Such annotations are *inherited*. |
| `@BeforeEach`            | Denotes that the annotated method should be executed *before* **each** `@Test`, `@RepeatedTest`, `@ParameterizedTest`, or `@TestFactory` method in the current class; analogous to JUnit 4’s `@Before`. Such methods are *inherited* unless they are *overridden*. |
| `@AfterEach`             | Denotes that the annotated method should be executed *after* **each** `@Test`, `@RepeatedTest`, `@ParameterizedTest`, or `@TestFactory` method in the current class; analogous to JUnit 4’s `@After`. Such methods are *inherited* unless they are *overridden*. |
| `@BeforeAll`             | Denotes that the annotated method should be executed *before* **all** `@Test`, `@RepeatedTest`, `@ParameterizedTest`, and `@TestFactory` methods in the current class; analogous to JUnit 4’s `@BeforeClass`. Such methods are *inherited* (unless they are *hidden* or *overridden*) and must be `static` (unless the "per-class" [test instance lifecycle](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle) is used). |
| `@AfterAll`              | Denotes that the annotated method should be executed *after* **all** `@Test`, `@RepeatedTest`, `@ParameterizedTest`, and `@TestFactory` methods in the current class; analogous to JUnit 4’s `@AfterClass`. Such methods are *inherited* (unless they are *hidden* or *overridden*) and must be `static` (unless the "per-class" [test instance lifecycle](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle) is used). |
| `@Nested`                | Denotes that the annotated class is a non-static [nested test class](https://junit.org/junit5/docs/current/user-guide/#writing-tests-nested). `@BeforeAll` and `@AfterAll` methods cannot be used directly in a `@Nested` test class unless the "per-class" [test instance lifecycle](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle) is used. Such annotations are not *inherited*. |
| `@Tag`                   | Used to declare [tags for filtering tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering), either at the class or method level; analogous to test groups in TestNG or Categories in JUnit 4. Such annotations are *inherited* at the class level but not at the method level. |
| `@Disabled`              | Used to [disable](https://junit.org/junit5/docs/current/user-guide/#writing-tests-disabling) a test class or test method; analogous to JUnit 4’s `@Ignore`. Such annotations are not *inherited*. |
| `@Timeout`               | Used to fail a test, test factory, test template, or lifecycle method if its execution exceeds a given duration. Such annotations are *inherited*. |
| `@ExtendWith`            | Used to [register extensions declaratively](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-declarative). Such annotations are *inherited*. |
| `@RegisterExtension`     | Used to [register extensions programmatically](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic) via fields. Such fields are *inherited* unless they are *shadowed*. |
| `@TempDir`               | Used to supply a [temporary directory](https://junit.org/junit5/docs/current/user-guide/#writing-tests-built-in-extensions-TempDirectory) via field injection or parameter injection in a lifecycle method or test method; located in the `org.junit.jupiter.api.io` package. |

```java
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 定义测试顺序的方案
class SpringbootTestApplicationTests {
    private static int num;

    @BeforeAll // 会在所有测试方法前执行，必须是static方法
    static void beforeAll() {
        System.out.println("Before All");
    }

    @AfterAll // 会在所有测试方法后执行，必须是static方法
    static void afterAll() {
        System.out.println("After All");
    }

    @BeforeEach // 每个测试方法前会执行
    void beforeEach() {
        System.out.println("Before Each");
    }

    @AfterEach // 每个测试方法后会执行
    void afterEach(){
        System.out.println("After Each");
    }

    @Test
    @DisplayName("Test 1") //测试的名字
    void Test1() {
        assertEquals(2, 1 + 1); // 断言，类似的还有assertSame、assertTrue、assertNotNull等
    }

    @Test
    @DisplayName("Test 2")
    void Test2() {
        assertTimeout(Duration.ofSeconds(1), () -> Thread.sleep(500)); // 超时断言
    }

    @Test
    void exceptionTest() {
        assertThrows(Exception.class, () -> { // 异常断言
            throw new Exception("Error");
        });
    }
    
     @RepeatedTest(2) // 重复测试
    void repeatedTest() {
        System.out.println("Repeated Test");
    }

    @Test
    @Disabled // 让测试失效
    void disabled() {
        System.out.println("This won't happen.");
    }

    @ParameterizedTest // 参数化测试
    // @ValueSource: 为参数化测试指定入参来源，支持八大基础类以及String类型,Class类型
    // @NullSource: 表示为参数化测试提供一个null的入参
    // @EnumSource: 表示为参数化测试提供一个枚举入参
    @ValueSource(strings = {"a", "b", "c"})
    void palindromes(String s) {
        assertTrue("abc".contains(s));
    }

    @Test
    @Order(1) // 测试顺序为1
    void setNum() {
        num = 1;
    }

    @Test
    @Order(2) // 测试顺序为2
    void testOrder() {
        assertEquals(1, num);
    }
}
```

MockMvc是模拟一个MVC的环境进行测试，在测试前需要进行初始化：

```java
@SpringBootTest
class UserControllerTest {
    private static MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
```

然后就可以模拟GET、PUT、POST等请求了，然后就可以利用MockMvc处理返回结果，进行判断。

假设用户模型为：

```java
public class User {
    private long id;
    private String name;

    public User() {
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

模拟一个Service：

```java
@Service
public class UserService {
    private List<User> users = new ArrayList<>();

    public void add(User user) {
        users.add(user);
    }

    public void delete(User user) {
        for (User u : users) {
            if (u.getId() == user.getId()) {
                users.remove(u);
                return;
            }
        }
    }

    public User getById(long id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User getByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }
}
```

然后定义接口：

```java
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("user/{userName}")
    public User getUserByName(@PathVariable(value = "userName") String userName) {
        return this.userService.getByName(userName);
    }

    @PostMapping("user/save")
    public void saveUser(@RequestBody User user) {
        this.userService.add(user);
    }
}
```

测试接口：

```java
@SpringBootTest
class UserControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userService.add(new User(1, "chun"));
        userService.add(new User(2, "cccc"));
    }

    @Test
    public void test() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/user/{userName}", "chun") // GET请求，定义参数
                        .contentType(MediaType.APPLICATION_JSON)) // GET请求的类型为JSON
                .andExpect(MockMvcResultMatchers.status().isOk()) // 请求状态为200
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("chun")) // 判断返回的JSON的正确性
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testSave() throws Exception {
        User user = new User(3, "xxx");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
```

