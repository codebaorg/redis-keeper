
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
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

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
     * Returns the value associated with the {@code key} in this cache, obtaining that value from the
     * {@code mappingFunction} if necessary. This method provides a simple substitute for the
     * conventional "if cached, return; otherwise create, cache and return" pattern.
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value
     * using the given mapping function and enters it into this cache unless {@code null}. The entire
     * method invocation is performed atomically, so the function is applied at most once per key.
     * Some attempted update operations on this cache by other threads may be blocked while the
     * computation is in progress, so the computation should be short and simple, and must not attempt
     * to update any other mappings of this cache.
     * <p>
     * <b>Warning:</b> {@code mappingFunction} <b>must not</b>
     * attempt to update any other mappings of this cache.
     *
     * @param name            the name
     * @param key             the key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the specified key, or null if the computed value is null
     * @throws NullPointerException  if the specified key or mappingFunction is null
     * @throws IllegalStateException if the computation detectably attempts a recursive update to this                               cache that would otherwise never complete
     * @throws RuntimeException      or Error if the mappingFunction does so, in which case the mapping is                               left unestablished
     */
    V get(String name, K key, Function<? super K, ? extends V> mappingFunction);

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
     * Returns a map of the values associated with the {@code keys}, creating or retrieving those
     * values if necessary. The returned map contains entries that were already cached, combined with
     * the newly loaded entries; it will never contain null keys or values.
     * <p>
     * A single request to the {@code mappingFunction} is performed for all keys which are not already
     * present in the cache. All entries returned by {@code mappingFunction} will be stored in the
     * cache, over-writing any previously cached values. If another call to {@link #get} tries to load
     * the value for a key in {@code keys}, implementations may either have that thread load the entry
     * or simply wait for this thread to finish and return the loaded value. In the case of
     * overlapping non-blocking loads, the last load to complete will replace the existing entry. Note
     * that multiple threads can concurrently load values for distinct keys. Any loaded values for
     * keys that were not specifically requested will not be returned, but will be stored in the
     * cache.
     * <p>
     * Note that duplicate elements in {@code keys}, as determined by {@link Object#equals}, will be
     * ignored.
     *
     * @param name            the name
     * @param keys            the keys whose associated values are to be returned
     * @param mappingFunction the function to compute the values
     * @return an unmodifiable mapping of keys to values for the specified keys in this cache
     * @throws NullPointerException if the specified collection is null or contains a null element, or                              if the map returned by the mappingFunction is null
     * @throws RuntimeException     or Error if the mappingFunction does so, in which case the mapping is                              left unestablished
     */
    Map<K, V> getAll(String name,
                     Iterable<? extends K> keys,
                     Function<? super Set<? extends K>, ? extends Map<? extends K, ? extends V>>
                             mappingFunction);

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
     * Copies all of the mappings from the specified map to the cache. The effect of this call is
     * equivalent to that of calling {@code put(k, v)} on this map once for each mapping from key
     * {@code k} to value {@code v} in the specified map. The behavior of this operation is undefined
     * if the specified map is modified while the operation is in progress.
     *
     * @param name the name
     * @param map  the mappings to be stored in this cache
     * @throws NullPointerException if the specified map is null or the specified map contains null                              keys or values
     */
    void putAll(String name, Map<? extends K, ? extends V> map);

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

    /**
     * Returns a view of the entries stored in this cache as a thread-safe map. Modifications made to
     * the map directly affect the cache.
     * <p>
     * A computation operation, such as {@link ConcurrentMap#compute}, performs the entire method
     * invocation atomically, so the function is applied at most once per key. Some attempted update
     * operations by other threads may be blocked while computation is in progress. The computation
     * must not attempt to update any other mappings of this cache.
     * <p>
     * Iterators from the returned map are at least <i>weakly consistent</i>: they are safe for
     * concurrent use, but if the cache is modified (including by eviction) after the iterator is
     * created, it is undefined which of the changes (if any) will be reflected in that iterator.
     *
     * @param name the name
     * @return a thread-safe view of this cache supporting all of the optional {@link Map} operations
     */
    ConcurrentMap<K, V> asMap(String name);

}
