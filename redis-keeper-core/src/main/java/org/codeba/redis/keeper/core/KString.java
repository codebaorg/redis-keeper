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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The interface K string.
 */
public interface KString extends KStringAsync {
    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     */
    void append(String key, Object value);

    /**
     * Decr long.
     *
     * @param key the key
     * @return the long
     */
    long decr(String key);

    /**
     * Decr by long.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the long
     */
    long decrBy(String key, long decrement);

    /**
     * Get optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> get(String key);

    /**
     * Gets object.
     *
     * @param key the key
     * @return the object
     */
    Optional<Object> getObject(String key);

    /**
     * Gets del.
     *
     * @param key the key
     * @return the del
     */
    Optional<Object> getDel(String key);

    /**
     * Gets ex.
     *
     * @param key     the key
     * @param seconds the seconds
     * @return the ex
     */
    Optional<Object> getEX(String key, long seconds);

    /**
     * Gets px.
     *
     * @param key          the key
     * @param milliseconds the milliseconds
     * @return the px
     */
    Optional<Object> getPX(String key, long milliseconds);

    /**
     * Gets ex at.
     *
     * @param key             the key
     * @param unixTimeSeconds the unix time seconds
     * @return the ex at
     */
    Optional<Object> getEXAt(String key, long unixTimeSeconds);

    /**
     * Gets px at.
     *
     * @param key                  the key
     * @param unixTimeMilliseconds the unix time milliseconds
     * @return the px at
     */
    Optional<Object> getPXAt(String key, long unixTimeMilliseconds);

    /**
     * Gets ex persist.
     *
     * @param key the key
     * @return the ex persist
     */
    Optional<Object> getEXPersist(String key);

    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     */
    long getLong(String key);

    /**
     * Incr long.
     *
     * @param key the key
     * @return the long
     */
    long incr(String key);

    /**
     * Incr by long.
     *
     * @param key       the key
     * @param increment the increment
     * @return the long
     */
    long incrBy(String key, long increment);

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     */
    double getDouble(String key);

    /**
     * Incr by float double.
     *
     * @param key       the key
     * @param increment the increment
     * @return the double
     */
    double incrByFloat(String key, double increment);

    /**
     * M get async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Map<String, Object>> mGetAsync(String... keys);


    /**
     * M set async completable future.
     *
     * @param kvMap the kv map
     * @return the completable future
     */
    CompletableFuture<Void> mSetAsync(Map<String, String> kvMap);


    /**
     * M set nx async completable future.
     *
     * @param kvMap the kv map
     * @return the completable future
     */
    CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap);

    /**
     * M get map.
     *
     * @param keys the keys
     * @return the map
     */
    Map<String, Object> mGet(String... keys);

    /**
     * M set.
     *
     * @param kvMap the kv map
     */
    void mSet(Map<String, String> kvMap);

    /**
     * M set nx boolean.
     *
     * @param kvMap the kv map
     * @return the boolean
     */
    boolean mSetNX(Map<String, String> kvMap);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, String value);

    /**
     * Sets ex.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     */
    void setEX(String key, String value, Duration duration);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, Long value);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, Double value);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void setObject(String key, Object value);

    /**
     * Sets object ex.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     */
    void setObjectEx(String key, Object value, Duration duration);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, String expect, String update);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, long expect, long update);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, double expect, double update);

    /**
     * Str len long.
     *
     * @param key the key
     * @return the long
     */
    long strLen(String key);

}
