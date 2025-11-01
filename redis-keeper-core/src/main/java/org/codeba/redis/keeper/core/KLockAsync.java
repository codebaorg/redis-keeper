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

/**
 * The interface K lock async.
 */
public interface KLockAsync {

    /**
     * Unlocks the lock independently of its state
     *
     * @param key the key
     * @return <code>true</code> if lock existed and now unlocked otherwise <code>false</code>
     */
    CompletableFuture<Boolean> forceUnlockAsync(String key);

    /**
     * Unlocks the lock
     *
     * @param key the key
     * @return void
     */
    CompletableFuture<Void> unlockAsync(String key);

    /**
     * Unlocks the lock. Throws {@link IllegalMonitorStateException}
     * if lock isn't locked by thread with specified <code>threadId</code>.
     *
     * @param key      the key
     * @param threadId id of thread
     * @return void
     */
    CompletableFuture<Void> unlockAsync(String key, long threadId);

    /**
     * Tries to acquire the lock.
     *
     * @param key the key
     * @return <code>true</code> if lock acquired otherwise <code>false</code>
     */
    CompletableFuture<Boolean> tryLockAsync(String key);

    /**
     * Acquires the lock.
     * Waits if necessary until lock became available.
     *
     * @param key the key
     * @return void
     */
    CompletableFuture<Void> lockAsync(String key);

    /**
     * Acquires the lock by thread with defined <code>threadId</code>.
     * Waits if necessary until lock became available.
     *
     * @param key      the key
     * @param threadId id of thread
     * @return void
     */
    CompletableFuture<Void> lockAsync(String key, long threadId);

    /**
     * Acquires the lock with defined <code>leaseTime</code>.
     * Waits if necessary until lock became available.
     * <p>
     * Lock will be released automatically after defined <code>leaseTime</code> interval.
     *
     * @param key       the key
     * @param leaseTime the maximum time to hold the lock after it's acquisition,                  if it hasn't already been released by invoking <code>unlock</code>.                  If leaseTime is -1, hold the lock until explicitly unlocked.
     * @param unit      the time unit
     * @return void
     */
    CompletableFuture<Void> lockAsync(String key, long leaseTime, TimeUnit unit);

    /**
     * Acquires the lock with defined <code>leaseTime</code> and <code>threadId</code>.
     * Waits if necessary until lock became available.
     * <p>
     * Lock will be released automatically after defined <code>leaseTime</code> interval.
     *
     * @param key       the key
     * @param leaseTime the maximum time to hold the lock after it's acquisition,                  if it hasn't already been released by invoking <code>unlock</code>.                  If leaseTime is -1, hold the lock until explicitly unlocked.
     * @param unit      the time unit
     * @param threadId  id of thread
     * @return void
     */
    CompletableFuture<Void> lockAsync(String key, long leaseTime, TimeUnit unit, long threadId);

    /**
     * Tries to acquire the lock by thread with specified <code>threadId</code>.
     *
     * @param key      the key
     * @param threadId id of thread
     * @return <code>true</code> if lock acquired otherwise <code>false</code>
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long threadId);

    /**
     * Tries to acquire the lock.
     * Waits up to defined <code>waitTime</code> if necessary until the lock became available.
     *
     * @param key      the key
     * @param waitTime the maximum time to acquire the lock
     * @param unit     time unit
     * @return <code>true</code> if lock is successfully acquired, otherwise <code>false</code> if lock is already set.
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit);

    /**
     * Tries to acquire the lock with defined <code>leaseTime</code>.
     * Waits up to defined <code>waitTime</code> if necessary until the lock became available.
     * <p>
     * Lock will be released automatically after defined <code>leaseTime</code> interval.
     *
     * @param key       the key
     * @param waitTime  the maximum time to acquire the lock
     * @param leaseTime lease time
     * @param unit      time unit
     * @return <code>true</code> if lock is successfully acquired, otherwise <code>false</code> if lock is already set.
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * Tries to acquire the lock by thread with specified <code>threadId</code> and  <code>leaseTime</code>.
     * Waits up to defined <code>waitTime</code> if necessary until the lock became available.
     * <p>
     * Lock will be released automatically after defined <code>leaseTime</code> interval.
     *
     * @param key       the key
     * @param waitTime  time interval to acquire lock
     * @param leaseTime time interval after which lock will be released automatically
     * @param unit      the time unit of the {@code waitTime} and {@code leaseTime} arguments
     * @param threadId  id of thread
     * @return <code>true</code> if lock acquired otherwise <code>false</code>
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit, long threadId);

    /**
     * Checks if the lock is held by thread with defined <code>threadId</code>
     *
     * @param key      the key
     * @param threadId Thread ID of locking thread
     * @return <code>true</code> if held by thread with given id otherwise <code>false</code>
     */
    CompletableFuture<Boolean> isHeldByThreadAsync(String key, long threadId);

    /**
     * Number of holds on this lock by the current thread
     *
     * @param key the key
     * @return holds or <code>0</code> if this lock is not held by current thread
     */
    CompletableFuture<Integer> getHoldCountAsync(String key);

    /**
     * Checks if the lock locked by any thread
     *
     * @param key the key
     * @return <code>true</code> if locked otherwise <code>false</code>
     */
    CompletableFuture<Boolean> isLockedAsync(String key);

    /**
     * Remaining time to live of the lock
     *
     * @param key the key
     * @return time in milliseconds -2 if the lock does not exist. -1 if the lock exists but has no associated expire.
     */
    CompletableFuture<Long> remainTimeToLiveAsync(String key);

}
