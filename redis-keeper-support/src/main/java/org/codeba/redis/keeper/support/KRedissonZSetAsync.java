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

import org.codeba.redis.keeper.core.KZSetAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RFuture;
import org.redisson.api.RLexSortedSetAsync;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScoredSortedSetAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type K redisson z set async.
 */
class KRedissonZSetAsync extends BaseAsync implements KZSetAsync {
    /**
     * Instantiates a new K redisson z set async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonZSetAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson z set async.
     *
     * @param batch the batch
     * @param codec the codec
     */
    public KRedissonZSetAsync(RBatch batch, Codec codec) {
        super(batch, codec);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min) {
        final RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstAsync(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLastAsync(timeout, unit);
        }
        return object.toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count) {
        final RFuture<Collection<Object>> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirstAsync(count);
        } else {
            list = getRScoredSortedSet(key).pollLastAsync(count);
        }
        return list.toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        final RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAnyAsync(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
        }
        return object.toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit) {
        return this.bzmPopAsync(timeout, unit, key, false);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count) {
        return this.bzmPopAsync(key, false, count);
    }

    @Override
    public CompletableFuture<Object> bzPopMinAsync(String key, long timeout, TimeUnit unit) {
        return this.bzmPopAsync(timeout, unit, key, true);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count) {
        return this.bzmPopAsync(key, true, count);
    }

    @Override
    public CompletableFuture<Boolean> zAddAsync(String key, double score, Object member) {
        return getRScoredSortedSet(key).addAsync(score, member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members) {
        return getRScoredSortedSet(key).addAllAsync(members).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zCardAsync(String key) {
        return getRScoredSortedSet(key).sizeAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zCountAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).countAsync(startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys) {
        return getRScoredSortedSet(key).readDiffAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys) {
        return getRScoredSortedSet(destination).diffAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Double> zIncrByAsync(String key, Number increment, Object member) {
        return getRScoredSortedSet(key).addScoreAsync(member, increment).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readIntersectionAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys) {
        return getRScoredSortedSet(destination).intersectionAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAggregateAsync(String destination, String aggregate, String... otherKeys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersectionAsync(sortedSetAggregate, otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).intersectionAsync(keyWithWeight).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersectionAsync(sortedSetAggregate, keyWithWeight).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zLexCountAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countAsync(fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countHeadAsync(toElement, toInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zLexCountTailAsync(String key, String fromElement, boolean fromInclusive) {
        return getRLexSortedSet(key).countTailAsync(fromElement, fromInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min) {
        RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstAsync();
        } else {
            object = getRScoredSortedSet(key).pollLastAsync();
        }
        return object.toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAnyAsync(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
        }
        return object.toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> zPopMaxAsync(String key) {
        return getRScoredSortedSet(key).pollLastAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count) {
        return getRScoredSortedSet(key).pollLastAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> zPopMinAsync(String key) {
        return getRScoredSortedSet(key).pollFirstAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count) {
        return getRScoredSortedSet(key).pollFirstAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> zRandMemberAsync(String key) {
        return getRScoredSortedSet(key).randomAsync().toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count) {
        return getRScoredSortedSet(key).randomAsync(count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeAsync(startIndex, endIndex).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeAsync(startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeAsync(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startIndex, endIndex).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zRankAsync(String key, Object member) {
        return getRScoredSortedSet(key).rankAsync(member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members) {
        return getRScoredSortedSet(key).removeAllAsync(members).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByLexAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).removeRangeAsync(fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).removeRangeByRankAsync(startIndex, endIndex).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByScoreAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).removeRangeByScoreAsync(startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zRevRankAsync(String key, Object member) {
        return getRScoredSortedSet(key).revRankAsync(member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members) {
        return getRScoredSortedSet(key).getScoreAsync(members).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readUnionAsync(otherKeys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys) {
        return getRScoredSortedSet(destination).unionAsync(keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAggregateAsync(String destination, String aggregate, String... keys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).unionAsync(sortedSetAggregate, keys).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).unionAsync(keyWithWeight).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).unionAsync(sortedSetAggregate, keyWithWeight).toCompletableFuture();
    }

    /**
     * Gets r scored sorted set.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the r scored sorted set
     */
    private <V> RScoredSortedSetAsync<V> getRScoredSortedSet(String key) {
        if (null != getBatch()) {
            return getBatch().getScoredSortedSet(key, getCodec());
        } else {
            return getRedissonClient().getScoredSortedSet(key, getCodec());
        }
    }

    /**
     * Gets r lex sorted set.
     *
     * @param key the key
     * @return the r lex sorted set
     */
    private RLexSortedSetAsync getRLexSortedSet(String key) {
        if (null != getBatch()) {
            return getBatch().getLexSortedSet(key);
        } else {
            return getRedissonClient().getLexSortedSet(key);
        }
    }

}
