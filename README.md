<h4 align="right"><strong>English</strong> | <a href="./README_zh.md">简体中文</a></h4>

# Redis keeper - Lightweight Redis Multi-datasource Management Tool
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.redisson/redisson/badge.svg)](https://central.sonatype.com/artifact/org.codeba/redis-keeper)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


**Supported JDK: 1.8 ... 21**

**Supported Redisson: 3.15.5 ... 3.27.0**

**Supported Redis: 3.0 ... 7.2**

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
       <version>2024.0.0<</version>
    </dependency> 

    <dependency>
       <groupId>org.codeba</groupId>
       <artifactId>redis-keeper-support</artifactId>
       <version>2024.0.0<</version>
    </dependency> 

#### Gradle
    implementation group: 'org.codeba', name: 'redis-keeper-core', version: '2024.0.0'

    implementation group: 'org.codeba', name: 'redis-keeper-support', version: '2024.0.0'

#### Sbt
    libraryDependencies += "org.codeba" % "redis-keeper-core" % "2024.0.0"
    
    libraryDependencies += "org.codeba" % "redis-keeper-support" % "2024.0.0"


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

## More Samples

1. [Redis-Keeper only](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-standalone)
2. [Redis-Keeper with Spring boot](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-springboot)
3. [Redis-Keeper with Spring cloud](https://github.com/codebaorg/redis-keeper/tree/main/redis-keeper-example/redis-keeper-example-springcloud)


## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=codebaorg/redis-keeper&type=Date)](https://star-history.com/#codebaorg/redis-keeper&Date)

