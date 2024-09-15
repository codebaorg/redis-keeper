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

import org.codeba.redis.keeper.core.KStringAsync;
import org.redisson.api.RAtomicDoubleAsync;
import org.redisson.api.RAtomicLongAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson string async.
 */
class KRedissonStringAsync extends BaseAsync implements KStringAsync {
    /**
     * Instantiates a new K redisson string async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonStringAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson string async.
     *
     * @param batch the batch
     * @param codec the codec
     */
    public KRedissonStringAsync(RBatch batch, Codec codec) {
        super(batch, codec);
    }

    @Override
    public CompletableFuture<Long> decrAsync(String key) {
        return getRAtomicLong(key).decrementAndGetAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> decrByAsync(String key, long decrement) {
        return getRAtomicLong(key).addAndGetAsync(-decrement).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> getAsync(String key) {
        return getRBucket(key, getCodec()).getAsync()
                .handle((object, throwable) -> {
                    if (null != throwable) {
                        return getRBucket(key).getAsync().join();
                    }
                    return object;
                }).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> getObjectAsync(String key) {
        return getRBucket(key).getAsync()
                .handle((object, throwable) -> {
                    if (null != throwable) {
                        return getRBucket(key, getCodec()).getAsync().join();
                    }
                    return object;
                }).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> getDelAsync(String key) {
        return getRBucket(key, getCodec()).getAndDeleteAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> getLongAsync(String key) {
        return getRAtomicLong(key).getAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> incrAsync(String key) {
        return getRAtomicLong(key).incrementAndGetAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> incrByAsync(String key, long increment) {
        return getRAtomicLong(key).addAndGetAsync(increment).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Double> getDoubleAsync(String key) {
        return getRAtomicDouble(key).getAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Double> incrByFloatAsync(String key, double increment) {
        return getRAtomicDouble(key).addAndGetAsync(increment).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update) {
        return getRAtomicLong(key).compareAndSetAsync(expect, update).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update) {
        return getRAtomicDouble(key).compareAndSetAsync(expect, update).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> setObjectAsync(String key, Object value) {
        if (value instanceof String) {
            return setAsync(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            return setAsync(key, Long.parseLong(value.toString()));
        } else if (value instanceof Float || value instanceof Double) {
            return setAsync(key, Double.parseDouble(value.toString()));
        } else {
            return getRBucket(key).setAsync(value).toCompletableFuture();
        }
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        return getRBucket(key, getCodec()).setAsync(value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Long value) {
        return getRAtomicLong(key).setAsync(value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Double value) {
        return getRAtomicDouble(key).setAsync(value).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update) {
        return getRBucket(key, getCodec()).compareAndSetAsync(expect, update).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> setEXAsync(String key, String value, Duration duration) {
        return getRBucket(key, getCodec()).setAsync(value, duration.toMillis(), TimeUnit.MILLISECONDS).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> strLenAsync(String key) {
        return getRBucket(key, getCodec()).sizeAsync().toCompletableFuture();
    }

    /**
     * Gets r bucket.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the r bucket
     */
    private <T> RBucketAsync<T> getRBucket(String key) {
        if (null != getBatch()) {
            return getBatch().getBucket(key);
        } else {
            return getRedissonClient().getBucket(key);
        }
    }

    /**
     * Gets r bucket.
     *
     * @param <T>   the type parameter
     * @param key   the key
     * @param codec the codec
     * @return the r bucket
     */
    private <T> RBucketAsync<T> getRBucket(String key, Codec codec) {
        if (null != getBatch()) {
            return getBatch().getBucket(key, codec);
        } else {
            return getRedissonClient().getBucket(key, codec);
        }
    }

    /**
     * Gets r atomic long.
     *
     * @param key the key
     * @return the r atomic long
     */
    private RAtomicLongAsync getRAtomicLong(String key) {
        if (null != getBatch()) {
            return getBatch().getAtomicLong(key);
        } else {
            return getRedissonClient().getAtomicLong(key);
        }
    }

    /**
     * Gets r atomic double.
     *
     * @param key the key
     * @return the r atomic double
     */
    private RAtomicDoubleAsync getRAtomicDouble(String key) {
        if (null != getBatch()) {
            return getBatch().getAtomicDouble(key);
        } else {
            return getRedissonClient().getAtomicDouble(key);
        }
    }

}
