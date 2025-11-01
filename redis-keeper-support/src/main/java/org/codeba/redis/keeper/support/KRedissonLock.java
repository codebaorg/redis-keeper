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

import org.codeba.redis.keeper.core.KLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * The type K redisson lock.
 */
class KRedissonLock extends KRedissonLockAsync implements KLock {
    /**
     * Instantiates a new K redisson lock.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonLock(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return getRLock(key).tryLock(waitTime, leaseTime, unit);
    }

    @Override
    public void lock(String key, long leaseTime, TimeUnit unit) {
        getRLock(key).lock(leaseTime, unit);
    }

    @Override
    public void lock(String key) {
        getRLock(key).lock();
    }

    @Override
    public void lockInterruptibly(String key) throws InterruptedException {
        getRLock(key).lockInterruptibly();
    }

    @Override
    public boolean tryLock(String key) {
        return getRLock(key).tryLock();
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        return getRLock(key).tryLock(waitTime, unit);
    }

    @Override
    public void unlock(String key) {
        getRLock(key).unlock();
    }

    @Override
    public void lockInterruptibly(String key, long leaseTime, TimeUnit unit) throws InterruptedException {
        getRLock(key).lockInterruptibly(leaseTime, unit);
    }

    @Override
    public boolean forceUnlock(String key) {
        return getRLock(key).forceUnlock();
    }

    @Override
    public boolean isLocked(String key) {
        return getRLock(key).isLocked();
    }

    @Override
    public boolean isHeldByThread(String key, long threadId) {
        return getRLock(key).isHeldByThread(threadId);
    }

    @Override
    public boolean isHeldByCurrentThread(String key) {
        return getRLock(key).isHeldByCurrentThread();
    }

    @Override
    public int getHoldCount(String key) {
        return getRLock(key).getHoldCount();
    }

    @Override
    public long remainTimeToLive(String key) {
        return getRLock(key).remainTimeToLive();
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
