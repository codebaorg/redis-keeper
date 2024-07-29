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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type R locks.
 */
class RLocks {

    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R locks.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RLocks(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Try lock boolean.
     *
     * @param key       the key
     * @param waitTime  the wait time
     * @param leaseTime the lease time
     * @param unit      the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return getRLock(key).tryLock(waitTime, leaseTime, unit);
    }


    /**
     * Try lock async completable future.
     *
     * @param key       the key
     * @param waitTime  the wait time
     * @param leaseTime the lease time
     * @param unit      the unit
     * @return the completable future
     */
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit) {
        return tryLockRFuture(key, waitTime, leaseTime, unit).toCompletableFuture();
    }

    private RFuture<Boolean> tryLockRFuture(String key, long waitTime, long leaseTime, TimeUnit unit) {
        return getRLock(key).tryLockAsync(waitTime, leaseTime, unit);
    }


    /**
     * Try lock boolean.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        return getRLock(key).tryLock(waitTime, unit);
    }


    /**
     * Try lock async completable future.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the completable future
     */
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit) {
        return tryLockRFuture(key, waitTime, unit).toCompletableFuture();
    }

    private RFuture<Boolean> tryLockRFuture(String key, long waitTime, TimeUnit unit) {
        return getRLock(key).tryLockAsync(waitTime, unit);
    }


    /**
     * Unlock.
     *
     * @param key the key
     */
    public void unlock(String key) {
        getRLock(key).unlock();
    }


    /**
     * Unlock async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Void> unlockAsync(String key) {
        return unlockRFuture(key).toCompletableFuture();
    }

    private RFuture<Void> unlockRFuture(String key) {
        return getRLock(key).unlockAsync();
    }


    /**
     * Unlock async completable future.
     *
     * @param key      the key
     * @param threadId the thread id
     * @return the completable future
     */
    public CompletableFuture<Void> unlockAsync(String key, long threadId) {
        return unlockAsyncRFuture(key, threadId).toCompletableFuture();
    }

    private RFuture<Void> unlockAsyncRFuture(String key, long threadId) {
        return getRLock(key).unlockAsync(threadId);
    }


    /**
     * Force unlock boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean forceUnlock(String key) {
        return getRLock(key).forceUnlock();
    }


    /**
     * Force unlock async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Boolean> forceUnlockAsync(String key) {
        return forceUnlockRFuture(key).toCompletableFuture();
    }

    private RFuture<Boolean> forceUnlockRFuture(String key) {
        return getRLock(key).forceUnlockAsync();
    }

    private RLock getRLock(String key) {
        return getDataSource().getLock(key);
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
