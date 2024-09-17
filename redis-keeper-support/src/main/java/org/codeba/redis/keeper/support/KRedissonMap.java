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

import org.codeba.redis.keeper.core.KMap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The type K redisson map.
 */
class KRedissonMap extends KRedissonMapAsync implements KMap {

    /**
     * The Redisson client.
     */
    private final RedissonClient redissonClient;
    /**
     * The Codec.
     */
    private final Codec codec;

    /**
     * Instantiates a new K redisson map.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonMap(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
        this.redissonClient = redissonClient;
        this.codec = codec;
    }

    @Override
    public Map<String, Boolean> hDel(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);

        final Map<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            final long fastRemove = rMap.fastRemove(field);
            resultMap.put(field, 0 != fastRemove);
        }

        return resultMap;
    }

    @Override
    public Map<String, Boolean> hExists(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKey(field));
        }

        return resultMap;
    }

    @Override
    public Optional<Object> hGet(String key, String field) {
        return Optional.ofNullable(getMap(key).get(field));
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        return getMap(key).readAllMap();
    }

    @Override
    public Object hIncrBy(String key, String field, Number value) {
        return getMap(key).addAndGet(field, value);
    }

    @Override
    public Collection<Object> hKeys(String key) {
        return getMap(key).readAllKeySet();
    }

    @Override
    public int hLen(String key) {
        return getMap(key).size();
    }

    @Override
    public Map<Object, Object> hmGet(String key, Set<Object> fields) {
        if (null == fields || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        return getMap(key).getAll(fields);
    }

    @Override
    public void hmSet(String key, Map<?, ?> kvMap) {
        getMap(key).putAll(kvMap, 100);
    }

    @Override
    public void hSet(String key, String field, Object value) {
        getMap(key).fastPut(field, value);
    }

    @Override
    public Set<Object> hRandField(String key, int count) {
        return getMap(key).randomKeys(count);
    }

    @Override
    public Map<Object, Object> hRandFieldWithValues(String key, int count) {
        return getMap(key).randomEntries(count);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern) {
        return getMap(key).entrySet(keyPattern).iterator();
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count) {
        return getMap(key).entrySet(keyPattern, count).iterator();
    }

    @Override
    public void hSetNX(String key, String field, Object value) {
        getMap(key).fastPutIfAbsent(field, value);
    }

    @Override
    public int hStrLen(String key, String field) {
        return getMap(key).valueSize(field);
    }

    @Override
    public Collection<Object> hVALs(String key) {
        return getMap(key).readAllValues();
    }

    /**
     * Gets map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param key the key
     * @return the map
     */
    private <K, V> RMap<K, V> getMap(String key) {
        return redissonClient.getMap(key, codec);
    }

}
