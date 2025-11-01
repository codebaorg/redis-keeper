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

import java.util.concurrent.TimeUnit;

/**
 * The interface K lock.
 */
public interface KLock extends KLockAsync {

    /**
     * Acquires the lock.
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until the
     * lock has been acquired.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>A {@code Lock} implementation may be able to detect erroneous use
     * of the lock, such as an invocation that would cause deadlock, and
     * may throw an (unchecked) exception in such circumstances.  The
     * circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     *
     * @param key the key
     */
    void lock(String key);

    /**
     * Acquires the lock unless the current thread is
     * {@linkplain Thread#interrupt interrupted}.
     *
     * <p>Acquires the lock if it is available and returns immediately.
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of two things happens:
     *
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported.
     * </ul>
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring the
     * lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some
     * implementations may not be possible, and if possible may be an
     * expensive operation.  The programmer should be aware that this
     * may be the case. An implementation should document when this is
     * the case.
     *
     * <p>An implementation can favor responding to an interrupt over
     * normal method return.
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would
     * cause deadlock, and may throw an (unchecked) exception in such
     * circumstances.  The circumstances and the exception type must
     * be documented by that {@code Lock} implementation.
     *
     * @param key the key
     * @throws InterruptedException if the current thread is                              interrupted while acquiring the lock (and interruption                              of lock acquisition is supported)
     */
    void lockInterruptibly(String key) throws InterruptedException;

    /**
     * Acquires the lock only if it is free at the time of invocation.
     *
     * <p>Acquires the lock if it is available and returns immediately
     * with the value {@code true}.
     * If the lock is not available then this method will return
     * immediately with the value {@code false}.
     *
     * <p>A typical usage idiom for this method would be:
     * <pre> {@code
     * Lock lock = ...;
     * if (lock.tryLock()) {
     *   try {
     *     // manipulate protected state
     *   } finally {
     *     lock.unlock();
     *   }
     * } else {
     *   // perform alternative actions
     * }}*</pre>
     * <p>
     * This usage ensures that the lock is unlocked if it was acquired, and
     * doesn't try to unlock if the lock was not acquired.
     *
     * @param key the key
     * @return {@code true} if the lock was acquired and {@code false} otherwise
     */
    boolean tryLock(String key);

    /**
     * Acquires the lock if it is free within the given waiting time and the
     * current thread has not been {@linkplain Thread#interrupt interrupted}.
     *
     * <p>If the lock is available this method returns immediately
     * with the value {@code true}.
     * If the lock is not available then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happens:
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported; or
     * <li>The specified waiting time elapses
     * </ul>
     *
     * <p>If the lock is acquired then the value {@code true} is returned.
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring
     * the lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some implementations
     * may not be possible, and if possible may
     * be an expensive operation.
     * The programmer should be aware that this may be the case. An
     * implementation should document when this is the case.
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return, or reporting a timeout.
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would cause
     * deadlock, and may throw an (unchecked) exception in such circumstances.
     * The circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     *
     * @param key  the key
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @return {@code true} if the lock was acquired and {@code false} if the waiting time elapsed before the lock was acquired
     * @throws InterruptedException if the current thread is interrupted                              while acquiring the lock (and interruption of lock                              acquisition is supported)
     */
    boolean tryLock(String key, long time, TimeUnit unit) throws InterruptedException;

    /**
     * Releases the lock.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>A {@code Lock} implementation will usually impose
     * restrictions on which thread can release a lock (typically only the
     * holder of the lock can release it) and may throw
     * an (unchecked) exception if the restriction is violated.
     * Any restrictions and the exception
     * type must be documented by that {@code Lock} implementation.
     *
     * @param key the key
     */
    void unlock(String key);

    /**
     * Acquires the lock with defined <code>leaseTime</code>.
     * Waits if necessary until lock became available.
     * <p>
     * Lock will be released automatically after defined <code>leaseTime</code> interval.
     *
     * @param key       the key
     * @param leaseTime the maximum time to hold the lock after it's acquisition,                  if it hasn't already been released by invoking <code>unlock</code>.                  If leaseTime is -1, hold the lock until explicitly unlocked.
     * @param unit      the time unit
     * @throws InterruptedException - if the thread is interrupted
     */
    void lockInterruptibly(String key, long leaseTime, TimeUnit unit) throws InterruptedException;

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
     * @throws InterruptedException - if the thread is interrupted
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * Acquires the lock with defined <code>leaseTime</code>.
     * Waits if necessary until lock became available.
     * <p>
     * Lock will be released automatically after defined <code>leaseTime</code> interval.
     *
     * @param key       the key
     * @param leaseTime the maximum time to hold the lock after it's acquisition,                  if it hasn't already been released by invoking <code>unlock</code>.                  If leaseTime is -1, hold the lock until explicitly unlocked.
     * @param unit      the time unit
     */
    void lock(String key, long leaseTime, TimeUnit unit);

    /**
     * Unlocks the lock independently of its state
     *
     * @param key the key
     * @return <code>true</code> if lock existed and now unlocked otherwise <code>false</code>
     */
    boolean forceUnlock(String key);

    /**
     * Checks if the lock locked by any thread
     *
     * @param key the key
     * @return <code>true</code> if locked otherwise <code>false</code>
     */
    boolean isLocked(String key);

    /**
     * Checks if the lock is held by thread with defined <code>threadId</code>
     *
     * @param key      the key
     * @param threadId Thread ID of locking thread
     * @return <code>true</code> if held by thread with given id otherwise <code>false</code>
     */
    boolean isHeldByThread(String key, long threadId);

    /**
     * Checks if this lock is held by the current thread
     *
     * @param key the key
     * @return <code>true</code> if held by current thread otherwise <code>false</code>
     */
    boolean isHeldByCurrentThread(String key);

    /**
     * Number of holds on this lock by the current thread
     *
     * @param key the key
     * @return holds or <code>0</code> if this lock is not held by current thread
     */
    int getHoldCount(String key);

    /**
     * Remaining time to live of the lock
     *
     * @param key the key
     * @return time in milliseconds -2 if the lock does not exist. -1 if the lock exists but has no associated expire.
     */
    long remainTimeToLive(String key);


}
