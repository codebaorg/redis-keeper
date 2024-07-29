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

package org.codeba.redis.keeper.support;

import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.codeba.redis.keeper.core.Provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The interface Cache datasource.
 *
 * @param <T> the type parameter
 * @author codeba
 */
public interface CacheDatasource<T> {

    /**
     * Instant template t.
     *
     * @param config the config
     * @return the t
     */
    T instantTemplate(CacheKeeperConfig config);

    /**
     * Initialize map.
     *
     * @param datasourceMap the datasource map
     * @return the map
     */
    default Map<String, T> initialize(Map<String, CacheKeeperConfig> datasourceMap) {
        final Map<String, T> map = new HashMap<>();

        if (null == datasourceMap || datasourceMap.isEmpty()) {
            return Collections.emptyMap();
        }

        for (Map.Entry<String, CacheKeeperConfig> entry : datasourceMap.entrySet()) {
            final String key = entry.getKey();
            final CacheKeeperConfig config = entry.getValue();

            // set CacheKeeperConfig
            configPostProcessor(v -> {
            }).accept(config);

            // cacheTemplate Instantiation
            final T template = instantTemplate(config);

            // <name, template>
            map.put(key.trim(), template);
            // <name-datasourceStatus, template>
            final CacheDatasourceStatus datasourceStatus = checkDatasourceStatus(config.getStatus());
            final String keyWithStatus = Provider.keyWithStatus(key.trim(), datasourceStatus);
            map.put(keyWithStatus, template);
        }

        return map;
    }

    /**
     * Initialize multi map.
     *
     * @param datasourceMap the datasource map
     * @return the map
     */
    default Map<String, List<T>> initializeMulti(Map<String, List<CacheKeeperConfig>> datasourceMap) {
        final Map<String, List<T>> map = new HashMap<>();

        if (null == datasourceMap || datasourceMap.isEmpty()) {
            return Collections.emptyMap();
        }

        for (Map.Entry<String, List<CacheKeeperConfig>> entry : datasourceMap.entrySet()) {
            final String key = entry.getKey();
            final List<CacheKeeperConfig> configList = entry.getValue();

            final List<T> templateList = configList.stream()
                    .map(config -> {
                        // set CacheKeeperConfig
                        configPostProcessor(v -> {
                        }).accept(config);

                        // cacheTemplate Instantiation
                        final T template = instantTemplate(config);

                        // <name-datasourceStatus, List<template>>
                        final CacheDatasourceStatus datasourceStatus = checkDatasourceStatus(config.getStatus());
                        final String keyWithStatus = Provider.keyWithStatus(key.trim(), datasourceStatus);
                        map.computeIfAbsent(keyWithStatus, k -> new ArrayList<>()).add(template);

                        return template;
                    })
                    .collect(Collectors.toList());

            if (templateList.isEmpty()) {
                continue;
            }

            // <name, List<template>>
            map.put(key.trim(), templateList);
        }

        return map;
    }

    /**
     * Config post processor consumer.
     *
     * @param consumer the consumer
     * @return the consumer
     */
    default Consumer<CacheKeeperConfig> configPostProcessor(Consumer<CacheKeeperConfig> consumer) {
        return consumer;
    }

    /**
     * Check datasource status cache datasource status.
     *
     * @param name the name
     * @return the cache datasource status
     */
    default CacheDatasourceStatus checkDatasourceStatus(String name) {
        try {
            return CacheDatasourceStatus.valueOf(name.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("org.codeba.redis.keeper.support.CacheKeeperConfig#status value must be one of "
                    + Arrays.toString(CacheDatasourceStatus.values()));
        }

    }

}
