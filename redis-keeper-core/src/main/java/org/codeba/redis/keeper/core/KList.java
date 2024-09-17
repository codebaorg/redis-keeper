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

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The interface K list.
 */
public interface KList extends KListAsync {
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
     * Bl pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> blPop(String key);

    /**
     * Bl pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> blPop(String key, int count);

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
     * Br pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> brPop(String key);

    /**
     * Br pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> brPop(String key, int count);

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
     * L index optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    Optional<Object> lIndex(String key, int index);

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
     * L len int.
     *
     * @param key the key
     * @return the int
     */
    int llen(String key);

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
     * L pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> lPop(String key, int count);

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
     * L range list.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the list
     */
    List<Object> lRange(String key, int fromIndex, int toIndex);

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
     * L rem optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    Optional<Object> lRem(String key, int index);

    /**
     * L set.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     */
    void lSet(String key, int index, Object element);

    /**
     * L trim.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     */
    void lTrim(String key, int fromIndex, int toIndex);

    /**
     * R pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> rPop(String key, int count);

    /**
     * R pop l push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @return the optional
     */
    Optional<Object> rPopLPush(String source, String destination);

    /**
     * R push boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    boolean rPush(String key, Object... elements);

    /**
     * R push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int rPushX(String key, Object... elements);

}
