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

import org.codeba.redis.keeper.core.KList;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDeque;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.api.queue.DequeMoveArgs;
import org.redisson.client.codec.Codec;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson list.
 */
class KRedissonList extends KRedissonListAsync implements KList {
    /**
     * Instantiates a new K redisson list.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonList(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    @Override
    public Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft) {
        Object object;
        if (pollLeft) {
            object = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            object = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> blPop(String key) {
        return Optional.ofNullable(getBlockingDeque(key).poll());
    }

    @Override
    public List<Object> blPop(String key, int count) {
        return getBlockingDeque(key).poll(count);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        final Object polled = getBlockingDeque(key).poll(timeout, unit);
        return Optional.ofNullable(polled);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        final Object object = getBlockingDeque(key).pollFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> brPop(String key) {
        final Object polled = getBlockingDeque(key).pollLast();
        return Optional.ofNullable(polled);
    }

    @Override
    public List<Object> brPop(String key, int count) {
        return getBlockingDeque(key).pollLast(count);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        final Object polled = getBlockingDeque(key).pollLast(timeout, unit);
        return Optional.ofNullable(polled);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        final Object polled = getBlockingDeque(key).pollLastFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(polled);
    }

    @Override
    public Optional<Object> brPopLPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException {
        final Object object = getBlockingDeque(source).pollLastAndOfferFirstTo(destination, timeout, unit);
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> lIndex(String key, int index) {
        return Optional.ofNullable(getList(key).get(index));
    }

    @Override
    public int lInsert(String key, boolean before, Object pivot, Object element) {
        int added;
        if (before) {
            added = getList(key).addBefore(pivot, element);
        } else {
            added = getList(key).addAfter(pivot, element);
        }
        return added;
    }

    @Override
    public int llen(String key) {
        return getList(key).size();
    }

    @Override
    public Optional<Object> lMove(String source, String destination, boolean pollLeft) {
        Object move;
        if (pollLeft) {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return Optional.ofNullable(move);
    }

    @Override
    public List<Object> lPop(String key, int count) {
        return getDeque(key).poll(count);
    }

    @Override
    public int lPush(String key, Object... elements) {
        Arrays.stream(elements)
                .forEach(element -> getDeque(key).addFirst(element));
        return elements.length;
    }

    @Override
    public int lPushX(String key, Object... elements) {
        return getDeque(key).addFirstIfExists(elements);
    }

    @Override
    public List<Object> lRange(String key, int fromIndex, int toIndex) {
        return getList(key).range(fromIndex, toIndex);
    }

    @Override
    public boolean lRem(String key, Object element) {
        return getList(key).remove(element);
    }

    @Override
    public void lRemAll(String key, Object element) {
        if (element instanceof Collection) {
            getList(key).removeAll((Collection<?>) element);
            return;
        }

        getList(key).removeAll(Collections.singletonList(element));
    }

    @Override
    public Optional<Object> lRem(String key, int index) {
        return Optional.ofNullable(getList(key).remove(index));
    }

    @Override
    public void lSet(String key, int index, Object element) {
        getList(key).fastSet(index, element);
    }

    @Override
    public void lTrim(String key, int fromIndex, int toIndex) {
        getList(key).trim(fromIndex, toIndex);
    }

    @Override
    public List<Object> rPop(String key, int count) {
        return getDeque(key).pollLast(count);
    }

    @Override
    public Optional<Object> rPopLPush(String source, String destination) {
        return Optional.ofNullable(getDeque(source).pollLastAndOfferFirstTo(destination));
    }

    @Override
    public boolean rPush(String key, Object... elements) {
        return getList(key).addAll(Arrays.asList(elements));
    }

    @Override
    public int rPushX(String key, Object... elements) {
        return getDeque(key).addLastIfExists(elements);
    }

    /**
     * Gets list.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the list
     */
    private <V> RList<V> getList(String key) {
        return getRedissonClient().getList(key, getCodec());
    }

    /**
     * Gets blocking deque.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the blocking deque
     */
    private <V> RBlockingDeque<V> getBlockingDeque(String key) {
        return getRedissonClient().getBlockingDeque(key, getCodec());
    }

    /**
     * Gets deque.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the deque
     */
    private <V> RDeque<V> getDeque(String key) {
        return getRedissonClient().getDeque(key, getCodec());
    }

}
