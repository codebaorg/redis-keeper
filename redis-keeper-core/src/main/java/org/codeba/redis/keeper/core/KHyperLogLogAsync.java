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
import java.util.concurrent.CompletableFuture;

public interface KHyperLogLogAsync {
    /**
     * Pf add async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Boolean> pfAddAsync(String key, Collection<Object> elements);

    /**
     * Pf count async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> pfCountAsync(String key);

    /**
     * Pf count async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Long> pfCountAsync(String key, String... otherKeys);

    /**
     * Pf merge async completable future.
     *
     * @param destKey    the dest key
     * @param sourceKeys the source keys
     * @return the completable future
     */
    CompletableFuture<Void> pfMergeAsync(String destKey, String... sourceKeys);

}
