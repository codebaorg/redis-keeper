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

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface KListAsync {
    /**
     * Bl move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param pollLeft    the poll left
     * @return the completable future
     */
    CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft);

    /**
     * Bl pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> blPopAsync(String key);

    /**
     * Bl pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> blPopAsync(String key, int count);

    /**
     * Bl pop async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     * @throws InterruptedException the interrupted exception
     */
    CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit);

    /**
     * Bl pop async completable future.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys);

    /**
     * Br pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> brPopAsync(String key);

    /**
     * Br pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> brPopAsync(String key, int count);

    /**
     * Br pop async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     * @throws InterruptedException the interrupted exception
     */
    CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit);

    /**
     * Br pop async completable future.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys);

    /**
     * Br pop push async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param unit        the unit
     * @return the completable future
     */
    CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit);

    /**
     * L index async completable future.
     *
     * @param key   the key
     * @param index the index
     * @return the completable future
     */
    CompletableFuture<Object> lIndexAsync(String key, int index);

    /**
     * L insert async completable future.
     *
     * @param key     the key
     * @param before  the before
     * @param pivot   the pivot
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element);

    /**
     * L len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> llenAsync(String key);

    /**
     * L move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param pollLeft    the poll left
     * @return the completable future
     */
    CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft);

    /**
     * L pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> lPopAsync(String key, int count);

    /**
     * L push x async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Integer> lPushXAsync(String key, Object... elements);

    /**
     * L range async completable future.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the completable future
     */
    CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex);

    /**
     * L rem async completable future.
     *
     * @param key     the key
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Boolean> lRemAsync(String key, Object element);

    /**
     * L rem all async completable future.
     *
     * @param key     the key
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Boolean> lRemAllAsync(String key, Object element);

    /**
     * L rem async completable future.
     *
     * @param key   the key
     * @param index the index
     * @return the completable future
     */
    CompletableFuture<Object> lRemAsync(String key, int index);

    /**
     * L set async completable future.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Void> lSetAsync(String key, int index, Object element);

    /**
     * L trim async completable future.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the completable future
     */
    CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex);

    /**
     * R pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> rPopAsync(String key, int count);

    /**
     * R pop l push async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @return the completable future
     */
    CompletableFuture<Object> rPopLPushAsync(String source, String destination);

    /**
     * R push async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Boolean> rPushAsync(String key, Object... elements);

    /**
     * R push x async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Integer> rPushXAsync(String key, Object... elements);

}
