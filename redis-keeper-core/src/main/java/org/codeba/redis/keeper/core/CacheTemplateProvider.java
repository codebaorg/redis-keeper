/*
 * Copyright (c) 2024-2025, redis-keeper (mimang447@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.codeba.redis.keeper.core;


import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The interface Cache template provider.
 *
 * @param <T> the type parameter
 * @author codeba
 */
public class CacheTemplateProvider<T> implements LoadBalanceProvider<T>, Serializable {

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final AtomicInteger statusCounter = new AtomicInteger(0);

    private final ThreadLocalRandom statusRandom = ThreadLocalRandom.current();

    private final Map<String, T> datasourceMap;

    private final Map<String, List<T>> datasourcesMap;

    /**
     * Instantiates a new Cache template provider.
     *
     * @param datasourceMap  the datasource map
     * @param datasourcesMap the datasources map
     */
    public CacheTemplateProvider(Map<String, T> datasourceMap, Map<String, List<T>> datasourcesMap) {
        this.datasourceMap = datasourceMap;
        this.datasourcesMap = datasourcesMap;
    }

    @Override
    public Optional<T> getTemplate(String name) {
        return Optional.ofNullable(this.datasourceMap.get(name));
    }

    @Override
    public Optional<T> getTemplate(String name, CacheDatasourceStatus status) {
        final T t = this.datasourceMap.get(Provider.keyWithStatus(name, status));
        return Optional.ofNullable(t);
    }

    @Override
    public Collection<T> getTemplates(String name) {
        final List<T> list = this.datasourcesMap.get(name);
        if (null == list || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list;
    }

    @Override
    public Collection<T> getTemplates(String name, CacheDatasourceStatus status) {
        final List<T> list = this.datasourcesMap.get(Provider.keyWithStatus(name, status));
        if (null == list || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list;
    }

    @Override
    public Optional<T> pollTemplate(String name) {
        final List<T> list = this.datasourcesMap.get(name);
        return poll(list, counter);
    }

    @Override
    public Optional<T> pollTemplate(String name, CacheDatasourceStatus status) {
        final List<T> list = this.datasourcesMap.get(Provider.keyWithStatus(name, status));
        return poll(list, statusCounter);
    }

    @Override
    public Optional<T> randomTemplate(String name) {
        final List<T> list = this.datasourcesMap.get(name);
        return random(list, random);
    }

    @Override
    public Optional<T> randomTemplate(String name, CacheDatasourceStatus status) {
        final List<T> list = this.datasourcesMap.get(Provider.keyWithStatus(name, status));
        return random(list, statusRandom);
    }

    private Optional<T> poll(List<T> list, AtomicInteger counter) {
        if (null == list || list.isEmpty()) {
            return Optional.empty();
        }

        int index = counter.accumulateAndGet(0, (pre, x) -> pre >= list.size() ? x : pre);
        counter.getAndIncrement();

        return Optional.ofNullable(list.get(index));
    }

    private Optional<T> random(List<T> list, ThreadLocalRandom threadLocalRandom) {
        if (null == list || list.isEmpty()) {
            return Optional.empty();
        }

        int index = threadLocalRandom.nextInt(list.size());
        return Optional.ofNullable(list.get(index));
    }

}
