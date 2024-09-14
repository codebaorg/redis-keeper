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

package org.codeba.redis.keeper.core;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The interface Cache template.
 *
 * @author codeba
 */
public interface CacheTemplate extends KBitSet, KGeneric, KGeo, KMap, KHyperLogLog {

    /**
     * Create batch k batch.
     *
     * @return the k batch
     */
    KBatch createBatch();

    /**
     * Bl move optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param pollLeft    the poll left
     * @return the optional
     */
    Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft);

    /**
     * Bl move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param pollLeft    the poll left
     * @return the completable future
     */
    CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft);

    /**
     * Bl pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> blPop(String key);

    /**
     * Bl pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> blPopAsync(String key);

    /**
     * Bl pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> blPop(String key, int count);

    /**
     * Bl pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> blPopAsync(String key, int count);

    /**
     * Bl pop optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Bl pop async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     * @throws InterruptedException the interrupted exception
     */
    CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit) throws InterruptedException;

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
    Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException;

    /**
     * Bl pop async completable future.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys);

    /**
     * Br pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> brPop(String key);

    /**
     * Br pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> brPopAsync(String key);

    /**
     * Br pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> brPop(String key, int count);

    /**
     * Br pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> brPopAsync(String key, int count);

    /**
     * Br pop optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Br pop async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     * @throws InterruptedException the interrupted exception
     */
    CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit) throws InterruptedException;

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
    Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException;

    /**
     * Br pop async completable future.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys);

    /**
     * Br pop push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param unit        the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> brPopLPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Br pop push async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param unit        the unit
     * @return the completable future
     */
    CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit);

    /**
     * L index optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    Optional<Object> lIndex(String key, int index);


    /**
     * L index async completable future.
     *
     * @param key   the key
     * @param index the index
     * @return the completable future
     */
    CompletableFuture<Object> lIndexAsync(String key, int index);

    /**
     * L insert int.
     *
     * @param key     the key
     * @param before  the before
     * @param pivot   the pivot
     * @param element the element
     * @return the int
     */
    int lInsert(String key, boolean before, Object pivot, Object element);

    /**
     * L insert async completable future.
     *
     * @param key     the key
     * @param before  the before
     * @param pivot   the pivot
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element);

    /**
     * L len int.
     *
     * @param key the key
     * @return the int
     */
    int llen(String key);

    /**
     * L len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> llenAsync(String key);

    /**
     * L move optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param pollLeft    the poll left
     * @return the optional
     */
    Optional<Object> lMove(String source, String destination, boolean pollLeft);

    /**
     * L move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param pollLeft    the poll left
     * @return the completable future
     */
    CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft);

    /**
     * L pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> lPop(String key, int count);

    /**
     * L pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> lPopAsync(String key, int count);

    /**
     * L push int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int lPush(String key, Object... elements);

    /**
     * L push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int lPushX(String key, Object... elements);

    /**
     * L push x async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Integer> lPushXAsync(String key, Object... elements);

    /**
     * L range list.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the list
     */
    List<Object> lRange(String key, int fromIndex, int toIndex);

    /**
     * L range async completable future.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the completable future
     */
    CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex);

    /**
     * L rem boolean.
     *
     * @param key     the key
     * @param element the element
     * @return the boolean
     */
    boolean lRem(String key, Object element);

    /**
     * L rem all.
     *
     * @param key     the key
     * @param element the element
     */
    void lRemAll(String key, Object element);

    /**
     * L rem async completable future.
     *
     * @param key     the key
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Boolean> lRemAsync(String key, Object element);

    /**
     * L rem all async completable future.
     *
     * @param key     the key
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Boolean> lRemAllAsync(String key, Object element);

    /**
     * L rem optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    Optional<Object> lRem(String key, int index);

    /**
     * L rem async completable future.
     *
     * @param key   the key
     * @param index the index
     * @return the completable future
     */
    CompletableFuture<Object> lRemAsync(String key, int index);

    /**
     * L set.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     */
    void lSet(String key, int index, Object element);

    /**
     * L set async completable future.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     * @return the completable future
     */
    CompletableFuture<Void> lSetAsync(String key, int index, Object element);

    /**
     * L trim.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     */
    void lTrim(String key, int fromIndex, int toIndex);

    /**
     * L trim async completable future.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the completable future
     */
    CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex);

    /**
     * R pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> rPop(String key, int count);

    /**
     * R pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<List<Object>> rPopAsync(String key, int count);

    /**
     * R pop l push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @return the optional
     */
    Optional<Object> rPopLPush(String source, String destination);

    /**
     * R pop l push async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @return the completable future
     */
    CompletableFuture<Object> rPopLPushAsync(String source, String destination);

    /**
     * R push boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    boolean rPush(String key, Object... elements);

    /**
     * R push async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Boolean> rPushAsync(String key, Object... elements);

    /**
     * R push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int rPushX(String key, Object... elements);

    /**
     * R push x async completable future.
     *
     * @param key      the key
     * @param elements the elements
     * @return the completable future
     */
    CompletableFuture<Integer> rPushXAsync(String key, Object... elements);

    /**
     * S add boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    boolean sAdd(String key, Object member);

    /**
     * S add async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Boolean> sAddAsync(String key, Object member);

    /**
     * S add boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean sAdd(String key, Collection<?> members);

    /**
     * S add async.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members);

    /**
     * S card int.
     *
     * @param key the key
     * @return the int
     */
    int sCard(String key);

    /**
     * S card async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> sCardAsync(String key);

    /**
     * S diff set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sDiff(String key, String... otherKeys);

    /**
     * S diff async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys);

    /**
     * S diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sDiffStore(String destination, String... keys);

    /**
     * S diff store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys);

    /**
     * S inter set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sInter(String key, String... otherKeys);

    /**
     * S inter async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys);

    /**
     * S inter store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sInterStore(String destination, String... keys);

    /**
     * S inter store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys);

    /**
     * S is member boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    boolean sIsMember(String key, Object member);

    /**
     * S is member async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Boolean> sIsMemberAsync(String key, Object member);

    /**
     * S members set.
     *
     * @param key the key
     * @return the set
     */
    Set<Object> sMembers(String key);

    /**
     * S members async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sMembersAsync(String key);

    /**
     * S move boolean.
     *
     * @param source      the source
     * @param destination the destination
     * @param member      the member
     * @return the boolean
     */
    boolean sMove(String source, String destination, Object member);

    /**
     * S move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param member      the member
     * @return the completable future
     */
    CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member);

    /**
     * S pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> sPop(String key);

    /**
     * S pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> sPopAsync(String key);

    /**
     * S pop set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> sPop(String key, int count);

    /**
     * S pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sPopAsync(String key, int count);

    /**
     * S rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> sRandMember(String key);

    /**
     * S rand member async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> sRandMemberAsync(String key);

    /**
     * S rand member set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> sRandMember(String key, int count);

    /**
     * S rand member async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count);

    /**
     * S rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean sRem(String key, Collection<?> members);

    /**
     * S rem async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members);

    /**
     * S scan iterator.
     *
     * @param key the key
     * @return the iterator
     */
    Iterator<Object> sScan(String key);

    /**
     * S scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @return the iterator
     */
    Iterator<Object> sScan(String key, String pattern);

    /**
     * S scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @param count   the count
     * @return the iterator
     */
    Iterator<Object> sScan(String key, String pattern, int count);

    /**
     * S union set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sUnion(String key, String... otherKeys);

    /**
     * S union async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys);

    /**
     * S union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sUnionStore(String destination, String... keys);

    /**
     * S union store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys);

    /**
     * Bzm pop optional.
     *
     * @param timeout the timeout
     * @param unit    the unit
     * @param key     the key
     * @param min     the min
     * @return the optional
     */
    Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min);

    /**
     * Bzm pop async completable future.
     *
     * @param timeout the timeout
     * @param unit    the unit
     * @param key     the key
     * @param min     the min
     * @return the completable future
     */
    CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min);

    /**
     * Bzm pop collection.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzmPop(String key, boolean min, int count);

    /**
     * Bzm pop async completable future.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count);

    /**
     * Bzm pop optional.
     *
     * @param timeout   the timeout
     * @param unit      the unit
     * @param key       the key
     * @param min       the min
     * @param otherKeys the other keys
     * @return the optional
     */
    Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys);

    /**
     * Bzm pop async completable future.
     *
     * @param timeout   the timeout
     * @param unit      the unit
     * @param key       the key
     * @param min       the min
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys);

    /**
     * Bz pop max optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop max async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzPopMax(String key, int count);

    /**
     * Bz pop max async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count);

    /**
     * Bz pop min optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop min async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    CompletableFuture<Object> bzPopMinAsync(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzPopMin(String key, int count);

    /**
     * Bz pop min async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count);

    /**
     * Z add boolean.
     *
     * @param key    the key
     * @param score  the score
     * @param member the member
     * @return the boolean
     */
    boolean zAdd(String key, double score, Object member);

    /**
     * Z add async completable future.
     *
     * @param key    the key
     * @param score  the score
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Boolean> zAddAsync(String key, double score, Object member);

    /**
     * Z add int.
     *
     * @param key     the key
     * @param members the members
     * @return the int
     */
    int zAdd(String key, Map<Object, Double> members);

    /**
     * Z add async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members);

    /**
     * Z card int.
     *
     * @param key the key
     * @return the int
     */
    int zCard(String key);

    /**
     * Z card async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> zCardAsync(String key);

    /**
     * Z count int.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the int
     */
    int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z count async completable future.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zCountAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z diff collection.
     *
     * @param key  the key
     * @param keys the keys
     * @return the collection
     */
    Collection<Object> zDiff(String key, String... keys);

    /**
     * Z diff async completable future.
     *
     * @param key  the key
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys);

    /**
     * Z diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int zDiffStore(String destination, String... keys);

    /**
     * Z diff store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys);

    /**
     * Z incr by double.
     *
     * @param key       the key
     * @param increment the increment
     * @param member    the member
     * @return the double
     */
    Double zIncrBy(String key, Number increment, Object member);

    /**
     * Z incr by async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @param member    the member
     * @return the completable future
     */
    CompletableFuture<Double> zIncrByAsync(String key, Number increment, Object member);

    /**
     * Z inter collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    Collection<Object> zInter(String key, String... otherKeys);

    /**
     * Z inter async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys);

    /**
     * Z inter store int.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the int
     */
    int zInterStore(String destination, String... otherKeys);

    /**
     * Z inter store async completable future.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the completable future
     */
    CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys);

    /**
     * Z inter store aggregate int.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param otherKeys   the other keys
     * @return the int
     */
    int zInterStoreAggregate(String destination, String aggregate, String... otherKeys);

    /**
     * Z inter store aggregate async completable future.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param otherKeys   the other keys
     * @return the completable future
     */
    CompletableFuture<Integer> zInterStoreAggregateAsync(String destination, String aggregate, String... otherKeys);

    /**
     * Z inter store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zInterStore(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z inter store async completable future.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z inter store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight);

    /**
     * Z inter store async completable future.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zInterStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight);

    /**
     * Z lex count int.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @param toElement     the to element
     * @param toInclusive   the to inclusive
     * @return the int
     */
    int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive);

    /**
     * Z lex count async completable future.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @param toElement     the to element
     * @param toInclusive   the to inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zLexCountAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive);

    /**
     * Z lex count head int.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the int
     */
    int zLexCountHead(String key, String toElement, boolean toInclusive);

    /**
     * Z lex count head async completable future.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive);

    /**
     * Z lex count tail int.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @return the int
     */
    int zLexCountTail(String key, String fromElement, boolean fromInclusive);

    /**
     * Z lex count tail async completable future.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zLexCountTailAsync(String key, String fromElement, boolean fromInclusive);

    /**
     * Zm pop optional.
     *
     * @param key the key
     * @param min the min
     * @return the optional
     */
    Optional<Object> zmPop(String key, boolean min);

    /**
     * Zm pop async completable future.
     *
     * @param key the key
     * @param min the min
     * @return the completable future
     */
    CompletableFuture<Object> zmPopAsync(String key, boolean min);

    /**
     * Zm pop optional.
     *
     * @param key       the key
     * @param min       the min
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the optional
     */
    Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys);

    /**
     * Zm pop async completable future.
     *
     * @param key       the key
     * @param min       the min
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Object> zmPopAsync(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys);


    /**
     * Z pop max optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zPopMax(String key);

    /**
     * Z pop max async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> zPopMaxAsync(String key);

    /**
     * Z pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zPopMax(String key, int count);

    /**
     * Z pop max async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count);

    /**
     * Z pop min optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zPopMin(String key);

    /**
     * Z pop min async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> zPopMinAsync(String key);

    /**
     * Z pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zPopMin(String key, int count);

    /**
     * Z pop min async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count);

    /**
     * Z rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zRandMember(String key);

    /**
     * Z rand member async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> zRandMemberAsync(String key);

    /**
     * Z rand member collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zRandMember(String key, int count);

    /**
     * Z rand member async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count);

    /**
     * Z range collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    Collection<Object> zRange(String key, int startIndex, int endIndex);

    /**
     * Z range async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeAsync(String key, int startIndex, int endIndex);

    /**
     * Z range collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the collection
     */
    Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z range async completable future.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z range collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @param offset              the offset
     * @param count               the count
     * @return the collection
     */
    Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count);

    /**
     * Z range async completable future.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @param offset              the offset
     * @param count               the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count);

    /**
     * Z range reversed collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, int startIndex, int endIndex);

    /**
     * Z range reversed async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex);

    /**
     * Z range reversed collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z range reversed async completable future.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z range reversed collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @param offset              the offset
     * @param count               the count
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count);

    /**
     * Z range reversed async completable future.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @param offset              the offset
     * @param count               the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count);

    /**
     * Z rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    Optional<Integer> zRank(String key, Object member);

    /**
     * Z rank async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Integer> zRankAsync(String key, Object member);

    /**
     * Z rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean zRem(String key, Collection<?> members);

    /**
     * Z rem async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members);

    /**
     * Z rem range by lex optional.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @param toElement     the to element
     * @param toInclusive   the to inclusive
     * @return the optional
     */
    Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive);

    /**
     * Z rem range by lex async completable future.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @param toElement     the to element
     * @param toInclusive   the to inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zRemRangeByLexAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive);

    /**
     * Z rem range by rank optional.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the optional
     */
    Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex);

    /**
     * Z rem range by rank async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex);

    /**
     * Z rem range by score optional.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the optional
     */
    Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z rem range by score async completable future.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zRemRangeByScoreAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z rev rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    Optional<Integer> zRevRank(String key, Object member);

    /**
     * Z rev rank async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Integer> zRevRankAsync(String key, Object member);

    /**
     * Z scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @return the iterator
     */
    Iterator<Object> zScan(String key, String pattern);

    /**
     * Z scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @param count   the count
     * @return the iterator
     */
    Iterator<Object> zScan(String key, String pattern, int count);

    /**
     * Z score list.
     *
     * @param key     the key
     * @param members the members
     * @return the list
     */
    List<Double> zScore(String key, List<Object> members);

    /**
     * Z score async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members);

    /**
     * Z union collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    Collection<Object> zUnion(String key, String... otherKeys);

    /**
     * Z union async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys);

    /**
     * Z union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int zUnionStore(String destination, String... keys);

    /**
     * Z union store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys);

    /**
     * Z union store aggregate int.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param keys        the keys
     * @return the int
     */
    int zUnionStoreAggregate(String destination, String aggregate, String... keys);

    /**
     * Z union store aggregate async completable future.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAggregateAsync(String destination, String aggregate, String... keys);

    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zUnionStore(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z union store async completable future.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight);

    /**
     * Z union store async completable future.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight);

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     */
    void append(String key, Object value);


    /**
     * Decr long.
     *
     * @param key the key
     * @return the long
     */
    long decr(String key);

    /**
     * Decr async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> decrAsync(String key);

    /**
     * Decr by long.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the long
     */
    long decrBy(String key, long decrement);

    /**
     * Decr by async completable future.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the completable future
     */
    CompletableFuture<Long> decrByAsync(String key, long decrement);

    /**
     * Get optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> get(String key);

    /**
     * Gets object.
     *
     * @param key the key
     * @return the object
     */
    Optional<Object> getObject(String key);

    /**
     * Gets async.
     *
     * @param key the key
     * @return the async
     */
    CompletableFuture<Object> getAsync(String key);

    /**
     * Gets object async.
     *
     * @param key the key
     * @return the object async
     */
    CompletableFuture<Object> getObjectAsync(String key);

    /**
     * Gets del.
     *
     * @param key the key
     * @return the del
     */
    Optional<Object> getDel(String key);

    /**
     * Gets del async.
     *
     * @param key the key
     * @return the del async
     */
    CompletableFuture<Object> getDelAsync(String key);

    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     */
    long getLong(String key);

    /**
     * Gets long async.
     *
     * @param key the key
     * @return the long async
     */
    CompletableFuture<Long> getLongAsync(String key);

    /**
     * Incr long.
     *
     * @param key the key
     * @return the long
     */
    long incr(String key);

    /**
     * Incr async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> incrAsync(String key);

    /**
     * Incr by long.
     *
     * @param key       the key
     * @param increment the increment
     * @return the long
     */
    long incrBy(String key, long increment);

    /**
     * Incr by async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @return the completable future
     */
    CompletableFuture<Long> incrByAsync(String key, long increment);

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     */
    double getDouble(String key);

    /**
     * Gets double async.
     *
     * @param key the key
     * @return the double async
     */
    CompletableFuture<Double> getDoubleAsync(String key);

    /**
     * Incr by float double.
     *
     * @param key       the key
     * @param increment the increment
     * @return the double
     */
    double incrByFloat(String key, double increment);

    /**
     * Incr by float async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @return the completable future
     */
    CompletableFuture<Double> incrByFloatAsync(String key, double increment);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, long expect, long update);

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, double expect, double update);

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void setObject(String key, Object value);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setObjectAsync(String key, Object value);

    /**
     * M get map.
     *
     * @param keys the keys
     * @return the map
     */
    Map<String, Object> mGet(String... keys);

    /**
     * M get async completable future.
     *
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Map<String, Object>> mGetAsync(String... keys);

    /**
     * M set.
     *
     * @param kvMap the kv map
     */
    void mSet(Map<String, String> kvMap);

    /**
     * M set async completable future.
     *
     * @param kvMap the kv map
     * @return the completable future
     */
    CompletableFuture<Void> mSetAsync(Map<String, String> kvMap);

    /**
     * M set nx boolean.
     *
     * @param kvMap the kv map
     * @return the boolean
     */
    boolean mSetNX(Map<String, String> kvMap);

    /**
     * M set nx async completable future.
     *
     * @param kvMap the kv map
     * @return the completable future
     */
    CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, String value);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setAsync(String key, String value);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, Long value);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setAsync(String key, Long value);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, Double value);

    /**
     * Sets async.
     *
     * @param key   the key
     * @param value the value
     * @return the async
     */
    CompletableFuture<Void> setAsync(String key, Double value);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, String expect, String update);

    /**
     * Compare and set async completable future.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the completable future
     */
    CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update);

    /**
     * Sets ex.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     */
    void setEX(String key, String value, Duration duration);

    /**
     * Sets ex async.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     * @return the ex async
     */
    CompletableFuture<Void> setEXAsync(String key, String value, Duration duration);

    /**
     * Str len long.
     *
     * @param key the key
     * @return the long
     */
    long strLen(String key);

    /**
     * Str len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Long> strLenAsync(String key);

    /**
     * Bf add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfAdd(String key, Object item);

    /**
     * Bf card long.
     *
     * @param key the key
     * @return the long
     */
    long bfCard(String key);

    /**
     * Bf exists boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfExists(String key, Object item);

    /**
     * Bfm add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfmAdd(String key, Object item);

    /**
     * Bf reserve boolean.
     *
     * @param key                the key
     * @param expectedInsertions the expected insertions
     * @param falseProbability   the false probability
     * @return the boolean
     */
    boolean bfReserve(String key, long expectedInsertions, double falseProbability);

    /**
     * Delete bf boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean deleteBf(String key);

    /**
     * Delete bf async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Boolean> deleteBfAsync(String key);

    /**
     * Try lock boolean.
     *
     * @param key       the key
     * @param waitTime  the wait time
     * @param leaseTime the lease time
     * @param unit      the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * Try lock async completable future.
     *
     * @param key       the key
     * @param waitTime  the wait time
     * @param leaseTime the lease time
     * @param unit      the unit
     * @return the completable future
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * Try lock boolean.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException;

    /**
     * Try lock async completable future.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the completable future
     */
    CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit);

    /**
     * Unlock.
     *
     * @param key the key
     */
    void unlock(String key);

    /**
     * Unlock async.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Void> unlockAsync(String key);

    /**
     * Unlock async.
     *
     * @param key      the key
     * @param threadId the thread id
     * @return the completable future
     */
    CompletableFuture<Void> unlockAsync(String key, long threadId);

    /**
     * Force unlock boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean forceUnlock(String key);

    /**
     * Force unlock async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Boolean> forceUnlockAsync(String key);

    /**
     * Execute script optional.
     *
     * @param script the script
     * @param keys   the keys
     * @param values the values
     * @return the optional
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException;

    /**
     * Execute script async completable future.
     *
     * @param script the script
     * @param keys   the keys
     * @param values the values
     * @return the completable future
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    CompletableFuture<Object> executeScriptAsync(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException;


    /**
     * Try set rate limiter boolean.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the boolean
     */
    boolean trySetRateLimiter(String key, long rate, long rateInterval);

    /**
     * Try set rate limiter async completable future.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the completable future
     */
    CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval);

    /**
     * Try acquire boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean tryAcquire(String key);

    /**
     * Try acquire async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Boolean> tryAcquireAsync(String key);

    /**
     * Try acquire boolean.
     *
     * @param key     the key
     * @param permits the permits
     * @return the boolean
     */
    boolean tryAcquire(String key, long permits);

    /**
     * Try acquire async completable future.
     *
     * @param key     the key
     * @param permits the permits
     * @return the completable future
     */
    CompletableFuture<Boolean> tryAcquireAsync(String key, long permits);

}
