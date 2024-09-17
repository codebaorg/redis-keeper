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

import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

/**
 * The type Base async.
 */
class BaseAsync {
    /**
     * The Redisson client.
     */
    private RedissonClient redissonClient;
    /**
     * The R batch.
     */
    private RBatch batch;
    /**
     * The Codec.
     */
    private Codec codec;

    /**
     * Instantiates a new Base async.
     *
     * @param redissonClient the redisson client
     */
    public BaseAsync(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * Instantiates a new Base async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public BaseAsync(RedissonClient redissonClient, Codec codec) {
        this.redissonClient = redissonClient;
        this.codec = codec;
    }

    /**
     * Instantiates a new Base async.
     *
     * @param batch the r batch
     * @param codec the codec
     */
    public BaseAsync(RBatch batch, Codec codec) {
        this.batch = batch;
        this.codec = codec;
    }

    /**
     * Instantiates a new Base async.
     *
     * @param batch the r batch
     */
    public BaseAsync(RBatch batch) {
        this.batch = batch;
    }

    /**
     * Gets redisson client.
     *
     * @return the redisson client
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    /**
     * Gets batch.
     *
     * @return the batch
     */
    public RBatch getBatch() {
        return batch;
    }

    /**
     * Gets codec.
     *
     * @return the codec
     */
    public Codec getCodec() {
        return codec;
    }
}
