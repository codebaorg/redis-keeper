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
import org.codeba.redis.keeper.core.KBatch;
import org.codeba.redis.keeper.core.KBitSet;
import org.codeba.redis.keeper.core.KBloomFilter;
import org.codeba.redis.keeper.core.KGeneric;
import org.codeba.redis.keeper.core.KHyperLogLog;
import org.codeba.redis.keeper.core.KList;
import org.codeba.redis.keeper.core.KLock;
import org.codeba.redis.keeper.core.KMap;
import org.codeba.redis.keeper.core.KRateLimiter;
import org.codeba.redis.keeper.core.KScript;
import org.codeba.redis.keeper.core.KSet;
import org.codeba.redis.keeper.core.KString;
import org.codeba.redis.keeper.core.KZSet;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
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
import java.util.function.Consumer;

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

    /**
     * The K bit set.
     */
    private KBitSet kBitSet;
    /**
     * The K map.
     */
    private KMap kMap;
    /**
     * The K hyper log log.
     */
    private KHyperLogLog kHyperLogLog;
    /**
     * The K list.
     */
    private KList kList;
    /**
     * The K set.
     */
    private KSet kSet;
    /**
     * The Kz set.
     */
    private KZSet kzSet;
    /**
     * The K string.
     */
    private KString kString;
    /**
     * The K bloom filter.
     */
    private KBloomFilter kBloomFilter;
    /**
     * The K lock.
     */
    private KLock kLock;
    /**
     * The K rate limiter.
     */
    private KRateLimiter kRateLimiter;
    /**
     * The K generic.
     */
    private KGeneric kGeneric;
    /**
     * The K redisson geo.
     */
    private KRedissonGeo kRedissonGeo;
    /**
     * The K script.
     */
    private KScript kScript;

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

        this.kMap = new KRedissonMap(redissonClient, StringCodec.INSTANCE);
        this.kList = new KRedissonList(redissonClient, StringCodec.INSTANCE);
        this.kSet = new KRedissonSet(redissonClient, StringCodec.INSTANCE);
        this.kzSet = new KRedissonZSet(redissonClient, StringCodec.INSTANCE);
        this.kString = new KRedissonString(redissonClient, StringCodec.INSTANCE);
        this.kScript = new KRedissonScript(redissonClient, StringCodec.INSTANCE);
        this.kBitSet = new KRedissonBitSet(redissonClient);
        this.kHyperLogLog = new KRedissonHyperLogLog(redissonClient);
        this.kBloomFilter = new KRedissonBloomFilter(redissonClient);
        this.kLock = new KRedissonLock(redissonClient);
        this.kRateLimiter = new KRedissonRateLimiter(redissonClient);
        this.kGeneric = new KRedissonGeneric(redissonClient);
        this.kRedissonGeo = new KRedissonGeo(redissonClient);
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

    @Override
    public RedissonClient getDataSource() {
        return this.redissonClient;
    }

    @Override
    public long bitCount(String key) {
        log("bitCount", key);
        return kBitSet.bitCount(key);
    }

    @Override
    public CompletableFuture<Long> bitCountAsync(String key) {
        log("bitCountAsync", key);
        return kBitSet.bitCountAsync(key);
    }

    @Override
    public long bitFieldSetSigned(String key, int size, long offset, long value) {
        log("bitFieldSetSigned", key, size, offset, value);
        return kBitSet.bitFieldSetSigned(key, size, offset, value);
    }

    @Override
    public CompletableFuture<Long> bitFieldSetSignedAsync(String key, int size, long offset, long value) {
        log("bitFieldSetSignedAsync", key, size, offset, value);
        return kBitSet.bitFieldSetSignedAsync(key, size, offset, value);
    }

    @Override
    public long bitFieldSetUnSigned(String key, int size, long offset, long value) {
        log("bitFieldSetUnSigned", key, size, offset, value);
        return kBitSet.bitFieldSetUnSigned(key, size, offset, value);
    }

    @Override
    public CompletableFuture<Long> bitFieldSetUnSignedAsync(String key, int size, long offset, long value) {
        log("bitFieldSetUnSignedAsync", key, size, offset, value);
        return kBitSet.bitFieldSetUnSignedAsync(key, size, offset, value);
    }

    @Override
    public long bitFieldGetSigned(String key, int size, long offset) {
        log("bitFieldGetSigned", key, offset);
        return kBitSet.bitFieldGetSigned(key, size, offset);
    }

    @Override
    public CompletableFuture<Long> bitFieldGetSignedAsync(String key, int size, long offset) {
        log("bitFieldGetSignedAsync", key, offset);
        return kBitSet.bitFieldGetSignedAsync(key, size, offset);
    }

    @Override
    public long bitFieldGetUnSigned(String key, int size, long offset) {
        log("bitFieldGetUnSigned", key, offset);
        return kBitSet.bitFieldGetUnSigned(key, size, offset);
    }

    @Override
    public CompletableFuture<Long> bitFieldGetUnSignedAsync(String key, int size, long offset) {
        log("bitFieldGetUnSignedAsync", key, offset);
        return kBitSet.bitFieldGetUnSignedAsync(key, size, offset);
    }

    @Override
    public void bitOpOr(String destKey, String... keys) {
        log("bitOpOr", destKey, keys);
        kBitSet.bitOpOr(destKey, keys);
    }

    @Override
    public CompletableFuture<Void> bitOpOrAsync(String destKey, String... keys) {
        log("bitOpOrAsync", destKey, keys);
        return kBitSet.bitOpOrAsync(destKey, keys);
    }

    @Override
    public boolean getBit(String key, long bitIndex) {
        log("getBit", key, bitIndex);
        return kBitSet.getBit(key, bitIndex);
    }

    @Override
    public CompletableFuture<Boolean> getBitAsync(String key, long bitIndex) {
        log("getBitAsync", key, bitIndex);
        return kBitSet.getBitAsync(key, bitIndex);
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        log("setBit", key, offset, value);
        return kBitSet.setBit(key, offset, value);
    }

    @Override
    public CompletableFuture<Boolean> setBitAsync(String key, long offset, boolean value) {
        log("setBitAsync", key, offset, value);
        return kBitSet.setBitAsync(key, offset, value);
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, Object member) {
        log("geoAdd", key, longitude, latitude, member);
        return kRedissonGeo.geoAdd(key, longitude, latitude, member);
    }

    @Override
    public CompletableFuture<Long> geoAddAsync(String key, double longitude, double latitude, Object member) {
        log("geoAddAsync", key, longitude, latitude, member);
        return kRedissonGeo.geoAddAsync(key, longitude, latitude, member);
    }

    @Override
    public boolean geoAddXX(String key, double longitude, double latitude, Object member) {
        log("geoAddXX", key, longitude, latitude, member);
        return kRedissonGeo.geoAddXX(key, longitude, latitude, member);
    }

    @Override
    public CompletableFuture<Boolean> geoAddXXAsync(String key, double longitude, double latitude, Object member) {
        log("geoAddXXAsync", key, longitude, latitude, member);
        return kRedissonGeo.geoAddXXAsync(key, longitude, latitude, member);
    }

    @Override
    public Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDist", key, firstMember, secondMember, geoUnit);
        return kRedissonGeo.geoDist(key, firstMember, secondMember, geoUnit);
    }

    @Override
    public CompletableFuture<Double> geoDistAsync(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDistAsync", key, firstMember, secondMember, geoUnit);
        return kRedissonGeo.geoDistAsync(key, firstMember, secondMember, geoUnit);
    }

    @Override
    public Map<Object, String> geoHash(String key, Object... members) {
        log("geoHash", key, members);
        return kRedissonGeo.geoHash(key, members);
    }

    @Override
    public CompletableFuture<Map<Object, String>> geoHashAsync(String key, Object... members) {
        log("geoHashAsync", key, members);
        return kRedissonGeo.geoHashAsync(key, members);
    }

    @Override
    public Map<Object, double[]> geoPos(String key, Object... members) {
        log("geoPos", key, members);
        return kRedissonGeo.geoPos(key, members);
    }

    @Override
    public CompletableFuture<Map<Object, double[]>> geoPosAsync(String key, Object... members) {
        log("geoPosAsync", key, members);
        return kRedissonGeo.geoPosAsync(key, members);
    }

    @Override
    public Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadius", key, longitude, latitude, radius, geoUnit);
        return kRedissonGeo.geoRadius(key, longitude, latitude, radius, geoUnit);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoRadiusAsync(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadiusAsync", key, longitude, latitude, radius, geoUnit);
        return kRedissonGeo.geoRadiusAsync(key, longitude, latitude, radius, geoUnit);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, order);
        return kRedissonGeo.geoSearch(key, longitude, latitude, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchAsync", key, longitude, latitude, radius, geoUnit, order);
        return kRedissonGeo.geoSearchAsync(key, longitude, latitude, radius, geoUnit, order);
    }

    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearch(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, longitude, latitude, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearchAsync(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, order);
        return kRedissonGeo.geoSearch(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchAsync", key, longitude, latitude, width, height, geoUnit, order);
        return kRedissonGeo.geoSearchAsync(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearch(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, longitude, latitude, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearchAsync(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearch", key, member, radius, geoUnit, order);
        return kRedissonGeo.geoSearch(key, member, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchAsync", key, member, radius, geoUnit, order);
        return kRedissonGeo.geoSearchAsync(key, member, radius, geoUnit, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, member, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearch(key, member, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, member, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearchAsync(key, member, radius, geoUnit, count, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, member, width, height, geoUnit, order);
        return kRedissonGeo.geoSearch(key, member, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchAsync", key, member, width, height, geoUnit, order);
        return kRedissonGeo.geoSearchAsync(key, member, width, height, geoUnit, order);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, member, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearch(key, member, width, height, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, member, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearchAsync(key, member, width, height, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistance(key, longitude, latitude, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, radius, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, longitude, latitude, radius, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistance(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, longitude, latitude, radius, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistance(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, width, height, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, longitude, latitude, width, height, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistance(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, longitude, latitude, width, height, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistance(key, member, radius, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, member, radius, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, member, radius, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistance(key, member, radius, geoUnit, count, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, member, radius, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, member, radius, geoUnit, count, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistance(key, member, width, height, geoUnit, order);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, member, width, height, geoUnit, order);
        return kRedissonGeo.geoSearchWithDistanceAsync(key, member, width, height, geoUnit, order);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, count, order);
        return kRedissonGeo.geoSearchWithDistance(key, member, width, height, geoUnit, count, order);
    }

    @Override
    public KBatch createBatch() {
        return new KRedissonBatch(redissonClient.createBatch());
    }

    @Override
    public void pipeline(Consumer<KBatch> batchConsumer) {
        KBatch batch = this.createBatch();
        batchConsumer.accept(batch);
        batch.execute();
        // gc
        batch = null;
    }

    @Override
    public List<?> pipelineWithResponses(Consumer<KBatch> batchConsumer) {
        KBatch batch = this.createBatch();
        batchConsumer.accept(batch);
        final List<?> responses = batch.executeWithResponses();
        // gc
        batch = null;
        return responses;
    }

    @Override
    public CompletableFuture<Void> pipelineAsync(Consumer<KBatch> batchConsumer) {
        KBatch batch = this.createBatch();
        batchConsumer.accept(batch);
        final CompletableFuture<Void> voidCompletableFuture = batch.executeAsync();
        // gc
        batch = null;
        return voidCompletableFuture;
    }

    @Override
    public CompletableFuture<List<?>> pipelineWithResponsesAsync(Consumer<KBatch> batchConsumer) {
        KBatch batch = this.createBatch();
        batchConsumer.accept(batch);
        final CompletableFuture<List<?>> listCompletableFuture = batch.executeWithResponsesAsync();
        // gc
        batch = null;
        return listCompletableFuture;
    }

    @Override
    public Map<String, Boolean> hDel(String key, String... fields) {
        log("hDel", key, fields);
        return kMap.hDel(key, fields);
    }

    @Override
    public CompletableFuture<Long> hDelAsync(String key, String... fields) {
        log("hDelAsync", key, fields);
        return kMap.hDelAsync(key, fields);
    }

    @Override
    public Map<String, Boolean> hExists(String key, String... fields) {
        log("hExists", key, fields);
        return kMap.hExists(key, fields);
    }

    @Override
    public Map<String, CompletableFuture<Boolean>> hExistsAsync(String key, String... fields) {
        log("hExistsAsync", key, fields);
        return kMap.hExistsAsync(key, fields);
    }

    @Override
    public Optional<Object> hGet(String key, String field) {
        log("hGet", key, field);
        return kMap.hGet(key, field);
    }

    @Override
    public CompletableFuture<Object> hGetAsync(String key, String field) {
        log("hGetAsync", key, field);
        return kMap.hGetAsync(key, field);
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        log("hGetAll", key);
        return kMap.hGetAll(key);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hGetAllAsync(String key) {
        log("hGetAllAsync", key);
        return kMap.hGetAllAsync(key);
    }

    @Override
    public Object hIncrBy(String key, String field, Number value) {
        log("hIncrBy", key, field, value);
        return kMap.hIncrBy(key, field, value);
    }

    @Override
    public CompletableFuture<Object> hIncrByAsync(String key, String field, Number value) {
        log("hIncrByAsync", key, field, value);
        return kMap.hIncrByAsync(key, field, value);
    }

    @Override
    public Collection<Object> hKeys(String key) {
        log("hKeys", key);
        return kMap.hKeys(key);
    }

    @Override
    public CompletableFuture<Set<Object>> hKeysAsync(String key) {
        log("hKeysAsync", key);
        return kMap.hKeysAsync(key);
    }

    @Override
    public int hLen(String key) {
        log("hLen", key);
        return kMap.hLen(key);
    }

    @Override
    public CompletableFuture<Integer> hLenAsync(String key) {
        log("hLenAsync", key);
        return kMap.hLenAsync(key);
    }

    @Override
    public Map<Object, Object> hmGet(String key, Set<Object> fields) {
        log("hmGet", key);
        if (null == fields || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        return kMap.hmGet(key, fields);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hmGetAsync(String key, Set<Object> fields) {
        log("hmGet", key);
        return kMap.hmGetAsync(key, fields);
    }

    @Override
    public void hmSet(String key, Map<?, ?> kvMap) {
        log("hmSet", key, kvMap);
        kMap.hmSet(key, kvMap);
    }

    @Override
    public CompletableFuture<Void> hmSetAsync(String key, Map<?, ?> kvMap) {
        log("hmSetAsync", key, kvMap);
        return kMap.hmSetAsync(key, kvMap);
    }

    @Override
    public Set<Object> hRandField(String key, int count) {
        log("hRandField", key, count);
        return kMap.hRandField(key, count);
    }

    @Override
    public CompletableFuture<Set<Object>> hRandFieldsAsync(String key, int count) {
        log("hRandFieldsAsync", key, count);
        return kMap.hRandFieldsAsync(key, count);
    }

    @Override
    public Map<Object, Object> hRandFieldWithValues(String key, int count) {
        log("hRandFieldWithValues", key, count);
        return kMap.hRandFieldWithValues(key, count);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hRandFieldWithValuesAsync(String key, int count) {
        log("hRandFieldWithValuesAsync", key, count);
        return kMap.hRandFieldWithValuesAsync(key, count);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern) {
        log("hScan", key, keyPattern);
        return kMap.hScan(key, keyPattern);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count) {
        log("hScan", key, keyPattern, count);
        return kMap.hScan(key, keyPattern, count);
    }

    @Override
    public void hSet(String key, String field, Object value) {
        log("hSet", key, field, value);
        kMap.hSet(key, field, value);
    }

    @Override
    public CompletableFuture<Boolean> hSetAsync(String key, String field, Object value) {
        log("hSetAsync", key, field, value);
        return kMap.hSetAsync(key, field, value);
    }

    @Override
    public void hSetNX(String key, String field, Object value) {
        log("hSetNX", key, field, value);
        kMap.hSetNX(key, field, value);
    }

    @Override
    public CompletableFuture<Boolean> hSetNXAsync(String key, String field, Object value) {
        log("hSetNXAsync", key, field, value);
        return kMap.hSetNXAsync(key, field, value);
    }

    @Override
    public int hStrLen(String key, String field) {
        log("hStrLen", key, field);
        return kMap.hStrLen(key, field);
    }

    @Override
    public CompletableFuture<Integer> hStrLenAsync(String key, String field) {
        log("hStrLenAsync", key, field);
        return kMap.hStrLenAsync(key, field);
    }

    @Override
    public Collection<Object> hVALs(String key) {
        log("hVALs", key);
        return kMap.hVALs(key);
    }

    @Override
    public CompletableFuture<Collection<Object>> hVALsAsync(String key) {
        log("hVALsAsync", key);
        return kMap.hVALsAsync(key);
    }

    @Override
    public boolean pfAdd(String key, Collection<Object> elements) {
        log("pfAdd", key, elements);
        return kHyperLogLog.pfAdd(key, elements);
    }

    @Override
    public CompletableFuture<Boolean> pfAddAsync(String key, Collection<Object> elements) {
        log("pfAddAsync", key, elements);
        return kHyperLogLog.pfAddAsync(key, elements);
    }

    @Override
    public long pfCount(String key) {
        log("pfCount", key);
        return kHyperLogLog.pfCount(key);
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key) {
        log("pfCountAsync", key);
        return kHyperLogLog.pfCountAsync(key);
    }

    @Override
    public long pfCount(String key, String... otherKeys) {
        log("pfCount", key, otherKeys);
        return kHyperLogLog.pfCount(key, otherKeys);
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key, String... otherKeys) {
        log("pfCountAsync", key, otherKeys);
        return kHyperLogLog.pfCountAsync(key, otherKeys);
    }

    @Override
    public void pfMerge(String destKey, String... sourceKeys) {
        log("pfMerge", destKey, sourceKeys);
        kHyperLogLog.pfMerge(destKey, sourceKeys);
    }

    @Override
    public CompletableFuture<Void> pfMergeAsync(String destKey, String... sourceKeys) {
        log("pfMergeAsync", destKey, sourceKeys);
        return kHyperLogLog.pfMergeAsync(destKey, sourceKeys);
    }

    @Override
    public Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft) {
        log("blMove", source, destination, timeout, pollLeft);
        return kList.blMove(source, destination, timeout, pollLeft);
    }

    @Override
    public CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft) {
        log("blMoveAsync", source, destination, timeout, pollLeft);
        return kList.blMoveAsync(source, destination, timeout, pollLeft);
    }

    @Override
    public Optional<Object> blPop(String key) {
        log("blPop", key);
        return kList.blPop(key);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key) {
        log("blPopAsync", key);
        return kList.blPopAsync(key);
    }

    @Override
    public List<Object> blPop(String key, int count) {
        log("blPop", key, count);
        return kList.blPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> blPopAsync(String key, int count) {
        log("blPopAsync", key, count);
        return kList.blPopAsync(key, count);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("blPop", key, timeout, unit);
        return kList.blPop(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit) {
        log("blPop", key, timeout, unit);
        return kList.blPopAsync(key, timeout, unit);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("blPop", key, timeout, unit, otherKeys);
        return kList.blPop(key, timeout, unit, otherKeys);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        log("blPopAsync", key, timeout, unit, otherKeys);
        return kList.blPopAsync(key, timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> brPop(String key) {
        log("brPop", key);
        return kList.brPop(key);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key) {
        return kList.brPopAsync(key);
    }

    @Override
    public List<Object> brPop(String key, int count) {
        log("brPop", key, count);
        return kList.brPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> brPopAsync(String key, int count) {
        log("brPopAsync", key, count);
        return kList.brPopAsync(key, count);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("brPop", key, timeout, unit);
        return kList.brPop(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit) {
        log("brPopAsync", key, timeout, unit);
        return kList.brPopAsync(key, timeout, unit);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("brPop", key, timeout, unit, otherKeys);
        return kList.brPop(key, timeout, unit, otherKeys);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        log("brPopAsync", key, timeout, unit, otherKeys);
        return kList.brPopAsync(key, timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> brPopLPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException {
        log("brPopLPush", source, destination, timeout, unit);
        return kList.brPopLPush(source, destination, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit) {
        log("brPopLPushAsync", source, destination, timeout, unit);
        return kList.brPopLPushAsync(source, destination, timeout, unit);
    }

    @Override
    public Optional<Object> lIndex(String key, int index) {
        log("lIndex", key, index);
        return kList.lIndex(key, index);
    }

    @Override
    public CompletableFuture<Object> lIndexAsync(String key, int index) {
        log("lIndexAsync", key, index);
        return kList.lIndexAsync(key, index);
    }

    @Override
    public int lInsert(String key, boolean before, Object pivot, Object element) {
        log("lInsert", key, before, pivot, element);
        return kList.lInsert(key, before, pivot, element);
    }

    @Override
    public CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element) {
        log("lInsertAsync", key, before, pivot, element);
        return kList.lInsertAsync(key, before, pivot, element);
    }

    @Override
    public int llen(String key) {
        log("llen", key);
        return kList.llen(key);
    }

    @Override
    public CompletableFuture<Integer> llenAsync(String key) {
        log("llenAsync", key);
        return kList.llenAsync(key);
    }

    @Override
    public Optional<Object> lMove(String source, String destination, boolean pollLeft) {
        log("lMove", source, destination, pollLeft);
        return kList.lMove(source, destination, pollLeft);
    }

    @Override
    public CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft) {
        log("lMove", source, destination, pollLeft);
        return kList.lMoveAsync(source, destination, pollLeft);
    }

    @Override
    public List<Object> lPop(String key, int count) {
        log("lPop", key, count);
        return kList.lPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> lPopAsync(String key, int count) {
        log("lPopAsync", key, count);
        return kList.lPopAsync(key, count);
    }

    @Override
    public int lPush(String key, Object... elements) {
        log("lPush", key, elements);
        return kList.lPush(key, elements);
    }

    @Override
    public int lPushX(String key, Object... elements) {
        log("lPushX", key, elements);
        return kList.lPushX(key, elements);
    }

    @Override
    public CompletableFuture<Integer> lPushXAsync(String key, Object... elements) {
        log("lPushXAsync", key, elements);
        return kList.lPushXAsync(key, elements);
    }

    @Override
    public List<Object> lRange(String key, int fromIndex, int toIndex) {
        log("lRange", key, fromIndex, toIndex);
        return kList.lRange(key, fromIndex, toIndex);
    }

    @Override
    public CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex) {
        log("lRangeAsync", key, fromIndex, toIndex);
        return kList.lRangeAsync(key, fromIndex, toIndex);
    }

    @Override
    public boolean lRem(String key, Object element) {
        log("lRem", key, element);
        return kList.lRem(key, element);
    }

    @Override
    public void lRemAll(String key, Object element) {
        log("lRemAll", key, element);
        kList.lRemAll(key, Collections.singletonList(element));
    }

    @Override
    public Optional<Object> lRem(String key, int index) {
        log("lRem", key, index);
        return kList.lRem(key, index);
    }

    @Override
    public CompletableFuture<Object> lRemAsync(String key, int index) {
        log("lRemAsync", key, index);
        return kList.lRemAsync(key, index);
    }

    @Override
    public CompletableFuture<Boolean> lRemAsync(String key, Object element) {
        log("lRemAsync", key, element);
        return kList.lRemAsync(key, element);
    }

    @Override
    public CompletableFuture<Boolean> lRemAllAsync(String key, Object element) {
        log("lRemAllAsync", key, element);
        return kList.lRemAllAsync(key, Collections.singletonList(element));
    }

    @Override
    public void lSet(String key, int index, Object element) {
        log("lSet", key, index, element);
        kList.lSet(key, index, element);
    }

    @Override
    public CompletableFuture<Void> lSetAsync(String key, int index, Object element) {
        log("lSetAsync", key, index, element);
        return kList.lSetAsync(key, index, element);
    }

    @Override
    public void lTrim(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        kList.lTrim(key, fromIndex, toIndex);
    }

    @Override
    public CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        return kList.lTrimAsync(key, fromIndex, toIndex);
    }

    @Override
    public List<Object> rPop(String key, int count) {
        log("rPop", key, count);
        return kList.rPop(key, count);
    }

    @Override
    public CompletableFuture<List<Object>> rPopAsync(String key, int count) {
        log("rPopAsync", key, count);
        return kList.rPopAsync(key, count);
    }

    @Override
    public Optional<Object> rPopLPush(String source, String destination) {
        log("rPopLPush", source, destination);
        return kList.rPopLPush(source, destination);
    }

    @Override
    public CompletableFuture<Object> rPopLPushAsync(String source, String destination) {
        log("rPopLPushAsync", source, destination);
        return kList.rPopLPushAsync(source, destination);
    }

    @Override
    public boolean rPush(String key, Object... elements) {
        log("rPush", key, elements);
        return kList.rPush(key, elements);
    }

    @Override
    public CompletableFuture<Boolean> rPushAsync(String key, Object... elements) {
        log("rPush", key, elements);
        return kList.rPushAsync(key, elements);
    }

    @Override
    public int rPushX(String key, Object... elements) {
        log("rPushX", key, elements);
        return kList.rPushX(key, elements);
    }

    @Override
    public CompletableFuture<Integer> rPushXAsync(String key, Object... elements) {
        log("rPushXAsync", key, elements);
        return kList.rPushXAsync(key, elements);
    }

    @Override
    public boolean sAdd(String key, Object member) {
        log("sAdd", key, member);
        return kSet.sAdd(key, member);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Object member) {
        log("sAddAsync", key, member);
        return kSet.sAddAsync(key, member);
    }

    @Override
    public boolean sAdd(String key, Collection<?> members) {
        log("sAdd", key, members);
        return kSet.sAdd(key, members);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members) {
        log("sAddAsync", key, members);
        return kSet.sAddAsync(key, members);
    }

    @Override
    public int sCard(String key) {
        log("sCard", key);
        return kSet.sCard(key);
    }

    @Override
    public CompletableFuture<Integer> sCardAsync(String key) {
        log("sCardAsync", key);
        return kSet.sCardAsync(key);
    }

    @Override
    public Set<Object> sDiff(String key, String... otherKeys) {
        log("sDiff", key, otherKeys);
        return kSet.sDiff(key, otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys) {
        log("sDiffAsync", key, otherKeys);
        return kSet.sDiffAsync(key, otherKeys);
    }

    @Override
    public int sDiffStore(String destination, String... keys) {
        log("sDiffStore", destination, keys);
        return kSet.sDiffStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys) {
        log("sDiffStoreAsync", destination, keys);
        return kSet.sDiffStoreAsync(destination, keys);
    }

    @Override
    public Set<Object> sInter(String key, String... otherKeys) {
        log("sinter", key, otherKeys);
        return kSet.sInter(key, otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys) {
        log("sInterAsync", key, otherKeys);
        return kSet.sInterAsync(key, otherKeys);
    }

    @Override
    public int sInterStore(String destination, String... keys) {
        log("sInterStore", destination, keys);
        return kSet.sInterStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys) {
        log("sInterStoreAsync", destination, keys);
        return kSet.sInterStoreAsync(destination, keys);
    }

    @Override
    public boolean sIsMember(String key, Object member) {
        log("sIsMember", key, member);
        return kSet.sIsMember(key, member);
    }

    @Override
    public CompletableFuture<Boolean> sIsMemberAsync(String key, Object member) {
        log("sIsMember", key, member);
        return kSet.sIsMemberAsync(key, member);
    }

    @Override
    public Set<Object> sMembers(String key) {
        log("sMembers", key);
        return kSet.sMembers(key);
    }

    @Override
    public CompletableFuture<Set<Object>> sMembersAsync(String key) {
        log("sMembersAsync", key);
        return kSet.sMembersAsync(key);
    }

    @Override
    public boolean sMove(String source, String destination, Object member) {
        log("sMove", source, destination, member);
        return kSet.sMove(source, destination, member);
    }

    @Override
    public CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member) {
        log("sMoveAsync", source, destination, member);
        return kSet.sMoveAsync(source, destination, member);
    }

    @Override
    public Optional<Object> sPop(String key) {
        log("sPop", key);
        return kSet.sPop(key);
    }

    @Override
    public CompletableFuture<Object> sPopAsync(String key) {
        log("sPopAsync", key);
        return kSet.sPopAsync(key);
    }

    @Override
    public Set<Object> sPop(String key, int count) {
        log("sPop", key, count);
        return kSet.sPop(key, count);
    }

    @Override
    public CompletableFuture<Set<Object>> sPopAsync(String key, int count) {
        log("sPopAsync", key, count);
        return kSet.sPopAsync(key, count);
    }

    @Override
    public Optional<Object> sRandMember(String key) {
        log("sRandMember", key);
        return kSet.sRandMember(key);
    }

    @Override
    public CompletableFuture<Object> sRandMemberAsync(String key) {
        log("sRandMemberAsync", key);
        return kSet.sRandMemberAsync(key);
    }

    @Override
    public Set<Object> sRandMember(String key, int count) {
        log("sRandMember", key, count);
        return kSet.sRandMember(key, count);
    }

    @Override
    public CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count) {
        log("sRandMember", key, count);
        return kSet.sRandMemberAsync(key, count);
    }

    @Override
    public boolean sRem(String key, Collection<?> members) {
        log("sRem", key, members);
        return kSet.sRem(key, members);
    }

    @Override
    public CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members) {
        log("sRemAsync", key, members);
        return kSet.sRemAsync(key, members);
    }

    @Override
    public Iterator<Object> sScan(String key) {
        log("sScan", key);
        return kSet.sScan(key);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern) {
        log("sScan", key, pattern);
        return kSet.sScan(key, pattern);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern, int count) {
        log("sScan", key, pattern, count);
        return kSet.sScan(key, pattern, count);
    }

    @Override
    public Set<Object> sUnion(String key, String... otherKeys) {
        log("sUnion", key, otherKeys);
        return kSet.sUnion(key, otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys) {
        log("sUnionAsync", key, otherKeys);
        return kSet.sUnionAsync(key, otherKeys);
    }

    @Override
    public int sUnionStore(String destination, String... keys) {
        log("sUnionStore", destination, keys);
        return kSet.sUnionStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys) {
        log("sUnionStoreAsync", destination, keys);
        return kSet.sUnionStoreAsync(destination, keys);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmPop", timeout, unit, key, min);
        return kzSet.bzmPop(timeout, unit, key, min);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmPopAsync", timeout, unit, key, min);
        return kzSet.bzmPopAsync(timeout, unit, key, min);
    }

    @Override
    public Collection<Object> bzmPop(String key, boolean min, int count) {
        log("bzmPop", key, min, count);
        return kzSet.bzmPop(key, min, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count) {
        log("bzmPopAsync", key, min, count);
        return kzSet.bzmPopAsync(key, min, count);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmPop", timeout, unit, key, min, otherKeys);
        return kzSet.bzmPop(timeout, unit, key, min, otherKeys);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmPopAsync", timeout, unit, key, min, otherKeys);
        return kzSet.bzmPopAsync(timeout, unit, key, min, otherKeys);
    }

    @Override
    public Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit) {
        log("bzPopMax", key, timeout, unit);
        return kzSet.bzPopMax(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit) {
        log("bzPopMaxAsync", key, timeout, unit);
        return kzSet.bzPopMaxAsync(key, timeout, unit);
    }

    @Override
    public Collection<Object> bzPopMax(String key, int count) {
        log("bzPopMax", key, count);
        return kzSet.bzPopMax(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count) {
        log("bzPopMaxAsync", key, count);
        return kzSet.bzPopMaxAsync(key, count);
    }

    @Override
    public Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit) {
        log("bzPopMin", key, timeout, unit);
        return kzSet.bzPopMin(key, timeout, unit);
    }

    @Override
    public CompletableFuture<Object> bzPopMinAsync(String key, long timeout, TimeUnit unit) {
        log("bzPopMin", key, timeout, unit);
        return kzSet.bzPopMinAsync(key, timeout, unit);
    }

    @Override
    public Collection<Object> bzPopMin(String key, int count) {
        log("bzPopMin", key, count);
        return kzSet.bzPopMin(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count) {
        log("bzPopMinAsync", key, count);
        return kzSet.bzPopMinAsync(key, count);
    }

    @Override
    public boolean zAdd(String key, double score, Object member) {
        log("zAdd", key, score, member);
        return kzSet.zAdd(key, score, member);
    }

    @Override
    public boolean zAdd(String key, String member) {
        log("zAdd", key, member);
        return kzSet.zAdd(key, member);
    }

    @Override
    public CompletableFuture<Boolean> zAddAsync(String key, double score, Object member) {
        log("zAddAsync", key, score, member);
        return kzSet.zAddAsync(key, score, member);
    }

    @Override
    public CompletableFuture<Boolean> zAddAsync(String key, String member) {
        log("zAddAsync", key, member);
        return kzSet.zAddAsync(key, member);
    }

    @Override
    public int zAdd(String key, Map<Object, Double> members) {
        log("zAdd", key, members);
        return kzSet.zAdd(key, members);
    }

    @Override
    public boolean zAdd(String key, Collection<? extends String> members) {
        log("zAdd", key, members);
        return kzSet.zAdd(key, members);
    }

    @Override
    public CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members) {
        log("zAddAsync", key, members);
        return kzSet.zAddAsync(key, members);
    }

    @Override
    public CompletableFuture<Boolean> zAddAsync(String key, Collection<? extends String> members) {
        log("zAddAsync", key, members);
        return kzSet.zAddAsync(key, members);
    }

    @Override
    public int zCard(String key) {
        log("zCard", key);
        return kzSet.zCard(key);
    }

    @Override
    public CompletableFuture<Integer> zCardAsync(String key) {
        log("zCardAsync", key);
        return kzSet.zCardAsync(key);
    }

    @Override
    public int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zCount", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zCount(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Integer> zCountAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zCountAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zCountAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    @Override
    public Collection<Object> zDiff(String key, String... keys) {
        log("zDiff", key, keys);
        return kzSet.zDiff(key, keys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys) {
        log("zDiffAsync", key, keys);
        return kzSet.zDiffAsync(key, keys);
    }

    @Override
    public int zDiffStore(String destination, String... keys) {
        log("zDiffStore", destination, keys);
        return kzSet.zDiffStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys) {
        log("zDiffStoreAsync", destination, keys);
        return kzSet.zDiffStoreAsync(destination, keys);
    }

    @Override
    public Double zIncrBy(String key, Number increment, Object member) {
        log("zIncrBy", key, increment, member);
        return kzSet.zIncrBy(key, increment, member);
    }

    @Override
    public CompletableFuture<Double> zIncrByAsync(String key, Number increment, Object member) {
        log("zIncrByAsync", key, increment, member);
        return kzSet.zIncrByAsync(key, increment, member);
    }

    @Override
    public Collection<Object> zInter(String key, String... otherKeys) {
        log("zInter", key, otherKeys);
        return kzSet.zInter(key, otherKeys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys) {
        log("zInterAsync", key, otherKeys);
        return kzSet.zInterAsync(key, otherKeys);
    }

    @Override
    public int zInterStore(String destination, String... otherKeys) {
        log("zInterStore", destination, otherKeys);
        return kzSet.zInterStore(destination, otherKeys);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys) {
        log("zInterStoreAsync", destination, otherKeys);
        return kzSet.zInterStoreAsync(destination, otherKeys);
    }

    @Override
    public int zInterStoreAggregate(String destination, String aggregate, String... otherKeys) {
        log("zInterStoreAggregate", destination, aggregate, otherKeys);
        return kzSet.zInterStoreAggregate(destination, aggregate, otherKeys);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAggregateAsync(String destination, String aggregate, String... otherKeys) {
        log("zInterStoreAggregateAsync", destination, aggregate, otherKeys);
        return kzSet.zInterStoreAggregateAsync(destination, aggregate, otherKeys);
    }

    @Override
    public int zInterStore(String destination, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, keyWithWeight);
        return kzSet.zInterStore(destination, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, keyWithWeight);
        return kzSet.zInterStoreAsync(destination, keyWithWeight);
    }

    @Override
    public int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, aggregate, keyWithWeight);
        return kzSet.zInterStore(destination, aggregate, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zInterStoreAsync", destination, aggregate, keyWithWeight);
        return kzSet.zInterStoreAsync(destination, aggregate, keyWithWeight);
    }

    @Override
    public int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zLexCount", fromElement, fromInclusive, toElement, toInclusive);
        return kzSet.zLexCount(key, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zLexCountAsync", fromElement, fromInclusive, toElement, toInclusive);
        return kzSet.zLexCountAsync(key, fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    @Override
    public int zLexCountHead(String key, String toElement, boolean toInclusive) {
        log("zLexCountHead", toElement, toInclusive);
        return kzSet.zLexCountHead(key, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive) {
        log("zLexCountHeadAsync", toElement, toInclusive);
        return kzSet.zLexCountHeadAsync(key, toElement, toInclusive);
    }

    @Override
    public int zLexCountTail(String key, String fromElement, boolean fromInclusive) {
        log("zLexCountTail", fromElement, fromInclusive);
        return kzSet.zLexCountTail(key, fromElement, fromInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountTailAsync(String key, String fromElement, boolean fromInclusive) {
        log("zLexCountTailAsync", fromElement, fromInclusive);
        return kzSet.zLexCountTailAsync(key, fromElement, fromInclusive);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min) {
        log("zmPop", key, min);
        return kzSet.zmPop(key, min);
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min) {
        log("zmPopAsync", key, min);
        return kzSet.zmPopAsync(key, min);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmPop", key, min, timeout, unit, otherKeys);
        return kzSet.zmPop(key, min, timeout, unit, otherKeys);
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmPopAsync", key, min, timeout, unit, otherKeys);
        return kzSet.zmPopAsync(key, min, timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> zPopMax(String key) {
        log("zPopMax", key);
        return kzSet.zPopMax(key);
    }

    @Override
    public CompletableFuture<Object> zPopMaxAsync(String key) {
        log("zPopMaxAsync", key);
        return kzSet.zPopMaxAsync(key);
    }

    @Override
    public Collection<Object> zPopMax(String key, int count) {
        log("zPopMax", key, count);
        return kzSet.zPopMax(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count) {
        log("zPopMaxAsync", key, count);
        return kzSet.zPopMaxAsync(key, count);
    }

    @Override
    public Optional<Object> zPopMin(String key) {
        log("zPopMin", key);
        return kzSet.zPopMin(key);
    }

    @Override
    public CompletableFuture<Object> zPopMinAsync(String key) {
        log("zPopMinAsync", key);
        return kzSet.zPopMinAsync(key);
    }

    @Override
    public Collection<Object> zPopMin(String key, int count) {
        log("zPopMin", key, count);
        return kzSet.zPopMin(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count) {
        log("zPopMinAsync", key, count);
        return kzSet.zPopMinAsync(key, count);
    }

    @Override
    public Optional<Object> zRandMember(String key) {
        log("zRandMember", key);
        return kzSet.zRandMember(key);
    }

    @Override
    public CompletableFuture<Object> zRandMemberAsync(String key) {
        log("zRandMemberAsync", key);
        return kzSet.zRandMemberAsync(key);
    }

    @Override
    public Collection<Object> zRandMember(String key, int count) {
        log("zRandMember", key, count);
        return kzSet.zRandMember(key, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count) {
        log("zRandMemberAsync", key, count);
        return kzSet.zRandMemberAsync(key, count);
    }

    @Override
    public Collection<Object> zRange(String key, int startIndex, int endIndex) {
        log("zRange", key, startIndex, endIndex);
        return kzSet.zRange(key, startIndex, endIndex);
    }

    @Override
    public Collection<String> zRangeByLEX(String key, int startIndex, int endIndex) {
        log("zRangeByLEX", key, startIndex, endIndex);
        return kzSet.zRangeByLEX(key, startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, int startIndex, int endIndex) {
        log("zRangeAsync", key, startIndex, endIndex);
        return kzSet.zRangeAsync(key, startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRange", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zRange(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<String> zRangeByLEX(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive) {
        log("zRangeByLEX", key, from, startScoreInclusive, to, endScoreInclusive);
        return kzSet.zRangeByLEX(key, from, startScoreInclusive, to, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zRangeAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRange", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return kzSet.zRange(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<String> zRangeByLEX(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive, int offset, int count) {
        log("zRangeByLEX", key, from, startScoreInclusive, to, endScoreInclusive, offset, count);
        return kzSet.zRangeByLEX(key, from, startScoreInclusive, to, endScoreInclusive, offset, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return kzSet.zRangeAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, int startIndex, int endIndex) {
        log("zRangeReversed", key, startIndex, endIndex);
        return kzSet.zRangeReversed(key, startIndex, endIndex);
    }

    @Override
    public Collection<String> zRangeByLEXReversed(String key, String startIndex, String endIndex) {
        log("zRangeByLEXReversed", key, startIndex, endIndex);
        return kzSet.zRangeByLEXReversed(key, startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex) {
        log("zRangeReversedAsync", key, startIndex, endIndex);
        return kzSet.zRangeReversedAsync(key, startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zRangeReversed(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<String> zRangeByLEXReversed(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive) {
        log("zRangeByLEXReversed", key, from, startScoreInclusive, to, endScoreInclusive);
        return kzSet.zRangeByLEXReversed(key, from, startScoreInclusive, to, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeReversedAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zRangeReversedAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return kzSet.zRangeReversed(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<String> zRangeByLEXReversed(String key, String from, boolean startScoreInclusive, String to, boolean endScoreInclusive, int offset, int count) {
        log("zRangeByLEXReversed", key, from, startScoreInclusive, to, endScoreInclusive, offset, count);
        return kzSet.zRangeByLEXReversed(key, from, startScoreInclusive, to, endScoreInclusive, offset, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeReversedAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return kzSet.zRangeReversedAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Optional<Integer> zRank(String key, Object member) {
        log("zRank", key, member);
        return kzSet.zRank(key, member);
    }

    @Override
    public CompletableFuture<Integer> zRankAsync(String key, Object member) {
        log("zRankAsync", key, member);
        return kzSet.zRankAsync(key, member);
    }

    @Override
    public boolean zRem(String key, Collection<?> members) {
        log("zRem", key, members);
        return kzSet.zRem(key, members);
    }

    @Override
    public CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members) {
        log("zRemAsync", key, members);
        return kzSet.zRemAsync(key, members);
    }

    @Override
    public Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zRemRangeByLex", key, fromElement, fromInclusive, toElement, toInclusive);
        return kzSet.zRemRangeByLex(key, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByLexAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zRemRangeByLexAsync", key, fromElement, fromInclusive, toElement, toInclusive);
        return kzSet.zRemRangeByLexAsync(key, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex) {
        log("zRemRangeByRank", key, startIndex, endIndex);
        return kzSet.zRemRangeByRank(key, startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex) {
        log("zRemRangeByRankAsync", key, startIndex, endIndex);
        return kzSet.zRemRangeByRankAsync(key, startIndex, endIndex);
    }

    @Override
    public Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRemRangeByScore", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zRemRangeByScore(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByScoreAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRemRangeByScoreAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return kzSet.zRemRangeByScoreAsync(key, startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Optional<Integer> zRevRank(String key, Object member) {
        log("zRevRank", key, member);
        return kzSet.zRevRank(key, member);
    }

    @Override
    public CompletableFuture<Integer> zRevRankAsync(String key, Object member) {
        log("zRevRankAsync", key, member);
        return kzSet.zRevRankAsync(key, member);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern) {
        log("zScan", key, pattern);
        return kzSet.zScan(key, pattern);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern, int count) {
        log("zScan", key, pattern, count);
        return kzSet.zScan(key, pattern, count);
    }

    @Override
    public List<Double> zScore(String key, List<Object> members) {
        log("zScore", key, members);
        return kzSet.zScore(key, members);
    }

    @Override
    public CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members) {
        log("zScoreAsync", key, members);
        return kzSet.zScoreAsync(key, members);
    }

    @Override
    public Collection<Object> zUnion(String key, String... otherKeys) {
        log("zUnion", key, otherKeys);
        return kzSet.zUnion(key, otherKeys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys) {
        log("zUnionAsync", key, otherKeys);
        return kzSet.zUnionAsync(key, otherKeys);
    }

    @Override
    public int zUnionStore(String destination, String... keys) {
        log("zUnionStore", destination, keys);
        return kzSet.zUnionStore(destination, keys);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys) {
        log("zUnionStoreAsync", destination, keys);
        return kzSet.zUnionStoreAsync(destination, keys);
    }

    @Override
    public int zUnionStoreAggregate(String destination, String aggregate, String... keys) {
        log("zUnionStoreAggregate", destination, aggregate, keys);
        return kzSet.zUnionStoreAggregate(destination, aggregate, keys);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAggregateAsync(String destination, String aggregate, String... keys) {
        log("zUnionStoreAggregateAsync", destination, aggregate, keys);
        return kzSet.zUnionStoreAggregateAsync(destination, aggregate, keys);
    }

    @Override
    public int zUnionStore(String destination, Map<String, Double> keyWithWeight) {
        log("zUnionStore", destination, keyWithWeight);
        return kzSet.zUnionStore(destination, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        log("zUnionStoreAsync", destination, keyWithWeight);
        return kzSet.zUnionStoreAsync(destination, keyWithWeight);
    }

    @Override
    public int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zUnionStore", destination, aggregate, keyWithWeight);
        return kzSet.zUnionStore(destination, aggregate, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zUnionStoreAsync", destination, aggregate, keyWithWeight);
        return kzSet.zUnionStoreAsync(destination, aggregate, keyWithWeight);
    }

    @Override
    public void append(String key, Object value) {
        log("append", key, value);
        kString.append(key, value);
    }

    @Override
    public long decr(String key) {
        log("decr", key);
        return kString.decr(key);
    }

    @Override
    public CompletableFuture<Long> decrAsync(String key) {
        log("decrAsync", key);
        return kString.decrAsync(key);
    }

    @Override
    public long decrBy(String key, long decrement) {
        log("decrBy", key, decrement);
        return kString.decrBy(key, decrement);
    }

    @Override
    public CompletableFuture<Long> decrByAsync(String key, long decrement) {
        log("decrByAsync", key, decrement);
        return kString.decrByAsync(key, decrement);
    }

    @Override
    public Optional<Object> get(String key) {
        log("get", key);
        return kString.get(key);
    }

    @Override
    public InputStream getBinary(String key) {
        log("getBinary", key);
        return kString.getBinary(key);
    }

    @Override
    public Optional<Object> getObject(String key) {
        log("getObject", key);
        return kString.getObject(key);
    }

    @Override
    public CompletableFuture<Object> getAsync(String key) {
        log("getAsync", key);
        return kString.getAsync(key);
    }

    @Override
    public CompletableFuture<Object> getObjectAsync(String key) {
        log("getObjectAsync", key);
        return kString.getObjectAsync(key);
    }

    @Override
    public Optional<Object> getDel(String key) {
        log("getDel", key);
        return kString.getDel(key);
    }

    @Override
    public CompletableFuture<Object> getDelAsync(String key) {
        log("getDelAsync", key);
        return kString.getDelAsync(key);
    }

    @Override
    public long getLong(String key) {
        log("getLong", key);
        return kString.getLong(key);
    }

    @Override
    public CompletableFuture<Long> getLongAsync(String key) {
        log("getLongAsync", key);
        return kString.getLongAsync(key);
    }

    @Override
    public long incr(String key) {
        log("incr", key);
        return kString.incr(key);
    }

    @Override
    public CompletableFuture<Long> incrAsync(String key) {
        log("incrAsync", key);
        return kString.incrAsync(key);
    }

    @Override
    public long incrBy(String key, long increment) {
        log("incrBy", key, increment);
        return kString.incrBy(key, increment);
    }

    @Override
    public CompletableFuture<Long> incrByAsync(String key, long increment) {
        log("incrByAsync", key, increment);
        return kString.incrByAsync(key, increment);
    }

    @Override
    public double getDouble(String key) {
        log("getDouble", key);
        return kString.getDouble(key);
    }

    @Override
    public CompletableFuture<Double> getDoubleAsync(String key) {
        log("getDoubleAsync", key);
        return kString.getDoubleAsync(key);
    }

    @Override
    public double incrByFloat(String key, double increment) {
        log("incrByFloat", key, increment);
        return kString.incrByFloat(key, increment);
    }

    @Override
    public CompletableFuture<Double> incrByFloatAsync(String key, double increment) {
        log("incrByFloatAsync", key, increment);
        return kString.incrByFloatAsync(key, increment);
    }

    @Override
    public boolean compareAndSet(String key, long expect, long update) {
        log("compareAndSet", key, expect, update);
        return kString.compareAndSet(key, expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update) {
        log("compareAndSetAsync", key, expect, update);
        return kString.compareAndSetAsync(key, expect, update);
    }

    @Override
    public boolean compareAndSet(String key, double expect, double update) {
        log("compareAndSet", key, expect, update);
        return kString.compareAndSet(key, expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update) {
        log("compareAndSetAsync", key, expect, update);
        return kString.compareAndSetAsync(key, expect, update);
    }

    @Override
    public void setObject(String key, Object value) {
        log("setObject", key, value);
        kString.setObject(key, value);
    }

    @Override
    public void setObjectEx(String key, Object value, Duration duration) {
        log("setObjectEx", key, value);
        kString.setObjectEx(key, value, duration);
    }

    @Override
    public CompletableFuture<Void> setObjectAsync(String key, Object value) {
        log("setObjectAsync", key, value);
        return kString.setObjectAsync(key, value);
    }

    @Override
    public CompletableFuture<Void> setObjectEXAsync(String key, Object value, Duration duration) {
        log("setObjectAsync", key, value);
        return kString.setObjectEXAsync(key, value, duration);
    }

    @Override
    public Map<String, Object> mGet(String... keys) {
        log("mGet", Arrays.toString(keys));
        return kString.mGet(keys);
    }

    @Override
    public CompletableFuture<Map<String, Object>> mGetAsync(String... keys) {
        log("mGetAsync", Arrays.toString(keys));
        return kString.mGetAsync(keys);
    }

    @Override
    public void mSet(Map<String, String> kvMap) {
        log("mSet", kvMap);
        kString.mSet(kvMap);
    }

    @Override
    public CompletableFuture<Void> mSetAsync(Map<String, String> kvMap) {
        log("mSetAsync", kvMap);
        return kString.mSetAsync(kvMap);
    }

    @Override
    public boolean mSetNX(Map<String, String> kvMap) {
        log("mSetNX", kvMap);
        return kString.mSetNX(kvMap);
    }

    @Override
    public CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap) {
        log("mSetNXAsync", kvMap);
        return kString.mSetNXAsync(kvMap);
    }

    @Override
    public void set(String key, String value) {
        log("set", key, value);
        kString.set(key, value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        log("set", key, value);
        return kString.setAsync(key, value);
    }

    @Override
    public void set(String key, Long value) {
        log("set", key, value);
        kString.set(key, value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Long value) {
        log("setAsync", key, value);
        return kString.setAsync(key, value);
    }

    @Override
    public void set(String key, Double value) {
        log("set", key, value);
        kString.set(key, value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Double value) {
        log("setAsync", key, value);
        return kString.setAsync(key, value);
    }

    @Override
    public CompletableFuture<Boolean> setNXAsync(String key, Object value) {
        log("setNXAsync", key, value);
        return kString.setNXAsync(key, value);
    }

    @Override
    public CompletableFuture<Boolean> setNXAsync(String key, Object value, Duration duration) {
        log("setNXAsync", key, value, duration);
        return kString.setNXAsync(key, value, duration);
    }

    @Override
    public boolean compareAndSet(String key, String expect, String update) {
        log("compareAndSet", key, expect, update);
        return kString.compareAndSet(key, expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update) {
        log("compareAndSetAsync", key, expect, update);
        return kString.compareAndSetAsync(key, expect, update);
    }

    @Override
    public void setEX(String key, String value, Duration duration) {
        log("setEX", key, value, duration);
        kString.setEX(key, value, duration);
    }

    @Override
    public boolean setNX(String key, Object value) {
        log("setNX", key, value);
        return kString.setNX(key, value);
    }

    @Override
    public boolean setNX(String key, Object value, Duration duration) {
        log("setNX", key, value, duration);
        return kString.setNX(key, value, duration);
    }

    @Override
    public CompletableFuture<Void> setEXAsync(String key, String value, Duration duration) {
        log("setEXAsync", key, value, duration);
        return kString.setEXAsync(key, value, duration);
    }

    @Override
    public long strLen(String key) {
        log("strlen", key);
        return kString.strLen(key);
    }

    @Override
    public CompletableFuture<Long> strLenAsync(String key) {
        log("strLenAsync", key);
        return kString.strLenAsync(key);
    }

    @Override
    public boolean bfAdd(String key, Object item) {
        log("bfAdd", key, item);
        return kBloomFilter.bfAdd(key, item);
    }

    @Override
    public long bfCard(String key) {
        log("bfCard", key);
        return kBloomFilter.bfCard(key);
    }

    @Override
    public boolean bfExists(String key, Object item) {
        log("bfExists", key);
        return kBloomFilter.bfExists(key, item);
    }

    @Override
    public boolean bfmAdd(String key, Object item) {
        log("bfmAdd", key, item);
        return kBloomFilter.bfmAdd(key, item);
    }

    @Override
    public boolean bfReserve(String key, long expectedInsertions, double falseProbability) {
        log("bfReserve", key, expectedInsertions, falseProbability);
        return kBloomFilter.bfReserve(key, expectedInsertions, falseProbability);
    }

    @Override
    public boolean deleteBf(String key) {
        log("deleteBf", key);
        return kBloomFilter.deleteBf(key);
    }

    @Override
    public CompletableFuture<Boolean> deleteBfAsync(String key) {
        log("deleteBfAsync", key);
        return kBloomFilter.deleteBfAsync(key);
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, leaseTime, unit);
        return kLock.tryLock(key, waitTime, leaseTime, unit);
    }

    @Override
    public void lock(String key, long leaseTime, TimeUnit unit) {
        log("lock", key, leaseTime, unit);
        kLock.lock(key, leaseTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit) {
        log("tryLockAsync", key, waitTime, leaseTime, unit);
        return kLock.tryLockAsync(key, waitTime, leaseTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit, long threadId) {
        log("tryLockAsync", key, waitTime, leaseTime, unit, threadId);
        return kLock.tryLockAsync(key, waitTime, leaseTime, unit, threadId);
    }

    @Override
    public CompletableFuture<Boolean> isHeldByThreadAsync(String key, long threadId) {
        log("isHeldByThreadAsync", key, threadId);
        return kLock.isHeldByThreadAsync(key, threadId);
    }

    @Override
    public CompletableFuture<Integer> getHoldCountAsync(String key) {
        log("getHoldCountAsync", key);
        return kLock.getHoldCountAsync(key);
    }

    @Override
    public CompletableFuture<Boolean> isLockedAsync(String key) {
        log("isLockedAsync", key);
        return kLock.isLockedAsync(key);
    }

    @Override
    public CompletableFuture<Long> remainTimeToLiveAsync(String key) {
        log("remainTimeToLiveAsync", key);
        return kLock.remainTimeToLiveAsync(key);
    }

    @Override
    public void lock(String key) {
        log("lock", key);
        kLock.lock(key);
    }

    @Override
    public void lockInterruptibly(String key) throws InterruptedException {
        log("lockInterruptibly", key);
        kLock.lockInterruptibly(key);
    }

    @Override
    public boolean tryLock(String key) {
        log("tryLock", key);
        return kLock.tryLock(key);
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, unit);
        return kLock.tryLock(key, waitTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit) {
        log("tryLockAsync", key, waitTime, unit);
        return kLock.tryLockAsync(key, waitTime, unit);
    }

    @Override
    public void unlock(String key) {
        log("unlock", key);
        kLock.unlock(key);
    }

    @Override
    public void lockInterruptibly(String key, long leaseTime, TimeUnit unit) throws InterruptedException {
        log("lockInterruptibly", key, leaseTime, unit);
        kLock.lockInterruptibly(key, leaseTime, unit);
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key) {
        log("unlockAsync", key);
        return kLock.unlockAsync(key);
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key, long threadId) {
        log("unlockAsync", key, threadId);
        return kLock.unlockAsync(key, threadId);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key) {
        log("tryLockAsync", key);
        return kLock.tryLockAsync(key);
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key) {
        log("lockAsync", key);
        return kLock.lockAsync(key);
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key, long threadId) {
        log("lockAsync", key, threadId);
        return kLock.lockAsync(key, threadId);
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key, long leaseTime, TimeUnit unit) {
        log("lockAsync", key, leaseTime, unit);
        return kLock.lockAsync(key, leaseTime, unit);
    }

    @Override
    public CompletableFuture<Void> lockAsync(String key, long leaseTime, TimeUnit unit, long threadId) {
        log("lockAsync", key, leaseTime, unit, threadId);
        return kLock.lockAsync(key, leaseTime, unit, threadId);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long threadId) {
        log("tryLockAsync", key, threadId);
        return kLock.tryLockAsync(key, threadId);
    }

    @Override
    public boolean forceUnlock(String key) {
        log("forceUnlock", key);
        return kLock.forceUnlock(key);
    }

    @Override
    public boolean isLocked(String key) {
        log("isLocked", key);
        return kLock.isLocked(key);
    }

    @Override
    public boolean isHeldByThread(String key, long threadId) {
        log("isHeldByThread", key, threadId);
        return kLock.isHeldByThread(key, threadId);
    }

    @Override
    public boolean isHeldByCurrentThread(String key) {
        log("isHeldByCurrentThread", key);
        return kLock.isHeldByCurrentThread(key);
    }

    @Override
    public int getHoldCount(String key) {
        log("getHoldCount", key);
        return kLock.getHoldCount(key);
    }

    @Override
    public long remainTimeToLive(String key) {
        log("remainTimeToLive", key);
        return kLock.remainTimeToLive(key);
    }

    @Override
    public CompletableFuture<Boolean> forceUnlockAsync(String key) {
        log("forceUnlockAsync", key);
        return kLock.forceUnlockAsync(key);
    }

    @Override
    public Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScript", script, keys, values);
        return kScript.executeScript(script, keys, values);
    }

    @Override
    public Optional<Object> executeScript(ReturnType returnType, String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        return kScript.executeScript(returnType, script, keys, values);
    }

    @Override
    public CompletableFuture<Object> executeScriptAsync(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScriptAsync", script, keys, values);
        return kScript.executeScriptAsync(script, keys, values);
    }

    @Override
    public long exists(String... keys) {
        log("exists", Arrays.toString(keys));
        return kGeneric.exists(keys);
    }

    @Override
    public CompletableFuture<Long> existsAsync(String... keys) {
        log("existsAsync", Arrays.toString(keys));
        return kGeneric.existsAsync(keys);
    }

    @Override
    public boolean expire(String key, long timeToLive, TimeUnit timeUnit) {
        log("expire", key, timeToLive, timeUnit);
        return kGeneric.expire(key, timeToLive, timeUnit);
    }

    @Override
    public CompletableFuture<Boolean> expireAsync(String key, long timeToLive, TimeUnit timeUnit) {
        log("expireAsync", key, timeToLive, timeUnit);
        return kGeneric.expireAsync(key, timeToLive, timeUnit).toCompletableFuture();
    }

    @Override
    public boolean expireAt(String key, long timestamp) {
        log("expireAt", key, timestamp);
        return kGeneric.expireAt(key, timestamp);
    }

    @Override
    public CompletableFuture<Boolean> expireAtAsync(String key, long timestamp) {
        log("expireAtAsync", key, timestamp);
        return kGeneric.expireAtAsync(key, timestamp).toCompletableFuture();
    }

    @Override
    public long del(String... keys) {
        log("del", Arrays.toString(keys));
        return kGeneric.del(keys);
    }

    @Override
    public CompletableFuture<Long> delAsync(String... keys) {
        log("delAsync", Arrays.toString(keys));
        return kGeneric.delAsync(keys);
    }

    @Override
    public long unlink(String... keys) {
        log("unlink", Arrays.toString(keys));
        return kGeneric.unlink(keys);
    }

    @Override
    public CompletableFuture<Long> unlinkAsync(String... keys) {
        log("unlinkAsync", Arrays.toString(keys));
        return kGeneric.unlinkAsync(keys).toCompletableFuture();
    }

    @Override
    public long ttl(String key) {
        log("ttl", key);
        return kGeneric.ttl(key);
    }

    @Override
    public CompletableFuture<Long> ttlAsync(String key) {
        log("ttlAsync", key);
        return kGeneric.ttlAsync(key);
    }

    @Override
    public long pTTL(String key) {
        log("pTTL", key);
        return kGeneric.pTTL(key);
    }

    @Override
    public CompletableFuture<Long> pTTLAsync(String key) {
        log("pTTLAsync", key);
        return kGeneric.pTTLAsync(key);
    }

    @Override
    public Iterable<String> scan(String keyPattern) {
        log("scan", keyPattern);
        return kGeneric.scan(keyPattern);
    }

    @Override
    public Iterable<String> scan(String keyPattern, int count) {
        log("scan", keyPattern, count);
        return kGeneric.scan(keyPattern, count);
    }

    @Override
    public KeyType type(String key) {
        log("type", key);
        return kGeneric.type(key);
    }

    @Override
    public CompletableFuture<KeyType> typeAsync(String key) {
        log("typeAsync", key);
        return kGeneric.typeAsync(key);
    }

    @Override
    public boolean trySetRateLimiter(String key, long rate, long rateInterval) {
        log("trySetRateLimiter", key, rate, rateInterval);
        return kRateLimiter.trySetRateLimiter(key, rate, rateInterval);
    }

    @Override
    public CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval) {
        log("trySetRateLimiterAsync", key, rate, rateInterval);
        return kRateLimiter.trySetRateLimiterAsync(key, rate, rateInterval);
    }

    @Override
    public boolean tryAcquire(String key) {
        log("tryAcquire", key);
        return kRateLimiter.tryAcquire(key);
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key) {
        log("tryAcquireAsync", key);
        return kRateLimiter.tryAcquireAsync(key);
    }

    @Override
    public boolean tryAcquire(String key, long permits) {
        log("tryAcquire", key, permits);
        return kRateLimiter.tryAcquire(key, permits);
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key, long permits) {
        log("tryAcquireAsync", key, permits);
        return kRateLimiter.tryAcquireAsync(key, permits);
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
            this.kBitSet = null;
            this.kMap = null;
            this.kHyperLogLog = null;
            this.kList = null;
            this.kSet = null;
            this.kzSet = null;
            this.kString = null;
            this.kBloomFilter = null;
            this.kLock = null;
            this.kRateLimiter = null;
            this.kGeneric = null;
            this.kRedissonGeo = null;
            this.kScript = null;
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
