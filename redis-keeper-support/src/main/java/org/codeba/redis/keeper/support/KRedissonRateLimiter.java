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

import org.codeba.redis.keeper.core.KRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson rate limiter.
 */
class KRedissonRateLimiter extends BaseAsync implements KRateLimiter {
    /**
     * Instantiates a new K redisson rate limiter.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonRateLimiter(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public boolean trySetRateLimiter(String key, long rate, long rateInterval) {
        return getRateLimiter(key).trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval) {
        return getRateLimiter(key).trySetRateAsync(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS).toCompletableFuture();
    }

    @Override
    public boolean tryAcquire(String key) {
        return getRateLimiter(key).tryAcquire();
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key) {
        return getRateLimiter(key).tryAcquireAsync().toCompletableFuture();
    }

    @Override
    public boolean tryAcquire(String key, long permits) {
        return getRateLimiter(key).tryAcquire(permits);
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key, long permits) {
        return getRateLimiter(key).tryAcquireAsync(permits).toCompletableFuture();
    }

    /**
     * Gets rate limiter.
     *
     * @param key the key
     * @return the rate limiter
     */
    private org.redisson.api.RRateLimiter getRateLimiter(String key) {
        return getRedissonClient().getRateLimiter(key);
    }

}
