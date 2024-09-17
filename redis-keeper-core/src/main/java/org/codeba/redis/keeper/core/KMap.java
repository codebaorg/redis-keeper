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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The interface K map.
 */
public interface KMap extends KMapAsync {

    /**
     * H del map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<String, Boolean> hDel(String key, String... fields);

    /**
     * H exists map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<String, Boolean> hExists(String key, String... fields);

    /**
     * H get optional.
     *
     * @param key   the key
     * @param field the field
     * @return the optional
     */
    Optional<Object> hGet(String key, String field);

    /**
     * H get all map.
     *
     * @param key the key
     * @return the map
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * H incr by object.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the object
     */
    Object hIncrBy(String key, String field, Number value);

    /**
     * H keys collection.
     *
     * @param key the key
     * @return the collection
     */
    Collection<Object> hKeys(String key);

    /**
     * H len int.
     *
     * @param key the key
     * @return the int
     */
    int hLen(String key);

    /**
     * Hm get map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<Object, Object> hmGet(String key, Set<Object> fields);

    /**
     * Hm set.
     *
     * @param key   the key
     * @param kvMap the kv map
     */
    void hmSet(String key, Map<?, ?> kvMap);

    /**
     * H set.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hSet(String key, String field, Object value);

    /**
     * H rand field set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> hRandField(String key, int count);

    /**
     * H rand field with values map.
     *
     * @param key   the key
     * @param count the count
     * @return the map
     */
    Map<Object, Object> hRandFieldWithValues(String key, int count);

    /**
     * H scan iterator.
     *
     * @param key        the key
     * @param keyPattern the key pattern
     * @return the iterator
     */
    Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern);

    /**
     * H scan iterator.
     *
     * @param key        the key
     * @param keyPattern the key pattern
     * @param count      the count
     * @return the iterator
     */
    Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count);

    /**
     * H set nx.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hSetNX(String key, String field, Object value);

    /**
     * H str len int.
     *
     * @param key   the key
     * @param field the field
     * @return the int
     */
    int hStrLen(String key, String field);

    /**
     * H va ls collection.
     *
     * @param key the key
     * @return the collection
     */
    Collection<Object> hVALs(String key);


}
