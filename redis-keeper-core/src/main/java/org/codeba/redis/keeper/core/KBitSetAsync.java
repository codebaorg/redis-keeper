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
 * The interface K bit map async.
 */
public interface KBitSetAsync {
    /**
     * Bit count async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> bitCountAsync(String key);

    /**
     * Bit field set signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the completable future
     */
    CompletableFuture<Long> bitFieldSetSignedAsync(String key, int size, long offset, long value);

    /**
     * Bit field set un signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the completable future
     */
    CompletableFuture<Long> bitFieldSetUnSignedAsync(String key, int size, long offset, long value);

    /**
     * Bit field get signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the completable future
     */
    CompletableFuture<Long> bitFieldGetSignedAsync(String key, int size, long offset);

    /**
     * Bit field get un signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the completable future
     */
    CompletableFuture<Long> bitFieldGetUnSignedAsync(String key, int size, long offset);

    /**
     * Bit op or async completable future.
     *
     * @param destKey the dest key
     * @param keys    the keys
     * @return the completable future
     */
    CompletableFuture<Void> bitOpOrAsync(String destKey, String... keys);

    /**
     * Gets bit async.
     *
     * @param key      the key
     * @param bitIndex the bit index
     * @return the bit async
     */
    CompletableFuture<Boolean> getBitAsync(String key, long bitIndex);

    /**
     * Sets bit async.
     *
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the bit async
     */
    CompletableFuture<Boolean> setBitAsync(String key, long offset, boolean value);

}
