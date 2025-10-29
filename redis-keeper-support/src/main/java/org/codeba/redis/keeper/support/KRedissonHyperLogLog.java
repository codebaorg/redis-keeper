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

import org.codeba.redis.keeper.core.KHyperLogLog;
import org.redisson.api.RedissonClient;

import java.util.Collection;

/**
 * The type K redisson hyper log log.
 */
class KRedissonHyperLogLog extends KRedissonHyperLogLogAsync implements KHyperLogLog {

    /**
     * The Redisson client.
     */
    private final RedissonClient redissonClient;

    /**
     * Instantiates a new K redisson hyper log log.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonHyperLogLog(RedissonClient redissonClient) {
        super(redissonClient);
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean pfAdd(String key, Collection<Object> elements) {
        return getHyperLogLog(key).addAll(elements);
    }

    @Override
    public long pfCount(String key) {
        return getHyperLogLog(key).count();
    }

    @Override
    public long pfCount(String key, String... otherKeys) {
        return getHyperLogLog(key).countWith(otherKeys);
    }

    @Override
    public void pfMerge(String destKey, String... sourceKeys) {
        getHyperLogLog(destKey).mergeWith(sourceKeys);
    }

    /**
     * Gets hyper log log.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the hyper log log
     */
    private <V> org.redisson.api.RHyperLogLog<V> getHyperLogLog(String key) {
        return redissonClient.getHyperLogLog(key);
    }

}
