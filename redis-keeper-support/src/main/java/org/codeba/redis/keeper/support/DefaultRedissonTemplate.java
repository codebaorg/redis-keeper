/*
 * Copyright (c) 2024-2025, redis-keeper (mimang447@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.codeba.redis.keeper.support;

import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.Redisson;
import org.redisson.api.GeoOrder;
import org.redisson.api.GeoPosition;
import org.redisson.api.GeoUnit;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBitSet;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RDeque;
import org.redisson.api.RGeo;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RKeys;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RSet;
import org.redisson.api.RType;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.api.queue.DequeMoveArgs;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Default redisson template.
 *
 * @author codeba
 */
public class DefaultRedissonTemplate implements RedissonTemplate, CacheTemplate {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String connectionInfo;
    private RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new Default redisson template.
     *
     * @param cacheKeeperConfig the cache keeper config
     */
    public DefaultRedissonTemplate(CacheKeeperConfig cacheKeeperConfig) {
        this.invokeParamsPrint = cacheKeeperConfig.isInvokeParamsPrint();
        this.connectionInfo = Utils.getConnectionInfo(cacheKeeperConfig.getConfig());

        try {
            this.redissonClient = Redisson.create(cacheKeeperConfig.getConfig());
        } catch (Exception e) {
            log.error("org.codeba.redis.keeper.support.DefaultCacheDatasource.instantTemplate(CacheKeeperConfig config)--", e);
        }
    }

    /**
     * Log.
     *
     * @param cmd    the cmd
     * @param params the params
     */
    protected void log(String cmd, Object... params) {
        if (this.invokeParamsPrint) {
            log.info("cmd:{}, params:{}, connectionInfo:[{}]", cmd, Arrays.toString(params), connectionInfo);
        }
    }

    /**
     * Sha 1 digest as hex string.
     *
     * @param input the input
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    protected String sha1DigestAsHex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Parse geo unit geo unit.
     *
     * @param geoUnit the geo unit
     * @return the geo unit
     */
    protected GeoUnit parseGeoUnit(String geoUnit) {
        final GeoUnit[] values = GeoUnit.values();
        for (GeoUnit value : values) {
            if (value.toString().equalsIgnoreCase(geoUnit)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public RedissonClient getDataSource() {
        return this.redissonClient;
    }

    @Override
    public long bitCount(String key) {
        log("bitCount", key);
        return getRBitSet(key).cardinality();
    }

    @Override
    public long bitFieldSetSigned(String key, int size, long offset, long value) {
        log("bitFieldSetSigned", key, size, offset, value);
        return getRBitSet(key).setSigned(size, offset, value);
    }

    @Override
    public long bitFieldSetUnSigned(String key, int size, long offset, long value) {
        log("bitFieldSetUnSigned", key, size, offset, value);
        return getRBitSet(key).setUnsigned(size, offset, value);
    }

    @Override
    public long bitFieldGetSigned(String key, int size, long offset) {
        log("bitFieldGetSigned", key, offset);
        return getRBitSet(key).getSigned(size, offset);
    }

    @Override
    public long bitFieldGetUnSigned(String key, int size, long offset) {
        log("bitFieldGetUnSigned", key, offset);
        return getRBitSet(key).getUnsigned(size, offset);
    }

    @Override
    public void bitOpOr(String destKey, String... keys) {
        log("bitOpOr", destKey, keys);
        getRBitSet(destKey).or(keys);
    }

    @Override
    public boolean getBit(String key, long bitIndex) {
        log("getBit", key, bitIndex);
        return getRBitSet(key).get(bitIndex);
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        log("setBit", key, offset, value);
        return getRBitSet(key).set(offset, value);
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, Object member) {
        log("geoAdd", key, longitude, latitude, member);
        return getRGeo(key).add(longitude, latitude, member);
    }

    @Override
    public boolean geoAddXX(String key, double longitude, double latitude, Object member) {
        log("geoAddXX", key, longitude, latitude, member);
        return getRGeo(key).addIfExists(longitude, latitude, member);
    }

    @Override
    public Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDist", key, firstMember, secondMember, geoUnit);
        return getRGeo(key).dist(firstMember, secondMember, parseGeoUnit(geoUnit));
    }

    @Override
    public Map<Object, String> geoHash(String key, Object... members) {
        log("geoHash", key, members);
        return getRGeo(key).hash(members);
    }

    @Override
    public Map<Object, double[]> geoPos(String key, Object... members) {
        log("geoPos", key, members);
        final Map<Object, GeoPosition> pos = getRGeo(key).pos(members);
        return pos.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> new double[]{entry.getValue().getLongitude(), entry.getValue().getLatitude()})
        );
    }

    @Override
    public Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadius", key, longitude, latitude, radius, geoUnit);
        return getRGeo(key).radiusWithDistance(longitude, latitude, radius, parseGeoUnit(geoUnit));
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).search(search);
    }

    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearch", key, member, radius, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, member, radius, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, member, width, height, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, member, width, height, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).search(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, count, order);
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        final OptionalGeoSearch search =
                GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count).order(geoOrder);
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<String, Boolean> hDel(String key, String... fields) {
        log("hDel", key, fields);

        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            final long fastRemove = rMap.fastRemove(field);
            resultMap.put(field, 0 != fastRemove);
        }

        return resultMap;
    }

    @Override
    public void hDelAsync(String key, String... fields) {
        log("hdelAsync", key, fields);

        final RMap<Object, Object> rMap = getMap(key);
        rMap.fastRemoveAsync(fields);
    }

    @Override
    public Map<String, Boolean> hExists(String key, String... fields) {
        log("hexists", key, fields);

        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKey(field));
        }

        return resultMap;
    }

    @Override
    public Optional<Object> hGet(String key, String field) {
        log("hget", key, field);
        final RMap<Object, Object> rMap = getMap(key);
        return Optional.ofNullable(rMap.get(field));
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        log("hgetAll", key);
        final RMap<Object, Object> rMap = getMap(key);
        return rMap.readAllMap();
    }

    @Override
    public Object hIncrBy(String key, String field, Number value) {
        log("hIncrBy", key, field, value);
        return getMap(key).addAndGet(field, value);
    }

    @Override
    public void hIncrByAsync(String key, String field, Number value) {
        log("hIncrByAsync", key, field, value);
        getMap(key).addAndGetAsync(field, value);
    }

    @Override
    public Collection<Object> hKeys(String key) {
        log("hkeys", key);
        return getMap(key).readAllKeySet();
    }

    @Override
    public int hLen(String key) {
        log("hlen", key);
        return getMap(key).size();
    }

    @Override
    public Map<Object, Object> hmGet(String key, Set<Object> fields) {
        log("hmget", key);

        if (null == fields || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        return getMap(key).getAll(fields);

    }

    @Override
    public void hmSet(String key, Map<?, ?> kvMap) {
        log("hmset", key, kvMap);
        getMap(key).putAll(kvMap, 100);
    }

    @Override
    public void hmSetAsync(String key, Map<?, ?> kvMap) {
        log("hmsetAsync", key, kvMap);
        getMap(key).putAllAsync(kvMap, 100);
    }

    @Override
    public Set<Object> hRandField(String key, int count) {
        log("hrandfield", key, count);
        return getMap(key).randomKeys(count);
    }

    @Override
    public Map<Object, Object> hRandFieldWithValues(String key, int count) {
        log("hrandfieldWithvalues", key, count);
        return getMap(key).randomEntries(count);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern) {
        log("hscan", key, keyPattern);
        return getMap(key).entrySet(keyPattern).iterator();
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count) {
        log("hscan", key, keyPattern, count);
        return getMap(key).entrySet(keyPattern, count).iterator();
    }

    @Override
    public void hSet(String key, String field, Object value) {
        log("hset", key, field, value);
        getMap(key).fastPut(field, value);
    }

    @Override
    public void hSetAsync(String key, String field, Object value) {
        log("hsetAsync", key, field, value);
        getMap(key).fastPutAsync(field, value);
    }

    @Override
    public void hSetNX(String key, String field, Object value) {
        log("hsetNX", key, field, value);
        getMap(key).fastPutIfAbsent(field, value);
    }

    @Override
    public void hSetNXAsync(String key, String field, Object value) {
        log("hsetNXAsync", key, field, value);
        getMap(key).fastPutIfAbsentAsync(field, value);
    }

    @Override
    public int hStrLen(String key, String field) {
        log("hstrlen", key, field);
        return getMap(key).valueSize(field);
    }

    @Override
    public Collection<Object> hVALs(String key) {
        log("hvals", key);
        return getMap(key).readAllValues();
    }

    @Override
    public boolean pfAdd(String key, Collection<Object> elements) {
        log("pfadd", key, elements);
        return getHyperLogLog(key).addAll(elements);
    }

    @Override
    public long pfCount(String key) {
        log("pfcount", key);
        return getHyperLogLog(key).count();
    }

    @Override
    public long pfCount(String key, String... otherKeys) {
        log("pfcount", key, otherKeys);
        return getHyperLogLog(key).countWith(otherKeys);
    }

    @Override
    public void pfMerge(String destKey, String... sourceKeys) {
        log("pfmerge", destKey, sourceKeys);
        getHyperLogLog(destKey).mergeWith(sourceKeys);
    }

    @Override
    public Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollFirst) {
        log("blmove", source, destination, timeout, pollFirst);

        Object move;
        if (pollFirst) {
            move = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollLast().addFirstTo(destination));
        }

        return Optional.ofNullable(move);
    }

    @Override
    public Optional<Object> blPop(String key) {
        log("blpop", key);

        final Object polled = getBlockingDeque(key).poll();
        return Optional.ofNullable(polled);
    }

    @Override
    public List<Object> blPop(String key, int count) {
        log("blpop", key, count);

        return getBlockingDeque(key).poll(count);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("blpop", key, timeout, unit);

        final Object polled = getBlockingDeque(key).poll(timeout, unit);
        return Optional.ofNullable(polled);

    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("blpop", key, timeout, unit, otherKeys);

        final Object object = getBlockingDeque(key).pollFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> brPop(String key) {
        log("brpop", key);

        final Object polled = getBlockingDeque(key).pollLast();
        return Optional.ofNullable(polled);
    }

    @Override
    public List<Object> brPop(String key, int count) {
        log("brpop", key, count);
        return getBlockingDeque(key).pollLast(count);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("brpop", key, timeout, unit);

        final Object polled = getBlockingDeque(key).pollLast(timeout, unit);
        return Optional.ofNullable(polled);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("brpop", key, timeout, unit, otherKeys);

        final Object polled = getBlockingDeque(key).pollLastFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(polled);
    }

    @Override
    public Optional<Object> brPoplPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException {
        log("brpoplpush", source, destination, timeout, unit);

        final Object object = getBlockingDeque(source).pollLastAndOfferFirstTo(destination, timeout, unit);
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> lIndex(String key, int index) {
        log("lindex", key, index);
        return Optional.ofNullable(getList(key).get(index));
    }

    @Override
    public int lInsert(String key, boolean before, Object pivot, Object element) {
        log("linsert", key, before, pivot, element);

        int added;
        if (before) {
            added = getList(key).addBefore(pivot, element);
        } else {
            added = getList(key).addAfter(pivot, element);
        }

        return added;
    }

    @Override
    public int llen(String key) {
        log("llen", key);
        return getList(key).size();
    }

    @Override
    public Optional<Object> lMove(String source, String destination, boolean pollLeft) {
        log("lmove", source, destination, pollLeft);

        Object move;
        if (pollLeft) {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollLast().addFirstTo(destination));
        }

        return Optional.ofNullable(move);
    }

//    @Override
//    public Optional<Map<String, List<Object>>> lmPop(String key, boolean left, int count, String... otherKeys) throws InterruptedException {
//        log("lmpop", key, left, count, otherKeys);
//
//        final Map<String, List<Object>> map;
//        if (left) {
//            map = getBlockingDeque(key).pollFirstFromAny(Duration.ofMinutes(1), count, otherKeys);
//        } else {
//            map = getBlockingDeque(key).pollLastFromAny(Duration.ofMinutes(1), count, otherKeys);
//        }
//
//        return Optional.ofNullable(map);
//    }

    @Override
    public List<Object> lPop(String key, int count) {
        log("lpop", key, count);
        return getDeque(key).poll(count);

    }

    @Override
    public int lPush(String key, Object... elements) {
        log("lpush", key, elements);
        for (Object element : elements) {
            getDeque(key).addFirst(element);
        }
        return elements.length;
//        return getDeque(key).addFirst(elements);
    }

    @Override
    public int lPushX(String key, Object... elements) {
        log("lpushx", key, elements);
        return getDeque(key).addFirstIfExists(elements);
    }

    @Override
    public List<Object> lRange(String key, int fromIndex, int toIndex) {
        log("lrange", key, fromIndex, toIndex);
        return getList(key).range(fromIndex, toIndex);
    }

    @Override
    public boolean lRem(String key, Object element) {
        log("lrem", key, element);
        return getList(key).remove(element);
    }

    @Override
    public void lSet(String key, int index, Object element) {
        log("lset", key, index, element);
        getList(key).fastSet(index, element);
    }

    @Override
    public void lTrim(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        getList(key).trim(fromIndex, toIndex);
    }

    @Override
    public List<Object> rPop(String key, int count) {
        log("ltrim", key, count);
        return getDeque(key).pollLast(count);
    }

    @Override
    public Optional<Object> rPoplPush(String source, String destination) {
        log("rpoplpush", source, destination);

        final Object object = getDeque(source).pollLastAndOfferFirstTo(destination);
        return Optional.ofNullable(object);
    }

    @Override
    public boolean rPush(String key, Object... elements) {
        log("rpush", key, elements);
        return getList(key).addAll(Arrays.asList(elements));
    }

    @Override
    public int rPushX(String key, Object... elements) {
        log("rpushx", key, elements);
        return getDeque(key).addLastIfExists(elements);
    }

    @Override
    public boolean sAdd(String key, Object member) {
        log("sadd", key, member);
        return getSet(key).add(member);
    }

    @Override
    public void sAddAsync(String key, Object member) {
        log("saddAsync", key, member);
        getSet(key).addAsync(member);
    }

    @Override
    public boolean sAdd(String key, Collection<?> members) {
        log("sadd", key, members);
        return getSet(key).addAll(members);
    }

    @Override
    public void sAddAsync(String key, Collection<?> members) {
        log("saddAsync", key, members);
        getSet(key).addAllAsync(members);
    }

    @Override
    public int sCard(String key) {
        log("scard", key);
        return getSet(key).size();
    }

    @Override
    public Set<Object> sDiff(String key, String... otherKeys) {
        log("sdiff", key, otherKeys);
        return getSet(key).readDiff(otherKeys);
    }

    @Override
    public int sDiffStore(String destination, String... keys) {
        log("sdiffstore", destination, keys);
        return getSet(destination).diff(keys);
    }

    @Override
    public Set<Object> sInter(String key, String... otherKeys) {
        log("sinter", key, otherKeys);
        return getSet(key).readIntersection(otherKeys);
    }

    @Override
    public int sInterStore(String destination, String... keys) {
        log("sinterstore", destination, keys);
        return getSet(destination).intersection(keys);
    }

    @Override
    public boolean sIsMember(String key, Object member) {
        log("sismember", key, member);
        return getSet(key).contains(member);
    }

    @Override
    public Set<Object> sMembers(String key) {
        log("smembers", key);
        return getSet(key).readAll();
    }

    @Override
    public boolean sMove(String source, String destination, Object member) {
        log("smove", source, destination, member);
        return getSet(source).move(destination, member);
    }

    @Override
    public Optional<Object> sPop(String key) {
        log("spop", key);
        return Optional.ofNullable(getSet(key).removeRandom());
    }

    @Override
    public Set<Object> sPop(String key, int count) {
        log("spop", key, count);
        return getSet(key).removeRandom(count);
    }

    @Override
    public Optional<Object> sRandMember(String key) {
        log("srandmember", key);
        return Optional.ofNullable(getSet(key).random());
    }

    @Override
    public Set<Object> sRandMember(String key, int count) {
        log("srandmember", key, count);
        return getSet(key).random(count);
    }

    @Override
    public boolean sRem(String key, Collection<?> members) {
        log("srem", key, members);
        return getSet(key).removeAll(members);
    }

    @Override
    public Iterator<Object> sScan(String key) {
        log("sscan", key);
        return getSet(key).iterator();
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern) {
        log("sscan", key, pattern);
        return getSet(key).iterator(pattern);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern, int count) {
        log("sscan", key, pattern, count);
        return getSet(key).iterator(pattern, count);
    }

    @Override
    public Set<Object> sUnion(String key, String... otherKeys) {
        log("sunion", key, otherKeys);
        return getSet(key).readUnion(otherKeys);
    }

    @Override
    public int sUnionStore(String destination, String... keys) {
        log("sunionstore", destination, keys);
        return getSet(destination).union(keys);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmpop", timeout, unit, key, min);

        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLast(timeout, unit);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Collection<Object> bzmPop(String key, boolean min, int count) {
        log("bzmpop", key, min, count);

        final Collection<Object> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirst(count);
        } else {
            list = getRScoredSortedSet(key).pollLast(count);
        }
        return list;
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmpop", timeout, unit, key, min, otherKeys);

        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit) {
        log("bzpopmax", key, timeout, unit);
        return bzmPop(timeout, unit, key, false);
    }

    @Override
    public Collection<Object> bzPopMax(String key, int count) {
        log("bzpopmax", key, count);
        return bzmPop(key, false, count);
    }

    @Override
    public Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit) {
        log("bzpopmin", key, timeout, unit);
        return bzmPop(timeout, unit, key, true);
    }

    @Override
    public Collection<Object> bzPopMin(String key, int count) {
        log("bzpopmin", key, count);
        return bzmPop(key, true, count);
    }

    @Override
    public boolean zAdd(String key, double score, Object member) {
        log("zadd", key, score, member);
        return getRScoredSortedSet(key).add(score, member);
    }

    @Override
    public int zAdd(String key, Map<Object, Double> members) {
        log("zadd", key, members);
        return getRScoredSortedSet(key).addAll(members);
    }

    @Override
    public int zCard(String key) {
        log("zcard", key);
        return getRScoredSortedSet(key).size();
    }

    @Override
    public int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zcard", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return getRScoredSortedSet(key).count(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zDiff(String key, String... keys) {
        log("zdiff", key, keys);
        return getRScoredSortedSet(key).readDiff(keys);
    }

    @Override
    public int zDiffStore(String destination, String... keys) {
        log("zdiffstore", destination, keys);
        return getRScoredSortedSet(destination).diff(keys);
    }

    @Override
    public Double zIncrBy(String key, Number increment, Object member) {
        log("zincrby", key, increment, member);
        return getRScoredSortedSet(key).addScore(member, increment);
    }

    @Override
    public Collection<Object> zInter(String key, String... otherKeys) {
        log("zinter", key, otherKeys);
        return getRScoredSortedSet(key).readIntersection(otherKeys);
    }

    @Override
    public int zInterStore(String destination, String... otherKeys) {
        log("zinterstore", destination, otherKeys);

        return getRScoredSortedSet(destination).intersection(otherKeys);
    }

    @Override
    public int zInterStoreAggregate(String destination, String aggregate, String... otherKeys) {
        log("zinterstore", destination, aggregate, otherKeys);

        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, otherKeys);
    }

    @Override
    public int zInterStore(String destination, Map<String, Double> keyWithWeight) {
        log("zinterstore", destination, keyWithWeight);

        return getRScoredSortedSet(destination).intersection(keyWithWeight);
    }

    @Override
    public int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zinterstore", destination, aggregate, keyWithWeight);

        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, keyWithWeight);
    }

    @Override
    public int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zlexcount", fromElement, fromInclusive, toElement, toInclusive);
        return getRLexSortedSet(key).count(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public int zLexCountHead(String key, String toElement, boolean toInclusive) {
        log("zlexcount", toElement, toInclusive);
        return getRLexSortedSet(key).countHead(toElement, toInclusive);
    }

    @Override
    public int zLexCountTail(String key, String fromElement, boolean fromInclusive) {
        log("zlexcount", fromElement, fromInclusive);
        return getRLexSortedSet(key).countTail(fromElement, fromInclusive);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min) {
        log("zmpop", key, min);

        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst();
        } else {
            object = getRScoredSortedSet(key).pollLast();
        }

        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmpop", key, min, timeout, unit, otherKeys);

        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }

        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> zPopMax(String key) {
        log("zpopmax", key);
        return Optional.ofNullable(getRScoredSortedSet(key).pollLast());
    }

    @Override
    public Collection<Object> zPopMax(String key, int count) {
        log("zpopmax", key, count);
        return getRScoredSortedSet(key).pollLast(count);
    }

    @Override
    public Optional<Object> zPopMin(String key) {
        log("zpopmin", key);
        return Optional.ofNullable(getRScoredSortedSet(key).pollFirst());
    }

    @Override
    public Collection<Object> zPopMin(String key, int count) {
        log("zpopmin", key, count);
        return getRScoredSortedSet(key).pollFirst(count);
    }

    @Override
    public Optional<Object> zRandMember(String key) {
        log("zrandmember", key);
        return Optional.ofNullable(getRScoredSortedSet(key).random());
    }

    @Override
    public Collection<Object> zRandMember(String key, int count) {
        log("zrandmember", key, count);
        return getRScoredSortedSet(key).random(count);
    }

    @Override
    public Collection<Object> zRange(String key, int startIndex, int endIndex) {
        log("zrange", key, startIndex, endIndex);
        return getRScoredSortedSet(key).valueRange(startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zrange", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zrange", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, int startIndex, int endIndex) {
        log("zrangeReversed", key, startIndex, endIndex);
        return getRScoredSortedSet(key).valueRangeReversed(startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zrangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zrangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Optional<Integer> zRank(String key, Object member) {
        log("zrank", key, member);
        return Optional.ofNullable(getRScoredSortedSet(key).rank(member));
    }

    @Override
    public boolean zRem(String key, Collection<?> members) {
        log("zrem", key, members);
        return getRScoredSortedSet(key).removeAll(members);
    }

    @Override
    public Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zremrangebylex", key, fromElement, fromInclusive, toElement, toInclusive);

        final int removeRange = getRLexSortedSet(key).removeRange(fromElement, fromInclusive, toElement, toInclusive);
        return Optional.of(removeRange);
    }

    @Override
    public Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex) {
        log("zremrangebyrank", key, startIndex, endIndex);
        return Optional.of(getRScoredSortedSet(key).removeRangeByRank(startIndex, endIndex));
    }

    @Override
    public Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zremrangebyscore", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return Optional.of(getRScoredSortedSet(key).removeRangeByScore(startScore, startScoreInclusive, endScore, endScoreInclusive));
    }

    @Override
    public Optional<Integer> zRevRank(String key, Object member) {
        log("zrevrank", key, member);
        return Optional.ofNullable(getRScoredSortedSet(key).revRank(member));
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern) {
        log("zscan", key, pattern);
        return getRScoredSortedSet(key).iterator(pattern);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern, int count) {
        log("zscan", key, pattern, count);
        return getRScoredSortedSet(key).iterator(pattern, count);
    }

    @Override
    public List<Double> zScore(String key, List<Object> members) {
        log("zscore", key, members);
        return getRScoredSortedSet(key).getScore(members);
    }

    @Override
    public Collection<Object> zUnion(String key, String... otherKeys) {
        log("zunion", key, otherKeys);
        return getRScoredSortedSet(key).readUnion(otherKeys);
    }

    @Override
    public int zUnionStore(String destination, String... keys) {
        log("zunionstore", destination, keys);
        return getRScoredSortedSet(destination).union(keys);
    }

    @Override
    public int zUnionStoreAggregate(String destination, String aggregate, String... keys) {
        log("zunionstore", destination, aggregate, keys);

        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keys);
    }

    @Override
    public int zUnionStore(String destination, Map<String, Double> keyWithWeight) {
        log("zunionstore", destination, keyWithWeight);

        return getRScoredSortedSet(destination).union(keyWithWeight);
    }

    @Override
    public int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zunionstore", destination, aggregate, keyWithWeight);

        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keyWithWeight);
    }

    @Override
    public void append(String key, Object value) {
        log("append", key, value);

        try (final OutputStream outputStream = getRBinaryStream(key).getOutputStream()) {
            outputStream.write(value.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public long decr(String key) {
        log("decr", key);
        return getRAtomicLong(key).decrementAndGet();
    }

    @Override
    public long decrBy(String key, long decrement) {
        log("decrby", key, decrement);
        return getRAtomicLong(key).addAndGet(-decrement);
    }

    @Override
    public Optional<Object> get(String key) {
        log("get", key);

        final Object object = getRBucket(key).get();
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> getDel(String key) {
        log("getdel", key);
        return Optional.ofNullable(getRBucket(key).getAndDelete());
    }

//    @Override
//    public Optional<Object> getEx(String key, Duration duration) {
//        log("getex", key, duration);
//        return Optional.of(getRBucket(key).getAndExpire(duration));
//    }

//    @Override
//    public Optional<Object> getEx(String key, Instant time) {
//        log("getex", key, time);
//        return Optional.of(getRBucket(key).getAndExpire(time));
//    }

//    @Override
//    public Optional<Object> getrange(String key, int start, int end) {
//        if (log(READS, "getrange", key, start, end)) {
//            reportStatus();
//        }
//
//        try (SeekableByteChannel channel = getRBinaryStream(key).getChannel()) {
//            final ByteBuffer byteBuffer = ByteBuffer.allocate(end - start + 1);
////            channel.read(byteBuffer);
////            return Optional.of(new String(byteBuffer.array(), StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @Override
    public long getLong(String key) {
        log("getLong", key);
        return getRAtomicLong(key).get();
    }

    @Override
    public long incr(String key) {
        log("incr", key);
        return getRAtomicLong(key).incrementAndGet();
    }

    @Override
    public long incrBy(String key, long increment) {
        log("incrby", key, increment);
        return getRAtomicLong(key).addAndGet(increment);
    }

    @Override
    public double getDouble(String key) {
        log("getDouble", key);
        return getRAtomicDouble(key).get();
    }

    @Override
    public double incrByFloat(String key, double increment) {
        log("incrbyfloat", key, increment);
        return getRAtomicDouble(key).addAndGet(increment);
    }

    @Override
    public boolean compareAndSet(String key, long expect, long update) {
        log("compareAndSet", key, expect, update);
        return getRAtomicLong(key).compareAndSet(expect, update);
    }

    @Override
    public boolean compareAndSet(String key, double expect, double update) {
        log("compareAndSet", key, expect, update);
        return getRAtomicDouble(key).compareAndSet(expect, update);
    }

    @Override
    public Map<String, Object> mGet(String... keys) {
        log("mget", keys);

        if (null == keys || keys.length == 0) {
            return Collections.emptyMap();
        }

        return getRBuckets().get(keys);
    }

    @Override
    public void mSet(Map<String, String> kvMap) {
        log("mset", kvMap);
        getRBuckets().set(kvMap);
    }

    @Override
    public boolean mSetNX(Map<String, String> kvMap) {
        log("msetnx", kvMap);
        return getRBuckets().trySet(kvMap);
    }

    @Override
    public void set(String key, String value) {
        log("set", key, value);
        getRBucket(key).set(value);
    }

    @Override
    public boolean compareAndSet(String key, String expect, String update) {
        log("compareAndSet", key, expect, update);
        return getRBucket(key).compareAndSet(expect, update);
    }

    @Override
    public void setEX(String key, String value, Duration duration) {
        log("setEX", key, value, duration);
        getRBucket(key).set(value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

//    @Override
//    public boolean setNX(String key, String value) {
//        log("setNX", key, value);
//        return getRBucket(key).setIfAbsent(value);
//    }

//    @Override
//    public boolean setNxEx(String key, String value, Duration duration) {
//        log("setNxEx", key, value, duration);
//        return getRBucket(key).setIfAbsent(value, duration);
//    }

//    @Override
//    public Optional<Object> setrange(String key, int offset, Object value) {
//        if (log(WRITES, "setrange", key, offset, value)) {
//            return Optional.empty();
//        }
//
//        try (SeekableByteChannel channel = getRBinaryStream(key).getChannel()) {
//            channel.write();
//            return Optional.of();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public long strLen(String key) {
        log("strlen", key);
        return getRBucket(key).size();
    }

    @Override
    public boolean bfAdd(String key, Object item) {
        log("bfadd", key, item);
        return getRBloomFilter(key).add(item);
    }

    @Override
    public long bfCard(String key) {
        log("bfcard", key);
        return getRBloomFilter(key).count();
    }

    @Override
    public boolean bfExists(String key, Object item) {
        log("bfexists", key);
        return getRBloomFilter(key).contains(item);
    }

    @Override
    public boolean bfmAdd(String key, Object item) {
        log("bfmadd", key, item);
        return getRBloomFilter(key).add(item);
    }

    @Override
    public boolean bfReserve(String key, long expectedInsertions, double falseProbability) {
        log("bfreserve", key, expectedInsertions, falseProbability);
        return getRBloomFilter(key).tryInit(expectedInsertions, falseProbability);
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, leaseTime, unit);
        return getRLock(key).tryLock(waitTime, leaseTime, unit);
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, unit);
        return getRLock(key).tryLock(waitTime, unit);
    }

    @Override
    public void unlock(String key) {
        log("unlock", key);
        getRLock(key).unlock();
    }

    @Override
    public void unlockAsync(String key) {
        log("unlockAsync", key);
        getRLock(key).unlockAsync();
    }

    @Override
    public void unlockAsync(String key, long threadId) {
        log("unlockAsync", key, threadId);
        getRLock(key).unlockAsync(threadId);
    }

    @Override
    public boolean forceUnlock(String key) {
        log("forceUnlock", key);
        return getRLock(key).forceUnlock();
    }

    @Override
    public Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScript", script, keys, values);

        /* evalSha: 此处必须加上 StringCodec.INSTANCE */
        final RScript rScript = getDataSource().getScript(StringCodec.INSTANCE);
        String shaDigests = sha1DigestAsHex(script);
        final List<Boolean> scriptExists = rScript.scriptExists(shaDigests);
        if (null != scriptExists && !scriptExists.isEmpty()) {
            final Boolean exists = scriptExists.get(0);
            if (exists) {
                try {
                    final Object object = rScript.evalSha(RScript.Mode.READ_WRITE, shaDigests, RScript.ReturnType.VALUE, keys, values);
                    return Optional.ofNullable(object);
                } catch (RedisException e) {
                    final Object eval = rScript.eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values);
                    return Optional.ofNullable(eval);
                }
            }
        }

        /* eval script */
        final Object eval = rScript.eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values);
        return Optional.ofNullable(eval);
    }

    @Override
    public long exists(String... keys) {
        log("exists", keys);
        return getRKeys().countExists(keys);
    }

    @Override
    public long del(String... keys) {
        log("del", keys);
        return getRKeys().delete(keys);
    }

    @Override
    public long ttl(String key) {
        log("ttl", key);
        return getDataSource().getBucket(key).remainTimeToLive();
    }

    @Override
    public Iterable<String> scan(String keyPattern) {
        log("scan", keyPattern);
        return getRKeys().getKeysByPattern(keyPattern);
    }

    @Override
    public Iterable<String> scan(String keyPattern, int count) {
        log("scan", keyPattern, count);
        return getRKeys().getKeysByPattern(keyPattern, count);
    }

    @Override
    public KeyType type(String key) {
        log("type", key);
        final RType type = getRKeys().getType(key);

        if (type == RType.OBJECT) {
            return KeyType.STRING;
        } else if (type == RType.MAP) {
            return KeyType.HASH;
        } else {
            return KeyType.valueOf(type.name());
        }
    }

    @Override
    public boolean trySetRateLimiter(String key, long rate, long rateInterval) {
        log("trySetRateLimiter", key, rate, rateInterval);
        return getRateLimiter(key).trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
    }

    @Override
    public boolean tryAcquire(String key) {
        log("tryAcquire", key);
        return getRateLimiter(key).tryAcquire();
    }

    @Override
    public boolean tryAcquire(String key, long permits) {
        log("tryAcquire", key, permits);
        return getRateLimiter(key).tryAcquire(permits);
    }

    private <T> RBucket<T> getRBucket(String key) {
        return getDataSource().getBucket(key, new StringCodec());
    }

    private RBuckets getRBuckets() {
        return getDataSource().getBuckets(new StringCodec());
    }

    private <K, V> RMap<K, V> getMap(String key) {
        return getDataSource().getMap(key, new StringCodec());
    }

    private <V> RList<V> getList(String key) {
        return getDataSource().getList(key, new StringCodec());
    }

    private <V> RBlockingDeque<V> getBlockingDeque(String key) {
        return getDataSource().getBlockingDeque(key, new StringCodec());
    }

    private <V> RDeque<V> getDeque(String key) {
        return getDataSource().getDeque(key, new StringCodec());
    }

    private <V> RSet<V> getSet(String key) {
        return getDataSource().getSet(key, new StringCodec());
    }

    private <V> RScoredSortedSet<V> getRScoredSortedSet(String key) {
        return getDataSource().getScoredSortedSet(key, new StringCodec());
    }

    private <V> RBloomFilter<V> getRBloomFilter(String key) {
        return getDataSource().getBloomFilter(key, new StringCodec());
    }

    private RKeys getRKeys() {
        return getDataSource().getKeys();
    }

    private RBinaryStream getRBinaryStream(String key) {
        return getDataSource().getBinaryStream(key);
    }

    private <V> RHyperLogLog<V> getHyperLogLog(String key) {
        return getDataSource().getHyperLogLog(key, new StringCodec());
    }

    private RAtomicLong getRAtomicLong(String key) {
        return getDataSource().getAtomicLong(key);
    }

    private RAtomicDouble getRAtomicDouble(String key) {
        return getDataSource().getAtomicDouble(key);
    }

    private RBitSet getRBitSet(String key) {
        return getDataSource().getBitSet(key);
    }

    private RLexSortedSet getRLexSortedSet(String key) {
        return getDataSource().getLexSortedSet(key);
    }

    private RLock getRLock(String key) {
        return getDataSource().getLock(key);
    }

    private RRateLimiter getRateLimiter(String key) {
        return getDataSource().getRateLimiter(key);
    }

    private <V> RGeo<V> getRGeo(String key) {
        return getDataSource().getGeo(key, new StringCodec());
    }

    /**
     * Gets redisson client.
     *
     * @return the redisson client
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    /**
     * Gets connection info.
     *
     * @return the connection info
     */
    public String getConnectionInfo() {
        return connectionInfo;
    }

    /**
     * Is invoke params print boolean.
     *
     * @return the boolean
     */
    public boolean isInvokeParamsPrint() {
        return invokeParamsPrint;
    }
}
