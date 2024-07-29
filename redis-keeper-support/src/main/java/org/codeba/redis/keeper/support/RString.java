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

import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

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
 * The type R string.
 */
class RString {

    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R string.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RString(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     */
    public void append(String key, Object value) {
        try (final OutputStream outputStream = getRBinaryStream(key).getOutputStream()) {
            outputStream.write(value.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decr long.
     *
     * @param key the key
     * @return the long
     */
    public long decr(String key) {
        return getRAtomicLong(key).decrementAndGet();
    }

    /**
     * Decr async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> decrAsync(String key) {
        return decrRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> decrRFuture(String key) {
        return getRAtomicLong(key).decrementAndGetAsync();
    }

    /**
     * Decr by long.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the long
     */
    public long decrBy(String key, long decrement) {
        return getRAtomicLong(key).addAndGet(-decrement);
    }

    /**
     * Decr by async completable future.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the completable future
     */
    public CompletableFuture<Long> decrByAsync(String key, long decrement) {
        return decrByRFuture(key, decrement).toCompletableFuture();
    }

    private RFuture<Long> decrByRFuture(String key, long decrement) {
        return getRAtomicLong(key).addAndGetAsync(-decrement);
    }

    /**
     * Get optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> get(String key) {
        return getObject(key);
    }

    /**
     * Gets object.
     *
     * @param key the key
     * @return the object
     */
    public Optional<Object> getObject(String key) {
        Object object;
        try {
            object = getRBucket(key).get();
        } catch (Exception e) {
            object = getRBucket(key, stringCodec).get();
        }
        return Optional.ofNullable(object);
    }

    /**
     * Gets async.
     *
     * @param key the key
     * @return the async
     */
    public CompletableFuture<Object> getAsync(String key) {
        return getRBucket(key, stringCodec).getAsync()
                .handle((object, throwable) -> {
                    if (null != throwable) {
                        return getRBucket(key).getAsync().join();
                    }
                    return object;
                }).toCompletableFuture();
    }

    /**
     * Gets object async.
     *
     * @param key the key
     * @return the object async
     */
    public CompletableFuture<Object> getObjectAsync(String key) {
        return getRBucket(key).getAsync()
                .handle((object, throwable) -> {
                    if (null != throwable) {
                        return getRBucket(key, stringCodec).getAsync().join();
                    }
                    return object;
                }).toCompletableFuture();
    }

    /**
     * Gets del.
     *
     * @param key the key
     * @return the del
     */
    public Optional<Object> getDel(String key) {
        return Optional.ofNullable(getRBucket(key, stringCodec).getAndDelete());
    }

    /**
     * Gets del async.
     *
     * @param key the key
     * @return the del async
     */
    public CompletableFuture<Object> getDelAsync(String key) {
        return getDelRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> getDelRFuture(String key) {
        return getRBucket(key, stringCodec).getAndDeleteAsync();
    }

//
//    public Optional<Object> getEx(String key, Duration duration) {
//        log("getex", key, duration);
//        return Optional.of(getRBucket(key).getAndExpire(duration));
//    }

//
//    public Optional<Object> getEx(String key, Instant time) {
//        log("getex", key, time);
//        return Optional.of(getRBucket(key).getAndExpire(time));
//    }

//
//    public Optional<Object> getrange(String key, int start, int end) {
//        if (log(READS, "getrange", key, start, end)) {
//            reportStatus();
//        }
//
//        try (SeekableByteChannel channel = getRBinaryStream(key).getChannel()) {
//            final ByteBuffer byteBuffer = ByteBuffer.allocate(end - start + 1);
////            channel.read(byteBuffer);
////            return Optional.of(new String(byteBuffer.array(), StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     */
    public long getLong(String key) {
        return getRAtomicLong(key).get();
    }

    /**
     * Gets long async.
     *
     * @param key the key
     * @return the long async
     */
    public CompletableFuture<Long> getLongAsync(String key) {
        return getLongRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> getLongRFuture(String key) {
        return getRAtomicLong(key).getAsync();
    }

    /**
     * Incr long.
     *
     * @param key the key
     * @return the long
     */
    public long incr(String key) {
        return getRAtomicLong(key).incrementAndGet();
    }

    /**
     * Incr async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> incrAsync(String key) {
        return incrRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> incrRFuture(String key) {
        return getRAtomicLong(key).incrementAndGetAsync();
    }

    /**
     * Incr by long.
     *
     * @param key       the key
     * @param increment the increment
     * @return the long
     */
    public long incrBy(String key, long increment) {
        return getRAtomicLong(key).addAndGet(increment);
    }

    /**
     * Incr by async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @return the completable future
     */
    public CompletableFuture<Long> incrByAsync(String key, long increment) {
        return incrByRFuture(key, increment).toCompletableFuture();
    }

    private RFuture<Long> incrByRFuture(String key, long increment) {
        return getRAtomicLong(key).addAndGetAsync(increment);
    }

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     */
    public double getDouble(String key) {
        return getRAtomicDouble(key).get();
    }

    /**
     * Gets double async.
     *
     * @param key the key
     * @return the double async
     */
    public CompletableFuture<Double> getDoubleAsync(String key) {
        return getDoubleRFuture(key).toCompletableFuture();
    }

    private RFuture<Double> getDoubleRFuture(String key) {
        return getRAtomicDouble(key).getAsync();
    }

    /**
     * Incr by float double.
     *
     * @param key       the key
     * @param increment the increment
     * @return the double
     */
    public double incrByFloat(String key, double increment) {
        return getRAtomicDouble(key).addAndGet(increment);
    }

    /**
     * Incr by float async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @return the completable future
     */
    public CompletableFuture<Double> incrByFloatAsync(String key, double increment) {
        return incrByFloatRFuture(key, increment).toCompletableFuture();
    }

    private RFuture<Double> incrByFloatRFuture(String key, double increment) {
        return getRAtomicDouble(key).addAndGetAsync(increment);
    }

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    public boolean compareAndSet(String key, long expect, long update) {
        return getRAtomicLong(key).compareAndSet(expect, update);
    }

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    public CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update) {
        return compareAndSetRFuture(key, expect, update).toCompletableFuture();
    }

    private RFuture<Boolean> compareAndSetRFuture(String key, long expect, long update) {
        return getRAtomicLong(key).compareAndSetAsync(expect, update);
    }

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    public boolean compareAndSet(String key, double expect, double update) {
        return getRAtomicDouble(key).compareAndSet(expect, update);
    }

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    public CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update) {
        return compareAndSetRFuture(key, expect, update).toCompletableFuture();
    }

    private RFuture<Boolean> compareAndSetRFuture(String key, double expect, double update) {
        return getRAtomicDouble(key).compareAndSetAsync(expect, update);
    }

    /**
     * Sets object.
     *
     * @param key   the key
     * @param value the value
     */
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

    /**
     * Sets object async.
     *
     * @param key   the key
     * @param value the value
     * @return the object async
     */
    public CompletableFuture<Void> setObjectAsync(String key, Object value) {

        if (value instanceof String) {
            return setAsync(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            return setAsync(key, Long.parseLong(value.toString()));
        } else if (value instanceof Float || value instanceof Double) {
            return setAsync(key, Double.parseDouble(value.toString()));
        } else {
            return getRBucket(key).setAsync(value).toCompletableFuture();
        }
    }

    /**
     * M get map.
     *
     * @param keys the keys
     * @return the map
     */
    public Map<String, Object> mGet(String... keys) {
        if (null == keys || keys.length == 0) {
            return Collections.emptyMap();
        }
        return getRBuckets(stringCodec).get(keys);
    }

    /**
     * M get async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    public CompletableFuture<Map<String, Object>> mGetAsync(String... keys) {
        return mGetRFuture(keys).toCompletableFuture();
    }

    private RFuture<Map<String, Object>> mGetRFuture(String... keys) {
        return getRBuckets(stringCodec).getAsync(keys);
    }

    /**
     * M set.
     *
     * @param kvMap the kv map
     */
    public void mSet(Map<String, String> kvMap) {
        getRBuckets(stringCodec).set(kvMap);
    }

    /**
     * M set async completable future.
     *
     * @param kvMap the kv map
     * @return the completable future
     */
    public CompletableFuture<Void> mSetAsync(Map<String, String> kvMap) {
        return mSetRFuture(kvMap).toCompletableFuture();
    }

    private RFuture<Void> mSetRFuture(Map<String, String> kvMap) {
        return getRBuckets(stringCodec).setAsync(kvMap);
    }

    /**
     * M set nx boolean.
     *
     * @param kvMap the kv map
     * @return the boolean
     */
    public boolean mSetNX(Map<String, String> kvMap) {
        return getRBuckets(stringCodec).trySet(kvMap);
    }

    /**
     * M set nx async completable future.
     *
     * @param kvMap the kv map
     * @return the completable future
     */
    public CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap) {
        return mSetNXRFuture(kvMap).toCompletableFuture();
    }

    private RFuture<Boolean> mSetNXRFuture(Map<String, String> kvMap) {
        return getRBuckets(stringCodec).trySetAsync(kvMap);
    }

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, String value) {
        getRBucket(key, stringCodec).set(value);
    }

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    public CompletableFuture<Void> setAsync(String key, String value) {
        return setRFuture(key, value).toCompletableFuture();
    }

    private RFuture<Void> setRFuture(String key, String value) {
        return getRBucket(key, stringCodec).setAsync(value);
    }

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, Long value) {
        getRAtomicLong(key).set(value);
    }

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    public CompletableFuture<Void> setAsync(String key, Long value) {
        return setRFuture(key, value).toCompletableFuture();
    }

    private RFuture<Void> setRFuture(String key, Long value) {
        return getRAtomicLong(key).setAsync(value);
    }

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(String key, Double value) {
        getRAtomicDouble(key).set(value);
    }

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    public CompletableFuture<Void> setAsync(String key, Double value) {
        return setRFuture(key, value).toCompletableFuture();
    }

    private RFuture<Void> setRFuture(String key, Double value) {
        return getRAtomicDouble(key).setAsync(value);
    }

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    public boolean compareAndSet(String key, String expect, String update) {
        return getRBucket(key, stringCodec).compareAndSet(expect, update);
    }

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    public CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update) {
        return compareAndSetRFuture(key, expect, update).toCompletableFuture();
    }

    private RFuture<Boolean> compareAndSetRFuture(String key, String expect, String update) {
        return getRBucket(key, stringCodec).compareAndSetAsync(expect, update);
    }

    /**
     * Sets ex.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     */
    public void setEX(String key, String value, Duration duration) {
        getRBucket(key, stringCodec).set(value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Sets ex async.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     * @return the ex async
     */
    public CompletableFuture<Void> setEXAsync(String key, String value, Duration duration) {
        return setEXRFuture(key, value, duration).toCompletableFuture();
    }

    private RFuture<Void> setEXRFuture(String key, String value, Duration duration) {
        return getRBucket(key, stringCodec).setAsync(value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

//
//    public boolean setNX(String key, String value) {
//        log("setNX", key, value);
//        return getRBucket(key).setIfAbsent(value);
//    }

//
//    public boolean setNxEx(String key, String value, Duration duration) {
//        log("setNxEx", key, value, duration);
//        return getRBucket(key).setIfAbsent(value, duration);
//    }

//
//    public Optional<Object> setrange(String key, int offset, Object value) {
//        if (log(WRITES, "setrange", key, offset, value)) {
//            return Optional.empty();
//        }
//
//        try (SeekableByteChannel channel = getRBinaryStream(key).getChannel()) {
//            channel.write();
//            return Optional.of();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Str len long.
     *
     * @param key the key
     * @return the long
     */
    public long strLen(String key) {
        return getRBucket(key, stringCodec).size();
    }

    /**
     * Str len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Long> strLenAsync(String key) {
        return strLenRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> strLenRFuture(String key) {
        return getRBucket(key, stringCodec).sizeAsync();
    }

    private <T> RBucket<T> getRBucket(String key) {
        return getDataSource().getBucket(key);
    }

    private <T> RBucket<T> getRBucket(String key, Codec codec) {
        return getDataSource().getBucket(key, codec);
    }

    private RBuckets getRBuckets(Codec codec) {
        return getDataSource().getBuckets(codec);
    }

    private RAtomicLong getRAtomicLong(String key) {
        return getDataSource().getAtomicLong(key);
    }

    private RAtomicDouble getRAtomicDouble(String key) {
        return getDataSource().getAtomicDouble(key);
    }

    private RBinaryStream getRBinaryStream(String key) {
        return getDataSource().getBinaryStream(key);
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
