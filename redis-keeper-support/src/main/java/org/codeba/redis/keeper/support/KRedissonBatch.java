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

import org.codeba.redis.keeper.core.KBatch;
import org.codeba.redis.keeper.core.KBitSetAsync;
import org.codeba.redis.keeper.core.KGenericAsync;
import org.codeba.redis.keeper.core.KGeoAsync;
import org.codeba.redis.keeper.core.KHyperLogLogAsync;
import org.codeba.redis.keeper.core.KMapAsync;
import org.redisson.api.RBatch;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson batch.
 */
class KRedissonBatch implements KBatch {

    /**
     * The Codec.
     */
    private final Codec codec = new StringCodec();
    /**
     * The R batch.
     */
    private final RBatch rBatch;

    /**
     * Instantiates a new K redisson batch.
     *
     * @param rBatch the r batch
     */
    public KRedissonBatch(RBatch rBatch) {
        this.rBatch = rBatch;
    }

    @Override
    public KBitSetAsync getBitMap() {
        return new KRedissonBitSetAsync(rBatch);
    }

    @Override
    public KGenericAsync getGeneric() {
        return new KRedissonGenericAsync(rBatch);
    }

    @Override
    public KGeoAsync getGeo() {
        return new KRedissonGeoAsync(rBatch, codec);
    }

    @Override
    public KMapAsync getHash() {
        return new KRedissonMapAsync(rBatch, codec);
    }

    @Override
    public KHyperLogLogAsync getHyperLogLog() {
        return new KRedissonHyperLogLogAsync(rBatch, codec);
    }


    @Override
    public void execute() {
        rBatch.execute();
    }

    @Override
    public CompletableFuture<Void> executeAsync() {
        return rBatch.executeAsync()
                .toCompletableFuture()
                .thenRun(() -> {
                });
    }

    @Override
    public List<?> executeWithResponses() {
        return rBatch.execute().getResponses();
    }

    @Override
    public CompletableFuture<List<?>> executeWithResponsesAsync() {
        CompletableFuture<List<?>> future = new CompletableFuture<>();
        rBatch.executeAsync()
                .whenComplete((batchResult, throwable) -> {
                    if (null != throwable) {
                        future.completeExceptionally(throwable);
                    }
                    future.complete(batchResult.getResponses());
                });
        return future;
    }


}
