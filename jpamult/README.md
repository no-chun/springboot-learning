# Spring Boot Jpa 多数据源

基于Spring Boot Jpa配置多个数据源

首先引入依赖：

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

进行配置：

```properties
# 第一个数据库的配置
spring.datasource.primary.jdbc-url=jdbc:mysql://localhost:3306/primary?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.primary.username=root
spring.datasource.primary.password=root
spring.datasource.primary.driver-class-name=com.mysql.cj.jdbc.Driver
# 第二个数据库的配置
spring.datasource.secondary.jdbc-url=jdbc:mysql://localhost:3306/secondary?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.secondary.username=root
spring.datasource.secondary.password=root
spring.datasource.secondary.driver-class-name=com.mysql.cj.jdbc.Driver
# hibernate的配置
spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

配置完成后，需要数据库的配置导入：

```java
@Configuration
public class DataSourceConfig {
    private final JpaProperties jpaProperties;

    private final HibernateProperties hibernateProperties;

    public DataSourceConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    @Bean(name = "primarySource")
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondarySource")
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "vendorProperties")
    public Map<String, Object> getVendorProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }
}
```

首先通过注解`@ConfigurationProperties`获取到配置后，利用DataSourceBuilder生成一个DataSource的Bean。

然后通过`JpaProperties`和`HibernateProperties`获取到其他的Jpa和Hibernate配置并返回Map；

导入之后，需要分别为每个DataSource进行配置：

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager",
        basePackages = {"com.chun.jpamult.repository.primaryRepository"}
)
public class PrimaryDataConfig {
    private final DataSource primaryDataSource;

    private final Map<String, Object> vendorProperties;

    public PrimaryDataConfig(@Qualifier("primarySource") DataSource primaryDataSource,
                             @Qualifier("vendorProperties") Map<String, Object> vendorProperties) {
        this.primaryDataSource = primaryDataSource;
        this.vendorProperties = vendorProperties;
    }

    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(primaryDataSource)
                .properties(vendorProperties)
                .packages("com.chun.jpamult.model")
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }

    @Bean(name = "primaryEntityManager")
    @Primary
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return primaryEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(primaryEntityManagerFactory(builder).getObject());
    }
}
```

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager",
        basePackages = {"com.chun.jpamult.repository.secondaryRepository"}
)
public class SecondaryDataConfig {
    private final DataSource secondaryDataSource;

    private final Map<String, Object> vendorProperties;

    public SecondaryDataConfig(@Qualifier("secondarySource") DataSource secondaryDataSource, @Qualifier("vendorProperties") Map<String, Object> vendorProperties) {
        this.secondaryDataSource = secondaryDataSource;
        this.vendorProperties = vendorProperties;
    }


    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource)
                .properties(vendorProperties)
                .packages("com.chun.jpamult.model")
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }


    @Bean(name = "secondaryEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return secondaryEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(secondaryEntityManagerFactory(builder).getObject());
    }
}
```

通过`EnableTransactionManagement`开始事务支持，`@EnableJpaRepositories`用来扫描和发现指定包及其子包中的Repository定义。entityManagerFactoryRef用于指定实体管理的Bean，transactionManagerRef用于指定事务管理的Bean，basePackages用于指定Repository类的位置。

然后在Config内，写生成实体管理和事务管理的Bean。

第二个DataSource的配置类似；

配置完成后，在对应的包内写模型和Repository即可。
