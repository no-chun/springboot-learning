# Spring boot JPA

Jpa (Java Persistence API) 是 Sun 官方提出的 Java 持久化规范。提供了一种对象/关联映射工具来管理 Java 应用中的关系数据。

Spring Boot Jpa是Spring基于ORM框架和jpa规范封装的一套应用框架；

Spring Boot Jpa的开发流程：

配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

首先定义好使用的MySQL，然后定义jpa，其中hibernate.hbm2ddl.auto定义加载hibernate时自动创建、更新或验证数据库表结构的参数；总共有4个值：

1. create： 每次加载 hibernate 时都会删除上一次的生成的表，然后根据你的 model 类再重新来生成新表，哪怕两次没有任何改变也要这样执行，这就是导致数据库表数据丢失的一个重要原因。
2. create-drop ：每次加载 hibernate 时根据 model 类生成表，但是 sessionFactory 一关闭,表就自动删除。
3. update：最常用的属性，第一次加载 hibernate 时根据 model 类会自动建立起表的结构（前提是先建立好数据库），以后加载 hibernate 时根据 model 类自动更新表结构，即使表结构改变了但表中的行仍然存在不会删除以前的行。要注意的是当部署到服务器后，表结构是不会被马上建立起来的，是要等 应用第一次运行起来后才会。
4. validate ：每次加载 hibernate 时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。

dialect：主要是指定生成表名的存储引擎
show-sql：是否打印出生成的 SQL，开发调试一般设置为true；

配置完成后，定义一个模型：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    private String passWord;

    @Column(unique = true)
    private String email;
    private Date registerTime;
}
```

`@Entity`表明该类为实体类，默认对应数据库中的表名为类的名字；可以使用`@Table`进行数据库表的配置；


`@Id`定义主键，可以用`@GeneratedValue`来使用JPA通用策略生成器，总共有4种，分别为：

* TABLE：使用一个特定的数据库表格来保存主键

* SEQUENCE：在某些数据库中,不支持主键自增长,比如Oracle,使用"序列(sequence)"的机制生成主键

* IDENTITY：主键自增长

* AUTO：把主键生成策略交给持久化引擎(persistence engine),根据数据库在以上三种主键生成策略中选择其中一种。

也可以用`@GenericGenerator`注解自定义主键生成策略生成器,GenericGenerator一般配合GeneratorValue来用,

```java
@Id  
@GeneratedValue(GenerationType.AUTO)
```

通常可与用下面的注解来实现

```java
@GeneratedValue(generator = "tableGenerator")    
@GenericGenerator(name = "tableGenerator", strategy = "assigned")
```

name属性指定生成器名称，strategy属性指定具体生成器的类名。 还有个parameters定义strategy指定的具体生成器所用到的参数。 

具体生成器的类名总共有13个：uuid、hilo、assigned、identity、select、sequence、seqhilo、increment、foreign、guid、uuid.hex、sequence-identity；

`@Column`注解用于配置数据库表中的列，常见配置如nullable、unique等。

Entity中不映射成列的字段得加`@Transient`注解;

定义完成后就可以继承JpaRepository，使用预先生成了一些基本的CURD的方法，也可以自定义查询的SQL语句：

```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Transactional(timeout = 10)
    @Modifying
    @Query("update User set userName = ?1 where id = ?2")
    void updateUserNameById(String username, Long id);

    @Transactional
    @Modifying
    @Query("delete from User where id = ?1")
    void deleteById();

    @Query("select u from User u")
    Page<User> findAll(Pageable pageable);

}
```

自定义的简单查询就是根据方法名来自动生成SQL，主要的语法是findXXBy,readAXXBy,queryXXBy,countXXBy, getXXBy后面跟属性名称,也使用一些加一些关键字And 、 Or;

在实际的开发中需要用到分页、删选、连表等查询的时候就需要特殊的方法或者自定义 SQL;

在SQL的查询方法上面使用@Query注解，如涉及到删除和修改在需要加上@Modifying.也可以根据需要添加 @Transactional对事物的支持，查询超时的设置等。

多表查询在Spring Boot Jpa 中有两种实现方式，第一种是利用 Hibernate 的级联查询来实现，第二种是创建一个结果集的接口来接收连表查询后的结果；

日常项目中因为使用的分布式开发模式，不同的服务有不同的数据源，常常需要在一个项目中使用多个数据源，因此需要配置 Spring Boot Jpa 对多数据源的使用，一般分一下为三步：

1.  配置多数据源

2. 不同源的实体类放入不同包路径

3. 声明不同的包路径下使用不同的数据源、事务支持

异构数据库多源支持:

实体类声明@Entity关系型数据库支持类型、声明@Document为Mongodb支持类型，不同的数据源使用不同的实体就可以了。