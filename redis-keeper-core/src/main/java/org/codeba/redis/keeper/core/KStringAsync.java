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

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * The interface K string async.
 */
public interface KStringAsync {

    /**
     * Decr async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> decrAsync(String key);


    /**
     * Decr by async completable future.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the completable future
     */
    CompletableFuture<Long> decrByAsync(String key, long decrement);


    /**
     * Gets async.
     *
     * @param key the key
     * @return the async
     */
    CompletableFuture<Object> getAsync(String key);

    /**
     * Gets object async.
     *
     * @param key the key
     * @return the object async
     */
    CompletableFuture<Object> getObjectAsync(String key);


    /**
     * Gets del async.
     *
     * @param key the key
     * @return the del async
     */
    CompletableFuture<Object> getDelAsync(String key);


    /**
     * Gets long async.
     *
     * @param key the key
     * @return the long async
     */
    CompletableFuture<Long> getLongAsync(String key);


    /**
     * Incr async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> incrAsync(String key);


    /**
     * Incr by async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @return the completable future
     */
    CompletableFuture<Long> incrByAsync(String key, long increment);


    /**
     * Gets double async.
     *
     * @param key the key
     * @return the double async
     */
    CompletableFuture<Double> getDoubleAsync(String key);


    /**
     * Incr by float async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @return the completable future
     */
    CompletableFuture<Double> incrByFloatAsync(String key, double increment);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setObjectAsync(String key, Object value);

    /**
     * Sets object async.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     * @return the object async
     */
    CompletableFuture<Void> setObjectEXAsync(String key, Object value, Duration duration);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setAsync(String key, String value);

    /**
     * Sets ex async.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     * @return the ex async
     */
    CompletableFuture<Void> setEXAsync(String key, String value, Duration duration);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setAsync(String key, Long value);


    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setAsync(String key, Double value);


    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update);

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update);


    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update);

    /**
     * Str len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> strLenAsync(String key);


}
