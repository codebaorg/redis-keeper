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

import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * The type R hyper log log.
 */
class RHyperLogLog {
    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R hyper log log.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RHyperLogLog(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Pf add boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    public boolean pfAdd(String key, Collection<Object> elements) {
        return getHyperLogLog(key).addAll(elements);
    }

    /**
     * Pf add async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    public CompletableFuture<Boolean> pfAddAsync(String key, Collection<Object> elements) {
        return pfAddRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Boolean> pfAddRFuture(String key, Collection<Object> elements) {
        return getHyperLogLog(key).addAllAsync(elements);
    }


    /**
     * Pf count long.
     *
     * @param key the key
     * @return the long
     */
    public long pfCount(String key) {
        return getHyperLogLog(key).count();
    }

    /**
     * Pf count async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> pfCountAsync(String key) {
        return pfCountRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> pfCountRFuture(String key) {
        return getHyperLogLog(key).countAsync();
    }


    /**
     * Pf count long.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the long
     */
    public long pfCount(String key, String... otherKeys) {
        return getHyperLogLog(key).countWith(otherKeys);
    }


    /**
     * Pf count async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Long> pfCountAsync(String key, String... otherKeys) {
        return pfCountRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Long> pfCountRFuture(String key, String... otherKeys) {
        return getHyperLogLog(key).countWithAsync(otherKeys);
    }


    /**
     * Pf merge.
     *
     * @param destKey    the dest key
     * @param sourceKeys the source keys
     */
    public void pfMerge(String destKey, String... sourceKeys) {
        getHyperLogLog(destKey).mergeWith(sourceKeys);
    }


    /**
     * Pf merge async completable future.
     *
     * @param destKey    the dest key
     * @param sourceKeys the source keys
     * @return the completable future
     */
    public CompletableFuture<Void> pfMergeAsync(String destKey, String... sourceKeys) {
        return pfMergeRFuture(destKey, sourceKeys).toCompletableFuture();
    }

    private RFuture<Void> pfMergeRFuture(String destKey, String... sourceKeys) {
        return getHyperLogLog(destKey).mergeWithAsync(sourceKeys);
    }

    private <V> org.redisson.api.RHyperLogLog<V> getHyperLogLog(String key) {
        return getDataSource().getHyperLogLog(key, stringCodec);
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
