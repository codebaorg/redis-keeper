<h4 align="right"><a href="./README.md">English</a> | <strong>简体中文</strong></h4>

# Redis keeper - 一个轻量级的 redis 多数据源管理工具
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.redisson/redisson/badge.svg)](https://central.sonatype.com/artifact/org.codeba/redis-keeper)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


**支持 JDK: 1.8 ... 21**

**支持 Redisson: 3.15.5 ... 3.35.0**

**支持 Redis: 3.0 ... 7.2**

## 特征

* 基于redisson封装，保留redisson所有强大功能
* 支持redis的多数据源配置和实时更新
* 支持redis数据源的“只读”、“只写”、“读写”、“跳过”的状态切换
* 具有优秀的拓展性和兼容性


## 快速开始

#### Maven
    <dependency>
       <groupId>org.codeba</groupId>
       <artifactId>redis-keeper-core</artifactId>
       <version>2024.2.3</version>
    </dependency> 

    <dependency>
       <groupId>org.codeba</groupId>
       <artifactId>redis-keeper-support</artifactId>
       <version>2024.2.3</version>
    </dependency>

#### Gradle

    implementation group: 'org.codeba', name: 'redis-keeper-core', version: '2024.2.3'

    implementation group: 'org.codeba', name: 'redis-keeper-support', version: '2024.2.3'

#### Sbt

    libraryDependencies += "org.codeba" % "redis-keeper-core" % "2024.2.3"
    
    libraryDependencies += "org.codeba" % "redis-keeper-support" % "2024.2.3"


#### Java

```java
// 1. 创建配置类
Config config = new Config();
config.useSingleServer().setAddress("redis://localhost:6379").setPassword(yourPass);
```

```java
// 2. 创建数据源
DefaultCacheDatasource datasource = new DefaultCacheDatasource();

Map<String, CacheTemplate> dsMap = datasource.initialize(new HashMap<String, CacheKeeperConfig>() {{
    put("ds1", new CacheKeeperConfig(config));
}});

Map<String, List<CacheTemplate>> dssMap = datasource.initializeMulti(new HashMap<String, List<CacheKeeperConfig>>() {{
    put("ds2", Collections.singletonList(new CacheKeeperConfig(config)));
}});
```

```java
// 3. 创建数据源提供者
CacheTemplateProvider<CacheTemplate> provider = new CacheTemplateProvider<>(dsMap, dssMap);
```

```java
// 4. 获取CacheTemplate
Optional<CacheTemplate> templateOptional = provider.getTemplate("ds1");
final CacheTemplate cacheTemplate = templateOptional.get();
cacheTemplate.set("foo", "bar");

// 或者获取“读写”状态的CacheTemplate
Optional<CacheTemplate> templateOptionalRW = provider.getTemplate("ds1", CacheDatasourceStatus.RW);

// 或者获取多个相同用途的CacheTemplate
Collection<CacheTemplate> cacheTemplates = provider.getTemplates("ds2");

// 或者从多个相同用途的CacheTemplate中轮询获取一个（负载均衡）
Optional<CacheTemplate> polledTemplate = provider.pollTemplate("ds2");

// 或者从多个相同用途的CacheTemplate中随机获取一个
Optional<CacheTemplate> randomedTemplate = provider.randomTemplate("ds2");
```

## Springboot

1. Maven

```java
<dependency>
	<groupId>org.codeba</groupId>
	<artifactId>redis-keeper-spring-boot-starter</artifactId>
	<version>2024.2.3</version>
</dependency>
```

2. 数据源配置示例如下：

```yaml
redis-keeper:
  redis:
    datasource:
      ds1:
        host: localhost
        port: 6379
        password: yourPass
        invoke-params-print: true

    datasources:
      ds2:
        - host: localhost
          port: 6379
          database: 1
          password: yourPass
          invoke-params-print: true

        - host: localhost
          port: 6379
          database: 2
          password: yourPass
          invoke-params-print: true

```

3. 常用方法示例：

```java
@SpringBootTest
public class AppTest {

    @Autowired
    private CacheTemplateProvider<CacheTemplate> provider;

    @Test
    public void test() {
        String key = "foo";
        String value = "bar";

        final CacheTemplate cacheTemplate = provider.getTemplate("ds1").get();
        // set
        cacheTemplate.set(key, value);
        cacheTemplate.setObject(key, value);
        // get
        cacheTemplate.get(key);
        cacheTemplate.getObject(key);
        cacheTemplate.getLong(key);
        cacheTemplate.getDouble(key);
        // incr
        cacheTemplate.incr(key);
        // set get bit
        cacheTemplate.setBit(key, 7, true);
        cacheTemplate.getBit(key, 7);
        // del exists expire ttl unlink
        cacheTemplate.del(key);
        cacheTemplate.exists(key);
        cacheTemplate.expire(key, 10, TimeUnit.SECONDS);
        cacheTemplate.expireAt(key, System.currentTimeMillis());
        cacheTemplate.ttl(key);
        cacheTemplate.unlink(key);
        // geo
        cacheTemplate.geoAdd(key, 13.361389, 38.115556, "Sicily");
        cacheTemplate.geoAdd(key, 15.087269, 37.502669, "Palermo");
        cacheTemplate.geoDist(key, "Sicily", "Palermo", "km");
        // hash
        cacheTemplate.hSet(key, "field1", value);
        cacheTemplate.hGet(key, "field1");
        // hyberloglog
        cacheTemplate.pfAdd(key, Arrays.asList("a"));
        cacheTemplate.pfCount(key);
        // list
        cacheTemplate.rPush(key, "world", "hello");
        cacheTemplate.lRange(key, 0, -1);
        // set
        cacheTemplate.sAdd(key, "hello");
        cacheTemplate.sAdd(key, "world");
        cacheTemplate.sAdd(key, "world");
        cacheTemplate.sMembers(key);
        // zset
        cacheTemplate.zAdd(key, 1, "one");
        cacheTemplate.zAdd(key, 2, "two");
        cacheTemplate.zAdd(key, 3, "three");
        cacheTemplate.zRange(key, 0, -1);
        // bloom filter
        cacheTemplate.bfReserve(key, 1000, 0.01);
        cacheTemplate.bfAdd(key, "item1");
        cacheTemplate.bfAdd(key, "item1");
        cacheTemplate.bfExists(key, "item2");
        // lock
        cacheTemplate.tryLock(key, 3, TimeUnit.SECONDS);
        cacheTemplate.unlock(key);
        cacheTemplate.forceUnlock(key);
        // rate limiter
        cacheTemplate.trySetRateLimiter(key, 100, 1);
        cacheTemplate.tryAcquire(key);
        cacheTemplate.tryAcquire(key, 10);
        // pipeline execute
        cacheTemplate.pipeline(kBatch -> {
            kBatch.getString().setAsync(key, "bar");
            kBatch.getGeo().geoAddAsync(key, 13.361389, 38.115556, "Sicily");
            kBatch.getList().llenAsync(key);
        });
        // pipeline execute and get response
        final List<?> responses = cacheTemplate.pipelineWithResponses(kBatch -> {
            kBatch.getString().setAsync(key, "bar");
            kBatch.getGeo().geoAddAsync(key, 13.361389, 38.115556, "Sicily");
            kBatch.getList().llenAsync(key);
        });
        // pipeline execute async
        final CompletableFuture<Void> voidCompletableFuture = cacheTemplate.pipelineAsync(kBatch -> {
            kBatch.getString().setAsync(key, "bar");
            kBatch.getGeo().geoAddAsync(key, 13.361389, 38.115556, "Sicily");
            kBatch.getList().llenAsync(key);
        });
        // pipeline execute and get response async
        final CompletableFuture<List<?>> listCompletableFuture = cacheTemplate.pipelineWithResponsesAsync(kBatch -> {
            kBatch.getString().setAsync(key, "bar");
            kBatch.getGeo().geoAddAsync(key, 13.361389, 38.115556, "Sicily");
            kBatch.getList().llenAsync(key);
        });
    }

}

```


## 无限扩展

#### CacheTemplate新增自定义方法

1. Maven依赖

```java
<dependency>
	<groupId>org.codeba</groupId>
	<artifactId>redis-keeper-spring-boot-starter</artifactId>
	<version>2024.2.3</version>
</dependency>
```

2. CacheTemplate新增自定义的方法

自定义 MyCacheTemplate.java

```java
import org.codeba.redis.keeper.support.CacheKeeperConfig;
import org.codeba.redis.keeper.support.DefaultRedissonTemplate;

public class MyCacheTemplate extends DefaultRedissonTemplate implements CacheTemplate {

    public MyCacheTemplate(CacheKeeperConfig cacheKeeperConfig) {
        super(cacheKeeperConfig);
    }

    public void test() {
	    final RedissonClient redissonClient = getDataSource();
        redissonClient.someMehotd();
        System.out.println("hello world");
    }

}
```

自定义 MyCacheDatasource.java

```java
import org.codeba.redis.keeper.support.CacheDatasource;
import org.codeba.redis.keeper.support.CacheKeeperConfig;

public class MyCacheDatasource implements CacheDatasource<MyCacheTemplate> {

    @Override
    public MyCacheTemplate instantTemplate(CacheKeeperConfig config) {
        return new MyCacheTemplate(config);
    }

}
```

使新的MyCacheDatasource生效

```java
import org.codeba.redis.keeper.support.CacheDatasource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean
    public CacheDatasource<MyCacheTemplate> cacheDatasource() {
        return new MyCacheDatasource();
    }

}
```

3. 使用新的CacheTemplate

```java
@SpringBootTest
public class AppTest {

    @Autowired
    private CacheTemplateProvider<MyCacheTemplate> myProvider;

    @Test
    public void testMyProvider() {
        final Optional<MyCacheTemplate> templateOptional = myProvider.getTemplate("ds1");

        if (templateOptional.isPresent()) {
            final MyCacheTemplate cacheTemplate = templateOptional.get();

            // Custom Methods
            cacheTemplate.test();

        }
    }

}
```


#### CacheDatasource自定义redisson配置

1. Maven依赖

```java
<dependency>
	<groupId>org.codeba</groupId>
	<artifactId>redis-keeper-spring-boot-starter</artifactId>
	<version>2024.2.3</version>
</dependency>
```

2. 比如自定义设置redisson序列化和反序列化的编码方式，同时使新的CacheDatasource生效。

```java
import org.codeba.redis.keeper.support.CacheDatasource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean
    public CacheDatasource<CacheTemplate> cacheDatasource() {
        return new DefaultCacheDatasource(){
            @Override
            public Consumer<CacheKeeperConfig> configPostProcessor(Consumer<CacheKeeperConfig> consumer) {
                return v -> v.getConfig().setCodec(new JsonJacksonCodec());
            }
        };
    }

}
```

## 配置说明

redis-keeper全部的可配置项完全兼容Spring Data Redis和Redisson的配置。示例如下：

```yaml
redis-keeper:
  redis:
    datasource:
    #.....
    datasources:
    #.....
  redisson:
    datasource:
    #.....
    datasources:
    #.....
```


#### redis.datasource

* 类型：`Map<String,RedisKeeperProperties>`
* 默认值：`null`

示例如下：

```yaml
redis-keeper:
    redis:
        datasource:
          ds1:
              host: xx
              port: xx
              database: xx
              password: xx
          ds2:
              host: xx
              port: xx
              database: xx
              password: xx
          ds3:
              host: xx
              port: xx
              database: xx
              password: xx
```

#### redis.datasources

* 类型：`Map<String, List<RedisKeeperProperties>`
* 默认值：`null`

```yaml
redis-keeper:
    redis:
        datasources:
            ds1:
                - host: xx
                  port: xx
                  database: xx
                  password: xx
                - host: xx
                  port: xx
                  database: xx
                  password: xx
            ds2:
                - host: xx
                  port: xx
                  database: xx
                  password: xx
                - host: xx
                  port: xx
                  database: xx
                  password: xx
            ds3:
                - host: xx
                  port: xx
                  database: xx
                  password: xx
                - host: xx
                  port: xx
                  database: xx
                  password: xx
```

#### RedisKeeperProperties

|        配置        |  类型  |  默认值  |     取值范围     |
| :-------------------: | :-------: | :---------: | :----------------: |
|       status       | string |    RW    | RO、WO、RW、SKIP |
| invoke-params-print | boolean |   false   |   true、false   |
|        host        | string | localhost |        -        |
|        port        | number |   6379   |        -        |
|      database      | number |     0     |        -        |
|      password      | string |     `null`     |        -        |

更多的配置请查看Spring Data Redis的配置类： [spring-boot/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/redis/RedisProperties.java at main · spring-projects/spring-boot (github.com)](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/redis/RedisProperties.java)



#### redisson.datasource

* 类型：`Map<String,RedissonKeeperProperties>`
* 默认值：`null`

示例如下：

```yaml
redis-keeper:
  redisson:
    datasource:
      ds1:
        invoke-params-print: true
        status: RW
        config: |
          singleServerConfig:
            address: redis://xxx:6379
            database: 0
            password: xxx
```

#### redisson.datasources

* 类型：`Map<String, List<RedissonKeeperProperties>`
* 默认值：`null`

示例如下：

```yaml
redis-keeper:
  redisson:
    datasources:
      ds2:
        - invoke-params-print: true
          status: RW
          config: |
            singleServerConfig:
            address: redis://xxx:6379
            database: 0
            password: xxx

        - invoke-params-print: true
          status: RW
          config: |
            singleServerConfig:
            address: redis://xxx:6379
            database: 0
            password: xxx
```

#### RedissonKeeperProperties

|        配置        |  类型  | 默认值 |     取值范围     |
| :-------------------: | :-------: | :------: | :----------------: |
|       status       | string |   RW   | RO、WO、RW、SKIP |
| invoke-params-print | boolean | false |   true、false   |
|       config       | string |    `null`    |        -        |
|        file        | string |    `null`    |        -        |

更多的配置请查看Redisson的配置类： [redisson/redisson-spring-boot-starter/src/main/java/org/redisson/spring/starter/RedissonProperties.java at master · redisson/redisson (github.com)](https://github.com/redisson/redisson/blob/master/redisson-spring-boot-starter/src/main/java/org/redisson/spring/starter/RedissonProperties.java)

## 更多示例

1. [Redis-Keeper only](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-standalone)
2. [Redis-Keeper with Spring boot](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-springboot)
3. [Redis-Keeper with Spring cloud](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-springcloud)

## 常见问题

1. [Fix: JsonJacksonCodec fails to serialize Throwable on Java17 by tomj-vm · Pull Request ](https://github.com/redisson/redisson/pull/5436)​#5436 · redisson/redisson (github.com)#

2. [Redisson.getBlockingDeque：java.lang.NoClassDefFoundError: java/util/SequencedCollection · Issue #5402 · redisson/redisson (github.com)](https://github.com/redisson/redisson/issues/5402) redisson的3.24.3 版本 有bug

