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

import org.codeba.redis.keeper.core.KSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * The type K redisson set.
 */
class KRedissonSet extends KRedissonSetAsync implements KSet {
    /**
     * Instantiates a new K redisson set.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonSet(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    @Override
    public boolean sAdd(String key, Object member) {
        return getSet(key).add(member);
    }

    @Override
    public boolean sAdd(String key, Collection<?> members) {
        return getSet(key).addAll(members);
    }

    @Override
    public int sCard(String key) {
        return getSet(key).size();
    }

    @Override
    public Set<Object> sDiff(String key, String... otherKeys) {
        return getSet(key).readDiff(otherKeys);
    }

    @Override
    public int sDiffStore(String destination, String... keys) {
        return getSet(destination).diff(keys);
    }

    @Override
    public Set<Object> sInter(String key, String... otherKeys) {
        return getSet(key).readIntersection(otherKeys);
    }

    @Override
    public int sInterStore(String destination, String... keys) {
        return getSet(destination).intersection(keys);
    }

    @Override
    public boolean sIsMember(String key, Object member) {
        return getSet(key).contains(member);
    }

    @Override
    public Set<Object> sMembers(String key) {
        return getSet(key).readAll();
    }

    @Override
    public boolean sMove(String source, String destination, Object member) {
        return getSet(source).move(destination, member);
    }

    @Override
    public Optional<Object> sPop(String key) {
        return Optional.ofNullable(getSet(key).removeRandom());
    }

    @Override
    public Set<Object> sPop(String key, int count) {
        return getSet(key).removeRandom(count);
    }

    @Override
    public Optional<Object> sRandMember(String key) {
        return Optional.ofNullable(getSet(key).random());
    }

    @Override
    public Set<Object> sRandMember(String key, int count) {
        return getSet(key).random(count);
    }

    @Override
    public boolean sRem(String key, Collection<?> members) {
        return getSet(key).removeAll(members);
    }

    @Override
    public Iterator<Object> sScan(String key) {
        return getSet(key).iterator();
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern) {
        return getSet(key).iterator(pattern);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern, int count) {
        return getSet(key).iterator(pattern, count);
    }

    @Override
    public Set<Object> sUnion(String key, String... otherKeys) {
        return getSet(key).readUnion(otherKeys);
    }

    @Override
    public int sUnionStore(String destination, String... keys) {
        return getSet(destination).union(keys);
    }

    /**
     * Gets set.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the set
     */
    private <V> RSet<V> getSet(String key) {
        return getRedissonClient().getSet(key, getCodec());
    }

}
