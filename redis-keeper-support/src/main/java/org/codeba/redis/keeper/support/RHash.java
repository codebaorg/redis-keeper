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
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The type R hash.
 */
class RHash {
    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R hash.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RHash(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * H del map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    public Map<String, Boolean> hDel(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);

        final Map<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            final long fastRemove = rMap.fastRemove(field);
            resultMap.put(field, 0 != fastRemove);
        }

        return resultMap;
    }


    /**
     * H del async completable future.
     *
     * @param key    the key
     * @param fields the fields
     * @return the completable future
     */
    public CompletableFuture<Long> hDelAsync(String key, String... fields) {
        return hDelRFuture(key, fields).toCompletableFuture();
    }

    private RFuture<Long> hDelRFuture(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);
        return rMap.fastRemoveAsync(fields);
    }


    /**
     * H exists map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    public Map<String, Boolean> hExists(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKey(field));
        }

        return resultMap;
    }


    /**
     * H exists async map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    public Map<String, CompletableFuture<Boolean>> hExistsAsync(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, CompletableFuture<Boolean>> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKeyAsync(field).toCompletableFuture());
        }

        return resultMap;
    }


    /**
     * H get optional.
     *
     * @param key   the key
     * @param field the field
     * @return the optional
     */
    public Optional<Object> hGet(String key, String field) {
        return Optional.ofNullable(getMap(key).get(field));
    }


    /**
     * H get async completable future.
     *
     * @param key   the key
     * @param field the field
     * @return the completable future
     */
    public CompletableFuture<Object> hGetAsync(String key, String field) {
        return hGetRFuture(key, field).toCompletableFuture();
    }

    private RFuture<Object> hGetRFuture(String key, String field) {
        return getMap(key).getAsync(field);
    }


    /**
     * H get all map.
     *
     * @param key the key
     * @return the map
     */
    public Map<Object, Object> hGetAll(String key) {
        return getMap(key).readAllMap();
    }


    /**
     * H get all async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Object>> hGetAllAsync(String key) {
        return hGetAllRFuture(key).toCompletableFuture();
    }

    private RFuture<Map<Object, Object>> hGetAllRFuture(String key) {
        final RMap<Object, Object> rMap = getMap(key);
        return rMap.readAllMapAsync();
    }


    /**
     * H incr by object.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the object
     */
    public Object hIncrBy(String key, String field, Number value) {
        return getMap(key).addAndGet(field, value);
    }


    /**
     * H incr by async completable future.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the completable future
     */
    public CompletableFuture<Object> hIncrByAsync(String key, String field, Number value) {
        return hIncrByRFuture(key, field, value).toCompletableFuture();
    }

    private RFuture<Object> hIncrByRFuture(String key, String field, Number value) {
        return getMap(key).addAndGetAsync(field, value);
    }


    /**
     * H keys collection.
     *
     * @param key the key
     * @return the collection
     */
    public Collection<Object> hKeys(String key) {
        return getMap(key).readAllKeySet();
    }


    /**
     * H keys async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> hKeysAsync(String key) {
        return hKeysRFuture(key).toCompletableFuture();
    }

    private RFuture<Set<Object>> hKeysRFuture(String key) {
        return getMap(key).readAllKeySetAsync();
    }


    /**
     * H len int.
     *
     * @param key the key
     * @return the int
     */
    public int hLen(String key) {
        return getMap(key).size();
    }


    /**
     * H len async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Integer> hLenAsync(String key) {
        return hLenRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> hLenRFuture(String key) {
        return getMap(key).sizeAsync();
    }


    /**
     * Hm get map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    public Map<Object, Object> hmGet(String key, Set<Object> fields) {
        if (null == fields || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        return getMap(key).getAll(fields);
    }


    /**
     * Hm get async completable future.
     *
     * @param key    the key
     * @param fields the fields
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Object>> hmGetAsync(String key, Set<Object> fields) {
        return hmGetRFuture(key, fields).toCompletableFuture();
    }

    private RFuture<Map<Object, Object>> hmGetRFuture(String key, Set<Object> fields) {
        return getMap(key).getAllAsync(fields);
    }


    /**
     * Hm set.
     *
     * @param key   the key
     * @param kvMap the kv map
     */
    public void hmSet(String key, Map<?, ?> kvMap) {
        getMap(key).putAll(kvMap, 100);
    }


    /**
     * Hm set async completable future.
     *
     * @param key   the key
     * @param kvMap the kv map
     * @return the completable future
     */
    public CompletableFuture<Void> hmSetAsync(String key, Map<?, ?> kvMap) {
        return hmSetRFuture(key, kvMap).toCompletableFuture();
    }

    private RFuture<Void> hmSetRFuture(String key, Map<?, ?> kvMap) {
        return getMap(key).putAllAsync(kvMap, 100);
    }


    /**
     * H rand field set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    public Set<Object> hRandField(String key, int count) {
        return getMap(key).randomKeys(count);
    }


    /**
     * H rand fields async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Set<Object>> hRandFieldsAsync(String key, int count) {
        return hRandFieldsRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Set<Object>> hRandFieldsRFuture(String key, int count) {
        return getMap(key).randomKeysAsync(count);
    }


    /**
     * H rand field with values map.
     *
     * @param key   the key
     * @param count the count
     * @return the map
     */
    public Map<Object, Object> hRandFieldWithValues(String key, int count) {
        return getMap(key).randomEntries(count);
    }


    /**
     * H rand field with values async completable future.
     *
     * @param key   the key
     * @param count the count
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Object>> hRandFieldWithValuesAsync(String key, int count) {
        return hRandFieldWithValuesRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Map<Object, Object>> hRandFieldWithValuesRFuture(String key, int count) {
        return getMap(key).randomEntriesAsync(count);
    }


    /**
     * H scan iterator.
     *
     * @param key        the key
     * @param keyPattern the key pattern
     * @return the iterator
     */
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern) {
        return getMap(key).entrySet(keyPattern).iterator();
    }


    /**
     * H scan iterator.
     *
     * @param key        the key
     * @param keyPattern the key pattern
     * @param count      the count
     * @return the iterator
     */
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count) {
        return getMap(key).entrySet(keyPattern, count).iterator();
    }


    /**
     * H set.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    public void hSet(String key, String field, Object value) {
        getMap(key).fastPut(field, value);
    }


    /**
     * H set async completable future.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the completable future
     */
    public CompletableFuture<Boolean> hSetAsync(String key, String field, Object value) {
        return hSetRFuture(key, field, value).toCompletableFuture();
    }

    private RFuture<Boolean> hSetRFuture(String key, String field, Object value) {
        return getMap(key).fastPutAsync(field, value);
    }


    /**
     * H set nx.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    public void hSetNX(String key, String field, Object value) {
        getMap(key).fastPutIfAbsent(field, value);
    }


    /**
     * H set nx async completable future.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the completable future
     */
    public CompletableFuture<Boolean> hSetNXAsync(String key, String field, Object value) {
        return hSetNXRFuture(key, field, value).toCompletableFuture();
    }

    private RFuture<Boolean> hSetNXRFuture(String key, String field, Object value) {
        return getMap(key).fastPutIfAbsentAsync(field, value);
    }


    /**
     * H str len int.
     *
     * @param key   the key
     * @param field the field
     * @return the int
     */
    public int hStrLen(String key, String field) {
        return getMap(key).valueSize(field);
    }


    /**
     * H str len async completable future.
     *
     * @param key   the key
     * @param field the field
     * @return the completable future
     */
    public CompletableFuture<Integer> hStrLenAsync(String key, String field) {
        return hStrLenRFuture(key, field).toCompletableFuture();
    }

    private RFuture<Integer> hStrLenRFuture(String key, String field) {
        return getMap(key).valueSizeAsync(field);
    }


    /**
     * H va ls collection.
     *
     * @param key the key
     * @return the collection
     */
    public Collection<Object> hVALs(String key) {
        return getMap(key).readAllValues();
    }


    /**
     * H va ls async completable future.
     *
     * @param key the key
     * @return the completable future
     */
    public CompletableFuture<Collection<Object>> hVALsAsync(String key) {
        return hVALsRFuture(key).toCompletableFuture();
    }

    private RFuture<Collection<Object>> hVALsRFuture(String key) {
        return getMap(key).readAllValuesAsync();
    }

    private <K, V> RMap<K, V> getMap(String key) {
        return getDataSource().getMap(key, stringCodec);
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
