# MyBatis多数据源

与SpringBoot Jpa类似处理即可：

* 第一个数据源的配置：

```java
@Configuration
@MapperScan(basePackages = {"com.chun.mybatismult.mapper.primary"}, sqlSessionTemplateRef = "primaryTemplate")
public class PrimaryDataConfig {

    @Bean(name = "primarySource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    @Primary
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory primarySqlSession(@Qualifier("primarySource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("primarySource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "primaryTemplate")
    @Primary
    public SqlSessionTemplate primaryTemplate(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

* 第二个数据源的配置：

```java
@Configuration
@MapperScan(basePackages = {"com.chun.mybatismult.mapper.secondary"}, sqlSessionTemplateRef = "secondaryTemplate")
public class SecondaryDataConfig {

    @Bean(name = "secondarySource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondarySqlSessionFactory")
    public SqlSessionFactory primarySqlSession(@Qualifier("secondarySource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "secondaryTransactionManager")
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("secondarySource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "secondaryTemplate")
    @Primary
    public SqlSessionTemplate primaryTemplate(@Qualifier("secondarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```