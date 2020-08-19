# Jackson的使用技巧

Spring Boot内置的JSON处理的类是Jackson，不仅可以完成简单序列化和反序列化操作，还能自定义序列化和反序列化操作。

## 自定义Mapper

将对象转为JSON时，Jackson会调用ObjectMapper将对象序列化为JSON，因此可以自定义Mapper；

假设一个对象User：

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    private String username;

    @JsonIgnore
    private String password;

    private Date birthday;
}
```

如果想将时间格式进行调整，则可以自定义mapper：

```java
@Configuration
public class JsonConfig {

    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return mapper;
    }
}
```

然后定义一个mapper，返回一个User时返回的时间就会按照`yyyy-MM-dd HH:mm:ss`这种格式输出了。

## 序列化

Jackson通过使用mapper的`writeValueAsString`方法将Java对象序列化为JSON格式字符串：`mapper.writeValueAsString(user)`

## 反序列化

当采用树遍历的方式时，JSON被读入到JsonNode对象中，可以像操作XML DOM那样读取JSON。

也可以利用对象进行绑定，然后就可以进行反序列化：`mapper.readValue(userStr, User.class)`；

## Jackson注解

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    private String username;

    @JsonIgnore
    private String password;

    private Date birthday;
}
```

* `@JsonProperty`，作用在属性上，用来为JSON Key指定一个别名。
* `@Jsonlgnore`，作用在属性上，用来忽略此属性。

```java
@JsonIgnoreProperties({"password", "registerTime"})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInfo {
    private String username;

    private String password;

    private String email;

    private Date registerTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
}
```

* `@JsonIgnoreProperties`，忽略一组属性，作用于类上
* `@JsonFormat`，用于日期格式化
* `@JsonNaming`，用于指定一个命名策略，作用于类或者属性上。

假设对象Info：

```java
@JsonSerialize(using = InfoSerializer.class)
@JsonDeserialize(using = InfoDeserializer.class)
public class Info {
    private String msg;
    
    private Date time;
}
```

* `@JsonSerialize`，指定一个实现类来自定义序列化。类必须实现`JsonSerializer`接口。

  ```java
  public class InfoSerializer extends JsonSerializer<Info> {
      @Override
      public void serialize(Info info, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
          jsonGenerator.writeStartObject();
          jsonGenerator.writeStringField("message", info.getMsg());
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          jsonGenerator.writeStringField("send_time", dateFormat.format(info.getTime()));
          jsonGenerator.writeEndObject();
      }
  }
  ```

* `@JsonDeserialize`，用户自定义反序列化，同`@JsonSerialize` ，类需要实现`JsonDeserializer`接口。

  ```java
  public class InfoDeserializer extends JsonDeserializer<Info> {
      @Override
      public Info deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          try {
              JsonNode node = jsonParser.getCodec().readTree(jsonParser);
              String message = node.get("message").asText();
              Date time = format.parse(node.get("send_time").asText());
              return new Info(message, time);
          } catch (ParseException e) {
              e.printStackTrace();
              return null;
          }
      }
  }
  ```

  

