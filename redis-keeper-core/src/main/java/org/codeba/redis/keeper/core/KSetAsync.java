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

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface KSetAsync {
    /**
     * S add async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Boolean> sAddAsync(String key, Object member);

    /**
     * S add async.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members);

    /**
     * S card async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> sCardAsync(String key);

    /**
     * S diff async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys);

    /**
     * S diff store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys);

    /**
     * S inter async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys);

    /**
     * S inter store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys);

    /**
     * S is member async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Boolean> sIsMemberAsync(String key, Object member);

    /**
     * S members async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sMembersAsync(String key);

    /**
     * S move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param member      the member
     * @return the completable future
     */
    CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member);

    /**
     * S pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> sPopAsync(String key);

    /**
     * S pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sPopAsync(String key, int count);

    /**
     * S rand member async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> sRandMemberAsync(String key);

    /**
     * S rand member async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count);

    /**
     * S rem async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members);

    /**
     * S union async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys);

    /**
     * S union store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys);

}
