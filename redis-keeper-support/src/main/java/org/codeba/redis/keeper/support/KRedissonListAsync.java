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

import org.codeba.redis.keeper.core.KListAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RBlockingDequeAsync;
import org.redisson.api.RDequeAsync;
import org.redisson.api.RFuture;
import org.redisson.api.RListAsync;
import org.redisson.api.RedissonClient;
import org.redisson.api.queue.DequeMoveArgs;
import org.redisson.client.codec.Codec;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson list async.
 */
class KRedissonListAsync extends BaseAsync implements KListAsync {
    /**
     * Instantiates a new K redisson list async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonListAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson list async.
     *
     * @param rBatch the r batch
     * @param codec  the codec
     */
    public KRedissonListAsync(RBatch rBatch, Codec codec) {
        super(rBatch, codec);
    }

    @Override
    public CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft) {
        if (pollLeft) {
            return getBlockingDeque(source)
                    .moveAsync(timeout, DequeMoveArgs.pollFirst().addLastTo(destination))
                    .toCompletableFuture();
        } else {
            return getBlockingDeque(source)
                    .moveAsync(timeout, DequeMoveArgs.pollLast().addFirstTo(destination))
                    .toCompletableFuture();
        }
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key) {
        return getBlockingDeque(key).pollAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> blPopAsync(String key, int count) {
        return getBlockingDeque(key).pollAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit) {
        return getBlockingDeque(key).pollAsync(timeout, unit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return getBlockingDeque(key).pollFromAnyAsync(timeout, unit, otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key) {
        return getBlockingDeque(key).pollLastAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> brPopAsync(String key, int count) {
        return getBlockingDeque(key).pollLastAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit) {
        return getBlockingDeque(key).pollLastAsync(timeout, unit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return getBlockingDeque(key).pollLastFromAnyAsync(timeout, unit, otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit) {
        return getBlockingDeque(source).pollLastAndOfferFirstToAsync(destination, timeout, unit).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> lIndexAsync(String key, int index) {
        return getList(key).getAsync(index).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element) {
        RFuture<Integer> added;
        if (before) {
            added = getList(key).addBeforeAsync(pivot, element);
        } else {
            added = getList(key).addAfterAsync(pivot, element);
        }
        return added.toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> llenAsync(String key) {
        return getList(key).sizeAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft) {
        RFuture<Object> move;
        if (pollLeft) {
            move = getBlockingDeque(source).moveAsync(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).moveAsync(DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return move.toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> lPopAsync(String key, int count) {
        return getDeque(key).pollAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> lPushXAsync(String key, Object... elements) {
        return getDeque(key).addFirstIfExistsAsync(elements).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex) {
        return getList(key).rangeAsync(fromIndex, toIndex).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> lRemAsync(String key, Object element) {
        return getList(key).removeAsync(element).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> lRemAllAsync(String key, Object element) {
        if (element instanceof Collection) {
            return getList(key).removeAllAsync((Collection<?>) element).toCompletableFuture();
        }
        return getList(key).removeAllAsync(Collections.singletonList(element)).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> lRemAsync(String key, int index) {
        return getList(key).removeAsync(index).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> lSetAsync(String key, int index, Object element) {
        return getList(key).fastSetAsync(index, element).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex) {
        return getList(key).trimAsync(fromIndex, toIndex).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> rPopAsync(String key, int count) {
        return getDeque(key).pollLastAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> rPopLPushAsync(String source, String destination) {
        return getDeque(source).pollLastAndOfferFirstToAsync(destination).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> rPushAsync(String key, Object... elements) {
        return getList(key).addAllAsync(Arrays.asList(elements)).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> rPushXAsync(String key, Object... elements) {
        return getDeque(key).addLastIfExistsAsync(elements).toCompletableFuture();
    }

    /**
     * Gets list async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the list async
     */
    private <V> RListAsync<V> getList(String key) {
        if (null != getBatch()) {
            return getBatch().getList(key, getCodec());
        } else {
            return getRedissonClient().getList(key, getCodec());
        }
    }

    /**
     * Gets blocking deque async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the blocking deque async
     */
    private <V> RBlockingDequeAsync<V> getBlockingDeque(String key) {
        if (null != getBatch()) {
            return getBatch().getBlockingDeque(key, getCodec());
        } else {
            return getRedissonClient().getBlockingDeque(key, getCodec());
        }
    }

    /**
     * Gets deque async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the deque async
     */
    private <V> RDequeAsync<V> getDeque(String key) {
        if (null != getBatch()) {
            return getBatch().getDeque(key, getCodec());
        } else {
            return getRedissonClient().getDeque(key, getCodec());
        }
    }


}
