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

import org.codeba.redis.keeper.core.KBitSet;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;

/**
 * The type K redisson bit set.
 */
class KRedissonBitSet extends KRedissonBitSetAsync implements KBitSet {

    /**
     * The Redisson client.
     */
    private final RedissonClient redissonClient;

    /**
     * Instantiates a new K redisson bit set.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonBitSet(RedissonClient redissonClient) {
        super(redissonClient);
        this.redissonClient = redissonClient;
    }

    @Override
    public long bitCount(String key) {
        return getRBitSet(key).cardinality();
    }

    @Override
    public long bitFieldSetSigned(String key, int size, long offset, long value) {
        return getRBitSet(key).setSigned(size, offset, value);
    }

    @Override
    public long bitFieldSetUnSigned(String key, int size, long offset, long value) {
        return getRBitSet(key).setUnsigned(size, offset, value);
    }

    @Override
    public long bitFieldGetSigned(String key, int size, long offset) {
        return getRBitSet(key).getSigned(size, offset);
    }

    @Override
    public long bitFieldGetUnSigned(String key, int size, long offset) {
        return getRBitSet(key).getUnsigned(size, offset);
    }

    @Override
    public void bitOpOr(String destKey, String... keys) {
        getRBitSet(destKey).or(keys);
    }

    @Override
    public boolean getBit(String key, long bitIndex) {
        return getRBitSet(key).get(bitIndex);
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        return getRBitSet(key).set(offset, value);
    }

    /**
     * Gets r bit set.
     *
     * @param key the key
     * @return the r bit set
     */
    private RBitSet getRBitSet(String key) {
        return redissonClient.getBitSet(key);
    }

}
