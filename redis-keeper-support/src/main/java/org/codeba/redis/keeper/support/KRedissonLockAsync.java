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

import org.codeba.redis.keeper.core.KLockAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson lock async.
 */
class KRedissonLockAsync extends BaseAsync implements KLockAsync {

    /**
     * Instantiates a new K redisson lock async.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonLockAsync(RedissonClient redissonClient) {
        super(redissonClient);
    }

    /**
     * Instantiates a new K redisson lock async.
     *
     * @param batch the batch
     */
    public KRedissonLockAsync(RBatch batch) {
        super(batch);
    }

    @Override
    public CompletableFuture<Boolean> forceUnlockAsync(String key) {
        return getRLock(key).forceUnlockAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key) {
        return getRLock(key).unlockAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key, long threadId) {
        return getRLock(key).unlockAsync(threadId).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key) {
        return getRLock(key).tryLockAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key) {
        return getRLock(key).lockAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key, long threadId) {
        return getRLock(key).lockAsync(threadId).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key, long leaseTime, TimeUnit unit) {
        return getRLock(key).lockAsync(leaseTime, unit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key, long leaseTime, TimeUnit unit, long threadId) {
        return getRLock(key).lockAsync(leaseTime, unit, threadId).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long threadId) {
        return getRLock(key).tryLockAsync(threadId).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit) {
        return getRLock(key).tryLockAsync(waitTime, unit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit) {
        return getRLock(key).tryLockAsync(waitTime, leaseTime, unit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit, long threadId) {
        return getRLock(key).tryLockAsync(waitTime, leaseTime, unit, threadId).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> isHeldByThreadAsync(String key, long threadId) {
        return getRLock(key).isHeldByThreadAsync(threadId).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> getHoldCountAsync(String key) {
        return getRLock(key).getHoldCountAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> isLockedAsync(String key) {
        return getRLock(key).isLockedAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Long> remainTimeToLiveAsync(String key) {
        return getRLock(key).remainTimeToLiveAsync().toCompletableFuture();
    }

    /**
     * Gets r lock.
     *
     * @param key the key
     * @return the r lock
     */
    private RLock getRLock(String key) {
        return getRedissonClient().getLock(key);
    }

}
