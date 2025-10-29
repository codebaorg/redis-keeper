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

import org.codeba.redis.keeper.core.KZSet;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson z set.
 */
class KRedissonZSet extends KRedissonZSetAsync implements KZSet {
    /**
     * Instantiates a new K redisson z set.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonZSet(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min) {
        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLast(timeout, unit);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Collection<Object> bzmPop(String key, boolean min, int count) {
        final Collection<Object> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirst(count);
        } else {
            list = getRScoredSortedSet(key).pollLast(count);
        }
        return list;
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit) {
        return this.bzmPop(timeout, unit, key, false);
    }

    @Override
    public Collection<Object> bzPopMax(String key, int count) {
        return this.bzmPop(key, false, count);
    }

    @Override
    public Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit) {
        return this.bzmPop(timeout, unit, key, true);
    }

    @Override
    public Collection<Object> bzPopMin(String key, int count) {
        return this.bzmPop(key, true, count);
    }

    @Override
    public boolean zAdd(String key, double score, Object member) {
        return getRScoredSortedSet(key).add(score, member);
    }

    @Override
    public boolean zAdd(String key, String member) {
        return getRLexSortedSet(key).add(member);
    }

    @Override
    public int zAdd(String key, Map<Object, Double> members) {
        return getRScoredSortedSet(key).addAll(members);
    }

    @Override
    public boolean zAdd(String key, Collection<? extends String> members) {
        return getRLexSortedSet(key).addAll(members);
    }

    @Override
    public int zCard(String key) {
        return getRScoredSortedSet(key).size();
    }

    @Override
    public int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).count(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zDiff(String key, String... keys) {
        return getRScoredSortedSet(key).readDiff(keys);
    }

    @Override
    public int zDiffStore(String destination, String... keys) {
        return getRScoredSortedSet(destination).diff(keys);
    }

    @Override
    public Double zIncrBy(String key, Number increment, Object member) {
        return getRScoredSortedSet(key).addScore(member, increment);
    }

    @Override
    public Collection<Object> zInter(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readIntersection(otherKeys);
    }

    @Override
    public int zInterStore(String destination, String... otherKeys) {
        return getRScoredSortedSet(destination).intersection(otherKeys);
    }

    @Override
    public int zInterStoreAggregate(String destination, String aggregate, String... otherKeys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, otherKeys);
    }

    @Override
    public int zInterStore(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).intersection(keyWithWeight);
    }

    @Override
    public int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, keyWithWeight);
    }

    @Override
    public int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).count(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public int zLexCountHead(String key, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countHead(toElement, toInclusive);
    }

    @Override
    public int zLexCountTail(String key, String fromElement, boolean fromInclusive) {
        return getRLexSortedSet(key).countTail(fromElement, fromInclusive);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min) {
        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst();
        } else {
            object = getRScoredSortedSet(key).pollLast();
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> zPopMax(String key) {
        return Optional.ofNullable(getRScoredSortedSet(key).pollLast());
    }

    @Override
    public Collection<Object> zPopMax(String key, int count) {
        return getRScoredSortedSet(key).pollLast(count);
    }

    @Override
    public Optional<Object> zPopMin(String key) {
        return Optional.ofNullable(getRScoredSortedSet(key).pollFirst());
    }

    @Override
    public Collection<Object> zPopMin(String key, int count) {
        return getRScoredSortedSet(key).pollFirst(count);
    }

    @Override
    public Optional<Object> zRandMember(String key) {
        return Optional.ofNullable(getRScoredSortedSet(key).random());
    }

    @Override
    public Collection<Object> zRandMember(String key, int count) {
        return getRScoredSortedSet(key).random(count);
    }

    @Override
    public Collection<Object> zRange(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRange(startIndex, endIndex);
    }

    @Override
    public Collection<String> zRangeByLEX(String key, int startIndex, int endIndex) {
        return getRLexSortedSet(key).range(startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<String> zRangeByLEX(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive) {
        return getRLexSortedSet(key).range(from, startScoreInclusive, to, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<String> zRangeByLEX(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive, int offset, int count) {
        return getRLexSortedSet(key).range(from, startScoreInclusive, to, endScoreInclusive);
    }


    @Override
    public Collection<Object> zRangeReversed(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeReversed(startIndex, endIndex);
    }

    @Override
    public Collection<String> zRangeByLEXReversed(String key, String startIndex, String endIndex) {
        return getRLexSortedSet(key).rangeReversed(startIndex, true, endIndex, true);
    }


    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<String> zRangeByLEXReversed(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive) {
        return getRLexSortedSet(key).rangeReversed(from, startScoreInclusive, to, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<String> zRangeByLEXReversed(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive, int offset, int count) {
        return getRLexSortedSet(key).rangeReversed(from, startScoreInclusive, to, endScoreInclusive);
    }

    @Override
    public Optional<Integer> zRank(String key, Object member) {
        return Optional.ofNullable(getRScoredSortedSet(key).rank(member));
    }

    @Override
    public boolean zRem(String key, Collection<?> members) {
        return getRScoredSortedSet(key).removeAll(members);
    }

    @Override
    public Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        final int removeRange = getRLexSortedSet(key).removeRange(fromElement, fromInclusive, toElement, toInclusive);
        return Optional.of(removeRange);
    }

    @Override
    public Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex) {
        return Optional.of(getRScoredSortedSet(key).removeRangeByRank(startIndex, endIndex));
    }

    @Override
    public Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return Optional.of(getRScoredSortedSet(key).removeRangeByScore(startScore, startScoreInclusive, endScore, endScoreInclusive));
    }

    @Override
    public Optional<Integer> zRevRank(String key, Object member) {
        return Optional.ofNullable(getRScoredSortedSet(key).revRank(member));
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern) {
        return getRScoredSortedSet(key).iterator(pattern);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern, int count) {
        return getRScoredSortedSet(key).iterator(pattern, count);
    }

    @Override
    public List<Double> zScore(String key, List<Object> members) {
        return getRScoredSortedSet(key).getScore(members);
    }

    @Override
    public Collection<Object> zUnion(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readUnion(otherKeys);
    }

    @Override
    public int zUnionStore(String destination, String... keys) {
        return getRScoredSortedSet(destination).union(keys);
    }

    @Override
    public int zUnionStoreAggregate(String destination, String aggregate, String... keys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keys);
    }

    @Override
    public int zUnionStore(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).union(keyWithWeight);
    }

    @Override
    public int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keyWithWeight);
    }

    /**
     * Gets r scored sorted set.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the r scored sorted set
     */
    private <V> RScoredSortedSet<V> getRScoredSortedSet(String key) {
        return getRedissonClient().getScoredSortedSet(key, getCodec());
    }

    /**
     * Gets r lex sorted set.
     *
     * @param key the key
     * @return the r lex sorted set
     */
    private RLexSortedSet getRLexSortedSet(String key) {
        return getRedissonClient().getLexSortedSet(key);
    }

}
