/*
 * Copyright (c) 2024-2025, redis-keeper (mimang447@gmail.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.codeba.redis.keeper.spring.boot;

import lombok.extern.slf4j.Slf4j;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.support.CacheDatasource;
import org.codeba.redis.keeper.support.CacheKeeperConfig;
import org.codeba.redis.keeper.support.DefaultCacheDatasource;
import org.codeba.redis.keeper.support.Utils;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Redis keeper autoconfiguration.
 *
 * @param <T> the type parameter
 * @author codeba
 */
@Slf4j
@Configuration
@SuppressWarnings({"unchecked"})
@EnableConfigurationProperties({RedisDatasourceProperties.class, RedissonDatasourceProperties.class})
public class RedisKeeperAutoConfiguration<T> {
    /**
     * The constant PROVIDER_BEAN_NAME.
     */
    private static final String PROVIDER_BEAN_NAME = "cacheTemplateProvider";

    /**
     * The Context.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * The Cache datasource.
     */
    @Autowired(required = false)
    private CacheDatasource<T> cacheDatasource;

    /**
     * The Redis properties.
     */
    @Autowired
    private RedisDatasourceProperties redisProperties;

    /**
     * The Redisson properties.
     */
    @Autowired
    private RedissonDatasourceProperties redissonProperties;

    /**
     * Cache template provider cache template provider.
     * <p>
     *
     * @return the cache template provider
     * @throws IOException the io exception
     */
    @Bean(name = RedisKeeperAutoConfiguration.PROVIDER_BEAN_NAME)
    @RefreshScope
    public CacheTemplateProvider<T> cacheTemplateProvider() throws IOException {
        final Map<String, T> loadMap = load(redisProperties, redissonProperties);
        final Map<String, List<T>> loadListMap = loads(redisProperties, redissonProperties);
        // clean
        cacheDatasource.clean();
        return new CacheTemplateProvider<>(loadMap, loadListMap);
    }

    /**
     * On refresh.
     */
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh() {
        if (!redisProperties.isLazyRefresh() || !redissonProperties.isLazyRefresh()) {
            this.context.getBean(ScopedProxyUtils.getTargetBeanName(PROVIDER_BEAN_NAME));
        }
    }

    /**
     * Load map.
     *
     * @param redisProperties    the redis properties
     * @param redissonProperties the redisson properties
     * @return the map
     * @throws IOException the io exception
     */
    private Map<String, T> load(RedisDatasourceProperties redisProperties, RedissonDatasourceProperties redissonProperties) throws IOException {
        if (null == cacheDatasource) {
            cacheDatasource = (CacheDatasource<T>) new DefaultCacheDatasource();
        }

        final Map<String, RedisKeeperProperties> redisMap = redisProperties.getDatasource();
        final Map<String, RedissonKeeperProperties> redissonMap = redissonProperties.getDatasource();

        final Map<String, CacheKeeperConfig> map = new HashMap<>();
        if (null != redisMap && !redisMap.isEmpty()) {
            for (Map.Entry<String, RedisKeeperProperties> entry : redisMap.entrySet()) {
                final RedisKeeperProperties redisKeeperProperties = entry.getValue();
                final String status = redisKeeperProperties.getStatus();
                final boolean invokeParamsPrint = redisKeeperProperties.isInvokeParamsPrint();
                final Config config = config(redisKeeperProperties);

                final CacheKeeperConfig cacheKeeperConfig = new CacheKeeperConfig(status, config, invokeParamsPrint);

                map.put(entry.getKey().trim(), cacheKeeperConfig);
            }
        }

        if (null != redissonMap && !redissonMap.isEmpty()) {
            for (Map.Entry<String, RedissonKeeperProperties> entry : redissonMap.entrySet()) {
                final RedissonKeeperProperties redissonKeeperProperties = entry.getValue();
                final String status = redissonKeeperProperties.getStatus();
                final boolean invokeParamsPrint = redissonKeeperProperties.isInvokeParamsPrint();
                final Config config = config(redissonKeeperProperties);

                final CacheKeeperConfig cacheKeeperConfig = new CacheKeeperConfig(status, config, invokeParamsPrint);

                map.put(entry.getKey().trim(), cacheKeeperConfig);
            }
        }

        return cacheDatasource.initialize(map);
    }

    /**
     * Loads map.
     *
     * @param redisProperties    the redis properties
     * @param redissonProperties the redisson properties
     * @return the map
     */
    private Map<String, List<T>> loads(RedisDatasourceProperties redisProperties, RedissonDatasourceProperties redissonProperties) {
        if (null == cacheDatasource) {
            cacheDatasource = (CacheDatasource<T>) new DefaultCacheDatasource();
        }

        final Map<String, List<RedisKeeperProperties>> redisMap = redisProperties.getDatasources();
        final Map<String, List<RedissonKeeperProperties>> redissonMap = redissonProperties.getDatasources();

        final Map<String, List<CacheKeeperConfig>> map = new HashMap<>();
        if (null != redisMap && !redisMap.isEmpty()) {
            for (Map.Entry<String, List<RedisKeeperProperties>> entry : redisMap.entrySet()) {
                final List<CacheKeeperConfig> keeperConfigList = entry.getValue()
                        .stream()
                        .map(redisKeeperProperties -> {
                            final String status = redisKeeperProperties.getStatus();
                            final boolean invokeParamsPrint = redisKeeperProperties.isInvokeParamsPrint();
                            final Config config;
                            try {
                                config = config(redisKeeperProperties);
                            } catch (IOException e) {
                                log.error("RedisKeeperAutoConfiguration RedisKeeperProperties loads--", e);
                                return null;
                            }
                            return new CacheKeeperConfig(status, config, invokeParamsPrint);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                map.put(entry.getKey().trim(), keeperConfigList);
            }
        }

        if (null != redissonMap && !redissonMap.isEmpty()) {
            for (Map.Entry<String, List<RedissonKeeperProperties>> entry : redissonMap.entrySet()) {
                final List<CacheKeeperConfig> keeperConfigList = entry.getValue()
                        .stream()
                        .map(redissonKeeperProperties -> {
                            final String status = redissonKeeperProperties.getStatus();
                            final boolean invokeParamsPrint = redissonKeeperProperties.isInvokeParamsPrint();
                            final Config config;
                            try {
                                config = config(redissonKeeperProperties);
                            } catch (IOException e) {
                                log.error("RedisKeeperAutoConfiguration RedissonKeeperProperties loads--", e);
                                return null;
                            }

                            return new CacheKeeperConfig(status, config, invokeParamsPrint);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                map.put(entry.getKey().trim(), keeperConfigList);
            }
        }

        return cacheDatasource.initializeMulti(map);
    }

    /**
     * Config config.
     *
     * @param redissonKeeperProperties the redisson keeper properties
     * @return the config
     * @throws IOException the io exception
     */
    private Config config(RedissonKeeperProperties redissonKeeperProperties) throws IOException {
        Config result = null;
        final String config = redissonKeeperProperties.getConfig();
        final String file = redissonKeeperProperties.getFile();
        if (StringUtils.hasText(config)) {
            result = Config.fromYAML(config);
        } else if (StringUtils.hasText(file)) {
            result = Config.fromYAML(file);
        }
        return result;
    }

    /**
     * Config config.
     *
     * @param redisProperties the redis properties
     * @return the config
     * @throws IOException the io exception
     */
    private Config config(RedisProperties redisProperties) throws IOException {
        Config config;
        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        Method usernameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getUsername");
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Method connectTimeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getConnectTimeout");
        Method clientNameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getClientName");
        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);

        Integer timeout = null;
        if (timeoutValue instanceof Duration) {
            timeout = (int) ((Duration) timeoutValue).toMillis();
        } else if (timeoutValue != null) {
            timeout = (Integer) timeoutValue;
        }

        Integer connectTimeout = null;
        if (connectTimeoutMethod != null) {
            Object connectTimeoutValue = ReflectionUtils.invokeMethod(connectTimeoutMethod, redisProperties);
            if (connectTimeoutValue != null) {
                connectTimeout = (int) ((Duration) connectTimeoutValue).toMillis();
            }
        } else {
            connectTimeout = timeout;
        }

        String clientName = null;
        if (clientNameMethod != null) {
            clientName = (String) ReflectionUtils.invokeMethod(clientNameMethod, redisProperties);
        }

        String username = null;
        if (usernameMethod != null) {
            username = (String) ReflectionUtils.invokeMethod(usernameMethod, redisProperties);
        }

        if (redisProperties.getSentinel() != null) {
            Method nodesMethod = ReflectionUtils.findMethod(RedisProperties.Sentinel.class, "getNodes");
            Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());

            String[] nodes;
            if (nodesValue instanceof String) {
                nodes = Utils.convert(Arrays.asList(((String) nodesValue).split(",")));
            } else {
                nodes = Utils.convert((List<String>) nodesValue);
            }

            config = new Config();
            SentinelServersConfig c = config.useSentinelServers()
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(nodes)
                    .setDatabase(redisProperties.getDatabase())
                    .setUsername(username)
                    .setPassword(redisProperties.getPassword())
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
        } else if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
            Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
            Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
            List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

            String[] nodes = Utils.convert(nodesObject);

            config = new Config();
            ClusterServersConfig c = config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setUsername(username)
                    .setPassword(redisProperties.getPassword())
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
        } else {
            config = new Config();
            String prefix = Utils.REDIS_PROTOCOL_PREFIX;
            Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
            if (method != null && (Boolean) ReflectionUtils.invokeMethod(method, redisProperties)) {
                prefix = Utils.REDISS_PROTOCOL_PREFIX;
            }

            SingleServerConfig c = config.useSingleServer()
                    .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setDatabase(redisProperties.getDatabase())
                    .setUsername(username)
                    .setPassword(redisProperties.getPassword())
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
        }

        return config;
    }

}
