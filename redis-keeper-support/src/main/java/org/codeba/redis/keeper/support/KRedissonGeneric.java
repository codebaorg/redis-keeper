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

import org.codeba.redis.keeper.core.KGeneric;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RType;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * The type K redisson generic.
 */
class KRedissonGeneric extends KRedissonGenericAsync implements KGeneric {
    /**
     * Instantiates a new K redisson generic.
     *
     * @param redissonClient the redisson client
     */
    public KRedissonGeneric(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public long exists(String... keys) {
        return getRKeys().countExists(keys);
    }

    @Override
    public boolean expire(String key, long timeToLive, TimeUnit timeUnit) {
        return getRKeys().expire(key, timeToLive, timeUnit);
    }

    @Override
    public boolean expireAt(String key, long timestamp) {
        return getRKeys().expireAt(key, timestamp * 1000);
    }

    @Override
    public long del(String... keys) {
        return getRKeys().delete(keys);
    }

    @Override
    public long unlink(String... keys) {
        return getRKeys().unlink(keys);
    }

    @Override
    public long ttl(String key) {
        final long remainTimeToLive = getRBucket(key).remainTimeToLive();
        return remainTimeToLive < 0 ? remainTimeToLive : remainTimeToLive / 1000;
    }

    @Override
    public long pTTL(String key) {
        return getRBucket(key).remainTimeToLive();
    }

    @Override
    public Iterable<String> scan(String keyPattern) {
        return getRKeys().getKeysByPattern(keyPattern);
    }

    @Override
    public Iterable<String> scan(String keyPattern, int count) {
        return getRKeys().getKeysByPattern(keyPattern, count);
    }

    @Override
    public KeyType type(String key) {
        final RType type = getRKeys().getType(key);

        if (type == RType.OBJECT) {
            return KeyType.STRING;
        } else if (type == RType.MAP) {
            return KeyType.HASH;
        } else {
            return KeyType.valueOf(type.name());
        }
    }

    /**
     * Gets r keys.
     *
     * @return the r keys
     */
    private RKeys getRKeys() {
        return getRedissonClient().getKeys();
    }

    /**
     * Gets r bucket.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the r bucket
     */
    private <V> RBucket<V> getRBucket(String key) {
        return getRedissonClient().getBucket(key);
    }

}
