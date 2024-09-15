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
import java.util.concurrent.TimeUnit;

public interface KLock {
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
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * Try lock async completable future.
     *
     * @param key       the key
     * @param waitTime  the wait time
     * @param leaseTime the lease time
     * @param unit      the unit
     * @return the completable future
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * Try lock boolean.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException;

    /**
     * Try lock async completable future.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the completable future
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit);

    /**
     * Unlock.
     *
     * @param key the key
     */
    void unlock(String key);

    /**
     * Unlock async.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Void> unlockAsync(String key);

    /**
     * Unlock async.
     *
     * @param key      the key
     * @param threadId the thread id
     * @return the completable future
     */
    CompletableFuture<Void> unlockAsync(String key, long threadId);

    /**
     * Force unlock boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean forceUnlock(String key);

    /**
     * Force unlock async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Boolean> forceUnlockAsync(String key);

}
