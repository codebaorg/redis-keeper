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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface KZSet extends KZSetAsync {
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
     * Bzm pop collection.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzmPop(String key, boolean min, int count);

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
     * Bz pop max optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzPopMax(String key, int count);

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
     * Bz pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzPopMin(String key, int count);

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
     * Z add int.
     *
     * @param key     the key
     * @param members the members
     * @return the int
     */
    int zAdd(String key, Map<Object, Double> members);

    /**
     * Z card int.
     *
     * @param key the key
     * @return the int
     */
    int zCard(String key);

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
     * Z diff collection.
     *
     * @param key  the key
     * @param keys the keys
     * @return the collection
     */
    Collection<Object> zDiff(String key, String... keys);

    /**
     * Z diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int zDiffStore(String destination, String... keys);

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
     * Z inter collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    Collection<Object> zInter(String key, String... otherKeys);

    /**
     * Z inter store int.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the int
     */
    int zInterStore(String destination, String... otherKeys);

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
     * Z inter store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zInterStore(String destination, Map<String, Double> keyWithWeight);

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
     * Z lex count head int.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the int
     */
    int zLexCountHead(String key, String toElement, boolean toInclusive);

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
     * Zm pop optional.
     *
     * @param key the key
     * @param min the min
     * @return the optional
     */
    Optional<Object> zmPop(String key, boolean min);

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
     * Z pop max optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zPopMax(String key);

    /**
     * Z pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zPopMax(String key, int count);

    /**
     * Z pop min optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zPopMin(String key);

    /**
     * Z pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zPopMin(String key, int count);

    /**
     * Z rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zRandMember(String key);

    /**
     * Z rand member collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zRandMember(String key, int count);

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
     * Z range reversed collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, int startIndex, int endIndex);

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
     * Z rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    Optional<Integer> zRank(String key, Object member);

    /**
     * Z rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean zRem(String key, Collection<?> members);

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
     * Z rem range by rank optional.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the optional
     */
    Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex);

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
     * Z rev rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    Optional<Integer> zRevRank(String key, Object member);

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
     * Z union collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    Collection<Object> zUnion(String key, String... otherKeys);

    /**
     * Z union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int zUnionStore(String destination, String... keys);

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
     * Z union store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zUnionStore(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight);

}
