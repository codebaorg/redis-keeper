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

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDeque;
import org.redisson.api.RFuture;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.api.queue.DequeMoveArgs;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type R lists.
 */
class RLists {

    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R lists.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RLists(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Bl move optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param pollLeft    the poll left
     * @return the optional
     */
    public Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft) {
        Object object;
        if (pollLeft) {
            object = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            object = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return Optional.ofNullable(object);
    }


    /**
     * Bl move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param pollLeft    the poll left
     * @return the completable future
     */
    public CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft) {
        return blMoveRFuture(source, destination, timeout, pollLeft).toCompletableFuture();
    }

    private RFuture<Object> blMoveRFuture(String source, String destination, Duration timeout, boolean pollLeft) {
        if (pollLeft) {
            return getBlockingDeque(source).moveAsync(timeout, DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            return getBlockingDeque(source).moveAsync(timeout, DequeMoveArgs.pollLast().addFirstTo(destination));
        }
    }


    /**
     * Bl pop optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> blPop(String key) {
        return Optional.ofNullable(getBlockingDeque(key).poll());
    }


    /**
     * Bl pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> blPopAsync(String key) {
        return blPopRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> blPopRFuture(String key) {
        return getBlockingDeque(key).pollAsync();
    }


    /**
     * Bl pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    public List<Object> blPop(String key, int count) {
        return getBlockingDeque(key).poll(count);
    }


    /**
     * Bl pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<List<Object>> blPopAsync(String key, int count) {
        return blPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> blPopRFuture(String key, int count) {
        return getBlockingDeque(key).pollAsync(count);
    }


    /**
     * Bl pop optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        final Object polled = getBlockingDeque(key).poll(timeout, unit);
        return Optional.ofNullable(polled);
    }


    /**
     * Bl pop async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit) {
        return blPopRFuture(key, timeout, unit).toCompletableFuture();
    }

    private RFuture<Object> blPopRFuture(String key, long timeout, TimeUnit unit) {
        return getBlockingDeque(key).pollAsync(timeout, unit);
    }


    /**
     * Bl pop optional.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        final Object object = getBlockingDeque(key).pollFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(object);
    }


    /**
     * Bl pop async completable future.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return blPopRFuture(key, timeout, unit, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> blPopRFuture(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return getBlockingDeque(key).pollFromAnyAsync(timeout, unit, otherKeys);
    }


    /**
     * Br pop optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> brPop(String key) {
        final Object polled = getBlockingDeque(key).pollLast();
        return Optional.ofNullable(polled);
    }


    /**
     * Br pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> brPopAsync(String key) {
        return brPopRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> brPopRFuture(String key) {
        return getBlockingDeque(key).pollLastAsync();
    }


    /**
     * Br pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    public List<Object> brPop(String key, int count) {
        return getBlockingDeque(key).pollLast(count);
    }


    /**
     * Br pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<List<Object>> brPopAsync(String key, int count) {
        return brPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> brPopRFuture(String key, int count) {
        return getBlockingDeque(key).pollLastAsync(count);
    }


    /**
     * Br pop optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        final Object polled = getBlockingDeque(key).pollLast(timeout, unit);
        return Optional.ofNullable(polled);
    }


    /**
     * Br pop async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit) {
        return brPopRFuture(key, timeout, unit).toCompletableFuture();
    }

    private RFuture<Object> brPopRFuture(String key, long timeout, TimeUnit unit) {
        return getBlockingDeque(key).pollLastAsync(timeout, unit);
    }


    /**
     * Br pop optional.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        final Object polled = getBlockingDeque(key).pollLastFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(polled);
    }


    /**
     * Br pop async completable future.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return brPopRFuture(key, timeout, unit, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> brPopRFuture(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return getBlockingDeque(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
    }


    /**
     * Br pop l push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param unit        the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    public Optional<Object> brPopLPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException {
        final Object object = getBlockingDeque(source).pollLastAndOfferFirstTo(destination, timeout, unit);
        return Optional.ofNullable(object);
    }


    /**
     * Br pop l push async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param unit        the unit
     * @return the completable future
     */
    public CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit) {
        return brPoplPushRFuture(source, destination, timeout, unit).toCompletableFuture();
    }

    private RFuture<Object> brPoplPushRFuture(String source, String destination, long timeout, TimeUnit unit) {
        return getBlockingDeque(source).pollLastAndOfferFirstToAsync(destination, timeout, unit);
    }


    /**
     * L index optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    public Optional<Object> lIndex(String key, int index) {
        return Optional.ofNullable(getList(key).get(index));
    }


    /**
     * L index async completable future.
     *
     * @param key   the key
     * @param index the index
     * @return the completable future
     */
    public CompletableFuture<Object> lIndexAsync(String key, int index) {
        return lIndexRFuture(key, index).toCompletableFuture();
    }

    private RFuture<Object> lIndexRFuture(String key, int index) {
        return getList(key).getAsync(index);
    }


    /**
     * L insert int.
     *
     * @param key     the key
     * @param before  the before
     * @param pivot   the pivot
     * @param element the element
     * @return the int
     */
    public int lInsert(String key, boolean before, Object pivot, Object element) {
        int added;
        if (before) {
            added = getList(key).addBefore(pivot, element);
        } else {
            added = getList(key).addAfter(pivot, element);
        }
        return added;
    }


    /**
     * L insert async completable future.
     *
     * @param key     the key
     * @param before  the before
     * @param pivot   the pivot
     * @param element the element
     * @return the completable future
     */
    public CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element) {
        return lInsertRFuture(key, before, pivot, element).toCompletableFuture();
    }

    private RFuture<Integer> lInsertRFuture(String key, boolean before, Object pivot, Object element) {
        RFuture<Integer> added;
        if (before) {
            added = getList(key).addBeforeAsync(pivot, element);
        } else {
            added = getList(key).addAfterAsync(pivot, element);
        }
        return added;
    }


    /**
     * Llen int.
     *
     * @param key the key
     * @return the int
     */
    public int llen(String key) {
        return getList(key).size();
    }


    /**
     * Llen async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Integer> llenAsync(String key) {
        return llenRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> llenRFuture(String key) {
        return getList(key).sizeAsync();
    }


    /**
     * L move optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param pollLeft    the poll left
     * @return the optional
     */
    public Optional<Object> lMove(String source, String destination, boolean pollLeft) {
        Object move;
        if (pollLeft) {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return Optional.ofNullable(move);
    }


    /**
     * L move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param pollLeft    the poll left
     * @return the completable future
     */
    public CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft) {
        return lMoveRFuture(source, destination, pollLeft).toCompletableFuture();
    }

    private RFuture<Object> lMoveRFuture(String source, String destination, boolean pollLeft) {
        RFuture<Object> move;
        if (pollLeft) {
            move = getBlockingDeque(source).moveAsync(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).moveAsync(DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return move;
    }

//
//    public Optional<Map<String, List<Object>>> lmPop(String key, boolean left, int count, String... otherKeys) throws InterruptedException {
//        log("lmpop", key, left, count, otherKeys);
//
//        final Map<String, List<Object>> map;
//        if (left) {
//            map = getBlockingDeque(key).pollFirstFromAny(Duration.ofMinutes(1), count, otherKeys);
//        } else {
//            map = getBlockingDeque(key).pollLastFromAny(Duration.ofMinutes(1), count, otherKeys);
//        }
//
//        return Optional.ofNullable(map);
//    }


    /**
     * L pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    public List<Object> lPop(String key, int count) {
        return getDeque(key).poll(count);
    }


    /**
     * L pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<List<Object>> lPopAsync(String key, int count) {
        return lPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> lPopRFuture(String key, int count) {
        return getDeque(key).pollAsync(count);
    }


    /**
     * L push int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    public int lPush(String key, Object... elements) {
        Arrays.stream(elements)
                .forEach(element -> getDeque(key).addFirst(element));
        return elements.length;
    }


    /**
     * L push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    public int lPushX(String key, Object... elements) {
        return getDeque(key).addFirstIfExists(elements);
    }


    /**
     * L push x async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    public CompletableFuture<Integer> lPushXAsync(String key, Object... elements) {
        return lPushXRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Integer> lPushXRFuture(String key, Object... elements) {
        return getDeque(key).addFirstIfExistsAsync(elements);
    }


    /**
     * L range list.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the list
     */
    public List<Object> lRange(String key, int fromIndex, int toIndex) {
        return getList(key).range(fromIndex, toIndex);
    }


    /**
     * L range async completable future.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the completable future
     */
    public CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex) {
        return lRangeRFuture(key, fromIndex, toIndex).toCompletableFuture();
    }

    private RFuture<List<Object>> lRangeRFuture(String key, int fromIndex, int toIndex) {
        return getList(key).rangeAsync(fromIndex, toIndex);
    }


    /**
     * L rem boolean.
     *
     * @param key     the key
     * @param element the element
     * @return the boolean
     */
    public boolean lRem(String key, Object element) {
        return getList(key).remove(element);
    }


    /**
     * L rem all boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    public boolean lRemAll(String key, Collection<Object> elements) {
        return getList(key).removeAll(elements);
    }

    /**
     * L rem all async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    public CompletableFuture<Boolean> lRemAllAsync(String key, Collection<Object> elements) {
        return getList(key).removeAllAsync(elements).toCompletableFuture();
    }

    /**
     * L rem optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    public Optional<Object> lRem(String key, int index) {
        return Optional.ofNullable(getList(key).remove(index));
    }

    /**
     * L rem async completable future.
     *
     * @param key   the key
     * @param index the index
     * @return the completable future
     */
    public CompletableFuture<Object> lRemAsync(String key, int index) {
        return getList(key).removeAsync(index).toCompletableFuture();
    }

    /**
     * L rem async completable future.
     *
     * @param key     the key
     * @param element the element
     * @return the completable future
     */
    public CompletableFuture<Boolean> lRemAsync(String key, Object element) {
        return lRemRFuture(key, element).toCompletableFuture();
    }

    private RFuture<Boolean> lRemRFuture(String key, Object element) {
        return getList(key).removeAsync(element);
    }


    /**
     * L set.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     */
    public void lSet(String key, int index, Object element) {
        getList(key).fastSet(index, element);
    }


    /**
     * L set async completable future.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     * @return the completable future
     */
    public CompletableFuture<Void> lSetAsync(String key, int index, Object element) {
        return lSetRFuture(key, index, element).toCompletableFuture();
    }

    private RFuture<Void> lSetRFuture(String key, int index, Object element) {
        return getList(key).fastSetAsync(index, element);
    }


    /**
     * L trim.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     */
    public void lTrim(String key, int fromIndex, int toIndex) {
        getList(key).trim(fromIndex, toIndex);
    }


    /**
     * L trim async completable future.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the completable future
     */
    public CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex) {
        return lTrimRFuture(key, fromIndex, toIndex).toCompletableFuture();
    }

    private RFuture<Void> lTrimRFuture(String key, int fromIndex, int toIndex) {
        return getList(key).trimAsync(fromIndex, toIndex);
    }


    /**
     * R pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    public List<Object> rPop(String key, int count) {
        return getDeque(key).pollLast(count);
    }


    /**
     * R pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<List<Object>> rPopAsync(String key, int count) {
        return rPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> rPopRFuture(String key, int count) {
        return getDeque(key).pollLastAsync(count);
    }


    /**
     * R pop l push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @return the optional
     */
    public Optional<Object> rPopLPush(String source, String destination) {
        return Optional.ofNullable(getDeque(source).pollLastAndOfferFirstTo(destination));
    }


    /**
     * R pop l push async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @return the completable future
     */
    public CompletableFuture<Object> rPopLPushAsync(String source, String destination) {
        return rPoplPushRFuture(source, destination).toCompletableFuture();
    }

    private RFuture<Object> rPoplPushRFuture(String source, String destination) {
        return getDeque(source).pollLastAndOfferFirstToAsync(destination);
    }


    /**
     * R push boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    public boolean rPush(String key, Object... elements) {
        return getList(key).addAll(Arrays.asList(elements));
    }


    /**
     * R push async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    public CompletableFuture<Boolean> rPushAsync(String key, Object... elements) {
        return rPushRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Boolean> rPushRFuture(String key, Object... elements) {
        return getList(key).addAllAsync(Arrays.asList(elements));
    }


    /**
     * R push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    public int rPushX(String key, Object... elements) {
        return getDeque(key).addLastIfExists(elements);
    }


    /**
     * R push x async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    public CompletableFuture<Integer> rPushXAsync(String key, Object... elements) {
        return rPushXRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Integer> rPushXRFuture(String key, Object... elements) {
        return getDeque(key).addLastIfExistsAsync(elements);
    }

    private <V> RList<V> getList(String key) {
        return getDataSource().getList(key, stringCodec);
    }

    private <V> RBlockingDeque<V> getBlockingDeque(String key) {
        return getDataSource().getBlockingDeque(key, stringCodec);
    }

    private <V> RDeque<V> getDeque(String key) {
        return getDataSource().getDeque(key, stringCodec);
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
