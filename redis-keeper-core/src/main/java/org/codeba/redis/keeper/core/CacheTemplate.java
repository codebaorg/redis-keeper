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
import java.util.function.Consumer;

/**
 * The interface Cache template.
 *
 * @author codeba
 */
public interface CacheTemplate extends KBitSet, KGeneric, KGeo, KMap, KHyperLogLog, KList, KSet, KZSet, KString, KBloomFilter, KLock, KRateLimiter, KScript {

    /**
     * Create batch k batch.
     *
     * @return the k batch
     */
    KBatch createBatch();

    /**
     * Pipeline.
     *
     * @param batchConsumer the batch consumer
     */
    void pipeline(Consumer<KBatch> batchConsumer);

    /**
     * Pipeline with responses list.
     *
     * @param batchConsumer the batch consumer
     * @return the list
     */
    List<?> pipelineWithResponses(Consumer<KBatch> batchConsumer);

    /**
     * Pipeline async completable future.
     *
     * @param batchConsumer the batch consumer
     * @return the completable future
     */
    CompletableFuture<Void> pipelineAsync(Consumer<KBatch> batchConsumer);

    /**
     * Pipeline with responses async completable future.
     *
     * @param batchConsumer the batch consumer
     * @return the completable future
     */
    CompletableFuture<List<?>> pipelineWithResponsesAsync(Consumer<KBatch> batchConsumer);

}
