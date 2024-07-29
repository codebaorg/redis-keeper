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

import org.redisson.api.RFuture;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type Rz set.
 */
class RZSet {

    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new Rz set.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    public RZSet(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Bzm pop optional.
     *
     * @param timeout the timeout
     * @param unit    the unit
     * @param key     the key
     * @param min     the min
     * @return the optional
     */
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min) {
        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLast(timeout, unit);
        }
        return Optional.ofNullable(object);
    }


    /**
     * Bzm pop async completable future.
     *
     * @param timeout the timeout
     * @param unit    the unit
     * @param key     the key
     * @param min     the min
     * @return the completable future
     */
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min) {
        return bzmPopRFuture(timeout, unit, key, min).toCompletableFuture();
    }

    private RFuture<Object> bzmPopRFuture(long timeout, TimeUnit unit, String key, boolean min) {
        final RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstAsync(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLastAsync(timeout, unit);
        }
        return object;
    }


    /**
     * Bzm pop collection.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the collection
     */
    public Collection<Object> bzmPop(String key, boolean min, int count) {
        final Collection<Object> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirst(count);
        } else {
            list = getRScoredSortedSet(key).pollLast(count);
        }
        return list;
    }


    /**
     * Bzm pop async completable future.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count) {
        return bzmPopRFuture(key, min, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> bzmPopRFuture(String key, boolean min, int count) {
        final RFuture<Collection<Object>> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirstAsync(count);
        } else {
            list = getRScoredSortedSet(key).pollLastAsync(count);
        }
        return list;
    }


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
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }


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
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        return bzmPopRFuture(timeout, unit, key, min, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> bzmPopRFuture(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        final RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAnyAsync(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
        }
        return object;
    }


    /**
     * Bz pop max optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    public Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit) {
        return bzmPop(timeout, unit, key, false);
    }


    /**
     * Bz pop max async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    public CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit) {
        return bzmPopRFuture(timeout, unit, key, false).toCompletableFuture();
    }


    /**
     * Bz pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    public Collection<Object> bzPopMax(String key, int count) {
        return bzmPop(key, false, count);
    }


    /**
     * Bz pop max async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count) {
        return bzmPopRFuture(key, false, count).toCompletableFuture();
    }


    /**
     * Bz pop min optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    public Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit) {
        return bzmPop(timeout, unit, key, true);
    }


    /**
     * Bz pop min async completable future.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the completable future
     */
    public CompletableFuture<Object> bzPopMinAsync(String key, long timeout, TimeUnit unit) {
        return bzmPopRFuture(timeout, unit, key, true).toCompletableFuture();
    }


    /**
     * Bz pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    public Collection<Object> bzPopMin(String key, int count) {
        return bzmPop(key, true, count);
    }


    /**
     * Bz pop min async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count) {
        return bzmPopRFuture(key, true, count).toCompletableFuture();
    }


    /**
     * Z add boolean.
     *
     * @param key    the key
     * @param score  the score
     * @param member the member
     * @return the boolean
     */
    public boolean zAdd(String key, double score, Object member) {
        return getRScoredSortedSet(key).add(score, member);
    }


    /**
     * Z add async completable future.
     *
     * @param key    the key
     * @param score  the score
     * @param member the member
     * @return the completable future
     */
    public CompletableFuture<Boolean> zAddAsync(String key, double score, Object member) {
        return zAddRFuture(key, score, member).toCompletableFuture();
    }

    private RFuture<Boolean> zAddRFuture(String key, double score, Object member) {
        return getRScoredSortedSet(key).addAsync(score, member);
    }


    /**
     * Z add int.
     *
     * @param key     the key
     * @param members the members
     * @return the int
     */
    public int zAdd(String key, Map<Object, Double> members) {
        return getRScoredSortedSet(key).addAll(members);
    }


    /**
     * Z add async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members) {
        return zAddRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Integer> zAddRFuture(String key, Map<Object, Double> members) {
        return getRScoredSortedSet(key).addAllAsync(members);
    }


    /**
     * Z card int.
     *
     * @param key the key
     * @return the int
     */
    public int zCard(String key) {
        return getRScoredSortedSet(key).size();
    }


    /**
     * Z card async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Integer> zCardAsync(String key) {
        return zCardRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> zCardRFuture(String key) {
        return getRScoredSortedSet(key).sizeAsync();
    }


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
    public int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).count(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


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
    public CompletableFuture<Integer> zCountAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return zCountRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zCountRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).countAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


    /**
     * Z diff collection.
     *
     * @param key  the key
     * @param keys the keys
     * @return the collection
     */
    public Collection<Object> zDiff(String key, String... keys) {
        return getRScoredSortedSet(key).readDiff(keys);
    }


    /**
     * Z diff async completable future.
     *
     * @param key  the key
     * @param keys the keys
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys) {
        return zDiffRFuture(key, keys).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zDiffRFuture(String key, String... keys) {
        return getRScoredSortedSet(key).readDiffAsync(keys);
    }


    /**
     * Z diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    public int zDiffStore(String destination, String... keys) {
        return getRScoredSortedSet(destination).diff(keys);
    }


    /**
     * Z diff store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    public CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys) {
        return zDiffStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> zDiffStoreRFuture(String destination, String... keys) {
        return getRScoredSortedSet(destination).diffAsync(keys);
    }


    /**
     * Z incr by double.
     *
     * @param key       the key
     * @param increment the increment
     * @param member    the member
     * @return the double
     */
    public Double zIncrBy(String key, Number increment, Object member) {
        return getRScoredSortedSet(key).addScore(member, increment);
    }


    /**
     * Z incr by async completable future.
     *
     * @param key       the key
     * @param increment the increment
     * @param member    the member
     * @return the completable future
     */
    public CompletableFuture<Double> zIncrByAsync(String key, Number increment, Object member) {
        return zIncrByRFuture(key, increment, member).toCompletableFuture();
    }

    private RFuture<Double> zIncrByRFuture(String key, Number increment, Object member) {
        return getRScoredSortedSet(key).addScoreAsync(member, increment);
    }


    /**
     * Z inter collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    public Collection<Object> zInter(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readIntersection(otherKeys);
    }


    /**
     * Z inter async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys) {
        return zInterRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zInterRFuture(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readIntersectionAsync(otherKeys);
    }


    /**
     * Z inter store int.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the int
     */
    public int zInterStore(String destination, String... otherKeys) {
        return getRScoredSortedSet(destination).intersection(otherKeys);
    }


    /**
     * Z inter store async completable future.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the completable future
     */
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys) {
        return zInterStoreRFuture(destination, otherKeys).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreRFuture(String destination, String... otherKeys) {
        return getRScoredSortedSet(destination).intersectionAsync(otherKeys);
    }


    /**
     * Z inter store aggregate int.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param otherKeys   the other keys
     * @return the int
     */
    public int zInterStoreAggregate(String destination, String aggregate, String... otherKeys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, otherKeys);
    }


    /**
     * Z inter store aggregate async completable future.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param otherKeys   the other keys
     * @return the completable future
     */
    public CompletableFuture<Integer> zInterStoreAggregateAsync(String destination, String aggregate, String... otherKeys) {
        return zInterStoreAggregateRFuture(destination, aggregate, otherKeys).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreAggregateRFuture(String destination, String aggregate, String... otherKeys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersectionAsync(sortedSetAggregate, otherKeys);
    }


    /**
     * Z inter store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    public int zInterStore(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).intersection(keyWithWeight);
    }


    /**
     * Z inter store async completable future.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    public CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        return zInterStoreRFuture(destination, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreRFuture(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).intersectionAsync(keyWithWeight);
    }


    /**
     * Z inter store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    public int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, keyWithWeight);
    }


    /**
     * Z inter store async completable future.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        return zInterStoreRFuture(destination, aggregate, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreRFuture(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersectionAsync(sortedSetAggregate, keyWithWeight);
    }


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
    public int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).count(fromElement, fromInclusive, toElement, toInclusive);
    }


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
    public CompletableFuture<Integer> zLexCountAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return zLexCountRFuture(key, fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zLexCountRFuture(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countAsync(fromElement, fromInclusive, toElement, toInclusive);
    }


    /**
     * Z lex count head int.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the int
     */
    public int zLexCountHead(String key, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countHead(toElement, toInclusive);
    }


    /**
     * Z lex count head async completable future.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the completable future
     */
    public CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive) {
        return zLexCountHeadRFuture(key, toElement, toInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zLexCountHeadRFuture(String key, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countHeadAsync(toElement, toInclusive);
    }


    /**
     * Z lex count tail int.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @return the int
     */
    public int zLexCountTail(String key, String fromElement, boolean fromInclusive) {
        return getRLexSortedSet(key).countTail(fromElement, fromInclusive);
    }


    /**
     * Z lex count tail async completable future.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @return the completable future
     */
    public CompletableFuture<Integer> zLexCountTailAsync(String key, String fromElement, boolean fromInclusive) {
        return zLexCountTailRFuture(key, fromElement, fromInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zLexCountTailRFuture(String key, String fromElement, boolean fromInclusive) {
        return getRLexSortedSet(key).countTailAsync(fromElement, fromInclusive);
    }


    /**
     * Zm pop optional.
     *
     * @param key the key
     * @param min the min
     * @return the optional
     */
    public Optional<Object> zmPop(String key, boolean min) {
        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst();
        } else {
            object = getRScoredSortedSet(key).pollLast();
        }
        return Optional.ofNullable(object);
    }


    /**
     * Zm pop async completable future.
     *
     * @param key the key
     * @param min the min
     * @return the completable future
     */
    public CompletableFuture<Object> zmPopAsync(String key, boolean min) {
        return zmPopRFuture(key, min).toCompletableFuture();
    }

    private RFuture<Object> zmPopRFuture(String key, boolean min) {
        RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstAsync();
        } else {
            object = getRScoredSortedSet(key).pollLastAsync();
        }
        return object;
    }


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
    public Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }


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
    public CompletableFuture<Object> zmPopAsync(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        return zmPopRFuture(key, min, timeout, unit, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> zmPopRFuture(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAnyAsync(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
        }
        return object;
    }


    /**
     * Z pop max optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> zPopMax(String key) {
        return Optional.ofNullable(getRScoredSortedSet(key).pollLast());
    }


    /**
     * Z pop max async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> zPopMaxAsync(String key) {
        return zPopMaxRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> zPopMaxRFuture(String key) {
        return getRScoredSortedSet(key).pollLastAsync();
    }


    /**
     * Z pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    public Collection<Object> zPopMax(String key, int count) {
        return getRScoredSortedSet(key).pollLast(count);
    }


    /**
     * Z pop max async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count) {
        return zPopMaxRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zPopMaxRFuture(String key, int count) {
        return getRScoredSortedSet(key).pollLastAsync(count);
    }


    /**
     * Z pop min optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> zPopMin(String key) {
        return Optional.ofNullable(getRScoredSortedSet(key).pollFirst());
    }


    /**
     * Z pop min async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> zPopMinAsync(String key) {
        return zPopMinRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> zPopMinRFuture(String key) {
        return getRScoredSortedSet(key).pollFirstAsync();
    }


    /**
     * Z pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    public Collection<Object> zPopMin(String key, int count) {
        return getRScoredSortedSet(key).pollFirst(count);
    }


    /**
     * Z pop min async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count) {
        return zPopMinRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zPopMinRFuture(String key, int count) {
        return getRScoredSortedSet(key).pollFirstAsync(count);
    }


    /**
     * Z rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> zRandMember(String key) {
        return Optional.ofNullable(getRScoredSortedSet(key).random());
    }


    /**
     * Z rand member async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> zRandMemberAsync(String key) {
        return zRandMemberRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> zRandMemberRFuture(String key) {
        return getRScoredSortedSet(key).randomAsync();
    }


    /**
     * Z rand member collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    public Collection<Object> zRandMember(String key, int count) {
        return getRScoredSortedSet(key).random(count);
    }


    /**
     * Z rand member async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count) {
        return zRandMemberRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRandMemberRFuture(String key, int count) {
        return getRScoredSortedSet(key).randomAsync(count);
    }


    /**
     * Z range collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    public Collection<Object> zRange(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRange(startIndex, endIndex);
    }


    /**
     * Z range async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, int startIndex, int endIndex) {
        return zRangeRFuture(key, startIndex, endIndex).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeRFuture(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeAsync(startIndex, endIndex);
    }


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
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


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
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return zRangeRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


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
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }


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
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return zRangeRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeAsync(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }


    /**
     * Z range reversed collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    public Collection<Object> zRangeReversed(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeReversed(startIndex, endIndex);
    }


    /**
     * Z range reversed async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex) {
        return zRangeReversedRFuture(key, startIndex, endIndex).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeReversedRFuture(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startIndex, endIndex);
    }


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
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


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
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return zRangeReversedRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeReversedRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


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
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }


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
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return zRangeReversedRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeReversedRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }


    /**
     * Z rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    public Optional<Integer> zRank(String key, Object member) {
        return Optional.ofNullable(getRScoredSortedSet(key).rank(member));
    }


    /**
     * Z rank async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    public CompletableFuture<Integer> zRankAsync(String key, Object member) {
        return zRankRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Integer> zRankRFuture(String key, Object member) {
        return getRScoredSortedSet(key).rankAsync(member);
    }


    /**
     * Z rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    public boolean zRem(String key, Collection<?> members) {
        return getRScoredSortedSet(key).removeAll(members);
    }


    /**
     * Z rem async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members) {
        return zRemRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Boolean> zRemRFuture(String key, Collection<?> members) {
        return getRScoredSortedSet(key).removeAllAsync(members);
    }


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
    public Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        final int removeRange = getRLexSortedSet(key).removeRange(fromElement, fromInclusive, toElement, toInclusive);
        return Optional.of(removeRange);
    }


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
    public CompletableFuture<Integer> zRemRangeByLexAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return zRemRangeByLexRFuture(key, fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zRemRangeByLexRFuture(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).removeRangeAsync(fromElement, fromInclusive, toElement, toInclusive);
    }


    /**
     * Z rem range by rank optional.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the optional
     */
    public Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex) {
        return Optional.of(getRScoredSortedSet(key).removeRangeByRank(startIndex, endIndex));
    }


    /**
     * Z rem range by rank async completable future.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the completable future
     */
    public CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex) {
        return zRemRangeByRankRFuture(key, startIndex, endIndex).toCompletableFuture();
    }

    private RFuture<Integer> zRemRangeByRankRFuture(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).removeRangeByRankAsync(startIndex, endIndex);
    }


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
    public Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return Optional.of(getRScoredSortedSet(key).removeRangeByScore(startScore, startScoreInclusive, endScore, endScoreInclusive));
    }


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
    public CompletableFuture<Integer> zRemRangeByScoreAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return zRemRangeByScoreRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zRemRangeByScoreRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).removeRangeByScoreAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }


    /**
     * Z rev rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    public Optional<Integer> zRevRank(String key, Object member) {
        return Optional.ofNullable(getRScoredSortedSet(key).revRank(member));
    }


    /**
     * Z rev rank async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    public CompletableFuture<Integer> zRevRankAsync(String key, Object member) {
        return zRevRankRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Integer> zRevRankRFuture(String key, Object member) {
        return getRScoredSortedSet(key).revRankAsync(member);
    }


    /**
     * Z scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @return the iterator
     */
    public Iterator<Object> zScan(String key, String pattern) {
        return getRScoredSortedSet(key).iterator(pattern);
    }


    /**
     * Z scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @param count   the count
     * @return the iterator
     */
    public Iterator<Object> zScan(String key, String pattern, int count) {
        return getRScoredSortedSet(key).iterator(pattern, count);
    }


    /**
     * Z score list.
     *
     * @param key     the key
     * @param members the members
     * @return the list
     */
    public List<Double> zScore(String key, List<Object> members) {
        return getRScoredSortedSet(key).getScore(members);
    }


    /**
     * Z score async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members) {
        return zScoreRFuture(key, members).toCompletableFuture();
    }

    private RFuture<List<Double>> zScoreRFuture(String key, List<Object> members) {
        return getRScoredSortedSet(key).getScoreAsync(members);
    }


    /**
     * Z union collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    public Collection<Object> zUnion(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readUnion(otherKeys);
    }


    /**
     * Z union async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys) {
        return zUnionRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zUnionRFuture(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readUnionAsync(otherKeys);
    }


    /**
     * Z union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    public int zUnionStore(String destination, String... keys) {
        return getRScoredSortedSet(destination).union(keys);
    }


    /**
     * Z union store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys) {
        return zUnionStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreRFuture(String destination, String... keys) {
        return getRScoredSortedSet(destination).unionAsync(keys);
    }


    /**
     * Z union store aggregate int.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param keys        the keys
     * @return the int
     */
    public int zUnionStoreAggregate(String destination, String aggregate, String... keys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keys);
    }


    /**
     * Z union store aggregate async completable future.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param keys        the keys
     * @return the completable future
     */
    public CompletableFuture<Integer> zUnionStoreAggregateAsync(String destination, String aggregate, String... keys) {
        return zUnionStoreAggregateRFuture(destination, aggregate, keys).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreAggregateRFuture(String destination, String aggregate, String... keys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).unionAsync(sortedSetAggregate, keys);
    }


    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    public int zUnionStore(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).union(keyWithWeight);
    }


    /**
     * Z union store async completable future.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        return zUnionStoreRFuture(destination, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreRFuture(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).unionAsync(keyWithWeight);
    }


    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    public int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keyWithWeight);
    }


    /**
     * Z union store async completable future.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the completable future
     */
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        return zUnionStoreRFuture(destination, aggregate, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreRFuture(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).unionAsync(sortedSetAggregate, keyWithWeight);
    }

    private <V> RScoredSortedSet<V> getRScoredSortedSet(String key) {
        return getDataSource().getScoredSortedSet(key, stringCodec);
    }

    private RLexSortedSet getRLexSortedSet(String key) {
        return getDataSource().getLexSortedSet(key);
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
