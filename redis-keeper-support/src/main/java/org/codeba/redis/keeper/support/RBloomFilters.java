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

import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.util.concurrent.CompletableFuture;

/**
 * The type R bloom filter.
 */
class RBloomFilters {

    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R bloom filter.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RBloomFilters(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Bf add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    public boolean bfAdd(String key, Object item) {
        return getRBloomFilter(key).add(item);
    }


    /**
     * Bf card long.
     *
     * @param key the key
     * @return the long
     */
    public long bfCard(String key) {
        return getRBloomFilter(key).count();
    }


    /**
     * Bf exists boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    public boolean bfExists(String key, Object item) {
        return getRBloomFilter(key).contains(item);
    }


    /**
     * Bfm add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    public boolean bfmAdd(String key, Object item) {
        return getRBloomFilter(key).add(item);
    }


    /**
     * Bf reserve boolean.
     *
     * @param key                the key
     * @param expectedInsertions the expected insertions
     * @param falseProbability   the false probability
     * @return the boolean
     */
    public boolean bfReserve(String key, long expectedInsertions, double falseProbability) {
        return getRBloomFilter(key).tryInit(expectedInsertions, falseProbability);
    }

    /**
     * Delete bf boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean deleteBf(String key) {
        return getRBloomFilter(key).delete();
    }

    /**
     * Delete bf async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Boolean> deleteBfAsync(String key) {
        return getRBloomFilter(key).deleteAsync().toCompletableFuture();
    }


    private <V> org.redisson.api.RBloomFilter<V> getRBloomFilter(String key) {
        return getDataSource().getBloomFilter(key, stringCodec);
    }

    /**
     * Gets data source.
     *
     * @return the data source
     */
    RedissonClient getDataSource() {
        return this.redissonClient;
    }


}
