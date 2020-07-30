# Mybatis使用

Mybatis和Hibernate是两种不同的ORM框架，Mybatis可以灵活的调试动态SQL语句。

Maven配置：

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.3</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
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

application.properties配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

mybatis.type-aliases-package=com.chun.mybatis.model
```

Mybatis有两种方式注入mapper：

* 注解：
    
    * 每个mapper的类中添加注解`@Mapper`
    
    * 在启动类上添加注解`@MapperScan(basePackages = "mapper所在的包")`

* xml文件：略


数据模型：

```java
public class User{
    private Long id;
    private String userName;
    private String passWord;
    private UserSexEnum userSex;
    private String nickName;
}
```

MySQL中生成Table：

```sql
create table user
(
    id        bigint auto_increment comment '主键id'
        primary key,
    userName  varchar(32) null comment '用户名',
    passWord  varchar(32) null comment '密码',
    user_sex  varchar(32) null,
    nick_name varchar(32) null
)
    charset = utf8;
```

开发Mapper：

```java
public interface UserMapper {
    @Select("SELECT * FROM USER")
    @Results({
            @Result(property = "userSex", column = "USER_SEX", javaType = UserSexEnum.class),
            @Result(property = "nickName", column = "NICK_NAME")
    })
    List<User> getAll();

    @Select("SELECT * FROM USER WHERE ID = #{id}")
    @Results({
            @Result(property = "userSex", column = "USER_SEX", javaType = UserSexEnum.class),
            @Result(property = "nickName", column = "NICK_NAME")
    })
    User getOne(Long id);

    @Insert("INSERT INTO USER(USERNAME, PASSWORD, USER_SEX) VALUES (#{userName}, #{passWord}, #{userSex})")
    void insert(User user);

    @Update("UPDATE USER SET USERNAME=#{userName},NICK_NAME=#{nickName} WHERE ID =#{id}")
    void update(User user);

    @Delete("DELETE FROM USER WHERE ID =#{id}")
    void delete(Long id);
}
```

* `@Select`是查询类的注解，所有的查询均使用这个

* `@Result`是修饰返回的结果集，关联实体类属性和数据库字段一一对应，如果实体类属性和数据库属性名保持一致，就不需要这个属性来修饰。

* `@Insert`插入数据库使用，直接传入实体类会自动解析属性到对应的值

* `@Update`负责修改，也可以直接传入对象

* `@delete`负责删除

