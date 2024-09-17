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

/**
 * The interface K bloom filter.
 */
public interface KBloomFilter {
    /**
     * Bf add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfAdd(String key, Object item);

    /**
     * Bf card long.
     *
     * @param key the key
     * @return the long
     */
    long bfCard(String key);

    /**
     * Bf exists boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfExists(String key, Object item);

    /**
     * Bfm add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfmAdd(String key, Object item);

    /**
     * Bf reserve boolean.
     *
     * @param key                the key
     * @param expectedInsertions the expected insertions
     * @param falseProbability   the false probability
     * @return the boolean
     */
    boolean bfReserve(String key, long expectedInsertions, double falseProbability);

    /**
     * Delete bf boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean deleteBf(String key);

    /**
     * Delete bf async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Boolean> deleteBfAsync(String key);

}
