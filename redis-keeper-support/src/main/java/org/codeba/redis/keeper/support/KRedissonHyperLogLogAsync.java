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

import org.codeba.redis.keeper.core.KHyperLogLogAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RHyperLogLogAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson hyper log async.
 */
class KRedissonHyperLogLogAsync extends BaseAsync implements KHyperLogLogAsync {
    /**
     * Instantiates a new K redisson hyper log log async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonHyperLogLogAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson hyper log log async.
     *
     * @param rBatch the r batch
     * @param codec  the codec
     */
    public KRedissonHyperLogLogAsync(RBatch rBatch, Codec codec) {
        super(rBatch, codec);
    }

    @Override
    public CompletableFuture<Boolean> pfAddAsync(String key, Collection<Object> elements) {
        return getHyperLogLog(key).addAllAsync(elements).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key) {
        return getHyperLogLog(key).countAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key, String... otherKeys) {
        return getHyperLogLog(key).countWithAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> pfMergeAsync(String destKey, String... sourceKeys) {
        return getHyperLogLog(destKey).mergeWithAsync(sourceKeys).toCompletableFuture();
    }

    /**
     * Gets hyper log log async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the hyper log async
     */
    private <V> RHyperLogLogAsync<V> getHyperLogLog(String key) {
        if (null != getBatch()) {
            return getBatch().getHyperLogLog(key, getCodec());
        } else {
            return getRedissonClient().getHyperLogLog(key, getCodec());
        }
    }

}
