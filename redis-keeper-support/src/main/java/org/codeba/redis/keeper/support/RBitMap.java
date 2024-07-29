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

import org.redisson.api.RBitSet;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

/**
 * The type R bit map.
 */
class RBitMap {

    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R bit map.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RBitMap(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Bit count long.
     *
     * @param key the key
     * @return the long
     */
    public long bitCount(String key) {
        return getRBitSet(key).cardinality();
    }


    /**
     * Bit count async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> bitCountAsync(String key) {
        return bitCountRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> bitCountRFuture(String key) {
        return getRBitSet(key).cardinalityAsync();
    }


    /**
     * Bit field set signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the long
     */
    public long bitFieldSetSigned(String key, int size, long offset, long value) {
        return getRBitSet(key).setSigned(size, offset, value);
    }


    /**
     * Bit field set signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the completable future
     */
    public CompletableFuture<Long> bitFieldSetSignedAsync(String key, int size, long offset, long value) {
        return bitFieldSetSignedRFuture(key, size, offset, value).toCompletableFuture();
    }

    private RFuture<Long> bitFieldSetSignedRFuture(String key, int size, long offset, long value) {
        return getRBitSet(key).setSignedAsync(size, offset, value);
    }


    /**
     * Bit field set un signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the long
     */
    public long bitFieldSetUnSigned(String key, int size, long offset, long value) {
        return getRBitSet(key).setUnsigned(size, offset, value);
    }


    /**
     * Bit field set un signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the completable future
     */
    public CompletableFuture<Long> bitFieldSetUnSignedAsync(String key, int size, long offset, long value) {
        return bitFieldSetUnSignedRFuture(key, size, offset, value).toCompletableFuture();
    }

    private RFuture<Long> bitFieldSetUnSignedRFuture(String key, int size, long offset, long value) {
        return getRBitSet(key).setUnsignedAsync(size, offset, value);
    }


    /**
     * Bit field get signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the long
     */
    public long bitFieldGetSigned(String key, int size, long offset) {
        return getRBitSet(key).getSigned(size, offset);
    }


    /**
     * Bit field get signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the completable future
     */
    public CompletableFuture<Long> bitFieldGetSignedAsync(String key, int size, long offset) {
        return bitFieldGetSignedRFuture(key, size, offset).toCompletableFuture();
    }

    private RFuture<Long> bitFieldGetSignedRFuture(String key, int size, long offset) {
        return getRBitSet(key).getSignedAsync(size, offset);
    }


    /**
     * Bit field get un signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the long
     */
    public long bitFieldGetUnSigned(String key, int size, long offset) {
        return getRBitSet(key).getUnsigned(size, offset);
    }


    /**
     * Bit field get un signed async completable future.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the completable future
     */
    public CompletableFuture<Long> bitFieldGetUnSignedAsync(String key, int size, long offset) {
        return bitFieldGetUnSignedRFuture(key, size, offset).toCompletableFuture();
    }

    private RFuture<Long> bitFieldGetUnSignedRFuture(String key, int size, long offset) {
        return getRBitSet(key).getUnsignedAsync(size, offset);
    }


    /**
     * Bit op or.
     *
     * @param destKey the dest key
     * @param keys    the keys
     */
    public void bitOpOr(String destKey, String... keys) {
        getRBitSet(destKey).or(keys);
    }


    /**
     * Bit op or async completable future.
     *
     * @param destKey the dest key
     * @param keys    the keys
     * @return the completable future
     */
    public CompletableFuture<Void> bitOpOrAsync(String destKey, String... keys) {
        return bitOpOrRFuture(destKey, keys).toCompletableFuture();
    }

    private RFuture<Void> bitOpOrRFuture(String destKey, String... keys) {
        return getRBitSet(destKey).orAsync(keys);
    }


    /**
     * Gets bit.
     *
     * @param key      the key
     * @param bitIndex the bit index
     * @return the bit
     */
    public boolean getBit(String key, long bitIndex) {
        return getRBitSet(key).get(bitIndex);
    }


    /**
     * Gets bit async.
     *
     * @param key      the key
     * @param bitIndex the bit index
     * @return the bit async
     */
    public CompletableFuture<Boolean> getBitAsync(String key, long bitIndex) {
        return getBitRFuture(key, bitIndex).toCompletableFuture();
    }

    private RFuture<Boolean> getBitRFuture(String key, long bitIndex) {
        return getRBitSet(key).getAsync(bitIndex);
    }


    /**
     * Sets bit.
     *
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the bit
     */
    public boolean setBit(String key, long offset, boolean value) {
        return getRBitSet(key).set(offset, value);
    }


    /**
     * Sets bit async.
     *
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the bit async
     */
    public CompletableFuture<Boolean> setBitAsync(String key, long offset, boolean value) {
        return setBitRFuture(key, offset, value).toCompletableFuture();
    }

    private RFuture<Boolean> setBitRFuture(String key, long offset, boolean value) {
        return getRBitSet(key).setAsync(offset, value);
    }

    private RBitSet getRBitSet(String key) {
        return getDataSource().getBitSet(key);
    }

    /**
     * Gets data source.
     *
     * @return the data source
     */
    RedissonClient getDataSource() {
        return this.redissonClient;
    }

}
