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

import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.Redisson;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type Default redisson template.
 *
 * @author codeba
 */
public class DefaultRedissonTemplate implements RedissonTemplate, CacheTemplate {
    /**
     * The Log.
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * The Connection info.
     */
    private final String connectionInfo;
    /**
     * The Status.
     */
    private final String status;
    /**
     * The Redisson client.
     */
    private RedissonClient redissonClient;
    /**
     * The Invoke params print.
     */
    private final boolean invokeParamsPrint;
    private RBitMap rBitMap;
    private RHash rHash;
    private RHyperLogLog rHyperLogLog;
    private RLists rLists;
    private RSets rSets;
    private RZSet rzSet;
    private RString rString;
    private RBloomFilters rBloomFilters;
    private RLocks rLocks;
    private RRateLimiters rRateLimiters;
    private RGeneric rGeneric;
    private RGeos rGeos;

    /**
     * Instantiates a new Default redisson template.
     *
     * @param cacheKeeperConfig the cache keeper config
     */
    public DefaultRedissonTemplate(CacheKeeperConfig cacheKeeperConfig) {
        this.invokeParamsPrint = cacheKeeperConfig.isInvokeParamsPrint();
        this.status = cacheKeeperConfig.getStatus();
        this.connectionInfo = Utils.getConnectionInfo(cacheKeeperConfig.getConfig());

        try {
            this.redissonClient = Redisson.create(cacheKeeperConfig.getConfig());
        } catch (Exception e) {
            log.error("org.codeba.redis.keeper.support.DefaultCacheDatasource.instantTemplate(CacheKeeperConfig config)--", e);
        }

        this.rBitMap = new RBitMap(connectionInfo, redissonClient, invokeParamsPrint);
        this.rHash = new RHash(connectionInfo, redissonClient, invokeParamsPrint);
        this.rHyperLogLog = new RHyperLogLog(connectionInfo, redissonClient, invokeParamsPrint);
        this.rLists = new RLists(connectionInfo, redissonClient, invokeParamsPrint);
        this.rSets = new RSets(connectionInfo, redissonClient, invokeParamsPrint);
        this.rzSet = new RZSet(connectionInfo, redissonClient, invokeParamsPrint);
        this.rString = new RString(connectionInfo, redissonClient, invokeParamsPrint);
        this.rBloomFilters = new RBloomFilters(connectionInfo, redissonClient, invokeParamsPrint);
        this.rLocks = new RLocks(connectionInfo, redissonClient, invokeParamsPrint);
        this.rRateLimiters = new RRateLimiters(connectionInfo, redissonClient, invokeParamsPrint);
        this.rGeneric = new RGeneric(connectionInfo, redissonClient, invokeParamsPrint);
        this.rGeos = new RGeos(connectionInfo, redissonClient, invokeParamsPrint);
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

    @Override
    public RedissonClient getDataSource() {
        return this.redissonClient;
    }

    @Override
    public long bitCount(String key) {
        log("bitCount", key);
        return rBitMap.bitCount(key);
    }

    @Override
    public CompletableFuture<Long> bitCountAsync(String key) {
        log("bitCountAsync", key);
        return rBitMap.bitCountAsync(key);
    }

    @Override
    public long bitFieldSetSigned(String key, int size, long offset, long value) {
        log("bitFieldSetSigned", key, size, offset, value);
        return rBitMap.bitFieldSetSigned(key, size, offset, value);
    }

    @Override
    public CompletableFuture<Long> bitFieldSetSignedAsync(String key, int size, long offset, long value) {
        log("bitFieldSetSignedAsync", key, size, offset, value);
        return rBitMap.bitFieldSetSignedAsync(key, size, offset, value);
    }

    @Override
    public long bitFieldSetUnSigned(String key, int size, long offset, long value) {
        log("bitFieldSetUnSigned", key, size, offset, value);
        return rBitMap.bitFieldSetUnSigned(key, size, offset, value);
    }

    @Override
    public CompletableFuture<Long> bitFieldSetUnSignedAsync(String key, int size, long offset, long value) {
        log("bitFieldSetUnSignedAsync", key, size, offset, value);
        return rBitMap.bitFieldSetUnSignedAsync(key, size, offset, value);
    }

    @Override
    public long bitFieldGetSigned(String key, int size, long offset) {
        log("bitFieldGetSigned", key, offset);
        return rBitMap.bitFieldGetSigned(key, size, offset);
    }

    @Override
    public CompletableFuture<Long> bitFieldGetSignedAsync(String key, int size, long offset) {
        log("bitFieldGetSignedAsync", key, offset);
        return rBitMap.bitFieldGetSignedAsync(key, size, offset);
    }

    @Override
    public long bitFieldGetUnSigned(String key, int size, long offset) {
        log("bitFieldGetUnSigned", key, offset);
        return rBitMap.bitFieldGetUnSigned(key, size, offset);
    }

    @Override
    public CompletableFuture<Long> bitFieldGetUnSignedAsync(String key, int size, long offset) {
        log("bitFieldGetUnSignedAsync", key, offset);
        return rBitMap.bitFieldGetUnSignedAsync(key, size, offset);
    }

    @Override
    public void bitOpOr(String destKey, String... keys) {
        log("bitOpOr", destKey, keys);
        rBitMap.bitOpOr(destKey, keys);
    }

    @Override
    public CompletableFuture<Void> bitOpOrAsync(String destKey, String... keys) {
        log("bitOpOrAsync", destKey, keys);
        return rBitMap.bitOpOrAsync(destKey, keys);
    }

    @Override
    public boolean getBit(String key, long bitIndex) {
        log("getBit", key, bitIndex);
        return rBitMap.getBit(key, bitIndex);
    }

    @Override
    public CompletableFuture<Boolean> getBitAsync(String key, long bitIndex) {
        log("getBitAsync", key, bitIndex);
        return rBitMap.getBitAsync(key, bitIndex);
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        log("setBit", key, offset, value);
        return rBitMap.setBit(key, offset, value);
    }

    @Override
    public CompletableFuture<Boolean> setBitAsync(String key, long offset, boolean value) {
        log("setBitAsync", key, offset, value);
        return rBitMap.setBitAsync(key, offset, value);
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, Object member) {
        log("geoAdd", key, longitude, latitude, member);
        return rGeos.geoAdd(key, longitude, latitude, member);
    }

    @Override
    public CompletableFuture<Long> geoAddAsync(String key, double longitude, double latitude, Object member) {
        log("geoAddAsync", key, longitude, latitude, member);
        return rGeos.geoAddAsync(key, longitude, latitude, member);
    }

    @Override
    public boolean geoAddXX(String key, double longitude, double latitude, Object member) {
        log("geoAddXX", key, longitude, latitude, member);
        return rGeos.geoAddXX(key, longitude, latitude, member);
    }

    @Override
    public CompletableFuture<Boolean> geoAddXXAsync(String key, double longitude, double latitude, Object member) {
        log("geoAddXXAsync", key, longitude, latitude, member);
        return rGeos.geoAddXXAsync(key, longitude, latitude, member);
    }

    @Override
    public Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDist", key, firstMember, secondMember, geoUnit);
        return rGeos.geoDist(key, firstMember, secondMember, geoUnit);
    }

    @Override
    public CompletableFuture<Double> geoDistAsync(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDistAsync", key, firstMember, secondMember, geoUnit);
        return rGeos.geoDistAsync(key, firstMember, secondMember, geoUnit);
    }

    @Override
    public Map<Object, String> geoHash(String key, Object... members) {
        log("geoHash", key, members);
        return rGeos.geoHash(key, members);
    }

    @Override
    public CompletableFuture<Map<Object, String>> geoHashAsync(String key, Object... members) {
        log("geoHashAsync", key, members);
        return rGeos.geoHashAsync(key, members);
    }

    @Override
    public Map<Object, double[]> geoPos(String key, Object... members) {
        log("geoPos", key, members);
        return rGeos.geoPos(key, members);
    }

    @Override
    public CompletableFuture<Map<Object, double[]>> geoPosAsync(String key, Object... members) {
        log("geoPosAsync", key, members);
        return rGeos.geoPosAsync(key, members);
    }

    @Override
    public Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadius", key, longitude, latitude, radius, geoUnit);
        return rGeos.geoRadius(key, longitude, latitude, radius, geoUnit);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoRadiusAsync(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadiusAsync", key, longitude, latitude, radius, geoUnit);
        return rGeos.geoRadiusAsync(key, longitude, latitude, radius, geoUnit);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, order);
        return rGeos.geoSearch(key, longitude, latitude, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchAsync", key, longitude, latitude, radius, geoUnit, order);
        return rGeos.geoSearchAsync(key, longitude, latitude, radius, geoUnit, order);
    }

    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, count, order);
        return rGeos.geoSearch(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, longitude, latitude, radius, geoUnit, count, order);
        return rGeos.geoSearchAsync(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, order);
        return rGeos.geoSearch(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchAsync", key, longitude, latitude, width, height, geoUnit, order);
        return rGeos.geoSearchAsync(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, count, order);
        return rGeos.geoSearch(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, longitude, latitude, width, height, geoUnit, count, order);
        return rGeos.geoSearchAsync(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearch", key, member, radius, geoUnit, order);
        return rGeos.geoSearch(key, member, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchAsync", key, member, radius, geoUnit, order);
        return rGeos.geoSearchAsync(key, member, radius, geoUnit, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, member, radius, geoUnit, count, order);
        return rGeos.geoSearch(key, member, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, member, radius, geoUnit, count, order);
        return rGeos.geoSearchAsync(key, member, radius, geoUnit, count, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, member, width, height, geoUnit, order);
        return rGeos.geoSearch(key, member, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchAsync", key, member, width, height, geoUnit, order);
        return rGeos.geoSearchAsync(key, member, width, height, geoUnit, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, member, width, height, geoUnit, count, order);
        return rGeos.geoSearch(key, member, width, height, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, member, width, height, geoUnit, count, order);
        return rGeos.geoSearchAsync(key, member, width, height, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, order);
        return rGeos.geoSearchWithDistance(key, longitude, latitude, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, radius, geoUnit, order);
        return rGeos.geoSearchWithDistanceAsync(key, longitude, latitude, radius, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, count, order);
        return rGeos.geoSearchWithDistance(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, radius, geoUnit, count, order);
        return rGeos.geoSearchWithDistanceAsync(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, order);
        return rGeos.geoSearchWithDistance(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, width, height, geoUnit, order);
        return rGeos.geoSearchWithDistanceAsync(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, count, order);
        return rGeos.geoSearchWithDistance(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, width, height, geoUnit, count, order);
        return rGeos.geoSearchWithDistanceAsync(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, order);
        return rGeos.geoSearchWithDistance(key, member, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, member, radius, geoUnit, order);
        return rGeos.geoSearchWithDistanceAsync(key, member, radius, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, count, order);
        return rGeos.geoSearchWithDistance(key, member, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, member, radius, geoUnit, count, order);
        return rGeos.geoSearchWithDistanceAsync(key, member, radius, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, order);
        return rGeos.geoSearchWithDistance(key, member, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, member, width, height, geoUnit, order);
        return rGeos.geoSearchWithDistanceAsync(key, member, width, height, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, count, order);
        return rGeos.geoSearchWithDistance(key, member, width, height, geoUnit, count, order);
    }

    @Override
    public Map<String, Boolean> hDel(String key, String... fields) {
        log("hDel", key, fields);
        return rHash.hDel(key, fields);
    }

    @Override
    public CompletableFuture<Long> hDelAsync(String key, String... fields) {
        log("hDelAsync", key, fields);
        return rHash.hDelAsync(key, fields);
    }

    @Override
    public Map<String, Boolean> hExists(String key, String... fields) {
        log("hExists", key, fields);
        return rHash.hExists(key, fields);
    }

    @Override
    public Map<String, CompletableFuture<Boolean>> hExistsAsync(String key, String... fields) {
        log("hExistsAsync", key, fields);
        return rHash.hExistsAsync(key, fields);
    }

    @Override
    public Optional<Object> hGet(String key, String field) {
        log("hGet", key, field);
        return rHash.hGet(key, field);
    }

    @Override
    public CompletableFuture<Object> hGetAsync(String key, String field) {
        log("hGetAsync", key, field);
        return rHash.hGetAsync(key, field);
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        log("hGetAll", key);
        return rHash.hGetAll(key);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hGetAllAsync(String key) {
        log("hGetAllAsync", key);
        return rHash.hGetAllAsync(key);
    }

    @Override
    public Object hIncrBy(String key, String field, Number value) {
        log("hIncrBy", key, field, value);
        return rHash.hIncrBy(key, field, value);
    }

    @Override
    public CompletableFuture<Object> hIncrByAsync(String key, String field, Number value) {
        log("hIncrByAsync", key, field, value);
        return rHash.hIncrByAsync(key, field, value);
    }

    @Override
    public Collection<Object> hKeys(String key) {
        log("hKeys", key);
        return rHash.hKeys(key);
    }

    @Override
    public CompletableFuture<Set<Object>> hKeysAsync(String key) {
        log("hKeysAsync", key);
        return rHash.hKeysAsync(key);
    }

    @Override
    public int hLen(String key) {
        log("hLen", key);
        return rHash.hLen(key);
    }

    @Override
    public CompletableFuture<Integer> hLenAsync(String key) {
        log("hLenAsync", key);
        return rHash.hLenAsync(key);
    }

    @Override
    public Map<Object, Object> hmGet(String key, Set<Object> fields) {
        log("hmGet", key);
        if (null == fields || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        return rHash.hmGet(key, fields);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hmGetAsync(String key, Set<Object> fields) {
        log("hmGet", key);
        return rHash.hmGetAsync(key, fields);
    }

    @Override
    public void hmSet(String key, Map<?, ?> kvMap) {
        log("hmSet", key, kvMap);
        rHash.hmSet(key, kvMap);
    }

    @Override
    public CompletableFuture<Void> hmSetAsync(String key, Map<?, ?> kvMap) {
        log("hmSetAsync", key, kvMap);
        return rHash.hmSetAsync(key, kvMap);
    }

    @Override
    public Set<Object> hRandField(String key, int count) {
        log("hRandField", key, count);
        return rHash.hRandField(key, count);
    }

    @Override
    public CompletableFuture<Set<Object>> hRandFieldsAsync(String key, int count) {
        log("hRandFieldsAsync", key, count);
        return rHash.hRandFieldsAsync(key, count);
    }

    @Override
    public Map<Object, Object> hRandFieldWithValues(String key, int count) {
        log("hRandFieldWithValues", key, count);
        return rHash.hRandFieldWithValues(key, count);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hRandFieldWithValuesAsync(String key, int count) {
        log("hRandFieldWithValuesAsync", key, count);
        return rHash.hRandFieldWithValuesAsync(key, count);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern) {
        log("hScan", key, keyPattern);
        return rHash.hScan(key, keyPattern);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count) {
        log("hScan", key, keyPattern, count);
        return rHash.hScan(key, keyPattern, count);
    }

    @Override
    public void hSet(String key, String field, Object value) {
        log("hSet", key, field, value);
        rHash.hSet(key, field, value);
    }

    @Override
    public CompletableFuture<Boolean> hSetAsync(String key, String field, Object value) {
        log("hSetAsync", key, field, value);
        return rHash.hSetAsync(key, field, value);
    }

    @Override
    public void hSetNX(String key, String field, Object value) {
        log("hSetNX", key, field, value);
        rHash.hSetNX(key, field, value);
    }

    @Override
    public CompletableFuture<Boolean> hSetNXAsync(String key, String field, Object value) {
        log("hSetNXAsync", key, field, value);
        return rHash.hSetNXAsync(key, field, value);
    }

    @Override
    public int hStrLen(String key, String field) {
        log("hStrLen", key, field);
        return rHash.hStrLen(key, field);
    }

    @Override
    public CompletableFuture<Integer> hStrLenAsync(String key, String field) {
        log("hStrLenAsync", key, field);
        return rHash.hStrLenAsync(key, field);
    }

    @Override
    public Collection<Object> hVALs(String key) {
        log("hVALs", key);
        return rHash.hVALs(key);
    }

    @Override
    public CompletableFuture<Collection<Object>> hVALsAsync(String key) {
        log("hVALsAsync", key);
        return rHash.hVALsAsync(key);
    }

    @Override
    public boolean pfAdd(String key, Collection<Object> elements) {
        log("pfAdd", key, elements);
        return rHyperLogLog.pfAdd(key, elements);
    }

    @Override
    public CompletableFuture<Boolean> pfAddAsync(String key, Collection<Object> elements) {
        log("pfAddAsync", key, elements);
        return rHyperLogLog.pfAddAsync(key, elements);
    }

    @Override
    public long pfCount(String key) {
        log("pfCount", key);
        return rHyperLogLog.pfCount(key);
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key) {
        log("pfCountAsync", key);
        return rHyperLogLog.pfCountAsync(key);
    }

    @Override
    public long pfCount(String key, String... otherKeys) {
        log("pfCount", key, otherKeys);
        return rHyperLogLog.pfCount(key, otherKeys);
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key, String... otherKeys) {
        log("pfCountAsync", key, otherKeys);
        return rHyperLogLog.pfCountAsync(key, otherKeys);
    }

    @Override
    public void pfMerge(String destKey, String... sourceKeys) {
        log("pfMerge", destKey, sourceKeys);
        rHyperLogLog.pfMerge(destKey, sourceKeys);
    }

    @Override
    public CompletableFuture<Void> pfMergeAsync(String destKey, String... sourceKeys) {
        log("pfMergeAsync", destKey, sourceKeys);
        return rHyperLogLog.pfMergeAsync(destKey, sourceKeys);
    }

    @Override
    public Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft) {
        log("blMove", source, destination, timeout, pollLeft);
        return rLists.blMove(source, destination, timeout, pollLeft);
    }

    @Override
    public CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft) {
        log("blMoveAsync", source, destination, timeout, pollLeft);
        return rLists.blMoveAsync(source, destination, timeout, pollLeft);
    }

    @Override
    public Optional<Object> blPop(String key) {
        log("blPop", key);
        return rLists.blPop(key);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key) {
        log("blPopAsync", key);
        return rLists.blPopAsync(key);
    }

    @Override
    public List<Object> blPop(String key, int count) {
        log("blPop", key, count);
        return rLists.blPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> blPopAsync(String key, int count) {
        log("blPopAsync", key, count);
        return rLists.blPopAsync(key, count);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("blPop", key, timeout, unit);
        return rLists.blPop(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit) {
        log("blPop", key, timeout, unit);
        return rLists.blPopAsync(key, timeout, unit);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("blPop", key, timeout, unit, otherKeys);
        return rLists.blPop(key, timeout, unit, otherKeys);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        log("blPopAsync", key, timeout, unit, otherKeys);
        return rLists.blPopAsync(key, timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> brPop(String key) {
        log("brPop", key);
        return rLists.brPop(key);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key) {
        return rLists.brPopAsync(key);
    }

    @Override
    public List<Object> brPop(String key, int count) {
        log("brPop", key, count);
        return rLists.brPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> brPopAsync(String key, int count) {
        log("brPopAsync", key, count);
        return rLists.brPopAsync(key, count);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("brPop", key, timeout, unit);
        return rLists.brPop(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit) {
        log("brPopAsync", key, timeout, unit);
        return rLists.brPopAsync(key, timeout, unit);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("brPop", key, timeout, unit, otherKeys);
        return rLists.brPop(key, timeout, unit, otherKeys);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        log("brPopAsync", key, timeout, unit, otherKeys);
        return rLists.brPopAsync(key, timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> brPopLPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException {
        log("brPopLPush", source, destination, timeout, unit);
        return rLists.brPopLPush(source, destination, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit) {
        log("brPopLPushAsync", source, destination, timeout, unit);
        return rLists.brPopLPushAsync(source, destination, timeout, unit);
    }

    @Override
    public Optional<Object> lIndex(String key, int index) {
        log("lIndex", key, index);
        return rLists.lIndex(key, index);
    }

    @Override
    public CompletableFuture<Object> lIndexAsync(String key, int index) {
        log("lIndexAsync", key, index);
        return rLists.lIndexAsync(key, index);
    }

    @Override
    public int lInsert(String key, boolean before, Object pivot, Object element) {
        log("lInsert", key, before, pivot, element);
        return rLists.lInsert(key, before, pivot, element);
    }

    @Override
    public CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element) {
        log("lInsertAsync", key, before, pivot, element);
        return rLists.lInsertAsync(key, before, pivot, element);
    }

    @Override
    public int llen(String key) {
        log("llen", key);
        return rLists.llen(key);
    }

    @Override
    public CompletableFuture<Integer> llenAsync(String key) {
        log("llenAsync", key);
        return rLists.llenAsync(key);
    }

    @Override
    public Optional<Object> lMove(String source, String destination, boolean pollLeft) {
        log("lMove", source, destination, pollLeft);
        return rLists.lMove(source, destination, pollLeft);
    }

    @Override
    public CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft) {
        log("lMove", source, destination, pollLeft);
        return rLists.lMoveAsync(source, destination, pollLeft);
    }

    @Override
    public List<Object> lPop(String key, int count) {
        log("lPop", key, count);
        return rLists.lPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> lPopAsync(String key, int count) {
        log("lPopAsync", key, count);
        return rLists.lPopAsync(key, count);
    }

    @Override
    public int lPush(String key, Object... elements) {
        log("lPush", key, elements);
        return rLists.lPush(key, elements);
    }

    @Override
    public int lPushX(String key, Object... elements) {
        log("lPushX", key, elements);
        return rLists.lPushX(key, elements);
    }

    @Override
    public CompletableFuture<Integer> lPushXAsync(String key, Object... elements) {
        log("lPushXAsync", key, elements);
        return rLists.lPushXAsync(key, elements);
    }

    @Override
    public List<Object> lRange(String key, int fromIndex, int toIndex) {
        log("lRange", key, fromIndex, toIndex);
        return rLists.lRange(key, fromIndex, toIndex);
    }

    @Override
    public CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex) {
        log("lRangeAsync", key, fromIndex, toIndex);
        return rLists.lRangeAsync(key, fromIndex, toIndex);
    }

    @Override
    public boolean lRem(String key, Object element) {
        log("lRem", key, element);
        return rLists.lRem(key, element);
    }

    @Override
    public void lRemAll(String key, Object element) {
        log("lRemAll", key, element);
        rLists.lRemAll(key, Collections.singletonList(element));
    }

    @Override
    public Optional<Object> lRem(String key, int index) {
        log("lRem", key, index);
        return rLists.lRem(key, index);
    }

    @Override
    public CompletableFuture<Object> lRemAsync(String key, int index) {
        log("lRemAsync", key, index);
        return rLists.lRemAsync(key, index);
    }

    @Override
    public CompletableFuture<Boolean> lRemAsync(String key, Object element) {
        log("lRemAsync", key, element);
        return rLists.lRemAsync(key, element);
    }

    @Override
    public CompletableFuture<Boolean> lRemAllAsync(String key, Object element) {
        log("lRemAllAsync", key, element);
        return rLists.lRemAllAsync(key, Collections.singletonList(element));
    }

    @Override
    public void lSet(String key, int index, Object element) {
        log("lSet", key, index, element);
        rLists.lSet(key, index, element);
    }

    @Override
    public CompletableFuture<Void> lSetAsync(String key, int index, Object element) {
        log("lSetAsync", key, index, element);
        return rLists.lSetAsync(key, index, element);
    }

    @Override
    public void lTrim(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        rLists.lTrim(key, fromIndex, toIndex);
    }

    @Override
    public CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        return rLists.lTrimAsync(key, fromIndex, toIndex);
    }

    @Override
    public List<Object> rPop(String key, int count) {
        log("rPop", key, count);
        return rLists.rPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> rPopAsync(String key, int count) {
        log("rPopAsync", key, count);
        return rLists.rPopAsync(key, count);
    }

    @Override
    public Optional<Object> rPopLPush(String source, String destination) {
        log("rPopLPush", source, destination);
        return rLists.rPopLPush(source, destination);
    }

    @Override
    public CompletableFuture<Object> rPopLPushAsync(String source, String destination) {
        log("rPopLPushAsync", source, destination);
        return rLists.rPopLPushAsync(source, destination);
    }

    @Override
    public boolean rPush(String key, Object... elements) {
        log("rPush", key, elements);
        return rLists.rPush(key, elements);
    }

    @Override
    public CompletableFuture<Boolean> rPushAsync(String key, Object... elements) {
        log("rPush", key, elements);
        return rLists.rPushAsync(key, elements);
    }

    @Override
    public int rPushX(String key, Object... elements) {
        log("rPushX", key, elements);
        return rLists.rPushX(key, elements);
    }

    @Override
    public CompletableFuture<Integer> rPushXAsync(String key, Object... elements) {
        log("rPushXAsync", key, elements);
        return rLists.rPushXAsync(key, elements);
    }

    @Override
    public boolean sAdd(String key, Object member) {
        log("sAdd", key, member);
        return rSets.sAdd(key, member);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Object member) {
        log("sAddAsync", key, member);
        return rSets.sAddAsync(key, member);
    }

    @Override
    public boolean sAdd(String key, Collection<?> members) {
        log("sAdd", key, members);
        return rSets.sAdd(key, members);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members) {
        log("sAddAsync", key, members);
        return rSets.sAddAsync(key, members);
    }

    @Override
    public int sCard(String key) {
        log("sCard", key);
        return rSets.sCard(key);
    }

    @Override
    public CompletableFuture<Integer> sCardAsync(String key) {
        log("sCardAsync", key);
        return rSets.sCardAsync(key);
    }

    @Override
    public Set<Object> sDiff(String key, String... otherKeys) {
        log("sDiff", key, otherKeys);
        return rSets.sDiff(key, otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys) {
        log("sDiffAsync", key, otherKeys);
        return rSets.sDiffAsync(key, otherKeys);
    }

    @Override
    public int sDiffStore(String destination, String... keys) {
        log("sDiffStore", destination, keys);
        return rSets.sDiffStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys) {
        log("sDiffStoreAsync", destination, keys);
        return rSets.sDiffStoreAsync(destination, keys);
    }

    @Override
    public Set<Object> sInter(String key, String... otherKeys) {
        log("sinter", key, otherKeys);
        return rSets.sInter(key, otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys) {
        log("sInterAsync", key, otherKeys);
        return rSets.sInterAsync(key, otherKeys);
    }

    @Override
    public int sInterStore(String destination, String... keys) {
        log("sInterStore", destination, keys);
        return rSets.sInterStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys) {
        log("sInterStoreAsync", destination, keys);
        return rSets.sInterStoreAsync(destination, keys);
    }

    @Override
    public boolean sIsMember(String key, Object member) {
        log("sIsMember", key, member);
        return rSets.sIsMember(key, member);
    }

    @Override
    public CompletableFuture<Boolean> sIsMemberAsync(String key, Object member) {
        log("sIsMember", key, member);
        return rSets.sIsMemberAsync(key, member);
    }

    @Override
    public Set<Object> sMembers(String key) {
        log("sMembers", key);
        return rSets.sMembers(key);
    }

    @Override
    public CompletableFuture<Set<Object>> sMembersAsync(String key) {
        log("sMembersAsync", key);
        return rSets.sMembersAsync(key);
    }

    @Override
    public boolean sMove(String source, String destination, Object member) {
        log("sMove", source, destination, member);
        return rSets.sMove(source, destination, member);
    }

    @Override
    public CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member) {
        log("sMoveAsync", source, destination, member);
        return rSets.sMoveAsync(source, destination, member);
    }

    @Override
    public Optional<Object> sPop(String key) {
        log("sPop", key);
        return rSets.sPop(key);
    }

    @Override
    public CompletableFuture<Object> sPopAsync(String key) {
        log("sPopAsync", key);
        return rSets.sPopAsync(key);
    }

    @Override
    public Set<Object> sPop(String key, int count) {
        log("sPop", key, count);
        return rSets.sPop(key, count);
    }

    @Override
    public CompletableFuture<Set<Object>> sPopAsync(String key, int count) {
        log("sPopAsync", key, count);
        return rSets.sPopAsync(key, count);
    }

    @Override
    public Optional<Object> sRandMember(String key) {
        log("sRandMember", key);
        return rSets.sRandMember(key);
    }

    @Override
    public CompletableFuture<Object> sRandMemberAsync(String key) {
        log("sRandMemberAsync", key);
        return rSets.sRandMemberAsync(key);
    }

    @Override
    public Set<Object> sRandMember(String key, int count) {
        log("sRandMember", key, count);
        return rSets.sRandMember(key, count);
    }

    @Override
    public CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count) {
        log("sRandMember", key, count);
        return rSets.sRandMemberAsync(key, count);
    }

    @Override
    public boolean sRem(String key, Collection<?> members) {
        log("sRem", key, members);
        return rSets.sRem(key, members);
    }

    @Override
    public CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members) {
        log("sRemAsync", key, members);
        return rSets.sRemAsync(key, members);
    }

    @Override
    public Iterator<Object> sScan(String key) {
        log("sScan", key);
        return rSets.sScan(key);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern) {
        log("sScan", key, pattern);
        return rSets.sScan(key, pattern);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern, int count) {
        log("sScan", key, pattern, count);
        return rSets.sScan(key, pattern, count);
    }

    @Override
    public Set<Object> sUnion(String key, String... otherKeys) {
        log("sUnion", key, otherKeys);
        return rSets.sUnion(key, otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys) {
        log("sUnionAsync", key, otherKeys);
        return rSets.sUnionAsync(key, otherKeys);
    }

    @Override
    public int sUnionStore(String destination, String... keys) {
        log("sUnionStore", destination, keys);
        return rSets.sUnionStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys) {
        log("sUnionStoreAsync", destination, keys);
        return rSets.sUnionStoreAsync(destination, keys);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmPop", timeout, unit, key, min);
        return rzSet.bzmPop(timeout, unit, key, min);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmPopAsync", timeout, unit, key, min);
        return rzSet.bzmPopAsync(timeout, unit, key, min);
    }

    @Override
    public Collection<Object> bzmPop(String key, boolean min, int count) {
        log("bzmPop", key, min, count);
        return rzSet.bzmPop(key, min, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count) {
        log("bzmPopAsync", key, min, count);
        return rzSet.bzmPopAsync(key, min, count);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmPop", timeout, unit, key, min, otherKeys);
        return rzSet.bzmPop(timeout, unit, key, min, otherKeys);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmPopAsync", timeout, unit, key, min, otherKeys);
        return rzSet.bzmPopAsync(timeout, unit, key, min, otherKeys);
    }

    @Override
    public Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit) {
        log("bzPopMax", key, timeout, unit);
        return rzSet.bzPopMax(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit) {
        log("bzPopMaxAsync", key, timeout, unit);
        return rzSet.bzPopMaxAsync(key, timeout, unit);
    }

    @Override
    public Collection<Object> bzPopMax(String key, int count) {
        log("bzPopMax", key, count);
        return rzSet.bzPopMax(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count) {
        log("bzPopMaxAsync", key, count);
        return rzSet.bzPopMaxAsync(key, count);
    }

    @Override
    public Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit) {
        log("bzPopMin", key, timeout, unit);
        return rzSet.bzPopMin(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> bzPopMinAsync(String key, long timeout, TimeUnit unit) {
        log("bzPopMin", key, timeout, unit);
        return rzSet.bzPopMinAsync(key, timeout, unit);
    }

    @Override
    public Collection<Object> bzPopMin(String key, int count) {
        log("bzPopMin", key, count);
        return rzSet.bzPopMin(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count) {
        log("bzPopMinAsync", key, count);
        return rzSet.bzPopMinAsync(key, count);
    }

    @Override
    public boolean zAdd(String key, double score, Object member) {
        log("zAdd", key, score, member);
        return rzSet.zAdd(key, score, member);
    }

    @Override
    public CompletableFuture<Boolean> zAddAsync(String key, double score, Object member) {
        log("zAddAsync", key, score, member);
        return rzSet.zAddAsync(key, score, member);
    }

    @Override
    public int zAdd(String key, Map<Object, Double> members) {
        log("zAdd", key, members);
        return rzSet.zAdd(key, members);
    }

    @Override
    public CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members) {
        log("zAddAsync", key, members);
        return rzSet.zAddAsync(key, members);
    }

    @Override
    public int zCard(String key) {
        log("zCard", key);
        return rzSet.zCard(key);
    }

    @Override
    public CompletableFuture<Integer> zCardAsync(String key) {
        log("zCardAsync", key);
        return rzSet.zCardAsync(key);
    }

    @Override
    public int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zCount", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zCount(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Integer> zCountAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zCountAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zCountAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    @Override
    public Collection<Object> zDiff(String key, String... keys) {
        log("zDiff", key, keys);
        return rzSet.zDiff(key, keys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys) {
        log("zDiffAsync", key, keys);
        return rzSet.zDiffAsync(key, keys);
    }

    @Override
    public int zDiffStore(String destination, String... keys) {
        log("zDiffStore", destination, keys);
        return rzSet.zDiffStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys) {
        log("zDiffStoreAsync", destination, keys);
        return rzSet.zDiffStoreAsync(destination, keys);
    }

    @Override
    public Double zIncrBy(String key, Number increment, Object member) {
        log("zIncrBy", key, increment, member);
        return rzSet.zIncrBy(key, increment, member);
    }

    @Override
    public CompletableFuture<Double> zIncrByAsync(String key, Number increment, Object member) {
        log("zIncrByAsync", key, increment, member);
        return rzSet.zIncrByAsync(key, increment, member);
    }

    @Override
    public Collection<Object> zInter(String key, String... otherKeys) {
        log("zInter", key, otherKeys);
        return rzSet.zInter(key, otherKeys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys) {
        log("zInterAsync", key, otherKeys);
        return rzSet.zInterAsync(key, otherKeys);
    }

    @Override
    public int zInterStore(String destination, String... otherKeys) {
        log("zInterStore", destination, otherKeys);
        return rzSet.zInterStore(destination, otherKeys);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys) {
        log("zInterStoreAsync", destination, otherKeys);
        return rzSet.zInterStoreAsync(destination, otherKeys);
    }

    @Override
    public int zInterStoreAggregate(String destination, String aggregate, String... otherKeys) {
        log("zInterStoreAggregate", destination, aggregate, otherKeys);
        return rzSet.zInterStoreAggregate(destination, aggregate, otherKeys);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAggregateAsync(String destination, String aggregate, String... otherKeys) {
        log("zInterStoreAggregateAsync", destination, aggregate, otherKeys);
        return rzSet.zInterStoreAggregateAsync(destination, aggregate, otherKeys);
    }

    @Override
    public int zInterStore(String destination, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, keyWithWeight);
        return rzSet.zInterStore(destination, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, keyWithWeight);
        return rzSet.zInterStoreAsync(destination, keyWithWeight);
    }

    @Override
    public int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, aggregate, keyWithWeight);
        return rzSet.zInterStore(destination, aggregate, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zInterStoreAsync", destination, aggregate, keyWithWeight);
        return rzSet.zInterStoreAsync(destination, aggregate, keyWithWeight);
    }

    @Override
    public int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zLexCount", fromElement, fromInclusive, toElement, toInclusive);
        return rzSet.zLexCount(key, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zLexCountAsync", fromElement, fromInclusive, toElement, toInclusive);
        return rzSet.zLexCountAsync(key, fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    @Override
    public int zLexCountHead(String key, String toElement, boolean toInclusive) {
        log("zLexCountHead", toElement, toInclusive);
        return rzSet.zLexCountHead(key, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive) {
        log("zLexCountHeadAsync", toElement, toInclusive);
        return rzSet.zLexCountHeadAsync(key, toElement, toInclusive);
    }

    @Override
    public int zLexCountTail(String key, String fromElement, boolean fromInclusive) {
        log("zLexCountTail", fromElement, fromInclusive);
        return rzSet.zLexCountTail(key, fromElement, fromInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountTailAsync(String key, String fromElement, boolean fromInclusive) {
        log("zLexCountTailAsync", fromElement, fromInclusive);
        return rzSet.zLexCountTailAsync(key, fromElement, fromInclusive);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min) {
        log("zmPop", key, min);
        return rzSet.zmPop(key, min);
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min) {
        log("zmPopAsync", key, min);
        return rzSet.zmPopAsync(key, min);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmPop", key, min, timeout, unit, otherKeys);
        return rzSet.zmPop(key, min, timeout, unit, otherKeys);
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmPopAsync", key, min, timeout, unit, otherKeys);
        return rzSet.zmPopAsync(key, min, timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> zPopMax(String key) {
        log("zPopMax", key);
        return rzSet.zPopMax(key);
    }

    @Override
    public CompletableFuture<Object> zPopMaxAsync(String key) {
        log("zPopMaxAsync", key);
        return rzSet.zPopMaxAsync(key);
    }

    @Override
    public Collection<Object> zPopMax(String key, int count) {
        log("zPopMax", key, count);
        return rzSet.zPopMax(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count) {
        log("zPopMaxAsync", key, count);
        return rzSet.zPopMaxAsync(key, count);
    }

    @Override
    public Optional<Object> zPopMin(String key) {
        log("zPopMin", key);
        return rzSet.zPopMin(key);
    }

    @Override
    public CompletableFuture<Object> zPopMinAsync(String key) {
        log("zPopMinAsync", key);
        return rzSet.zPopMinAsync(key);
    }

    @Override
    public Collection<Object> zPopMin(String key, int count) {
        log("zPopMin", key, count);
        return rzSet.zPopMin(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count) {
        log("zPopMinAsync", key, count);
        return rzSet.zPopMinAsync(key, count);
    }

    @Override
    public Optional<Object> zRandMember(String key) {
        log("zRandMember", key);
        return rzSet.zRandMember(key);
    }

    @Override
    public CompletableFuture<Object> zRandMemberAsync(String key) {
        log("zRandMemberAsync", key);
        return rzSet.zRandMemberAsync(key);
    }

    @Override
    public Collection<Object> zRandMember(String key, int count) {
        log("zRandMember", key, count);
        return rzSet.zRandMember(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count) {
        log("zRandMemberAsync", key, count);
        return rzSet.zRandMemberAsync(key, count);
    }

    @Override
    public Collection<Object> zRange(String key, int startIndex, int endIndex) {
        log("zRange", key, startIndex, endIndex);
        return rzSet.zRange(key, startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, int startIndex, int endIndex) {
        log("zRangeAsync", key, startIndex, endIndex);
        return rzSet.zRangeAsync(key, startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRange", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zRange(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zRangeAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRange", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return rzSet.zRange(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return rzSet.zRangeAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, int startIndex, int endIndex) {
        log("zRangeReversed", key, startIndex, endIndex);
        return rzSet.zRangeReversed(key, startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex) {
        log("zRangeReversedAsync", key, startIndex, endIndex);
        return rzSet.zRangeReversedAsync(key, startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zRangeReversed(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeReversedAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zRangeReversedAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return rzSet.zRangeReversed(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeReversedAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return rzSet.zRangeReversedAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Optional<Integer> zRank(String key, Object member) {
        log("zRank", key, member);
        return rzSet.zRank(key, member);
    }

    @Override
    public CompletableFuture<Integer> zRankAsync(String key, Object member) {
        log("zRankAsync", key, member);
        return rzSet.zRankAsync(key, member);
    }

    @Override
    public boolean zRem(String key, Collection<?> members) {
        log("zRem", key, members);
        return rzSet.zRem(key, members);
    }

    @Override
    public CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members) {
        log("zRemAsync", key, members);
        return rzSet.zRemAsync(key, members);
    }

    @Override
    public Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zRemRangeByLex", key, fromElement, fromInclusive, toElement, toInclusive);
        return rzSet.zRemRangeByLex(key, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByLexAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zRemRangeByLexAsync", key, fromElement, fromInclusive, toElement, toInclusive);
        return rzSet.zRemRangeByLexAsync(key, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex) {
        log("zRemRangeByRank", key, startIndex, endIndex);
        return rzSet.zRemRangeByRank(key, startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex) {
        log("zRemRangeByRankAsync", key, startIndex, endIndex);
        return rzSet.zRemRangeByRankAsync(key, startIndex, endIndex);
    }

    @Override
    public Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRemRangeByScore", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zRemRangeByScore(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByScoreAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRemRangeByScoreAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return rzSet.zRemRangeByScoreAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Optional<Integer> zRevRank(String key, Object member) {
        log("zRevRank", key, member);
        return rzSet.zRevRank(key, member);
    }

    @Override
    public CompletableFuture<Integer> zRevRankAsync(String key, Object member) {
        log("zRevRankAsync", key, member);
        return rzSet.zRevRankAsync(key, member);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern) {
        log("zScan", key, pattern);
        return rzSet.zScan(key, pattern);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern, int count) {
        log("zScan", key, pattern, count);
        return rzSet.zScan(key, pattern, count);
    }

    @Override
    public List<Double> zScore(String key, List<Object> members) {
        log("zScore", key, members);
        return rzSet.zScore(key, members);
    }

    @Override
    public CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members) {
        log("zScoreAsync", key, members);
        return rzSet.zScoreAsync(key, members);
    }

    @Override
    public Collection<Object> zUnion(String key, String... otherKeys) {
        log("zUnion", key, otherKeys);
        return rzSet.zUnion(key, otherKeys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys) {
        log("zUnionAsync", key, otherKeys);
        return rzSet.zUnionAsync(key, otherKeys);
    }

    @Override
    public int zUnionStore(String destination, String... keys) {
        log("zUnionStore", destination, keys);
        return rzSet.zUnionStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys) {
        log("zUnionStoreAsync", destination, keys);
        return rzSet.zUnionStoreAsync(destination, keys);
    }

    @Override
    public int zUnionStoreAggregate(String destination, String aggregate, String... keys) {
        log("zUnionStoreAggregate", destination, aggregate, keys);
        return rzSet.zUnionStoreAggregate(destination, aggregate, keys);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAggregateAsync(String destination, String aggregate, String... keys) {
        log("zUnionStoreAggregateAsync", destination, aggregate, keys);
        return rzSet.zUnionStoreAggregateAsync(destination, aggregate, keys);
    }

    @Override
    public int zUnionStore(String destination, Map<String, Double> keyWithWeight) {
        log("zUnionStore", destination, keyWithWeight);
        return rzSet.zUnionStore(destination, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        log("zUnionStoreAsync", destination, keyWithWeight);
        return rzSet.zUnionStoreAsync(destination, keyWithWeight);
    }

    @Override
    public int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zUnionStore", destination, aggregate, keyWithWeight);
        return rzSet.zUnionStore(destination, aggregate, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zUnionStoreAsync", destination, aggregate, keyWithWeight);
        return rzSet.zUnionStoreAsync(destination, aggregate, keyWithWeight);
    }

    @Override
    public void append(String key, Object value) {
        log("append", key, value);
        rString.append(key, value);
    }

    @Override
    public long decr(String key) {
        log("decr", key);
        return rString.decr(key);
    }

    @Override
    public CompletableFuture<Long> decrAsync(String key) {
        log("decrAsync", key);
        return rString.decrAsync(key);
    }

    @Override
    public long decrBy(String key, long decrement) {
        log("decrBy", key, decrement);
        return rString.decrBy(key, decrement);
    }

    @Override
    public CompletableFuture<Long> decrByAsync(String key, long decrement) {
        log("decrByAsync", key, decrement);
        return rString.decrByAsync(key, decrement);
    }

    @Override
    public Optional<Object> get(String key) {
        log("get", key);
        return rString.get(key);
    }

    @Override
    public Optional<Object> getObject(String key) {
        log("getObject", key);
        return rString.getObject(key);
    }

    @Override
    public CompletableFuture<Object> getAsync(String key) {
        log("getAsync", key);
        return rString.getAsync(key);
    }

    @Override
    public CompletableFuture<Object> getObjectAsync(String key) {
        log("getObjectAsync", key);
        return rString.getObjectAsync(key);
    }

    @Override
    public Optional<Object> getDel(String key) {
        log("getDel", key);
        return rString.getDel(key);
    }

    @Override
    public CompletableFuture<Object> getDelAsync(String key) {
        log("getDelAsync", key);
        return rString.getDelAsync(key);
    }

    @Override
    public long getLong(String key) {
        log("getLong", key);
        return rString.getLong(key);
    }

    @Override
    public CompletableFuture<Long> getLongAsync(String key) {
        log("getLongAsync", key);
        return rString.getLongAsync(key);
    }

    @Override
    public long incr(String key) {
        log("incr", key);
        return rString.incr(key);
    }

    @Override
    public CompletableFuture<Long> incrAsync(String key) {
        log("incrAsync", key);
        return rString.incrAsync(key);
    }

    @Override
    public long incrBy(String key, long increment) {
        log("incrBy", key, increment);
        return rString.incrBy(key, increment);
    }

    @Override
    public CompletableFuture<Long> incrByAsync(String key, long increment) {
        log("incrByAsync", key, increment);
        return rString.incrByAsync(key, increment);
    }

    @Override
    public double getDouble(String key) {
        log("getDouble", key);
        return rString.getDouble(key);
    }

    @Override
    public CompletableFuture<Double> getDoubleAsync(String key) {
        log("getDoubleAsync", key);
        return rString.getDoubleAsync(key);
    }

    @Override
    public double incrByFloat(String key, double increment) {
        log("incrByFloat", key, increment);
        return rString.incrByFloat(key, increment);
    }

    @Override
    public CompletableFuture<Double> incrByFloatAsync(String key, double increment) {
        log("incrByFloatAsync", key, increment);
        return rString.incrByFloatAsync(key, increment);
    }

    @Override
    public boolean compareAndSet(String key, long expect, long update) {
        log("compareAndSet", key, expect, update);
        return rString.compareAndSet(key, expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update) {
        log("compareAndSetAsync", key, expect, update);
        return rString.compareAndSetAsync(key, expect, update);
    }

    @Override
    public boolean compareAndSet(String key, double expect, double update) {
        log("compareAndSet", key, expect, update);
        return rString.compareAndSet(key, expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update) {
        log("compareAndSetAsync", key, expect, update);
        return rString.compareAndSetAsync(key, expect, update);
    }

    @Override
    public void setObject(String key, Object value) {
        log("setObject", key, value);
        rString.setObject(key, value);
    }

    @Override
    public CompletableFuture<Void> setObjectAsync(String key, Object value) {
        log("setObjectAsync", key, value);
        return rString.setObjectAsync(key, value);
    }

    @Override
    public Map<String, Object> mGet(String... keys) {
        log("mGet", Arrays.toString(keys));
        return rString.mGet(keys);
    }

    @Override
    public CompletableFuture<Map<String, Object>> mGetAsync(String... keys) {
        log("mGetAsync", Arrays.toString(keys));
        return rString.mGetAsync(keys);
    }

    @Override
    public void mSet(Map<String, String> kvMap) {
        log("mSet", kvMap);
        rString.mSet(kvMap);
    }

    @Override
    public CompletableFuture<Void> mSetAsync(Map<String, String> kvMap) {
        log("mSetAsync", kvMap);
        return rString.mSetAsync(kvMap);
    }

    @Override
    public boolean mSetNX(Map<String, String> kvMap) {
        log("mSetNX", kvMap);
        return rString.mSetNX(kvMap);
    }

    @Override
    public CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap) {
        log("mSetNXAsync", kvMap);
        return rString.mSetNXAsync(kvMap);
    }

    @Override
    public void set(String key, String value) {
        log("set", key, value);
        rString.set(key, value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        log("set", key, value);
        return rString.setAsync(key, value);
    }

    @Override
    public void set(String key, Long value) {
        log("set", key, value);
        rString.set(key, value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Long value) {
        log("setAsync", key, value);
        return rString.setAsync(key, value);
    }

    @Override
    public void set(String key, Double value) {
        log("set", key, value);
        rString.set(key, value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Double value) {
        log("setAsync", key, value);
        return rString.setAsync(key, value);
    }

    @Override
    public boolean compareAndSet(String key, String expect, String update) {
        log("compareAndSet", key, expect, update);
        return rString.compareAndSet(key, expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update) {
        log("compareAndSetAsync", key, expect, update);
        return rString.compareAndSetAsync(key, expect, update);
    }

    @Override
    public void setEX(String key, String value, Duration duration) {
        log("setEX", key, value, duration);
        rString.setEX(key, value, duration);
    }

    @Override
    public CompletableFuture<Void> setEXAsync(String key, String value, Duration duration) {
        log("setEXAsync", key, value, duration);
        return rString.setEXAsync(key, value, duration);
    }

    @Override
    public long strLen(String key) {
        log("strlen", key);
        return rString.strLen(key);
    }

    @Override
    public CompletableFuture<Long> strLenAsync(String key) {
        log("strLenAsync", key);
        return rString.strLenAsync(key);
    }

    @Override
    public boolean bfAdd(String key, Object item) {
        log("bfAdd", key, item);
        return rBloomFilters.bfAdd(key, item);
    }

    @Override
    public long bfCard(String key) {
        log("bfCard", key);
        return rBloomFilters.bfCard(key);
    }

    @Override
    public boolean bfExists(String key, Object item) {
        log("bfExists", key);
        return rBloomFilters.bfExists(key, item);
    }

    @Override
    public boolean bfmAdd(String key, Object item) {
        log("bfmAdd", key, item);
        return rBloomFilters.bfmAdd(key, item);
    }

    @Override
    public boolean bfReserve(String key, long expectedInsertions, double falseProbability) {
        log("bfReserve", key, expectedInsertions, falseProbability);
        return rBloomFilters.bfReserve(key, expectedInsertions, falseProbability);
    }

    @Override
    public boolean deleteBf(String key) {
        log("deleteBf", key);
        return rBloomFilters.deleteBf(key);
    }

    @Override
    public CompletableFuture<Boolean> deleteBfAsync(String key) {
        log("deleteBfAsync", key);
        return rBloomFilters.deleteBfAsync(key);
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, leaseTime, unit);
        return rLocks.tryLock(key, waitTime, leaseTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit) {
        log("tryLockAsync", key, waitTime, leaseTime, unit);
        return rLocks.tryLockAsync(key, waitTime, leaseTime, unit);
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, unit);
        return rLocks.tryLock(key, waitTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit) {
        log("tryLockAsync", key, waitTime, unit);
        return rLocks.tryLockAsync(key, waitTime, unit);
    }

    @Override
    public void unlock(String key) {
        log("unlock", key);
        rLocks.unlock(key);
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key) {
        log("unlockAsync", key);
        return rLocks.unlockAsync(key);
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key, long threadId) {
        log("unlockAsync", key, threadId);
        return rLocks.unlockAsync(key, threadId);
    }

    @Override
    public boolean forceUnlock(String key) {
        log("forceUnlock", key);
        return rLocks.forceUnlock(key);
    }

    @Override
    public CompletableFuture<Boolean> forceUnlockAsync(String key) {
        log("forceUnlockAsync", key);
        return rLocks.forceUnlockAsync(key);
    }

    @Override
    public Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScript", script, keys, values);
        return Optional.ofNullable(executeScriptRFuture(script, keys, values).join());
    }

    @Override
    public CompletableFuture<Object> executeScriptAsync(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScriptAsync", script, keys, values);
        return executeScriptRFuture(script, keys, values);
    }

    private CompletableFuture<Object> executeScriptRFuture(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        /* evalSha: 此处必须加上 StringCodec.INSTANCE */
        final RScript rScript = getDataSource().getScript(StringCodec.INSTANCE);
        String shaDigests = sha1DigestAsHex(script);
        return rScript.evalShaAsync(RScript.Mode.READ_WRITE, shaDigests, RScript.ReturnType.VALUE, keys, values)
                .handle((result, error) -> {
                    if (error instanceof RedisException) {
                        return rScript.evalAsync(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values)
                                .toCompletableFuture()
                                .join();
                    }
                    return result;
                })
                .toCompletableFuture();
    }

    @Override
    public long exists(String... keys) {
        log("exists", Arrays.toString(keys));
        return rGeneric.exists(keys);
    }

    @Override
    public CompletableFuture<Long> existsAsync(String... keys) {
        log("existsAsync", Arrays.toString(keys));
        return rGeneric.existsAsync(keys);
    }

    @Override
    public boolean expire(String key, long timeToLive, TimeUnit timeUnit) {
        log("expire", key, timeToLive, timeUnit);
        return rGeneric.expire(key, timeToLive, timeUnit);
    }

    @Override
    public CompletableFuture<Boolean> expireAsync(String key, long timeToLive, TimeUnit timeUnit) {
        log("expireAsync", key, timeToLive, timeUnit);
        return rGeneric.expireAsync(key, timeToLive, timeUnit).toCompletableFuture();
    }

    @Override
    public boolean expireAt(String key, long timestamp) {
        log("expireAt", key, timestamp);
        return rGeneric.expireAt(key, timestamp);
    }

    @Override
    public CompletableFuture<Boolean> expireAtAsync(String key, long timestamp) {
        log("expireAtAsync", key, timestamp);
        return rGeneric.expireAtAsync(key, timestamp).toCompletableFuture();
    }

    @Override
    public long del(String... keys) {
        log("del", Arrays.toString(keys));
        return rGeneric.del(keys);
    }

    @Override
    public CompletableFuture<Long> delAsync(String... keys) {
        log("delAsync", Arrays.toString(keys));
        return rGeneric.delAsync(keys);
    }

    @Override
    public long unlink(String... keys) {
        log("unlink", Arrays.toString(keys));
        return rGeneric.unlink(keys);
    }

    @Override
    public CompletableFuture<Long> unlinkAsync(String... keys) {
        log("unlinkAsync", Arrays.toString(keys));
        return rGeneric.unlinkAsync(keys).toCompletableFuture();
    }

    @Override
    public long ttl(String key) {
        log("ttl", key);
        return rGeneric.ttl(key);
    }

    @Override
    public CompletableFuture<Long> ttlAsync(String key) {
        log("ttlAsync", key);
        return rGeneric.ttlAsync(key);
    }

    @Override
    public long pTTL(String key) {
        log("pTTL", key);
        return rGeneric.pTTL(key);
    }

    @Override
    public CompletableFuture<Long> pTTLAsync(String key) {
        log("pTTLAsync", key);
        return rGeneric.pTTLAsync(key);
    }

    @Override
    public Iterable<String> scan(String keyPattern) {
        log("scan", keyPattern);
        return rGeneric.scan(keyPattern);
    }

    @Override
    public Iterable<String> scan(String keyPattern, int count) {
        log("scan", keyPattern, count);
        return rGeneric.scan(keyPattern, count);
    }

    @Override
    public KeyType type(String key) {
        log("type", key);
        return rGeneric.type(key);
    }

    @Override
    public CompletableFuture<KeyType> typeAsync(String key) {
        log("typeAsync", key);
        return rGeneric.typeAsync(key);
    }

    @Override
    public boolean trySetRateLimiter(String key, long rate, long rateInterval) {
        log("trySetRateLimiter", key, rate, rateInterval);
        return rRateLimiters.trySetRateLimiter(key, rate, rateInterval);
    }

    @Override
    public CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval) {
        log("trySetRateLimiterAsync", key, rate, rateInterval);
        return rRateLimiters.trySetRateLimiterAsync(key, rate, rateInterval);
    }

    @Override
    public boolean tryAcquire(String key) {
        log("tryAcquire", key);
        return rRateLimiters.tryAcquire(key);
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key) {
        log("tryAcquireAsync", key);
        return rRateLimiters.tryAcquireAsync(key);
    }

    @Override
    public boolean tryAcquire(String key, long permits) {
        log("tryAcquire", key, permits);
        return rRateLimiters.tryAcquire(key, permits);
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key, long permits) {
        log("tryAcquireAsync", key, permits);
        return rRateLimiters.tryAcquireAsync(key, permits);
    }

    @Override
    public void destroy() {
        try {
            if (null != this.redissonClient) {
                this.redissonClient.shutdown();
            }
        } catch (Exception e) {
            log.error("org.codeba.redis.keeper.support.DefaultCacheDatasource.destroy()--", e);
        } finally {
            this.rBitMap = null;
            this.rHash = null;
            this.rHyperLogLog = null;
            this.rLists = null;
            this.rSets = null;
            this.rzSet = null;
            this.rString = null;
            this.rBloomFilters = null;
            this.rLocks = null;
            this.rRateLimiters = null;
            this.rGeneric = null;
            this.rGeos = null;
        }

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
