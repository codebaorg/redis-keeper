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

import org.codeba.redis.keeper.core.KBloomFilter;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson bloom filter.
 */
class KRedissonBloomFilter extends BaseAsync implements KBloomFilter {
    /**
     * Instantiates a new K redisson bloom filter.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonBloomFilter(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public boolean bfAdd(String key, Object item) {
        return getRBloomFilter(key).add(item);
    }

    @Override
    public long bfCard(String key) {
        return getRBloomFilter(key).count();
    }

    @Override
    public boolean bfExists(String key, Object item) {
        return getRBloomFilter(key).contains(item);
    }

    @Override
    public boolean bfmAdd(String key, Object item) {
        return getRBloomFilter(key).add(item);
    }

    @Override
    public boolean bfReserve(String key, long expectedInsertions, double falseProbability) {
        return getRBloomFilter(key).tryInit(expectedInsertions, falseProbability);
    }

    @Override
    public boolean deleteBf(String key) {
        return getRBloomFilter(key).delete();
    }

    @Override
    public CompletableFuture<Boolean> deleteBfAsync(String key) {
        return getRBloomFilter(key).deleteAsync().toCompletableFuture();
    }

    /**
     * Gets r bloom filter.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the r bloom filter
     */
    private <V> org.redisson.api.RBloomFilter<V> getRBloomFilter(String key) {
        return getRedissonClient().getBloomFilter(key, getCodec());
    }

}
