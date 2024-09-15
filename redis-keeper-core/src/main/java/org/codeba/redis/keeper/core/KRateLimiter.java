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

public interface KRateLimiter {
    /**
     * Try set rate limiter boolean.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the boolean
     */
    boolean trySetRateLimiter(String key, long rate, long rateInterval);

    /**
     * Try set rate limiter async completable future.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the completable future
     */
    CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval);

    /**
     * Try acquire boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean tryAcquire(String key);

    /**
     * Try acquire async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Boolean> tryAcquireAsync(String key);

    /**
     * Try acquire boolean.
     *
     * @param key     the key
     * @param permits the permits
     * @return the boolean
     */
    boolean tryAcquire(String key, long permits);

    /**
     * Try acquire async completable future.
     *
     * @param key     the key
     * @param permits the permits
     * @return the completable future
     */
    CompletableFuture<Boolean> tryAcquireAsync(String key, long permits);

}
