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

import org.redisson.api.RFuture;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

/**
 * The type R rate limiter.
 */
class RRateLimiters {

    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R rate limiter.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RRateLimiters(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Try set rate limiter boolean.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the boolean
     */
    public boolean trySetRateLimiter(String key, long rate, long rateInterval) {
        return getRateLimiter(key).trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
    }

    /**
     * Try set rate limiter async completable future.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the completable future
     */
    public CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval) {
        return trySetRateLimiterRFuture(key, rate, rateInterval).toCompletableFuture();
    }

    private RFuture<Boolean> trySetRateLimiterRFuture(String key, long rate, long rateInterval) {
        return getRateLimiter(key).trySetRateAsync(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
    }

    /**
     * Try acquire boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean tryAcquire(String key) {
        return getRateLimiter(key).tryAcquire();
    }


    /**
     * Try acquire async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Boolean> tryAcquireAsync(String key) {
        return tryAcquireRFuture(key).toCompletableFuture();
    }

    private RFuture<Boolean> tryAcquireRFuture(String key) {
        return getRateLimiter(key).tryAcquireAsync();
    }


    /**
     * Try acquire boolean.
     *
     * @param key     the key
     * @param permits the permits
     * @return the boolean
     */
    public boolean tryAcquire(String key, long permits) {
        return getRateLimiter(key).tryAcquire(permits);
    }


    /**
     * Try acquire async completable future.
     *
     * @param key     the key
     * @param permits the permits
     * @return the completable future
     */
    public CompletableFuture<Boolean> tryAcquireAsync(String key, long permits) {
        return tryAcquireRFuture(key, permits).toCompletableFuture();
    }

    private RFuture<Boolean> tryAcquireRFuture(String key, long permits) {
        return getRateLimiter(key).tryAcquireAsync(permits);
    }

    private org.redisson.api.RRateLimiter getRateLimiter(String key) {
        return getDataSource().getRateLimiter(key);
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
