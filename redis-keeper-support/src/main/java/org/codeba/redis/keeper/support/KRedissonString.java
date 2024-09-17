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

import org.codeba.redis.keeper.core.KString;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson string.
 */
class KRedissonString extends KRedissonStringAsync implements KString {
    /**
     * Instantiates a new K redisson string.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonString(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    @Override
    public void append(String key, Object value) {
        try (final OutputStream outputStream = getRBinaryStream(key).getOutputStream()) {
            outputStream.write(value.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long decr(String key) {
        return getRAtomicLong(key).decrementAndGet();
    }

    @Override
    public long decrBy(String key, long decrement) {
        return getRAtomicLong(key).addAndGet(-decrement);
    }

    @Override
    public Optional<Object> get(String key) {
        return getObject(key);
    }

    @Override
    public Optional<Object> getObject(String key) {
        Object object;
        try {
            object = getRBucket(key).get();
        } catch (Exception e) {
            object = getRBucket(key, getCodec()).get();
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> getDel(String key) {
        return Optional.ofNullable(getRBucket(key, getCodec()).getAndDelete());
    }

    @Override
    public long getLong(String key) {
        return getRAtomicLong(key).get();
    }

    @Override
    public long incr(String key) {
        return getRAtomicLong(key).incrementAndGet();
    }

    @Override
    public long incrBy(String key, long increment) {
        return getRAtomicLong(key).addAndGet(increment);
    }

    @Override
    public double getDouble(String key) {
        return getRAtomicDouble(key).get();
    }

    @Override
    public double incrByFloat(String key, double increment) {
        return getRAtomicDouble(key).addAndGet(increment);
    }

    @Override
    public boolean compareAndSet(String key, long expect, long update) {
        return getRAtomicLong(key).compareAndSet(expect, update);
    }

    @Override
    public boolean compareAndSet(String key, double expect, double update) {
        return getRAtomicDouble(key).compareAndSet(expect, update);
    }

    @Override
    public void setObject(String key, Object value) {
        if (value instanceof String) {
            set(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            set(key, Long.parseLong(value.toString()));
        } else if (value instanceof Float || value instanceof Double) {
            set(key, Double.parseDouble(value.toString()));
        } else {
            getRBucket(key).set(value);
        }
    }

    @Override
    public void setObjectEx(String key, Object value, Duration duration) {
        if (value instanceof String) {
            setEX(key, value.toString(), duration);
        } else if (value instanceof Integer || value instanceof Long) {
            final RAtomicLong rAtomicLong = getRAtomicLong(key);
            rAtomicLong.set(Long.parseLong(value.toString()));
            rAtomicLong.expire(duration.toMillis(), TimeUnit.MILLISECONDS);
        } else if (value instanceof Float || value instanceof Double) {
            final RAtomicDouble rAtomicDouble = getRAtomicDouble(key);
            rAtomicDouble.set(Double.parseDouble(value.toString()));
            rAtomicDouble.expire(duration.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            getRBucket(key).set(value, duration.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> mGetAsync(String... keys) {
        return getRBuckets(getCodec()).getAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> mSetAsync(Map<String, String> kvMap) {
        return getRBuckets(getCodec()).setAsync(kvMap).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap) {
        return getRBuckets(getCodec()).trySetAsync(kvMap).toCompletableFuture();
    }

    @Override
    public Map<String, Object> mGet(String... keys) {
        if (null == keys || keys.length == 0) {
            return Collections.emptyMap();
        }
        return getRBuckets(getCodec()).get(keys);
    }

    @Override
    public void mSet(Map<String, String> kvMap) {
        getRBuckets(getCodec()).set(kvMap);
    }

    @Override
    public boolean mSetNX(Map<String, String> kvMap) {
        return getRBuckets(getCodec()).trySet(kvMap);
    }

    @Override
    public void set(String key, String value) {
        getRBucket(key, getCodec()).set(value);
    }

    @Override
    public void set(String key, Long value) {
        getRAtomicLong(key).set(value);
    }

    @Override
    public void set(String key, Double value) {
        getRAtomicDouble(key).set(value);
    }

    @Override
    public boolean compareAndSet(String key, String expect, String update) {
        return getRBucket(key, getCodec()).compareAndSet(expect, update);
    }

    @Override
    public void setEX(String key, String value, Duration duration) {
        getRBucket(key, getCodec()).set(value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long strLen(String key) {
        return getRBucket(key, getCodec()).size();
    }

    /**
     * Gets r bucket.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the r bucket
     */
    private <T> RBucket<T> getRBucket(String key) {
        return getRedissonClient().getBucket(key);
    }

    /**
     * Gets r bucket.
     *
     * @param <T>   the type parameter
     * @param key   the key
     * @param codec the codec
     * @return the r bucket
     */
    private <T> RBucket<T> getRBucket(String key, Codec codec) {
        return getRedissonClient().getBucket(key, codec);
    }

    /**
     * Gets r buckets.
     *
     * @param codec the codec
     * @return the r buckets
     */
    private RBuckets getRBuckets(Codec codec) {
        return getRedissonClient().getBuckets(codec);
    }

    /**
     * Gets r atomic long.
     *
     * @param key the key
     * @return the r atomic long
     */
    private RAtomicLong getRAtomicLong(String key) {
        return getRedissonClient().getAtomicLong(key);
    }

    /**
     * Gets r atomic double.
     *
     * @param key the key
     * @return the r atomic double
     */
    private RAtomicDouble getRAtomicDouble(String key) {
        return getRedissonClient().getAtomicDouble(key);
    }

    /**
     * Gets r binary stream.
     *
     * @param key the key
     * @return the r binary stream
     */
    private RBinaryStream getRBinaryStream(String key) {
        return getRedissonClient().getBinaryStream(key);
    }

}
