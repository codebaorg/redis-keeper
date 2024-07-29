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
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The type R sets.
 */
class RSets {

    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R sets.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RSets(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * S add boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    public boolean sAdd(String key, Object member) {
        return getSet(key).add(member);
    }


    /**
     * S add async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    public CompletableFuture<Boolean> sAddAsync(String key, Object member) {
        return sAddRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Boolean> sAddRFuture(String key, Object member) {
        return getSet(key).addAsync(member);
    }


    /**
     * S add boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    public boolean sAdd(String key, Collection<?> members) {
        return getSet(key).addAll(members);
    }


    /**
     * S add async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members) {
        return sAddRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Boolean> sAddRFuture(String key, Collection<?> members) {
        return getSet(key).addAllAsync(members);
    }


    /**
     * S card int.
     *
     * @param key the key
     * @return the int
     */
    public int sCard(String key) {
        return getSet(key).size();
    }


    /**
     * S card async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Integer> sCardAsync(String key) {
        return sCardRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> sCardRFuture(String key) {
        return getSet(key).sizeAsync();
    }


    /**
     * S diff set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    public Set<Object> sDiff(String key, String... otherKeys) {
        return getSet(key).readDiff(otherKeys);
    }


    /**
     * S diff async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys) {
        return sDiffRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Set<Object>> sDiffRFuture(String key, String... otherKeys) {
        return getSet(key).readDiffAsync(otherKeys);
    }


    /**
     * S diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    public int sDiffStore(String destination, String... keys) {
        return getSet(destination).diff(keys);
    }


    /**
     * S diff store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    public CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys) {
        return sDiffStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> sDiffStoreRFuture(String destination, String... keys) {
        return getSet(destination).diffAsync(keys);
    }


    /**
     * S inter set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    public Set<Object> sInter(String key, String... otherKeys) {
        return getSet(key).readIntersection(otherKeys);
    }


    /**
     * S inter async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys) {
        return sInterRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Set<Object>> sInterRFuture(String key, String... otherKeys) {
        return getSet(key).readIntersectionAsync(otherKeys);
    }


    /**
     * S inter store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    public int sInterStore(String destination, String... keys) {
        return getSet(destination).intersection(keys);
    }


    /**
     * S inter store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    public CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys) {
        return sInterStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> sInterStoreRFuture(String destination, String... keys) {
        return getSet(destination).intersectionAsync(keys);
    }


    /**
     * S is member boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    public boolean sIsMember(String key, Object member) {
        return getSet(key).contains(member);
    }


    /**
     * S is member async completable future.
     *
     * @param key    the key
     * @param member the member
     * @return the completable future
     */
    public CompletableFuture<Boolean> sIsMemberAsync(String key, Object member) {
        return sIsMemberRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Boolean> sIsMemberRFuture(String key, Object member) {
        return getSet(key).containsAsync(member);
    }


    /**
     * S members set.
     *
     * @param key the key
     * @return the set
     */
    public Set<Object> sMembers(String key) {
        return getSet(key).readAll();
    }


    /**
     * S members async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> sMembersAsync(String key) {
        return sMembersRFuture(key).toCompletableFuture();
    }

    private RFuture<Set<Object>> sMembersRFuture(String key) {
        return getSet(key).readAllAsync();
    }


    /**
     * S move boolean.
     *
     * @param source      the source
     * @param destination the destination
     * @param member      the member
     * @return the boolean
     */
    public boolean sMove(String source, String destination, Object member) {
        return getSet(source).move(destination, member);
    }


    /**
     * S move async completable future.
     *
     * @param source      the source
     * @param destination the destination
     * @param member      the member
     * @return the completable future
     */
    public CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member) {
        return sMoveRFuture(source, destination, member).toCompletableFuture();
    }

    private RFuture<Boolean> sMoveRFuture(String source, String destination, Object member) {
        return getSet(source).moveAsync(destination, member);
    }


    /**
     * S pop optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> sPop(String key) {
        return Optional.ofNullable(getSet(key).removeRandom());
    }


    /**
     * S pop async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> sPopAsync(String key) {
        return sPopRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> sPopRFuture(String key) {
        return getSet(key).removeRandomAsync();
    }


    /**
     * S pop set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    public Set<Object> sPop(String key, int count) {
        return getSet(key).removeRandom(count);
    }


    /**
     * S pop async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> sPopAsync(String key, int count) {
        return sPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Set<Object>> sPopRFuture(String key, int count) {
        return getSet(key).removeRandomAsync(count);
    }


    /**
     * S rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    public Optional<Object> sRandMember(String key) {
        return Optional.ofNullable(getSet(key).random());
    }


    /**
     * S rand member async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Object> sRandMemberAsync(String key) {
        return sRandMemberRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> sRandMemberRFuture(String key) {
        return getSet(key).randomAsync();
    }


    /**
     * S rand member set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    public Set<Object> sRandMember(String key, int count) {
        return getSet(key).random(count);
    }


    /**
     * S rand member async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count) {
        return sRandMemberRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Set<Object>> sRandMemberRFuture(String key, int count) {
        return getSet(key).randomAsync(count);
    }


    /**
     * S rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    public boolean sRem(String key, Collection<?> members) {
        return getSet(key).removeAll(members);
    }


    /**
     * S rem async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members) {
        return sRemRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Boolean> sRemRFuture(String key, Collection<?> members) {
        return getSet(key).removeAllAsync(members);
    }

    /**
     * S scan iterator.
     *
     * @param key the key
     * @return the iterator
     */
    public Iterator<Object> sScan(String key) {
        return getSet(key).iterator();
    }

    /**
     * S scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @return the iterator
     */
    public Iterator<Object> sScan(String key, String pattern) {
        return getSet(key).iterator(pattern);
    }


    /**
     * S scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @param count   the count
     * @return the iterator
     */
    public Iterator<Object> sScan(String key, String pattern, int count) {
        return getSet(key).iterator(pattern, count);
    }


    /**
     * S union set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    public Set<Object> sUnion(String key, String... otherKeys) {
        return getSet(key).readUnion(otherKeys);
    }


    /**
     * S union async completable future.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys) {
        return sUnionRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Set<Object>> sUnionRFuture(String key, String... otherKeys) {
        return getSet(key).readUnionAsync(otherKeys);
    }

    /**
     * S union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    public int sUnionStore(String destination, String... keys) {
        return getSet(destination).union(keys);
    }


    /**
     * S union store async completable future.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the completable future
     */
    public CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys) {
        return sUnionStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> sUnionStoreRFuture(String destination, String... keys) {
        return getSet(destination).unionAsync(keys);
    }

    private <V> RSet<V> getSet(String key) {
        return getDataSource().getSet(key, stringCodec);
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
