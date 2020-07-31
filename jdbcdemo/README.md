# jdbcTemplate

jdbcTemplate的demo

首先引入依赖：

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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

准备数据：

```sql
create table user
(
    id       bigint auto_increment
        primary key,
    name     varchar(45) not null,
    password varchar(45) not null,
    constraint user_name_uindex
        unique (name)
);

insert into user values  (1, 'chun', '123456');
insert into user values  (2, 'cccc', 'xxxxxx');
```

数据模型：

```java
public class User implements Serializable {

    private static final long serialVersionId = 1L;

    private Long id;
    private String name;
    private String password;
}
```

Dao层的interface类：

```java
public interface UserDao {
    int add(User user);

    int update(User user);

    int deleteById(Long id);

    List<Map<String, Object>> queryUsersListMap();

    User queryUserById(Long id);
}
```

在实现类里使用jdbcTemplate:

```java
@Repository("UserDao")
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int add(User user) {
        String sql = "insert into user(name,password) values(:name,:password)";
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        return template.update(sql, new BeanPropertySqlParameterSource(user));
    }

    @Override
    public int update(User user) {
        String sql = "update user set name = ?, password = ? where id = ?";
        Object[] args = {user.getName(), user.getPassword(), user.getId()};
        int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.BIGINT};
        return this.jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from user where id = ?";
        Object[] args = {id};
        int[] argTypes = {Types.BIGINT};
        return this.jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public List<Map<String, Object>> queryUsersListMap() {
        String sql = "select * from user";
        return this.jdbcTemplate.queryForList(sql);
    }

    @Override
    public User queryUserById(Long id) {
        String sql = "select * from user where id = ?";
        Object[] args = {id};
        int[] argTypes = {Types.BIGINT};
        List<User> userList = this.jdbcTemplate.query(sql, args, argTypes, new UserMapper());
        if (userList.size() >= 1) {
            return userList.get(0);
        }else{
            return null;
        }
    }
}
```

首先直接注入jdbcTemplate，然后对于保存操作有两种不同的方法，当插入的表字段较多的情况下，推荐使用NamedParameterJdbcTemplate。

对于CRUD操作：

* 增加、修改和删除使用jdbcTemplate.update，一般传入一条sql语句，里面需要传入的参数由第二个参数Object[] args确定，第三个参数为args各个位置对于数据库的类型；

* 对于查询操作，查询单行记录的一个值使用jbdcTemplate.queryForObject，对于多行采用jdbcTemplate.queryForList

* jdbcTemplate.query用于查询多条数据，第一个参数为sql语句，第二个为sql中的参数，第三个为对应数据库中的类型，第四个参数是为了接受时是将库表对应的实体对象，因此需要编写一个实现了org.springframework.jdbc.core.RowMapper的对象，用于将实体对象属性和库表字段一一对应：

```java
public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));
        return user;
    }
}
```

然后就是实现service层、controller层，然后进行测试；