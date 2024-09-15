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

import org.codeba.redis.keeper.core.KGenericAsync;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.api.RBatch;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RKeysAsync;
import org.redisson.api.RType;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson generic async.
 */
class KRedissonGenericAsync extends BaseAsync implements KGenericAsync {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Instantiates a new K redisson generic async.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonGenericAsync(RedissonClient redissonClient) {
        super(redissonClient);
    }

    /**
     * Instantiates a new K redisson generic async.
     *
     * @param rBatch the r batch
     */
    public KRedissonGenericAsync(RBatch rBatch) {
        super(rBatch);
    }

    /**
     * Log.
     *
     * @param cmd    the cmd
     * @param params the params
     */
    void log(String cmd, Object... params) {
        log.info("cmd:{}, params:{}", cmd, Arrays.toString(params));
    }

    @Override
    public CompletableFuture<Long> existsAsync(String... keys) {
        return getRKeys().countExistsAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> expireAsync(String key, long timeToLive, TimeUnit timeUnit) {
        return getRKeys().expireAsync(key, timeToLive, timeUnit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> expireAtAsync(String key, long timestamp) {
        return getRKeys().expireAtAsync(key, timestamp).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> delAsync(String... keys) {
        return getRKeys().deleteAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> unlinkAsync(String... keys) {
        return getRKeys().unlinkAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> ttlAsync(String key) {
        return getRBucket(key).remainTimeToLiveAsync()
                .handle((v, e) -> {
                    if (null != e) {
                        log("ttlAsync", key, e);
                        return 0L;
                    }
                    return v < 0 ? v : v / 1000;
                })
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> pTTLAsync(String key) {
        return getRBucket(key).remainTimeToLiveAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<KeyType> typeAsync(String key) {
        return getRKeys().getTypeAsync(key)
                .handle((type, e) -> {
                    if (null != e) {
                        log("typeAsync", key, e);
                    }
                    if (type == RType.OBJECT) {
                        return KeyType.STRING;
                    } else if (type == RType.MAP) {
                        return KeyType.HASH;
                    } else {
                        return KeyType.valueOf(type.name());
                    }
                })
                .toCompletableFuture();
    }

    /**
     * Gets keys async.
     *
     * @return the keys async
     */
    private RKeysAsync getRKeys() {
        if (null != getBatch()) {
            return super.getBatch().getKeys();
        } else {
            return super.getRedissonClient().getKeys();
        }
    }

    /**
     * Gets r bucket async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the r bucket async
     */
    private <V> RBucketAsync<V> getRBucket(String key) {
        if (null != getBatch()) {
            return super.getBatch().getBucket(key);
        } else {
            return super.getRedissonClient().getBucket(key);
        }
    }

}
