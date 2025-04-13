<h4 align="right"><strong>English</strong> | <a href="./README_zh.md">简体中文</a></h4>

# Redis keeper - Lightweight Redis Multi-datasource Management Tool
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.redisson/redisson/badge.svg)](https://central.sonatype.com/artifact/org.codeba/redis-keeper)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


**Supported JDK: 1.8 ... 21**

**Supported Redisson: 3.15.5 ... 3.33.0**

**Supported Redis: 3.0 ... 7.4**

## Features

* Based on redisson package, retain all the powerful features of redisson
* Support for multiple data source configuration and real-time updates for redis
* Support "read-only", "write-only", "read-write" and "skip" state switching for redis data sources
* Excellent expandability and compatibility to meet all your needs


## Quick start

#### Maven
    <dependency>
       <groupId>org.codeba</groupId>
       <artifactId>redis-keeper-core</artifactId>
       <version>2024.1.3</version>
    </dependency> 

    <dependency>
       <groupId>org.codeba</groupId>
       <artifactId>redis-keeper-support</artifactId>
       <version>2024.1.3</version>
    </dependency> 

#### Gradle

    implementation group: 'org.codeba', name: 'redis-keeper-core', version: '2024.1.3'

    implementation group: 'org.codeba', name: 'redis-keeper-support', version: '2024.1.3'

#### Sbt

    libraryDependencies += "org.codeba" % "redis-keeper-core" % "2024.1.3"
    
    libraryDependencies += "org.codeba" % "redis-keeper-support" % "2024.1.3"


#### Java

```java
// 1. Create config object
Config config = new Config();
config.useSingleServer().setAddress("redis://localhost:6379").setPassword(yourPass);
```

```java
// 2. Create datasource
DefaultCacheDatasource datasource = new DefaultCacheDatasource();

Map<String, CacheTemplate> dsMap = datasource.initialize(new HashMap<String, CacheKeeperConfig>() {{
    put("ds1", new CacheKeeperConfig(config));
}});

Map<String, List<CacheTemplate>> dssMap = datasource.initializeMulti(new HashMap<String, List<CacheKeeperConfig>>() {{
    put("ds2", Collections.singletonList(new CacheKeeperConfig(config)));
}});
```

```java
// 3. Create datasource provider
CacheTemplateProvider<CacheTemplate> provider = new CacheTemplateProvider<>(dsMap, dssMap);
```

```java
// 4. Get redis template
Optional<CacheTemplate> templateOptional = provider.getTemplate("ds1");
final CacheTemplate cacheTemplate = templateOptional.get();
cacheTemplate.set("foo", "bar");

// or get the read and write state of the cacheTemplate
Optional<CacheTemplate> templateOptionalRW = provider.getTemplate("ds1", CacheDatasourceStatus.RW);

// or get multiple cacheTemplates
Collection<CacheTemplate> cacheTemplates = provider.getTemplates("ds2");

// or load balanced polling to get cacheTemplate from multiple data sources
Optional<CacheTemplate> polledTemplate = provider.pollTemplate("ds2");

// or randomize cacheTemplate from multiple data sources
Optional<CacheTemplate> randomedTemplate = provider.randomTemplate("ds2");
```

## Springboot

1. Maven

```java
<dependency>
	<groupId>org.codeba</groupId>
	<artifactId>redis-keeper-spring-boot-starter</artifactId>
	<version>2024.1.3</version>
</dependency>
```

2. Example datasource configuration as follows：

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

3. Examples of common methods：

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
    }

}

```

## Unlimited Expansion

#### CacheTemplate adds new custom methods

1. Maven

```java
<dependency>
	<groupId>org.codeba</groupId>
	<artifactId>redis-keeper-spring-boot-starter</artifactId>
	<version>2024.1.3</version>
</dependency>
```

2. CacheTemplate adds new custom methods

MyCacheTemplate.java

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

MyCacheDatasource.java

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

Enabling the new MyCacheDatasource

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

3. Enabling the new CacheTemplate

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

#### CacheDatasource custom redisson configuration

1. Maven

```java
<dependency>
	<groupId>org.codeba</groupId>
	<artifactId>redis-keeper-spring-boot-starter</artifactId>
	<version>2024.1.3</version>
</dependency>
```

2. For example, custom setting the encoding of redisson serialization and deserialization while enabling the new CacheDatasource.

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

## More Samples

1. [Redis-Keeper only](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-standalone)
2. [Redis-Keeper with Spring boot](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-springboot)
3. [Redis-Keeper with Spring cloud](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-springcloud)


## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=codebaorg/redis-keeper&type=Date)](https://star-history.com/#codebaorg/redis-keeper&Date)

