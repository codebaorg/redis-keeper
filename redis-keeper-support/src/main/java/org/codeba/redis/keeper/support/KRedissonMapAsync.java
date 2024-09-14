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

import org.codeba.redis.keeper.core.KMapAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RMapAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson map async.
 */
class KRedissonMapAsync extends BaseAsync implements KMapAsync {

    /**
     * Instantiates a new K redisson map async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonMapAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson map async.
     *
     * @param rBatch the r batch
     * @param codec  the codec
     */
    public KRedissonMapAsync(RBatch rBatch, Codec codec) {
        super(rBatch, codec);
    }

    @Override
    public CompletableFuture<Long> hDelAsync(String key, String... fields) {
        return getMapAsync(key).fastRemoveAsync(fields).toCompletableFuture();
    }

    @Override
    public Map<String, CompletableFuture<Boolean>> hExistsAsync(String key, String... fields) {
        final RMapAsync<Object, Object> rMap = getMapAsync(key);

        final HashMap<String, CompletableFuture<Boolean>> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKeyAsync(field).toCompletableFuture());
        }

        return resultMap;
    }

    @Override
    public CompletableFuture<Object> hGetAsync(String key, String field) {
        return getMapAsync(key).getAsync(field).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hGetAllAsync(String key) {
        return getMapAsync(key).readAllMapAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> hIncrByAsync(String key, String field, Number value) {
        return getMapAsync(key).addAndGetAsync(field, value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> hKeysAsync(String key) {
        return getMapAsync(key).readAllKeySetAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> hLenAsync(String key) {
        return getMapAsync(key).sizeAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hmGetAsync(String key, Set<Object> fields) {
        return getMapAsync(key).getAllAsync(fields).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> hmSetAsync(String key, Map<?, ?> kvMap) {
        return getMapAsync(key).putAllAsync(kvMap, 100).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> hSetAsync(String key, String field, Object value) {
        return getMapAsync(key).fastPutAsync(field, value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> hRandFieldsAsync(String key, int count) {
        return getMapAsync(key).randomKeysAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hRandFieldWithValuesAsync(String key, int count) {
        return getMapAsync(key).randomEntriesAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> hSetNXAsync(String key, String field, Object value) {
        return getMapAsync(key).fastPutIfAbsentAsync(field, value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> hStrLenAsync(String key, String field) {
        return getMapAsync(key).valueSizeAsync(field).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> hVALsAsync(String key) {
        return getMapAsync(key).readAllValuesAsync().toCompletableFuture();
    }

    /**
     * Gets map async.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param key the key
     * @return the map async
     */
    protected <K, V> RMapAsync<K, V> getMapAsync(String key) {
        if (null != getrBatch()) {
            return getrBatch().getMap(key, getCodec());
        } else {
            return getRedissonClient().getMap(key, getCodec());
        }
    }

}
