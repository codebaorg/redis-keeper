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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The interface K generic async.
 */
public interface KGenericAsync {
    /**
     * Exists async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Long> existsAsync(String... keys);

    /**
     * Expire async completable future.
     *
     * @param key        the key
     * @param timeToLive the time to live
     * @param timeUnit   the time unit
     * @return the completable future
     */
    CompletableFuture<Boolean> expireAsync(String key, long timeToLive, TimeUnit timeUnit);

    /**
     * Expire at async completable future.
     *
     * @param key       the key
     * @param timestamp the timestamp
     * @return the completable future
     */
    CompletableFuture<Boolean> expireAtAsync(String key, long timestamp);

    /**
     * Del async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Long> delAsync(String... keys);

    /**
     * Unlink async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Long> unlinkAsync(String... keys);

    /**
     * Ttl async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> ttlAsync(String key);

    /**
     * P ttl async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> pTTLAsync(String key);

    /**
     * Type async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<KeyType> typeAsync(String key);

}
