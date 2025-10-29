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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The interface Kz set async.
 */
public interface KZSetAsync {
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
     * Bzm pop async completable future.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count);

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
     * Bz pop max async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop max async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count);

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
     * Bz pop min async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count);

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
     * Z add async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Boolean> zAddAsync(String key, String member);

    /**
     * Z add async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members);

    /**
     * Z add async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> zAddAsync(String key, Collection<? extends String> members);

    /**
     * Z card async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Integer> zCardAsync(String key);

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
     * Z diff async completable future.
     *
     * @param key  the key
     * @param keys the keys
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys);

    /**
     * Z diff store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys);

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
     * Z inter async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys);

    /**
     * Z inter store async completable future.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the completable future
     */
    CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys);

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
     * Z inter store async completable future.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight);

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
     * Z lex count head async completable future.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the completable future
     */
    CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive);

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
     * Zm pop async completable future.
     *
     * @param key the key
     * @param min the min
     * @return the completable future
     */
    CompletableFuture<Object> zmPopAsync(String key, boolean min);

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
     * Z pop max async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> zPopMaxAsync(String key);

    /**
     * Z pop max async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count);

    /**
     * Z pop min async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> zPopMinAsync(String key);

    /**
     * Z pop min async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count);

    /**
     * Z rand member async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    CompletableFuture<Object> zRandMemberAsync(String key);

    /**
     * Z rand member async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count);

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
     * Z range reversed async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex);

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
     * Z rank async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Integer> zRankAsync(String key, Object member);

    /**
     * Z rem async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members);

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
     * Z rem range by rank async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex);

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
     * Z rev rank async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    CompletableFuture<Integer> zRevRankAsync(String key, Object member);

    /**
     * Z score async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members);

    /**
     * Z union async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys);

    /**
     * Z union store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys);

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
     * Z union store async completable future.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z union store async completable future.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight);

}
