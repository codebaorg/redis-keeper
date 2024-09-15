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

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The interface K batch.
 */
public interface KBatch {
    /**
     * Gets bit map.
     *
     * @return the bit map
     */
    KBitSetAsync getBitMap();

    /**
     * Gets generic.
     *
     * @return the generic
     */
    KGenericAsync getGeneric();

    /**
     * Gets geo.
     *
     * @return the geo
     */
    KGeoAsync getGeo();

    /**
     * Gets hash.
     *
     * @return the hash
     */
    KMapAsync getHash();

    /**
     * Gets hyper log log.
     *
     * @return the hyper log log
     */
    KHyperLogLogAsync getHyperLogLog();

    /**
     * Gets list.
     *
     * @return the list
     */
    KListAsync getList();

    /**
     * Gets set.
     *
     * @return the set
     */
    KSetAsync getSet();

    /**
     * Gets sorted set.
     *
     * @return the sorted set
     */
    KZSetAsync getSortedSet();

    /**
     * Gets string.
     *
     * @return the string
     */
    KStringAsync getString();

    /**
     * Gets script.
     *
     * @return the script
     */
    KScriptAsync getScript();

    /**
     * Execute.
     */
    void execute();

    /**
     * Execute async completable future.
     *
     * @return the completable future
     */
    CompletableFuture<Void> executeAsync();

    /**
     * Execute with responses list.
     *
     * @return the list
     */
    List<?> executeWithResponses();

    /**
     * Execute with responses async completable future.
     *
     * @return the completable future
     */
    CompletableFuture<List<?>> executeWithResponsesAsync();

}
