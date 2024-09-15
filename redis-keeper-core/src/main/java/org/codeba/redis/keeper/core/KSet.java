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
import java.util.Optional;
import java.util.Set;

public interface KSet extends KSetAsync {
    /**
     * S add boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    boolean sAdd(String key, Object member);

    /**
     * S add boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean sAdd(String key, Collection<?> members);

    /**
     * S card int.
     *
     * @param key the key
     * @return the int
     */
    int sCard(String key);

    /**
     * S diff set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sDiff(String key, String... otherKeys);

    /**
     * S diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sDiffStore(String destination, String... keys);

    /**
     * S inter set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sInter(String key, String... otherKeys);

    /**
     * S inter store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sInterStore(String destination, String... keys);

    /**
     * S is member boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    boolean sIsMember(String key, Object member);

    /**
     * S members set.
     *
     * @param key the key
     * @return the set
     */
    Set<Object> sMembers(String key);

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
     * S pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> sPop(String key);

    /**
     * S pop set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> sPop(String key, int count);

    /**
     * S rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> sRandMember(String key);

    /**
     * S rand member set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> sRandMember(String key, int count);

    /**
     * S rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean sRem(String key, Collection<?> members);

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
     * S union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sUnionStore(String destination, String... keys);

}
