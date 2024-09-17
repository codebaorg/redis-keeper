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

/**
 * The interface K hyper log log.
 */
public interface KHyperLogLog extends KHyperLogLogAsync {

    /**
     * Pf add boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    boolean pfAdd(String key, Collection<Object> elements);

    /**
     * Pf count long.
     *
     * @param key the key
     * @return the long
     */
    long pfCount(String key);

    /**
     * Pf count long.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the long
     */
    long pfCount(String key, String... otherKeys);

    /**
     * Pf merge.
     *
     * @param destKey    the dest key
     * @param sourceKeys the source keys
     */
    void pfMerge(String destKey, String... sourceKeys);

}
