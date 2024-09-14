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

import org.codeba.redis.keeper.core.KBitSetAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RBitSetAsync;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson bit set async.
 */
class KRedissonBitSetAsync extends BaseAsync implements KBitSetAsync {
    /**
     * Instantiates a new K redisson bit set async.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonBitSetAsync(RedissonClient redissonClient) {
        super(redissonClient);
    }

    /**
     * Instantiates a new K redisson bit set async.
     *
     * @param rBatch the r batch
     */
    public KRedissonBitSetAsync(RBatch rBatch) {
        super(rBatch);
    }

    @Override
    public CompletableFuture<Long> bitCountAsync(String key) {
        return getRBitSetAsync(key).cardinalityAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> bitFieldSetSignedAsync(String key, int size, long offset, long value) {
        return getRBitSetAsync(key).setSignedAsync(size, offset, value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> bitFieldSetUnSignedAsync(String key, int size, long offset, long value) {
        return getRBitSetAsync(key).setUnsignedAsync(size, offset, value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> bitFieldGetSignedAsync(String key, int size, long offset) {
        return getRBitSetAsync(key).getSignedAsync(size, offset).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> bitFieldGetUnSignedAsync(String key, int size, long offset) {
        return getRBitSetAsync(key).getUnsignedAsync(size, offset).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> bitOpOrAsync(String destKey, String... keys) {
        return getRBitSetAsync(destKey).orAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> getBitAsync(String key, long bitIndex) {
        return getRBitSetAsync(key).getAsync(bitIndex).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> setBitAsync(String key, long offset, boolean value) {
        return getRBitSetAsync(key).setAsync(offset, value).toCompletableFuture();
    }

    /**
     * Gets r bit set.
     *
     * @param key the key
     * @return the r bit set
     */
    protected RBitSetAsync getRBitSetAsync(String key) {
        if (null != getrBatch()) {
            return super.getrBatch().getBitSet(key);
        } else {
            return super.getRedissonClient().getBitSet(key);
        }
    }

}
