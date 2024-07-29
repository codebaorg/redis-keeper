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

import org.codeba.redis.keeper.core.KeyType;
import org.redisson.api.RFuture;
import org.redisson.api.RKeys;
import org.redisson.api.RType;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type R generic.
 */
class RGeneric {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R generic.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RGeneric(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Log.
     *
     * @param cmd    the cmd
     * @param params the params
     */
    void log(String cmd, Object... params) {
        if (this.invokeParamsPrint) {
            log.info("cmd:{}, params:{}, connectionInfo:[{}]", cmd, Arrays.toString(params), connectionInfo);
        }
    }

    /**
     * Exists long.
     *
     * @param keys the keys
     * @return the long
     */
    public long exists(String... keys) {
        return getRKeys().countExists(keys);
    }

    /**
     * Exists async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    public CompletableFuture<Long> existsAsync(String... keys) {
        return existsRFuture(keys).toCompletableFuture();
    }

    private RFuture<Long> existsRFuture(String... keys) {
        return getRKeys().countExistsAsync(keys);
    }

    /**
     * Expire boolean.
     *
     * @param key        the key
     * @param timeToLive the time to live
     * @param timeUnit   the time unit
     * @return the boolean
     */
    public boolean expire(String key, long timeToLive, TimeUnit timeUnit) {
        return getRKeys().expire(key, timeToLive, timeUnit);
    }

    /**
     * Expire async r future.
     *
     * @param key        the key
     * @param timeToLive the time to live
     * @param timeUnit   the time unit
     * @return the r future
     */
    public RFuture<Boolean> expireAsync(String key, long timeToLive, TimeUnit timeUnit) {
        return getRKeys().expireAsync(key, timeToLive, timeUnit);
    }

    /**
     * Expire at boolean.
     *
     * @param key       the key
     * @param timestamp the timestamp
     * @return the boolean
     */
    public boolean expireAt(String key, long timestamp) {
        return getRKeys().expireAt(key, timestamp);
    }

    /**
     * Expire at async r future.
     *
     * @param key       the key
     * @param timestamp the timestamp
     * @return the r future
     */
    public RFuture<Boolean> expireAtAsync(String key, long timestamp) {
        return getRKeys().expireAtAsync(key, timestamp);
    }

    /**
     * Del long.
     *
     * @param keys the keys
     * @return the long
     */
    public long del(String... keys) {
        return getRKeys().delete(keys);
    }

    /**
     * Del async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    public CompletableFuture<Long> delAsync(String... keys) {
        return delRFuture(keys).toCompletableFuture();
    }

    private RFuture<Long> delRFuture(String... keys) {
        return getRKeys().deleteAsync(keys);
    }

    /**
     * Unlink long.
     *
     * @param keys the keys
     * @return the long
     */
    public long unlink(String... keys) {
        return getRKeys().unlink(keys);
    }

    /**
     * Unlink async r future.
     *
     * @param keys the keys
     * @return the r future
     */
    public RFuture<Long> unlinkAsync(String... keys) {
        return getRKeys().unlinkAsync(keys);
    }

    /**
     * Ttl long.
     *
     * @param key the key
     * @return the long
     */
    public long ttl(String key) {
        final long remainTimeToLive = getDataSource().getBucket(key).remainTimeToLive();
        return remainTimeToLive < 0 ? remainTimeToLive : remainTimeToLive / 1000;
    }


    /**
     * Ttl async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> ttlAsync(String key) {
        return ttlRFuture(key)
                .handle((v, e) -> {
                    if (null != e) {
                        log("ttlAsync", key, e);
                        return 0L;
                    }
                    return v < 0 ? v : v / 1000;
                })
                .toCompletableFuture();
    }

    /**
     * P ttl long.
     *
     * @param key the key
     * @return the long
     */
    public long pTTL(String key) {
        return getDataSource().getBucket(key).remainTimeToLive();
    }

    /**
     * P ttl async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> pTTLAsync(String key) {
        return ttlRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> ttlRFuture(String key) {
        return getDataSource().getBucket(key).remainTimeToLiveAsync();
    }

    /**
     * Scan iterable.
     *
     * @param keyPattern the key pattern
     * @return the iterable
     */
    public Iterable<String> scan(String keyPattern) {
        return getRKeys().getKeysByPattern(keyPattern);
    }


    /**
     * Scan iterable.
     *
     * @param keyPattern the key pattern
     * @param count      the count
     * @return the iterable
     */
    public Iterable<String> scan(String keyPattern, int count) {
        return getRKeys().getKeysByPattern(keyPattern, count);
    }


    /**
     * Type key type.
     *
     * @param key the key
     * @return the key type
     */
    public KeyType type(String key) {
        final RType type = getRKeys().getType(key);

        if (type == RType.OBJECT) {
            return KeyType.STRING;
        } else if (type == RType.MAP) {
            return KeyType.HASH;
        } else {
            return KeyType.valueOf(type.name());
        }
    }

    /**
     * Type async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<KeyType> typeAsync(String key) {
        return typeRFuture(key)
                .toCompletableFuture()
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
                });
    }

    private RFuture<RType> typeRFuture(String key) {
        return getRKeys().getTypeAsync(key);
    }

    private RKeys getRKeys() {
        return getDataSource().getKeys();
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
