
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


import java.util.Map;

/**
 * The type Local cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author codeba
 */
public interface LocalCache<K, V> {

    /**
     * Return the underlying native cache provider.
     *
     * @param name the name
     * @return the native cache
     */
    Object getNativeCache(String name);

    /**
     * Returns the value associated with the {@code key} in this cache, or {@code null} if there is no
     * cached value for the {@code key}.
     *
     * @param name the name
     * @param key  the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this cache contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     */
    V getIfPresent(String name, Object key);


    /**
     * Returns a map of the values associated with the {@code keys} in this cache. The returned map
     * will only contain entries which are already present in the cache.
     * <p>
     * Note that duplicate elements in {@code keys}, as determined by {@link Object#equals}, will be
     * ignored.
     *
     * @param name the name
     * @param keys the keys whose associated values are to be returned
     * @return the unmodifiable mapping of keys to values for the specified keys found in this cache
     * @throws NullPointerException if the specified collection is null or contains a null element
     */
    Map<K, V> getAllPresent(String name, Iterable<?> keys);

    /**
     * Associates the {@code value} with the {@code key} in this cache. If the cache previously
     * contained a value associated with the {@code key}, the old value is replaced by the new
     * {@code value}.
     * <p>
     * Prefer  when using the conventional "if cached, return; otherwise
     * create, cache and return" pattern.
     *
     * @param name  the name
     * @param key   the key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws NullPointerException if the specified key or value is null
     */
    void put(String name, K key, V value);

    /**
     * Discards any cached value for the {@code key}. The behavior of this operation is undefined for
     * an entry that is being loaded (or reloaded) and is otherwise not present.
     *
     * @param name the name
     * @param key  the key whose mapping is to be removed from the cache
     * @throws NullPointerException if the specified key is null
     */
    void invalidate(String name, Object key);

    /**
     * Discards any cached values for the {@code keys}. The behavior of this operation is undefined
     * for an entry that is being loaded (or reloaded) and is otherwise not present.
     *
     * @param name the name
     * @param keys the keys whose associated values are to be removed
     * @throws NullPointerException if the specified collection is null or contains a null element
     */
    void invalidateAll(String name, Iterable<?> keys);

    /**
     * Discards all entries in the cache. The behavior of this operation is undefined for an entry
     * that is being loaded (or reloaded) and is otherwise not present.
     *
     * @param name the name
     */
    void invalidateAll(String name);

}
