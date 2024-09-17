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

import org.codeba.redis.keeper.core.KSetAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RSetAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson set async.
 */
class KRedissonSetAsync extends BaseAsync implements KSetAsync {
    /**
     * Instantiates a new K redisson set async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonSetAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson set async.
     *
     * @param batch the batch
     * @param codec the codec
     */
    public KRedissonSetAsync(RBatch batch, Codec codec) {
        super(batch, codec);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Object member) {
        return getSet(key).addAsync(member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members) {
        return getSet(key).addAllAsync(members).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> sCardAsync(String key) {
        return getSet(key).sizeAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys) {
        return getSet(key).readDiffAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys) {
        return getSet(destination).diffAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys) {
        return getSet(key).readIntersectionAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys) {
        return getSet(destination).intersectionAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> sIsMemberAsync(String key, Object member) {
        return getSet(key).containsAsync(member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> sMembersAsync(String key) {
        return getSet(key).readAllAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member) {
        return getSet(source).moveAsync(destination, member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> sPopAsync(String key) {
        return getSet(key).removeRandomAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> sPopAsync(String key, int count) {
        return getSet(key).removeRandomAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> sRandMemberAsync(String key) {
        return getSet(key).randomAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count) {
        return getSet(key).randomAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members) {
        return getSet(key).removeAllAsync(members).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys) {
        return getSet(key).readUnionAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys) {
        return getSet(destination).unionAsync(keys).toCompletableFuture();
    }

    /**
     * Gets set async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the set async
     */
    private <V> RSetAsync<V> getSet(String key) {
        if (null != getBatch()) {
            return getBatch().getSet(key, getCodec());
        } else {
            return getRedissonClient().getSet(key, getCodec());
        }
    }

}
