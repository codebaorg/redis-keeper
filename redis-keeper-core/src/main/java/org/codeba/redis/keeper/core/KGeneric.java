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

import java.util.concurrent.TimeUnit;

/**
 * The interface K generic.
 */
public interface KGeneric extends KGenericAsync {
    /**
     * Exists long.
     *
     * @param keys the keys
     * @return the long
     */
    long exists(String... keys);

    /**
     * Expire boolean.
     *
     * @param key        the key
     * @param timeToLive the time to live
     * @param timeUnit   the time unit
     * @return the boolean
     */
    boolean expire(String key, long timeToLive, TimeUnit timeUnit);

    /**
     * Expire at boolean.
     *
     * @param key       the key
     * @param timestamp the timestamp
     * @return the boolean
     */
    boolean expireAt(String key, long timestamp);

    /**
     * Del long.
     *
     * @param keys the keys
     * @return the long
     */
    long del(String... keys);

    /**
     * Unlink long.
     *
     * @param keys the keys
     * @return the long
     */
    long unlink(String... keys);

    /**
     * Ttl long.
     *
     * @param key the key
     * @return the long
     */
    long ttl(String key);

    /**
     * P ttl long.
     *
     * @param key the key
     * @return the long
     */
    long pTTL(String key);

    /**
     * Scan iterable.
     *
     * @param keyPattern the key pattern
     * @return the iterable
     */
    Iterable<String> scan(String keyPattern);

    /**
     * Scan iterable.
     *
     * @param keyPattern the key pattern
     * @param count      the count
     * @return the iterable
     */
    Iterable<String> scan(String keyPattern, int count);

    /**
     * Type key type.
     *
     * @param key the key
     * @return the key type
     */
    KeyType type(String key);

}
