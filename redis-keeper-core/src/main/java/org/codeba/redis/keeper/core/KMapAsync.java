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

package org.codeba.redis.keeper.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The interface K map async.
 */
public interface KMapAsync {

    /**
     * H del async.
     *
     * @param key    the key
     * @param fields the fields
     * @return the completable future
     */
    CompletableFuture<Long> hDelAsync(String key, String... fields);

    /**
     * H exists async map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<String, CompletableFuture<Boolean>> hExistsAsync(String key, String... fields);

    /**
     * H get async completable future.
     *
     * @param key   the key
     * @param field the field
     * @return the completable future
     */
    CompletableFuture<Object> hGetAsync(String key, String field);

    /**
     * H get all async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Map<Object, Object>> hGetAllAsync(String key);

    /**
     * H incr by async.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the completable future
     */
    CompletableFuture<Object> hIncrByAsync(String key, String field, Number value);

    /**
     * H keys async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Set<Object>> hKeysAsync(String key);

    /**
     * H len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> hLenAsync(String key);

    /**
     * Hm get async completable future.
     *
     * @param key    the key
     * @param fields the fields
     * @return the completable future
     */
    CompletableFuture<Map<Object, Object>> hmGetAsync(String key, Set<Object> fields);

    /**
     * Hm set async.
     *
     * @param key   the key
     * @param kvMap the kv map
     * @return the completable future
     */
    CompletableFuture<Void> hmSetAsync(String key, Map<?, ?> kvMap);

    /**
     * H set async.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the completable future
     */
    CompletableFuture<Boolean> hSetAsync(String key, String field, Object value);

    /**
     * H rand fields async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Set<Object>> hRandFieldsAsync(String key, int count);

    /**
     * H rand field with values async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Map<Object, Object>> hRandFieldWithValuesAsync(String key, int count);

    /**
     * H set nx async.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the completable future
     */
    CompletableFuture<Boolean> hSetNXAsync(String key, String field, Object value);

    /**
     * H str len async completable future.
     *
     * @param key   the key
     * @param field the field
     * @return the completable future
     */
    CompletableFuture<Integer> hStrLenAsync(String key, String field);

    /**
     * H va ls async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> hVALsAsync(String key);

}
