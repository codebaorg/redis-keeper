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
import org.redisson.api.RFuture;
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
import org.redisson.client.codec.Codec;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Default redisson template.
 *
 * @author codeba
 */
public class DefaultRedissonTemplate implements RedissonTemplate, CacheTemplate {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Codec stringCodec = new StringCodec();

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
    public CompletableFuture<Long> bitCountAsync(String key) {
        log("bitCountAsync", key);
        return bitCountRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> bitCountRFuture(String key) {
        return getRBitSet(key).cardinalityAsync();
    }

    @Override
    public long bitFieldSetSigned(String key, int size, long offset, long value) {
        log("bitFieldSetSigned", key, size, offset, value);
        return getRBitSet(key).setSigned(size, offset, value);
    }

    @Override
    public CompletableFuture<Long> bitFieldSetSignedAsync(String key, int size, long offset, long value) {
        log("bitFieldSetSignedAsync", key, size, offset, value);
        return bitFieldSetSignedRFuture(key, size, offset, value).toCompletableFuture();
    }

    private RFuture<Long> bitFieldSetSignedRFuture(String key, int size, long offset, long value) {
        return getRBitSet(key).setSignedAsync(size, offset, value);
    }

    @Override
    public long bitFieldSetUnSigned(String key, int size, long offset, long value) {
        log("bitFieldSetUnSigned", key, size, offset, value);
        return getRBitSet(key).setUnsigned(size, offset, value);
    }

    @Override
    public CompletableFuture<Long> bitFieldSetUnSignedAsync(String key, int size, long offset, long value) {
        log("bitFieldSetUnSignedAsync", key, size, offset, value);
        return bitFieldSetUnSignedRFuture(key, size, offset, value).toCompletableFuture();
    }

    private RFuture<Long> bitFieldSetUnSignedRFuture(String key, int size, long offset, long value) {
        return getRBitSet(key).setUnsignedAsync(size, offset, value);
    }

    @Override
    public long bitFieldGetSigned(String key, int size, long offset) {
        log("bitFieldGetSigned", key, offset);
        return getRBitSet(key).getSigned(size, offset);
    }

    @Override
    public CompletableFuture<Long> bitFieldGetSignedAsync(String key, int size, long offset) {
        log("bitFieldGetSignedAsync", key, offset);
        return bitFieldGetSignedRFuture(key, size, offset).toCompletableFuture();
    }

    private RFuture<Long> bitFieldGetSignedRFuture(String key, int size, long offset) {
        return getRBitSet(key).getSignedAsync(size, offset);
    }

    @Override
    public long bitFieldGetUnSigned(String key, int size, long offset) {
        log("bitFieldGetUnSigned", key, offset);
        return getRBitSet(key).getUnsigned(size, offset);
    }

    @Override
    public CompletableFuture<Long> bitFieldGetUnSignedAsync(String key, int size, long offset) {
        log("bitFieldGetUnSignedAsync", key, offset);
        return bitFieldGetUnSignedRFuture(key, size, offset).toCompletableFuture();
    }

    private RFuture<Long> bitFieldGetUnSignedRFuture(String key, int size, long offset) {
        return getRBitSet(key).getUnsignedAsync(size, offset);
    }

    @Override
    public void bitOpOr(String destKey, String... keys) {
        log("bitOpOr", destKey, keys);
        getRBitSet(destKey).or(keys);
    }

    @Override
    public CompletableFuture<Void> bitOpOrAsync(String destKey, String... keys) {
        log("bitOpOrAsync", destKey, keys);
        return bitOpOrRFuture(destKey, keys).toCompletableFuture();
    }

    private RFuture<Void> bitOpOrRFuture(String destKey, String... keys) {
        return getRBitSet(destKey).orAsync(keys);
    }

    @Override
    public boolean getBit(String key, long bitIndex) {
        log("getBit", key, bitIndex);
        return getRBitSet(key).get(bitIndex);
    }

    @Override
    public CompletableFuture<Boolean> getBitAsync(String key, long bitIndex) {
        log("getBitAsync", key, bitIndex);
        return getBitRFuture(key, bitIndex).toCompletableFuture();
    }

    private RFuture<Boolean> getBitRFuture(String key, long bitIndex) {
        return getRBitSet(key).getAsync(bitIndex);
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        log("setBit", key, offset, value);
        return getRBitSet(key).set(offset, value);
    }

    @Override
    public CompletableFuture<Boolean> setBitAsync(String key, long offset, boolean value) {
        log("setBitAsync", key, offset, value);
        return setBitRFuture(key, offset, value).toCompletableFuture();
    }

    private RFuture<Boolean> setBitRFuture(String key, long offset, boolean value) {
        return getRBitSet(key).setAsync(offset, value);
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, Object member) {
        log("geoAdd", key, longitude, latitude, member);
        return getRGeo(key).add(longitude, latitude, member);
    }

    @Override
    public CompletableFuture<Long> geoAddAsync(String key, double longitude, double latitude, Object member) {
        log("geoAddAsync", key, longitude, latitude, member);
        return geoAddRFuture(key, longitude, latitude, member).toCompletableFuture();
    }

    private RFuture<Long> geoAddRFuture(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).addAsync(longitude, latitude, member);
    }

    @Override
    public boolean geoAddXX(String key, double longitude, double latitude, Object member) {
        log("geoAddXX", key, longitude, latitude, member);
        return getRGeo(key).addIfExists(longitude, latitude, member);
    }

    @Override
    public CompletableFuture<Boolean> geoAddXXAsync(String key, double longitude, double latitude, Object member) {
        log("geoAddXXAsync", key, longitude, latitude, member);
        return geoAddXXRFuture(key, longitude, latitude, member).toCompletableFuture();
    }

    private RFuture<Boolean> geoAddXXRFuture(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).addIfExistsAsync(longitude, latitude, member);
    }

    @Override
    public Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDist", key, firstMember, secondMember, geoUnit);
        return getRGeo(key).dist(firstMember, secondMember, parseGeoUnit(geoUnit));
    }

    @Override
    public CompletableFuture<Double> geoDistAsync(String key, Object firstMember, Object secondMember, String geoUnit) {
        log("geoDistAsync", key, firstMember, secondMember, geoUnit);
        return geoDistRFuture(key, firstMember, secondMember, geoUnit).toCompletableFuture();
    }

    private RFuture<Double> geoDistRFuture(String key, Object firstMember, Object secondMember, String geoUnit) {
        return getRGeo(key).distAsync(firstMember, secondMember, parseGeoUnit(geoUnit));
    }

    @Override
    public Map<Object, String> geoHash(String key, Object... members) {
        log("geoHash", key, members);
        return getRGeo(key).hash(members);
    }

    @Override
    public CompletableFuture<Map<Object, String>> geoHashAsync(String key, Object... members) {
        log("geoHashAsync", key, members);
        return geoHashRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Map<Object, String>> geoHashRFuture(String key, Object... members) {
        return getRGeo(key).hashAsync(members);
    }

    @Override
    public Map<Object, double[]> geoPos(String key, Object... members) {
        log("geoPos", key, members);
        final Map<Object, GeoPosition> pos = getRGeo(key).pos(members);
        return posMap(pos);
    }

    @Override
    public CompletableFuture<Map<Object, double[]>> geoPosAsync(String key, Object... members) {
        log("geoPosAsync", key, members);
        final RFuture<Map<Object, GeoPosition>> mapRFuture = geoPosRFuture(key, members);
        return mapRFuture.handle((pos, e) -> {
            if (null != e) {
                log("geoPosAsync", key, members, e);
                return Collections.<Object, double[]>emptyMap();
            }
            return posMap(pos);
        }).toCompletableFuture();
    }

    private RFuture<Map<Object, GeoPosition>> geoPosRFuture(String key, Object... members) {
        return getRGeo(key).posAsync(members);
    }

    private Map<Object, double[]> posMap(Map<Object, GeoPosition> pos) {
        return pos.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new double[]{entry.getValue().getLongitude(), entry.getValue().getLatitude()}
                )
        );
    }

    @Override
    public Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadius", key, longitude, latitude, radius, geoUnit);
        return getRGeo(key).searchWithDistance(GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoRadiusAsync(String key, double longitude, double latitude, double radius, String geoUnit) {
        log("geoRadiusAsync", key, longitude, latitude, radius, geoUnit);
        return geoRadiusRFuture(key, longitude, latitude, radius, geoUnit).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoRadiusRFuture(String key, double longitude, double latitude, double radius, String geoUnit) {
        return getRGeo(key).radiusWithDistanceAsync(longitude, latitude, radius, parseGeoUnit(geoUnit));
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(longitude, latitude, radius, geoUnit, order);
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchAsync", key, longitude, latitude, radius, geoUnit, order);
        return geoSearchRFuture(key, longitude, latitude, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(longitude, latitude, radius, geoUnit, order);
        return getRGeo(key).searchAsync(search);
    }

    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, radius, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, longitude, latitude, radius, geoUnit, count, order);
        return geoSearchRFuture(key, longitude, latitude, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchAsync", key, longitude, latitude, width, height, geoUnit, order);
        return geoSearchRFuture(key, longitude, latitude, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, longitude, latitude, width, height, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, longitude, latitude, width, height, geoUnit, count, order);
        return geoSearchRFuture(key, longitude, latitude, width, height, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearch", key, member, radius, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchAsync", key, member, radius, geoUnit, order);
        return geoSearchRFuture(key, member, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearch", key, member, radius, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, member, radius, geoUnit, count, order);
        return geoSearchRFuture(key, member, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearch", key, member, width, height, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchAsync", key, member, width, height, geoUnit, order);
        return geoSearchRFuture(key, member, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearch", key, member, width, height, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchAsync", key, member, width, height, geoUnit, count, order);
        return geoSearchRFuture(key, member, width, height, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, radius, geoUnit, order);
        return geoSearchWithDistanceRFuture(key, longitude, latitude, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, radius, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, radius, geoUnit, count, order);
        return geoSearchWithDistanceRFuture(key, longitude, latitude, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, width, height, geoUnit, order);
        return geoSearchWithDistanceRFuture(key, longitude, latitude, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, longitude, latitude, width, height, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, longitude, latitude, width, height, geoUnit, count, order);
        return geoSearchWithDistanceRFuture(key, longitude, latitude, width, height, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, member, radius, geoUnit, order);
        return geoSearchWithDistanceRFuture(key, member, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, radius, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        log("geoSearchWithDistanceAsync", key, member, radius, geoUnit, count, order);
        return geoSearchWithDistanceRFuture(key, member, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        log("geoSearchWithDistanceAsync", key, member, width, height, geoUnit, order);
        return geoSearchWithDistanceRFuture(key, member, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        log("geoSearchWithDistance", key, member, width, height, geoUnit, count, order);
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    private OptionalGeoSearch getOptionalGeoSearch(double longitude, double latitude, double radius, String geoUnit, String order) {
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        return GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).order(geoOrder);
    }

    private OptionalGeoSearch getOptionalGeoSearch(String order, OptionalGeoSearch longitude) {
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        return longitude.order(geoOrder);
    }

    @Override
    public Map<String, Boolean> hDel(String key, String... fields) {
        log("hDel", key, fields);

        final RMap<Object, Object> rMap = getMap(key);

        final Map<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            final long fastRemove = rMap.fastRemove(field);
            resultMap.put(field, 0 != fastRemove);
        }

        return resultMap;
    }

    @Override
    public CompletableFuture<Long> hDelAsync(String key, String... fields) {
        log("hDelAsync", key, fields);
        return hDelRFuture(key, fields).toCompletableFuture();
    }

    private RFuture<Long> hDelRFuture(String key, String... fields) {
        final RMap<Object, Object> rMap = getMap(key);
        return rMap.fastRemoveAsync(fields);
    }

    @Override
    public Map<String, Boolean> hExists(String key, String... fields) {
        log("hExists", key, fields);

        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, Boolean> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKey(field));
        }

        return resultMap;
    }

    @Override
    public Map<String, CompletableFuture<Boolean>> hExistsAsync(String key, String... fields) {
        log("hExistsAsync", key, fields);

        final RMap<Object, Object> rMap = getMap(key);

        final HashMap<String, CompletableFuture<Boolean>> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, rMap.containsKeyAsync(field).toCompletableFuture());
        }

        return resultMap;
    }

    @Override
    public Optional<Object> hGet(String key, String field) {
        log("hGet", key, field);
        return Optional.ofNullable(getMap(key).get(field));
    }

    @Override
    public CompletableFuture<Object> hGetAsync(String key, String field) {
        log("hGetAsync", key, field);
        return hGetRFuture(key, field).toCompletableFuture();
    }

    private RFuture<Object> hGetRFuture(String key, String field) {
        return getMap(key).getAsync(field);
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        log("hGetAll", key);
        return getMap(key).readAllMap();
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hGetAllAsync(String key) {
        log("hGetAllAsync", key);
        return hGetAllRFuture(key).toCompletableFuture();
    }

    private RFuture<Map<Object, Object>> hGetAllRFuture(String key) {
        final RMap<Object, Object> rMap = getMap(key);
        return rMap.readAllMapAsync();
    }

    @Override
    public Object hIncrBy(String key, String field, Number value) {
        log("hIncrBy", key, field, value);
        return getMap(key).addAndGet(field, value);
    }

    @Override
    public CompletableFuture<Object> hIncrByAsync(String key, String field, Number value) {
        log("hIncrByAsync", key, field, value);
        return hIncrByRFuture(key, field, value).toCompletableFuture();
    }

    private RFuture<Object> hIncrByRFuture(String key, String field, Number value) {
        return getMap(key).addAndGetAsync(field, value);
    }

    @Override
    public Collection<Object> hKeys(String key) {
        log("hKeys", key);
        return getMap(key).readAllKeySet();
    }

    @Override
    public CompletableFuture<Set<Object>> hKeysAsync(String key) {
        log("hKeysAsync", key);
        return hKeysRFuture(key).toCompletableFuture();
    }

    private RFuture<Set<Object>> hKeysRFuture(String key) {
        return getMap(key).readAllKeySetAsync();
    }

    @Override
    public int hLen(String key) {
        log("hLen", key);
        return getMap(key).size();
    }

    @Override
    public CompletableFuture<Integer> hLenAsync(String key) {
        log("hLenAsync", key);
        return hLenRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> hLenRFuture(String key) {
        return getMap(key).sizeAsync();
    }

    @Override
    public Map<Object, Object> hmGet(String key, Set<Object> fields) {
        log("hmGet", key);
        if (null == fields || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        return getMap(key).getAll(fields);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hmGetAsync(String key, Set<Object> fields) {
        log("hmGet", key);
        return hmGetRFuture(key, fields).toCompletableFuture();
    }

    private RFuture<Map<Object, Object>> hmGetRFuture(String key, Set<Object> fields) {
        return getMap(key).getAllAsync(fields);
    }

    @Override
    public void hmSet(String key, Map<?, ?> kvMap) {
        log("hmSet", key, kvMap);
        getMap(key).putAll(kvMap, 100);
    }

    @Override
    public CompletableFuture<Void> hmSetAsync(String key, Map<?, ?> kvMap) {
        log("hmSetAsync", key, kvMap);
        return hmSetRFuture(key, kvMap).toCompletableFuture();
    }

    private RFuture<Void> hmSetRFuture(String key, Map<?, ?> kvMap) {
        return getMap(key).putAllAsync(kvMap, 100);
    }

    @Override
    public Set<Object> hRandField(String key, int count) {
        log("hRandField", key, count);
        return getMap(key).randomKeys(count);
    }

    @Override
    public CompletableFuture<Set<Object>> hRandFieldsAsync(String key, int count) {
        log("hRandFieldsAsync", key, count);
        return hRandFieldsRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Set<Object>> hRandFieldsRFuture(String key, int count) {
        return getMap(key).randomKeysAsync(count);
    }

    @Override
    public Map<Object, Object> hRandFieldWithValues(String key, int count) {
        log("hRandFieldWithValues", key, count);
        return getMap(key).randomEntries(count);
    }

    @Override
    public CompletableFuture<Map<Object, Object>> hRandFieldWithValuesAsync(String key, int count) {
        log("hRandFieldWithValuesAsync", key, count);
        return hRandFieldWithValuesRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Map<Object, Object>> hRandFieldWithValuesRFuture(String key, int count) {
        return getMap(key).randomEntriesAsync(count);
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern) {
        log("hScan", key, keyPattern);
        return getMap(key).entrySet(keyPattern).iterator();
    }

    @Override
    public Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count) {
        log("hScan", key, keyPattern, count);
        return getMap(key).entrySet(keyPattern, count).iterator();
    }

    @Override
    public void hSet(String key, String field, Object value) {
        log("hSet", key, field, value);
        getMap(key).fastPut(field, value);
    }

    @Override
    public CompletableFuture<Boolean> hSetAsync(String key, String field, Object value) {
        log("hSetAsync", key, field, value);
        return hSetRFuture(key, field, value).toCompletableFuture();
    }

    private RFuture<Boolean> hSetRFuture(String key, String field, Object value) {
        return getMap(key).fastPutAsync(field, value);
    }

    @Override
    public void hSetNX(String key, String field, Object value) {
        log("hSetNX", key, field, value);
        getMap(key).fastPutIfAbsent(field, value);
    }

    @Override
    public CompletableFuture<Boolean> hSetNXAsync(String key, String field, Object value) {
        log("hSetNXAsync", key, field, value);
        return hSetNXRFuture(key, field, value).toCompletableFuture();
    }

    private RFuture<Boolean> hSetNXRFuture(String key, String field, Object value) {
        return getMap(key).fastPutIfAbsentAsync(field, value);
    }

    @Override
    public int hStrLen(String key, String field) {
        log("hStrLen", key, field);
        return getMap(key).valueSize(field);
    }

    @Override
    public CompletableFuture<Integer> hStrLenAsync(String key, String field) {
        log("hStrLenAsync", key, field);
        return hStrLenRFuture(key, field).toCompletableFuture();
    }

    private RFuture<Integer> hStrLenRFuture(String key, String field) {
        return getMap(key).valueSizeAsync(field);
    }

    @Override
    public Collection<Object> hVALs(String key) {
        log("hVALs", key);
        return getMap(key).readAllValues();
    }

    @Override
    public CompletableFuture<Collection<Object>> hVALsAsync(String key) {
        log("hVALsAsync", key);
        return hVALsRFuture(key).toCompletableFuture();
    }

    private RFuture<Collection<Object>> hVALsRFuture(String key) {
        return getMap(key).readAllValuesAsync();
    }

    @Override
    public boolean pfAdd(String key, Collection<Object> elements) {
        log("pfAdd", key, elements);
        return getHyperLogLog(key).addAll(elements);
    }

    @Override
    public CompletableFuture<Boolean> pfAddAsync(String key, Collection<Object> elements) {
        log("pfAddAsync", key, elements);
        return pfAddRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Boolean> pfAddRFuture(String key, Collection<Object> elements) {
        return getHyperLogLog(key).addAllAsync(elements);
    }

    @Override
    public long pfCount(String key) {
        log("pfCount", key);
        return getHyperLogLog(key).count();
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key) {
        log("pfCountAsync", key);
        return pfCountRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> pfCountRFuture(String key) {
        return getHyperLogLog(key).countAsync();
    }

    @Override
    public long pfCount(String key, String... otherKeys) {
        log("pfCount", key, otherKeys);
        return getHyperLogLog(key).countWith(otherKeys);
    }

    @Override
    public CompletableFuture<Long> pfCountAsync(String key, String... otherKeys) {
        log("pfCountAsync", key, otherKeys);
        return pfCountRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Long> pfCountRFuture(String key, String... otherKeys) {
        return getHyperLogLog(key).countWithAsync(otherKeys);
    }

    @Override
    public void pfMerge(String destKey, String... sourceKeys) {
        log("pfMerge", destKey, sourceKeys);
        getHyperLogLog(destKey).mergeWith(sourceKeys);
    }

    @Override
    public CompletableFuture<Void> pfMergeAsync(String destKey, String... sourceKeys) {
        log("pfMergeAsync", destKey, sourceKeys);
        return pfMergeRFuture(destKey, sourceKeys).toCompletableFuture();
    }

    private RFuture<Void> pfMergeRFuture(String destKey, String... sourceKeys) {
        return getHyperLogLog(destKey).mergeWithAsync(sourceKeys);
    }

    @Override
    public Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft) {
        log("blMove", source, destination, timeout, pollLeft);
        Object object;
        if (pollLeft) {
            object = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            object = getBlockingDeque(source).move(timeout, DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> blMoveAsync(String source, String destination, Duration timeout, boolean pollLeft) {
        log("blMoveAsync", source, destination, timeout, pollLeft);
        return blMoveRFuture(source, destination, timeout, pollLeft).toCompletableFuture();
    }

    private RFuture<Object> blMoveRFuture(String source, String destination, Duration timeout, boolean pollLeft) {
        if (pollLeft) {
            return getBlockingDeque(source).moveAsync(timeout, DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            return getBlockingDeque(source).moveAsync(timeout, DequeMoveArgs.pollLast().addFirstTo(destination));
        }
    }

    @Override
    public Optional<Object> blPop(String key) {
        log("blPop", key);
        return Optional.ofNullable(getBlockingDeque(key).poll());
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key) {
        log("blPopAsync", key);
        return blPopRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> blPopRFuture(String key) {
        return getBlockingDeque(key).pollAsync();
    }

    @Override
    public List<Object> blPop(String key, int count) {
        log("blPop", key, count);
        return getBlockingDeque(key).poll(count);
    }

    @Override
    public CompletableFuture<List<Object>> blPopAsync(String key, int count) {
        log("blPopAsync", key, count);
        return blPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> blPopRFuture(String key, int count) {
        return getBlockingDeque(key).pollAsync(count);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("blPop", key, timeout, unit);
        final Object polled = getBlockingDeque(key).poll(timeout, unit);
        return Optional.ofNullable(polled);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit) {
        log("blPop", key, timeout, unit);
        return blPopRFuture(key, timeout, unit).toCompletableFuture();
    }

    private RFuture<Object> blPopRFuture(String key, long timeout, TimeUnit unit) {
        return getBlockingDeque(key).pollAsync(timeout, unit);
    }

    @Override
    public Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("blPop", key, timeout, unit, otherKeys);
        final Object object = getBlockingDeque(key).pollFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> blPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        log("blPopAsync", key, timeout, unit, otherKeys);
        return blPopRFuture(key, timeout, unit, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> blPopRFuture(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return getBlockingDeque(key).pollFromAnyAsync(timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> brPop(String key) {
        log("brPop", key);
        final Object polled = getBlockingDeque(key).pollLast();
        return Optional.ofNullable(polled);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key) {
        return brPopRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> brPopRFuture(String key) {
        return getBlockingDeque(key).pollLastAsync();
    }

    @Override
    public List<Object> brPop(String key, int count) {
        log("brPop", key, count);
        return getBlockingDeque(key).pollLast(count);
    }

    @Override
    public CompletableFuture<List<Object>> brPopAsync(String key, int count) {
        log("brPopAsync", key, count);
        return brPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> brPopRFuture(String key, int count) {
        return getBlockingDeque(key).pollLastAsync(count);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException {
        log("brPop", key, timeout, unit);
        final Object polled = getBlockingDeque(key).pollLast(timeout, unit);
        return Optional.ofNullable(polled);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit) {
        log("brPopAsync", key, timeout, unit);
        return brPopRFuture(key, timeout, unit).toCompletableFuture();
    }

    private RFuture<Object> brPopRFuture(String key, long timeout, TimeUnit unit) {
        return getBlockingDeque(key).pollLastAsync(timeout, unit);
    }

    @Override
    public Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException {
        log("brPop", key, timeout, unit, otherKeys);
        final Object polled = getBlockingDeque(key).pollLastFromAny(timeout, unit, otherKeys);
        return Optional.ofNullable(polled);
    }

    @Override
    public CompletableFuture<Object> brPopAsync(String key, long timeout, TimeUnit unit, String... otherKeys) {
        log("brPopAsync", key, timeout, unit, otherKeys);
        return brPopRFuture(key, timeout, unit, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> brPopRFuture(String key, long timeout, TimeUnit unit, String... otherKeys) {
        return getBlockingDeque(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
    }

    @Override
    public Optional<Object> brPopLPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException {
        log("brPopLPush", source, destination, timeout, unit);
        final Object object = getBlockingDeque(source).pollLastAndOfferFirstTo(destination, timeout, unit);
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> brPopLPushAsync(String source, String destination, long timeout, TimeUnit unit) {
        log("brPopLPushAsync", source, destination, timeout, unit);
        return brPoplPushRFuture(source, destination, timeout, unit).toCompletableFuture();
    }

    private RFuture<Object> brPoplPushRFuture(String source, String destination, long timeout, TimeUnit unit) {
        return getBlockingDeque(source).pollLastAndOfferFirstToAsync(destination, timeout, unit);
    }

    @Override
    public Optional<Object> lIndex(String key, int index) {
        log("lIndex", key, index);
        return Optional.ofNullable(getList(key).get(index));
    }

    @Override
    public CompletableFuture<Object> lIndexAsync(String key, int index) {
        log("lIndexAsync", key, index);
        return lIndexRFuture(key, index).toCompletableFuture();
    }

    private RFuture<Object> lIndexRFuture(String key, int index) {
        return getList(key).getAsync(index);
    }

    @Override
    public int lInsert(String key, boolean before, Object pivot, Object element) {
        log("lInsert", key, before, pivot, element);
        int added;
        if (before) {
            added = getList(key).addBefore(pivot, element);
        } else {
            added = getList(key).addAfter(pivot, element);
        }
        return added;
    }

    @Override
    public CompletableFuture<Integer> lInsertAsync(String key, boolean before, Object pivot, Object element) {
        log("lInsertAsync", key, before, pivot, element);
        return lInsertRFuture(key, before, pivot, element).toCompletableFuture();
    }

    private RFuture<Integer> lInsertRFuture(String key, boolean before, Object pivot, Object element) {
        RFuture<Integer> added;
        if (before) {
            added = getList(key).addBeforeAsync(pivot, element);
        } else {
            added = getList(key).addAfterAsync(pivot, element);
        }
        return added;
    }

    @Override
    public int llen(String key) {
        log("llen", key);
        return getList(key).size();
    }

    @Override
    public CompletableFuture<Integer> llenAsync(String key) {
        log("llenAsync", key);
        return llenRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> llenRFuture(String key) {
        return getList(key).sizeAsync();
    }

    @Override
    public Optional<Object> lMove(String source, String destination, boolean pollLeft) {
        log("lMove", source, destination, pollLeft);
        Object move;
        if (pollLeft) {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).move(DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return Optional.ofNullable(move);
    }

    @Override
    public CompletableFuture<Object> lMoveAsync(String source, String destination, boolean pollLeft) {
        log("lMove", source, destination, pollLeft);
        return lMoveRFuture(source, destination, pollLeft).toCompletableFuture();
    }

    private RFuture<Object> lMoveRFuture(String source, String destination, boolean pollLeft) {
        RFuture<Object> move;
        if (pollLeft) {
            move = getBlockingDeque(source).moveAsync(DequeMoveArgs.pollFirst().addLastTo(destination));
        } else {
            move = getBlockingDeque(source).moveAsync(DequeMoveArgs.pollLast().addFirstTo(destination));
        }
        return move;
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
        log("lPop", key, count);
        return getDeque(key).poll(count);
    }

    @Override
    public CompletableFuture<List<Object>> lPopAsync(String key, int count) {
        log("lPopAsync", key, count);
        return lPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> lPopRFuture(String key, int count) {
        return getDeque(key).pollAsync(count);
    }

    @Override
    public int lPush(String key, Object... elements) {
        log("lPush", key, elements);
        Arrays.stream(elements)
                .forEach(element -> getDeque(key).addFirst(element));
        return elements.length;
    }

    @Override
    public int lPushX(String key, Object... elements) {
        log("lPushX", key, elements);
        return getDeque(key).addFirstIfExists(elements);
    }

    @Override
    public CompletableFuture<Integer> lPushXAsync(String key, Object... elements) {
        log("lPushXAsync", key, elements);
        return lPushXRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Integer> lPushXRFuture(String key, Object... elements) {
        return getDeque(key).addFirstIfExistsAsync(elements);
    }

    @Override
    public List<Object> lRange(String key, int fromIndex, int toIndex) {
        log("lRange", key, fromIndex, toIndex);
        return getList(key).range(fromIndex, toIndex);
    }

    @Override
    public CompletableFuture<List<Object>> lRangeAsync(String key, int fromIndex, int toIndex) {
        log("lRangeAsync", key, fromIndex, toIndex);
        return lRangeRFuture(key, fromIndex, toIndex).toCompletableFuture();
    }

    private RFuture<List<Object>> lRangeRFuture(String key, int fromIndex, int toIndex) {
        return getList(key).rangeAsync(fromIndex, toIndex);
    }

    @Override
    public boolean lRem(String key, Object element) {
        log("lRem", key, element);
        return getList(key).remove(element);
    }

    @Override
    public CompletableFuture<Boolean> lRemAsync(String key, Object element) {
        log("lRemAsync", key, element);
        return lRemRFuture(key, element).toCompletableFuture();
    }

    private RFuture<Boolean> lRemRFuture(String key, Object element) {
        return getList(key).removeAsync(element);
    }

    @Override
    public void lSet(String key, int index, Object element) {
        log("lSet", key, index, element);
        getList(key).fastSet(index, element);
    }

    @Override
    public CompletableFuture<Void> lSetAsync(String key, int index, Object element) {
        log("lSetAsync", key, index, element);
        return lSetRFuture(key, index, element).toCompletableFuture();
    }

    private RFuture<Void> lSetRFuture(String key, int index, Object element) {
        return getList(key).fastSetAsync(index, element);
    }

    @Override
    public void lTrim(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        getList(key).trim(fromIndex, toIndex);
    }

    @Override
    public CompletableFuture<Void> lTrimAsync(String key, int fromIndex, int toIndex) {
        log("ltrim", key, fromIndex, toIndex);
        return lTrimRFuture(key, fromIndex, toIndex).toCompletableFuture();
    }

    private RFuture<Void> lTrimRFuture(String key, int fromIndex, int toIndex) {
        return getList(key).trimAsync(fromIndex, toIndex);
    }

    @Override
    public List<Object> rPop(String key, int count) {
        log("rPop", key, count);
        return getDeque(key).pollLast(count);
    }

    @Override
    public CompletableFuture<List<Object>> rPopAsync(String key, int count) {
        log("rPopAsync", key, count);
        return rPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<List<Object>> rPopRFuture(String key, int count) {
        return getDeque(key).pollLastAsync(count);
    }

    @Override
    public Optional<Object> rPopLPush(String source, String destination) {
        log("rPopLPush", source, destination);
        return Optional.ofNullable(getDeque(source).pollLastAndOfferFirstTo(destination));
    }

    @Override
    public CompletableFuture<Object> rPopLPushAsync(String source, String destination) {
        log("rPopLPushAsync", source, destination);
        return rPoplPushRFuture(source, destination).toCompletableFuture();
    }

    private RFuture<Object> rPoplPushRFuture(String source, String destination) {
        return getDeque(source).pollLastAndOfferFirstToAsync(destination);
    }

    @Override
    public boolean rPush(String key, Object... elements) {
        log("rPush", key, elements);
        return getList(key).addAll(Arrays.asList(elements));
    }

    @Override
    public CompletableFuture<Boolean> rPushAsync(String key, Object... elements) {
        log("rPush", key, elements);
        return rPushRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Boolean> rPushRFuture(String key, Object... elements) {
        return getList(key).addAllAsync(Arrays.asList(elements));
    }

    @Override
    public int rPushX(String key, Object... elements) {
        log("rPushX", key, elements);
        return getDeque(key).addLastIfExists(elements);
    }

    @Override
    public CompletableFuture<Integer> rPushXAsync(String key, Object... elements) {
        log("rPushXAsync", key, elements);
        return rPushXRFuture(key, elements).toCompletableFuture();
    }

    private RFuture<Integer> rPushXRFuture(String key, Object... elements) {
        return getDeque(key).addLastIfExistsAsync(elements);
    }

    @Override
    public boolean sAdd(String key, Object member) {
        log("sAdd", key, member);
        return getSet(key).add(member);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Object member) {
        log("sAddAsync", key, member);
        return sAddRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Boolean> sAddRFuture(String key, Object member) {
        return getSet(key).addAsync(member);
    }

    @Override
    public boolean sAdd(String key, Collection<?> members) {
        log("sAdd", key, members);
        return getSet(key).addAll(members);
    }

    @Override
    public CompletableFuture<Boolean> sAddAsync(String key, Collection<?> members) {
        log("sAddAsync", key, members);
        return sAddRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Boolean> sAddRFuture(String key, Collection<?> members) {
        return getSet(key).addAllAsync(members);
    }

    @Override
    public int sCard(String key) {
        log("sCard", key);
        return getSet(key).size();
    }

    @Override
    public CompletableFuture<Integer> sCardAsync(String key) {
        log("sCardAsync", key);
        return sCardRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> sCardRFuture(String key) {
        return getSet(key).sizeAsync();
    }

    @Override
    public Set<Object> sDiff(String key, String... otherKeys) {
        log("sDiff", key, otherKeys);
        return getSet(key).readDiff(otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sDiffAsync(String key, String... otherKeys) {
        log("sDiffAsync", key, otherKeys);
        return sDiffRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Set<Object>> sDiffRFuture(String key, String... otherKeys) {
        return getSet(key).readDiffAsync(otherKeys);
    }

    @Override
    public int sDiffStore(String destination, String... keys) {
        log("sDiffStore", destination, keys);
        return getSet(destination).diff(keys);
    }

    @Override
    public CompletableFuture<Integer> sDiffStoreAsync(String destination, String... keys) {
        log("sDiffStoreAsync", destination, keys);
        return sDiffStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> sDiffStoreRFuture(String destination, String... keys) {
        return getSet(destination).diffAsync(keys);
    }

    @Override
    public Set<Object> sInter(String key, String... otherKeys) {
        log("sinter", key, otherKeys);
        return getSet(key).readIntersection(otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sInterAsync(String key, String... otherKeys) {
        log("sInterAsync", key, otherKeys);
        return sInterRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Set<Object>> sInterRFuture(String key, String... otherKeys) {
        return getSet(key).readIntersectionAsync(otherKeys);
    }

    @Override
    public int sInterStore(String destination, String... keys) {
        log("sInterStore", destination, keys);
        return getSet(destination).intersection(keys);
    }

    @Override
    public CompletableFuture<Integer> sInterStoreAsync(String destination, String... keys) {
        log("sInterStoreAsync", destination, keys);
        return sInterStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> sInterStoreRFuture(String destination, String... keys) {
        return getSet(destination).intersectionAsync(keys);
    }

    @Override
    public boolean sIsMember(String key, Object member) {
        log("sIsMember", key, member);
        return getSet(key).contains(member);
    }

    @Override
    public CompletableFuture<Boolean> sIsMemberAsync(String key, Object member) {
        log("sIsMember", key, member);
        return sIsMemberRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Boolean> sIsMemberRFuture(String key, Object member) {
        return getSet(key).containsAsync(member);
    }

    @Override
    public Set<Object> sMembers(String key) {
        log("sMembers", key);
        return getSet(key).readAll();
    }

    @Override
    public CompletableFuture<Set<Object>> sMembersAsync(String key) {
        log("sMembersAsync", key);
        return sMembersRFuture(key).toCompletableFuture();
    }

    private RFuture<Set<Object>> sMembersRFuture(String key) {
        return getSet(key).readAllAsync();
    }

    @Override
    public boolean sMove(String source, String destination, Object member) {
        log("sMove", source, destination, member);
        return getSet(source).move(destination, member);
    }

    @Override
    public CompletableFuture<Boolean> sMoveAsync(String source, String destination, Object member) {
        log("sMoveAsync", source, destination, member);
        return sMoveRFuture(source, destination, member).toCompletableFuture();
    }

    private RFuture<Boolean> sMoveRFuture(String source, String destination, Object member) {
        return getSet(source).moveAsync(destination, member);
    }

    @Override
    public Optional<Object> sPop(String key) {
        log("sPop", key);
        return Optional.ofNullable(getSet(key).removeRandom());
    }

    @Override
    public CompletableFuture<Object> sPopAsync(String key) {
        log("sPopAsync", key);
        return sPopRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> sPopRFuture(String key) {
        return getSet(key).removeRandomAsync();
    }

    @Override
    public Set<Object> sPop(String key, int count) {
        log("sPop", key, count);
        return getSet(key).removeRandom(count);
    }

    @Override
    public CompletableFuture<Set<Object>> sPopAsync(String key, int count) {
        log("sPopAsync", key, count);
        return sPopRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Set<Object>> sPopRFuture(String key, int count) {
        return getSet(key).removeRandomAsync(count);
    }

    @Override
    public Optional<Object> sRandMember(String key) {
        log("sRandMember", key);
        return Optional.ofNullable(getSet(key).random());
    }

    @Override
    public CompletableFuture<Object> sRandMemberAsync(String key) {
        log("sRandMemberAsync", key);
        return sRandMemberRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> sRandMemberRFuture(String key) {
        return getSet(key).randomAsync();
    }

    @Override
    public Set<Object> sRandMember(String key, int count) {
        log("sRandMember", key, count);
        return getSet(key).random(count);
    }

    @Override
    public CompletableFuture<Set<Object>> sRandMemberAsync(String key, int count) {
        log("sRandMember", key, count);
        return sRandMemberRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Set<Object>> sRandMemberRFuture(String key, int count) {
        return getSet(key).randomAsync(count);
    }

    @Override
    public boolean sRem(String key, Collection<?> members) {
        log("sRem", key, members);
        return getSet(key).removeAll(members);
    }

    @Override
    public CompletableFuture<Boolean> sRemAsync(String key, Collection<?> members) {
        log("sRemAsync", key, members);
        return sRemRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Boolean> sRemRFuture(String key, Collection<?> members) {
        return getSet(key).removeAllAsync(members);
    }

    @Override
    public Iterator<Object> sScan(String key) {
        log("sScan", key);
        return getSet(key).iterator();
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern) {
        log("sScan", key, pattern);
        return getSet(key).iterator(pattern);
    }

    @Override
    public Iterator<Object> sScan(String key, String pattern, int count) {
        log("sScan", key, pattern, count);
        return getSet(key).iterator(pattern, count);
    }

    @Override
    public Set<Object> sUnion(String key, String... otherKeys) {
        log("sUnion", key, otherKeys);
        return getSet(key).readUnion(otherKeys);
    }

    @Override
    public CompletableFuture<Set<Object>> sUnionAsync(String key, String... otherKeys) {
        log("sUnionAsync", key, otherKeys);
        return sUnionRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Set<Object>> sUnionRFuture(String key, String... otherKeys) {
        return getSet(key).readUnionAsync(otherKeys);
    }

    @Override
    public int sUnionStore(String destination, String... keys) {
        log("sUnionStore", destination, keys);
        return getSet(destination).union(keys);
    }

    @Override
    public CompletableFuture<Integer> sUnionStoreAsync(String destination, String... keys) {
        log("sUnionStoreAsync", destination, keys);
        return sUnionStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> sUnionStoreRFuture(String destination, String... keys) {
        return getSet(destination).unionAsync(keys);
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmPop", timeout, unit, key, min);
        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLast(timeout, unit);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min) {
        log("bzmPopAsync", timeout, unit, key, min);
        return bzmPopRFuture(timeout, unit, key, min).toCompletableFuture();
    }

    private RFuture<Object> bzmPopRFuture(long timeout, TimeUnit unit, String key, boolean min) {
        final RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstAsync(timeout, unit);
        } else {
            object = getRScoredSortedSet(key).pollLastAsync(timeout, unit);
        }
        return object;
    }

    @Override
    public Collection<Object> bzmPop(String key, boolean min, int count) {
        log("bzmPop", key, min, count);
        final Collection<Object> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirst(count);
        } else {
            list = getRScoredSortedSet(key).pollLast(count);
        }
        return list;
    }

    @Override
    public CompletableFuture<Collection<Object>> bzmPopAsync(String key, boolean min, int count) {
        log("bzmPopAsync", key, min, count);
        return bzmPopRFuture(key, min, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> bzmPopRFuture(String key, boolean min, int count) {
        final RFuture<Collection<Object>> list;
        if (min) {
            list = getRScoredSortedSet(key).pollFirstAsync(count);
        } else {
            list = getRScoredSortedSet(key).pollLastAsync(count);
        }
        return list;
    }

    @Override
    public Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmPop", timeout, unit, key, min, otherKeys);
        final Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> bzmPopAsync(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        log("bzmPopAsync", timeout, unit, key, min, otherKeys);
        return bzmPopRFuture(timeout, unit, key, min, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> bzmPopRFuture(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys) {
        final RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAnyAsync(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
        }
        return object;
    }

    @Override
    public Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit) {
        log("bzPopMax", key, timeout, unit);
        return bzmPop(timeout, unit, key, false);
    }

    @Override
    public CompletableFuture<Object> bzPopMaxAsync(String key, long timeout, TimeUnit unit) {
        log("bzPopMaxAsync", key, timeout, unit);
        return bzmPopRFuture(timeout, unit, key, false).toCompletableFuture();
    }

    @Override
    public Collection<Object> bzPopMax(String key, int count) {
        log("bzPopMax", key, count);
        return bzmPop(key, false, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMaxAsync(String key, int count) {
        log("bzPopMaxAsync", key, count);
        return bzmPopRFuture(key, false, count).toCompletableFuture();
    }

    @Override
    public Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit) {
        log("bzPopMin", key, timeout, unit);
        return bzmPop(timeout, unit, key, true);
    }

    @Override
    public CompletableFuture<Object> bzPopMinAsync(String key, long timeout, TimeUnit unit) {
        log("bzPopMin", key, timeout, unit);
        return bzmPopRFuture(timeout, unit, key, true).toCompletableFuture();
    }

    @Override
    public Collection<Object> bzPopMin(String key, int count) {
        log("bzPopMin", key, count);
        return bzmPop(key, true, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> bzPopMinAsync(String key, int count) {
        log("bzPopMinAsync", key, count);
        return bzmPopRFuture(key, true, count).toCompletableFuture();
    }

    @Override
    public boolean zAdd(String key, double score, Object member) {
        log("zAdd", key, score, member);
        return getRScoredSortedSet(key).add(score, member);
    }

    @Override
    public CompletableFuture<Boolean> zAddAsync(String key, double score, Object member) {
        log("zAddAsync", key, score, member);
        return zAddRFuture(key, score, member).toCompletableFuture();
    }

    private RFuture<Boolean> zAddRFuture(String key, double score, Object member) {
        return getRScoredSortedSet(key).addAsync(score, member);
    }

    @Override
    public int zAdd(String key, Map<Object, Double> members) {
        log("zAdd", key, members);
        return getRScoredSortedSet(key).addAll(members);
    }

    @Override
    public CompletableFuture<Integer> zAddAsync(String key, Map<Object, Double> members) {
        log("zAddAsync", key, members);
        return zAddRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Integer> zAddRFuture(String key, Map<Object, Double> members) {
        return getRScoredSortedSet(key).addAllAsync(members);
    }

    @Override
    public int zCard(String key) {
        log("zCard", key);
        return getRScoredSortedSet(key).size();
    }

    @Override
    public CompletableFuture<Integer> zCardAsync(String key) {
        log("zCardAsync", key);
        return zCardRFuture(key).toCompletableFuture();
    }

    private RFuture<Integer> zCardRFuture(String key) {
        return getRScoredSortedSet(key).sizeAsync();
    }

    @Override
    public int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zCount", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return getRScoredSortedSet(key).count(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Integer> zCountAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zCountAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return zCountRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zCountRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).countAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zDiff(String key, String... keys) {
        log("zDiff", key, keys);
        return getRScoredSortedSet(key).readDiff(keys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zDiffAsync(String key, String... keys) {
        log("zDiffAsync", key, keys);
        return zDiffRFuture(key, keys).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zDiffRFuture(String key, String... keys) {
        return getRScoredSortedSet(key).readDiffAsync(keys);
    }

    @Override
    public int zDiffStore(String destination, String... keys) {
        log("zDiffStore", destination, keys);
        return getRScoredSortedSet(destination).diff(keys);
    }

    @Override
    public CompletableFuture<Integer> zDiffStoreAsync(String destination, String... keys) {
        log("zDiffStoreAsync", destination, keys);
        return zDiffStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> zDiffStoreRFuture(String destination, String... keys) {
        return getRScoredSortedSet(destination).diffAsync(keys);
    }

    @Override
    public Double zIncrBy(String key, Number increment, Object member) {
        log("zIncrBy", key, increment, member);
        return getRScoredSortedSet(key).addScore(member, increment);
    }

    @Override
    public CompletableFuture<Double> zIncrByAsync(String key, Number increment, Object member) {
        log("zIncrByAsync", key, increment, member);
        return zIncrByRFuture(key, increment, member).toCompletableFuture();
    }

    private RFuture<Double> zIncrByRFuture(String key, Number increment, Object member) {
        return getRScoredSortedSet(key).addScoreAsync(member, increment);
    }

    @Override
    public Collection<Object> zInter(String key, String... otherKeys) {
        log("zInter", key, otherKeys);
        return getRScoredSortedSet(key).readIntersection(otherKeys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zInterAsync(String key, String... otherKeys) {
        log("zInterAsync", key, otherKeys);
        return zInterRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zInterRFuture(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readIntersectionAsync(otherKeys);
    }

    @Override
    public int zInterStore(String destination, String... otherKeys) {
        log("zInterStore", destination, otherKeys);
        return getRScoredSortedSet(destination).intersection(otherKeys);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String... otherKeys) {
        log("zInterStoreAsync", destination, otherKeys);
        return zInterStoreRFuture(destination, otherKeys).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreRFuture(String destination, String... otherKeys) {
        return getRScoredSortedSet(destination).intersectionAsync(otherKeys);
    }

    @Override
    public int zInterStoreAggregate(String destination, String aggregate, String... otherKeys) {
        log("zInterStoreAggregate", destination, aggregate, otherKeys);
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, otherKeys);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAggregateAsync(String destination, String aggregate, String... otherKeys) {
        log("zInterStoreAggregateAsync", destination, aggregate, otherKeys);
        return zInterStoreAggregateRFuture(destination, aggregate, otherKeys).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreAggregateRFuture(String destination, String aggregate, String... otherKeys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersectionAsync(sortedSetAggregate, otherKeys);
    }

    @Override
    public int zInterStore(String destination, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, keyWithWeight);
        return getRScoredSortedSet(destination).intersection(keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, keyWithWeight);
        return zInterStoreRFuture(destination, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreRFuture(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).intersectionAsync(keyWithWeight);
    }

    @Override
    public int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zInterStore", destination, aggregate, keyWithWeight);
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersection(sortedSetAggregate, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zInterStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zInterStoreAsync", destination, aggregate, keyWithWeight);
        return zInterStoreRFuture(destination, aggregate, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zInterStoreRFuture(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).intersectionAsync(sortedSetAggregate, keyWithWeight);
    }

    @Override
    public int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zLexCount", fromElement, fromInclusive, toElement, toInclusive);
        return getRLexSortedSet(key).count(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zLexCountAsync", fromElement, fromInclusive, toElement, toInclusive);
        return zLexCountRFuture(key, fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zLexCountRFuture(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countAsync(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public int zLexCountHead(String key, String toElement, boolean toInclusive) {
        log("zLexCountHead", toElement, toInclusive);
        return getRLexSortedSet(key).countHead(toElement, toInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountHeadAsync(String key, String toElement, boolean toInclusive) {
        log("zLexCountHeadAsync", toElement, toInclusive);
        return zLexCountHeadRFuture(key, toElement, toInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zLexCountHeadRFuture(String key, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).countHeadAsync(toElement, toInclusive);
    }

    @Override
    public int zLexCountTail(String key, String fromElement, boolean fromInclusive) {
        log("zLexCountTail", fromElement, fromInclusive);
        return getRLexSortedSet(key).countTail(fromElement, fromInclusive);
    }

    @Override
    public CompletableFuture<Integer> zLexCountTailAsync(String key, String fromElement, boolean fromInclusive) {
        log("zLexCountTailAsync", fromElement, fromInclusive);
        return zLexCountTailRFuture(key, fromElement, fromInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zLexCountTailRFuture(String key, String fromElement, boolean fromInclusive) {
        return getRLexSortedSet(key).countTailAsync(fromElement, fromInclusive);
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min) {
        log("zmPop", key, min);
        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirst();
        } else {
            object = getRScoredSortedSet(key).pollLast();
        }
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min) {
        log("zmPopAsync", key, min);
        return zmPopRFuture(key, min).toCompletableFuture();
    }

    private RFuture<Object> zmPopRFuture(String key, boolean min) {
        RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstAsync();
        } else {
            object = getRScoredSortedSet(key).pollLastAsync();
        }
        return object;
    }

    @Override
    public Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmPop", key, min, timeout, unit, otherKeys);
        Object object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAny(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAny(timeout, unit, otherKeys);
        }
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> zmPopAsync(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        log("zmPopAsync", key, min, timeout, unit, otherKeys);
        return zmPopRFuture(key, min, timeout, unit, otherKeys).toCompletableFuture();
    }

    private RFuture<Object> zmPopRFuture(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys) {
        RFuture<Object> object;
        if (min) {
            object = getRScoredSortedSet(key).pollFirstFromAnyAsync(timeout, unit, otherKeys);
        } else {
            object = getRScoredSortedSet(key).pollLastFromAnyAsync(timeout, unit, otherKeys);
        }
        return object;
    }

    @Override
    public Optional<Object> zPopMax(String key) {
        log("zPopMax", key);
        return Optional.ofNullable(getRScoredSortedSet(key).pollLast());
    }

    @Override
    public CompletableFuture<Object> zPopMaxAsync(String key) {
        log("zPopMaxAsync", key);
        return zPopMaxRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> zPopMaxRFuture(String key) {
        return getRScoredSortedSet(key).pollLastAsync();
    }

    @Override
    public Collection<Object> zPopMax(String key, int count) {
        log("zPopMax", key, count);
        return getRScoredSortedSet(key).pollLast(count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMaxAsync(String key, int count) {
        log("zPopMaxAsync", key, count);
        return zPopMaxRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zPopMaxRFuture(String key, int count) {
        return getRScoredSortedSet(key).pollLastAsync(count);
    }

    @Override
    public Optional<Object> zPopMin(String key) {
        log("zPopMin", key);
        return Optional.ofNullable(getRScoredSortedSet(key).pollFirst());
    }

    @Override
    public CompletableFuture<Object> zPopMinAsync(String key) {
        log("zPopMinAsync", key);
        return zPopMinRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> zPopMinRFuture(String key) {
        return getRScoredSortedSet(key).pollFirstAsync();
    }

    @Override
    public Collection<Object> zPopMin(String key, int count) {
        log("zPopMin", key, count);
        return getRScoredSortedSet(key).pollFirst(count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zPopMinAsync(String key, int count) {
        log("zPopMinAsync", key, count);
        return zPopMinRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zPopMinRFuture(String key, int count) {
        return getRScoredSortedSet(key).pollFirstAsync(count);
    }

    @Override
    public Optional<Object> zRandMember(String key) {
        log("zRandMember", key);
        return Optional.ofNullable(getRScoredSortedSet(key).random());
    }

    @Override
    public CompletableFuture<Object> zRandMemberAsync(String key) {
        log("zRandMemberAsync", key);
        return zRandMemberRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> zRandMemberRFuture(String key) {
        return getRScoredSortedSet(key).randomAsync();
    }

    @Override
    public Collection<Object> zRandMember(String key, int count) {
        log("zRandMember", key, count);
        return getRScoredSortedSet(key).random(count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRandMemberAsync(String key, int count) {
        log("zRandMemberAsync", key, count);
        return zRandMemberRFuture(key, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRandMemberRFuture(String key, int count) {
        return getRScoredSortedSet(key).randomAsync(count);
    }

    @Override
    public Collection<Object> zRange(String key, int startIndex, int endIndex) {
        log("zRange", key, startIndex, endIndex);
        return getRScoredSortedSet(key).valueRange(startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, int startIndex, int endIndex) {
        log("zRangeAsync", key, startIndex, endIndex);
        return zRangeRFuture(key, startIndex, endIndex).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeRFuture(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeAsync(startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRange", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return zRangeRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRange", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return getRScoredSortedSet(key).valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return zRangeRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeAsync(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, int startIndex, int endIndex) {
        log("zRangeReversed", key, startIndex, endIndex);
        return getRScoredSortedSet(key).valueRangeReversed(startIndex, endIndex);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, int startIndex, int endIndex) {
        log("zRangeReversedAsync", key, startIndex, endIndex);
        return zRangeReversedRFuture(key, startIndex, endIndex).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeReversedRFuture(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startIndex, endIndex);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRangeReversedAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return zRangeReversedRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeReversedRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeReversed", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return getRScoredSortedSet(key).valueRangeReversed(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }

    @Override
    public CompletableFuture<Collection<Object>> zRangeReversedAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        log("zRangeReversedAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
        return zRangeReversedRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zRangeReversedRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count) {
        return getRScoredSortedSet(key).valueRangeReversedAsync(startScore, startScoreInclusive, endScore, endScoreInclusive, offset, count);
    }


    @Override
    public Optional<Integer> zRank(String key, Object member) {
        log("zRank", key, member);
        return Optional.ofNullable(getRScoredSortedSet(key).rank(member));
    }

    @Override
    public CompletableFuture<Integer> zRankAsync(String key, Object member) {
        log("zRankAsync", key, member);
        return zRankRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Integer> zRankRFuture(String key, Object member) {
        return getRScoredSortedSet(key).rankAsync(member);
    }

    @Override
    public boolean zRem(String key, Collection<?> members) {
        log("zRem", key, members);
        return getRScoredSortedSet(key).removeAll(members);
    }

    @Override
    public CompletableFuture<Boolean> zRemAsync(String key, Collection<?> members) {
        log("zRemAsync", key, members);
        return zRemRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Boolean> zRemRFuture(String key, Collection<?> members) {
        return getRScoredSortedSet(key).removeAllAsync(members);
    }

    @Override
    public Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zRemRangeByLex", key, fromElement, fromInclusive, toElement, toInclusive);
        final int removeRange = getRLexSortedSet(key).removeRange(fromElement, fromInclusive, toElement, toInclusive);
        return Optional.of(removeRange);
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByLexAsync(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        log("zRemRangeByLexAsync", key, fromElement, fromInclusive, toElement, toInclusive);
        return zRemRangeByLexRFuture(key, fromElement, fromInclusive, toElement, toInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zRemRangeByLexRFuture(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive) {
        return getRLexSortedSet(key).removeRangeAsync(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex) {
        log("zRemRangeByRank", key, startIndex, endIndex);
        return Optional.of(getRScoredSortedSet(key).removeRangeByRank(startIndex, endIndex));
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByRankAsync(String key, int startIndex, int endIndex) {
        log("zRemRangeByRankAsync", key, startIndex, endIndex);
        return zRemRangeByRankRFuture(key, startIndex, endIndex).toCompletableFuture();
    }

    private RFuture<Integer> zRemRangeByRankRFuture(String key, int startIndex, int endIndex) {
        return getRScoredSortedSet(key).removeRangeByRankAsync(startIndex, endIndex);
    }

    @Override
    public Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRemRangeByScore", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return Optional.of(getRScoredSortedSet(key).removeRangeByScore(startScore, startScoreInclusive, endScore, endScoreInclusive));
    }

    @Override
    public CompletableFuture<Integer> zRemRangeByScoreAsync(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log("zRemRangeByScoreAsync", key, startScore, startScoreInclusive, endScore, endScoreInclusive);
        return zRemRangeByScoreRFuture(key, startScore, startScoreInclusive, endScore, endScoreInclusive).toCompletableFuture();
    }

    private RFuture<Integer> zRemRangeByScoreRFuture(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return getRScoredSortedSet(key).removeRangeByScoreAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
    }

    @Override
    public Optional<Integer> zRevRank(String key, Object member) {
        log("zRevRank", key, member);
        return Optional.ofNullable(getRScoredSortedSet(key).revRank(member));
    }

    @Override
    public CompletableFuture<Integer> zRevRankAsync(String key, Object member) {
        log("zRevRankAsync", key, member);
        return zRevRankRFuture(key, member).toCompletableFuture();
    }

    private RFuture<Integer> zRevRankRFuture(String key, Object member) {
        return getRScoredSortedSet(key).revRankAsync(member);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern) {
        log("zScan", key, pattern);
        return getRScoredSortedSet(key).iterator(pattern);
    }

    @Override
    public Iterator<Object> zScan(String key, String pattern, int count) {
        log("zScan", key, pattern, count);
        return getRScoredSortedSet(key).iterator(pattern, count);
    }

    @Override
    public List<Double> zScore(String key, List<Object> members) {
        log("zScore", key, members);
        return getRScoredSortedSet(key).getScore(members);
    }

    @Override
    public CompletableFuture<List<Double>> zScoreAsync(String key, List<Object> members) {
        log("zScoreAsync", key, members);
        return zScoreRFuture(key, members).toCompletableFuture();
    }

    private RFuture<List<Double>> zScoreRFuture(String key, List<Object> members) {
        return getRScoredSortedSet(key).getScoreAsync(members);
    }

    @Override
    public Collection<Object> zUnion(String key, String... otherKeys) {
        log("zUnion", key, otherKeys);
        return getRScoredSortedSet(key).readUnion(otherKeys);
    }

    @Override
    public CompletableFuture<Collection<Object>> zUnionAsync(String key, String... otherKeys) {
        log("zUnionAsync", key, otherKeys);
        return zUnionRFuture(key, otherKeys).toCompletableFuture();
    }

    private RFuture<Collection<Object>> zUnionRFuture(String key, String... otherKeys) {
        return getRScoredSortedSet(key).readUnionAsync(otherKeys);
    }

    @Override
    public int zUnionStore(String destination, String... keys) {
        log("zUnionStore", destination, keys);
        return getRScoredSortedSet(destination).union(keys);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String... keys) {
        log("zUnionStoreAsync", destination, keys);
        return zUnionStoreRFuture(destination, keys).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreRFuture(String destination, String... keys) {
        return getRScoredSortedSet(destination).unionAsync(keys);
    }

    @Override
    public int zUnionStoreAggregate(String destination, String aggregate, String... keys) {
        log("zUnionStoreAggregate", destination, aggregate, keys);
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keys);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAggregateAsync(String destination, String aggregate, String... keys) {
        log("zUnionStoreAggregateAsync", destination, aggregate, keys);
        return zUnionStoreAggregateRFuture(destination, aggregate, keys).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreAggregateRFuture(String destination, String aggregate, String... keys) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).unionAsync(sortedSetAggregate, keys);
    }

    @Override
    public int zUnionStore(String destination, Map<String, Double> keyWithWeight) {
        log("zUnionStore", destination, keyWithWeight);
        return getRScoredSortedSet(destination).union(keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, Map<String, Double> keyWithWeight) {
        log("zUnionStoreAsync", destination, keyWithWeight);
        return zUnionStoreRFuture(destination, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreRFuture(String destination, Map<String, Double> keyWithWeight) {
        return getRScoredSortedSet(destination).unionAsync(keyWithWeight);
    }

    @Override
    public int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zUnionStore", destination, aggregate, keyWithWeight);
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).union(sortedSetAggregate, keyWithWeight);
    }

    @Override
    public CompletableFuture<Integer> zUnionStoreAsync(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        log("zUnionStoreAsync", destination, aggregate, keyWithWeight);
        return zUnionStoreRFuture(destination, aggregate, keyWithWeight).toCompletableFuture();
    }

    private RFuture<Integer> zUnionStoreRFuture(String destination, String aggregate, Map<String, Double> keyWithWeight) {
        final RScoredSortedSet.Aggregate sortedSetAggregate = RScoredSortedSet.Aggregate.valueOf(aggregate.toUpperCase(Locale.ROOT));
        return getRScoredSortedSet(destination).unionAsync(sortedSetAggregate, keyWithWeight);
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
    public CompletableFuture<Long> decrAsync(String key) {
        log("decrAsync", key);
        return decrRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> decrRFuture(String key) {
        return getRAtomicLong(key).decrementAndGetAsync();
    }

    @Override
    public long decrBy(String key, long decrement) {
        log("decrBy", key, decrement);
        return getRAtomicLong(key).addAndGet(-decrement);
    }

    @Override
    public CompletableFuture<Long> decrByAsync(String key, long decrement) {
        log("decrByAsync", key, decrement);
        return decrByRFuture(key, decrement).toCompletableFuture();
    }

    private RFuture<Long> decrByRFuture(String key, long decrement) {
        return getRAtomicLong(key).addAndGetAsync(-decrement);
    }

    @Override
    public Optional<Object> get(String key) {
        log("get", key);
        final Object object = getRBucket(key, stringCodec).get();
        return Optional.ofNullable(object);
    }

    @Override
    public Optional<Object> getObject(String key) {
        log("getObject", key);
        Object object;
        try {
            object = getRBucket(key).get();
        } catch (Exception e) {
            object = getRBucket(key, stringCodec).get();
        }
        return Optional.ofNullable(object);
    }

    @Override
    public CompletableFuture<Object> getAsync(String key) {
        log("getAsync", key);
        return getRFuture(key).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Object> getObjectAsync(String key) {
        log("getObjectAsync", key);
        return getRBucket(key).getAsync()
                .handle((object, throwable) -> {
                    if (null != throwable) {
                        return getRBucket(key, stringCodec).getAsync().join();
                    }
                    return object;
                }).toCompletableFuture();
    }

    private RFuture<Object> getRFuture(String key) {
        return getRBucket(key, stringCodec).getAsync();
    }

    @Override
    public Optional<Object> getDel(String key) {
        log("getDel", key);
        return Optional.ofNullable(getRBucket(key, stringCodec).getAndDelete());
    }

    @Override
    public CompletableFuture<Object> getDelAsync(String key) {
        log("getDelAsync", key);
        return getDelRFuture(key).toCompletableFuture();
    }

    private RFuture<Object> getDelRFuture(String key) {
        return getRBucket(key, stringCodec).getAndDeleteAsync();
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
    public CompletableFuture<Long> getLongAsync(String key) {
        log("getLongAsync", key);
        return getLongRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> getLongRFuture(String key) {
        return getRAtomicLong(key).getAsync();
    }

    @Override
    public long incr(String key) {
        log("incr", key);
        return getRAtomicLong(key).incrementAndGet();
    }

    @Override
    public CompletableFuture<Long> incrAsync(String key) {
        log("incrAsync", key);
        return incrRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> incrRFuture(String key) {
        return getRAtomicLong(key).incrementAndGetAsync();
    }

    @Override
    public long incrBy(String key, long increment) {
        log("incrBy", key, increment);
        return getRAtomicLong(key).addAndGet(increment);
    }

    @Override
    public CompletableFuture<Long> incrByAsync(String key, long increment) {
        log("incrByAsync", key, increment);
        return incrByRFuture(key, increment).toCompletableFuture();
    }

    private RFuture<Long> incrByRFuture(String key, long increment) {
        return getRAtomicLong(key).addAndGetAsync(increment);
    }

    @Override
    public double getDouble(String key) {
        log("getDouble", key);
        return getRAtomicDouble(key).get();
    }

    @Override
    public CompletableFuture<Double> getDoubleAsync(String key) {
        log("getDoubleAsync", key);
        return getDoubleRFuture(key).toCompletableFuture();
    }

    private RFuture<Double> getDoubleRFuture(String key) {
        return getRAtomicDouble(key).getAsync();
    }

    @Override
    public double incrByFloat(String key, double increment) {
        log("incrByFloat", key, increment);
        return getRAtomicDouble(key).addAndGet(increment);
    }

    @Override
    public CompletableFuture<Double> incrByFloatAsync(String key, double increment) {
        log("incrByFloatAsync", key, increment);
        return incrByFloatRFuture(key, increment).toCompletableFuture();
    }

    private RFuture<Double> incrByFloatRFuture(String key, double increment) {
        return getRAtomicDouble(key).addAndGetAsync(increment);
    }

    @Override
    public boolean compareAndSet(String key, long expect, long update) {
        log("compareAndSet", key, expect, update);
        return getRAtomicLong(key).compareAndSet(expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, long expect, long update) {
        log("compareAndSetAsync", key, expect, update);
        return compareAndSetRFuture(key, expect, update).toCompletableFuture();
    }

    private RFuture<Boolean> compareAndSetRFuture(String key, long expect, long update) {
        return getRAtomicLong(key).compareAndSetAsync(expect, update);
    }

    @Override
    public boolean compareAndSet(String key, double expect, double update) {
        log("compareAndSet", key, expect, update);
        return getRAtomicDouble(key).compareAndSet(expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, double expect, double update) {
        log("compareAndSetAsync", key, expect, update);
        return compareAndSetRFuture(key, expect, update).toCompletableFuture();
    }

    private RFuture<Boolean> compareAndSetRFuture(String key, double expect, double update) {
        return getRAtomicDouble(key).compareAndSetAsync(expect, update);
    }

    @Override
    public void setObject(String key, Object value) {
        log("setObject", key, value);

        if (value instanceof String) {
            set(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            set(key, Long.parseLong(value.toString()));
        } else if (value instanceof Float || value instanceof Double) {
            set(key, Double.parseDouble(value.toString()));
        } else {
            getRBucket(key).set(value);
        }
    }

    @Override
    public CompletableFuture<Void> setObjectAsync(String key, Object value) {
        log("setObjectAsync", key, value);

        if (value instanceof String) {
            return setAsync(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            return setAsync(key, Long.parseLong(value.toString()));
        } else if (value instanceof Float || value instanceof Double) {
            return setAsync(key, Double.parseDouble(value.toString()));
        } else {
            return getRBucket(key).setAsync(value).toCompletableFuture();
        }
    }

    @Override
    public Map<String, Object> mGet(String... keys) {
        log("mGet", keys);
        if (null == keys || keys.length == 0) {
            return Collections.emptyMap();
        }
        return getRBuckets(stringCodec).get(keys);
    }

    @Override
    public CompletableFuture<Map<String, Object>> mGetAsync(String... keys) {
        log("mGetAsync", keys);
        return mGetRFuture(keys).toCompletableFuture();
    }

    private RFuture<Map<String, Object>> mGetRFuture(String... keys) {
        return getRBuckets(stringCodec).getAsync(keys);
    }

    @Override
    public void mSet(Map<String, String> kvMap) {
        log("mSet", kvMap);
        getRBuckets(stringCodec).set(kvMap);
    }

    @Override
    public CompletableFuture<Void> mSetAsync(Map<String, String> kvMap) {
        log("mSetAsync", kvMap);
        return mSetRFuture(kvMap).toCompletableFuture();
    }

    private RFuture<Void> mSetRFuture(Map<String, String> kvMap) {
        return getRBuckets(stringCodec).setAsync(kvMap);
    }

    @Override
    public boolean mSetNX(Map<String, String> kvMap) {
        log("mSetNX", kvMap);
        return getRBuckets(stringCodec).trySet(kvMap);
    }

    @Override
    public CompletableFuture<Boolean> mSetNXAsync(Map<String, String> kvMap) {
        log("mSetNXAsync", kvMap);
        return mSetNXRFuture(kvMap).toCompletableFuture();
    }

    private RFuture<Boolean> mSetNXRFuture(Map<String, String> kvMap) {
        return getRBuckets(stringCodec).trySetAsync(kvMap);
    }

    @Override
    public void set(String key, String value) {
        log("set", key, value);
        getRBucket(key, stringCodec).set(value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        log("set", key, value);
        return setRFuture(key, value).toCompletableFuture();
    }

    private RFuture<Void> setRFuture(String key, String value) {
        return getRBucket(key, stringCodec).setAsync(value);
    }

    @Override
    public void set(String key, Long value) {
        log("set", key, value);
        getRAtomicLong(key).set(value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Long value) {
        log("setAsync", key, value);
        return setRFuture(key, value).toCompletableFuture();
    }

    private RFuture<Void> setRFuture(String key, Long value) {
        return getRAtomicLong(key).setAsync(value);
    }

    @Override
    public void set(String key, Double value) {
        log("set", key, value);
        getRAtomicDouble(key).set(value);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, Double value) {
        log("setAsync", key, value);
        return setRFuture(key, value).toCompletableFuture();
    }

    private RFuture<Void> setRFuture(String key, Double value) {
        return getRAtomicDouble(key).setAsync(value);
    }

    @Override
    public boolean compareAndSet(String key, String expect, String update) {
        log("compareAndSet", key, expect, update);
        return getRBucket(key, stringCodec).compareAndSet(expect, update);
    }

    @Override
    public CompletableFuture<Boolean> compareAndSetAsync(String key, String expect, String update) {
        log("compareAndSetAsync", key, expect, update);
        return compareAndSetRFuture(key, expect, update).toCompletableFuture();
    }

    private RFuture<Boolean> compareAndSetRFuture(String key, String expect, String update) {
        return getRBucket(key, stringCodec).compareAndSetAsync(expect, update);
    }

    @Override
    public void setEX(String key, String value, Duration duration) {
        log("setEX", key, value, duration);
        getRBucket(key, stringCodec).set(value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public CompletableFuture<Void> setEXAsync(String key, String value, Duration duration) {
        log("setEXAsync", key, value, duration);
        return setEXRFuture(key, value, duration).toCompletableFuture();
    }

    private RFuture<Void> setEXRFuture(String key, String value, Duration duration) {
        return getRBucket(key, stringCodec).setAsync(value, duration.toMillis(), TimeUnit.MILLISECONDS);
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
        return getRBucket(key, stringCodec).size();
    }

    @Override
    public CompletableFuture<Long> strLenAsync(String key) {
        log("strLenAsync", key);
        return strLenRFuture(key).toCompletableFuture();
    }

    private RFuture<Long> strLenRFuture(String key) {
        return getRBucket(key, stringCodec).sizeAsync();
    }

    @Override
    public boolean bfAdd(String key, Object item) {
        log("bfAdd", key, item);
        return getRBloomFilter(key).add(item);
    }

    @Override
    public long bfCard(String key) {
        log("bfCard", key);
        return getRBloomFilter(key).count();
    }

    @Override
    public boolean bfExists(String key, Object item) {
        log("bfExists", key);
        return getRBloomFilter(key).contains(item);
    }

    @Override
    public boolean bfmAdd(String key, Object item) {
        log("bfmAdd", key, item);
        return getRBloomFilter(key).add(item);
    }

    @Override
    public boolean bfReserve(String key, long expectedInsertions, double falseProbability) {
        log("bfReserve", key, expectedInsertions, falseProbability);
        return getRBloomFilter(key).tryInit(expectedInsertions, falseProbability);
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, leaseTime, unit);
        return getRLock(key).tryLock(waitTime, leaseTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, long leaseTime, TimeUnit unit) {
        log("tryLockAsync", key, waitTime, leaseTime, unit);
        return tryLockRFuture(key, waitTime, leaseTime, unit).toCompletableFuture();
    }

    private RFuture<Boolean> tryLockRFuture(String key, long waitTime, long leaseTime, TimeUnit unit) {
        return getRLock(key).tryLockAsync(waitTime, leaseTime, unit);
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        log("tryLock", key, waitTime, unit);
        return getRLock(key).tryLock(waitTime, unit);
    }

    @Override
    public CompletableFuture<Boolean> tryLockAsync(String key, long waitTime, TimeUnit unit) {
        log("tryLockAsync", key, waitTime, unit);
        return tryLockRFuture(key, waitTime, unit).toCompletableFuture();
    }

    private RFuture<Boolean> tryLockRFuture(String key, long waitTime, TimeUnit unit) {
        return getRLock(key).tryLockAsync(waitTime, unit);
    }

    @Override
    public void unlock(String key) {
        log("unlock", key);
        getRLock(key).unlock();
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key) {
        log("unlockAsync", key);
        return unlockRFuture(key).toCompletableFuture();
    }

    private RFuture<Void> unlockRFuture(String key) {
        return getRLock(key).unlockAsync();
    }

    @Override
    public CompletableFuture<Void> unlockAsync(String key, long threadId) {
        log("unlockAsync", key, threadId);
        return unlockAsyncRFuture(key, threadId).toCompletableFuture();
    }

    private RFuture<Void> unlockAsyncRFuture(String key, long threadId) {
        return getRLock(key).unlockAsync(threadId);
    }

    @Override
    public boolean forceUnlock(String key) {
        log("forceUnlock", key);
        return getRLock(key).forceUnlock();
    }

    @Override
    public CompletableFuture<Boolean> forceUnlockAsync(String key) {
        log("forceUnlockAsync", key);
        return forceUnlockRFuture(key).toCompletableFuture();
    }

    private RFuture<Boolean> forceUnlockRFuture(String key) {
        return getRLock(key).forceUnlockAsync();
    }

    @Override
    public Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScript", script, keys, values);
        return Optional.ofNullable(executeScriptRFuture(script, keys, values).toCompletableFuture().join());
    }

    @Override
    public CompletableFuture<Object> executeScriptAsync(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        log("executeScriptAsync", script, keys, values);
        return executeScriptRFuture(script, keys, values).toCompletableFuture();
    }

    private RFuture<Object> executeScriptRFuture(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        /* evalSha: 此处必须加上 StringCodec.INSTANCE */
        final RScript rScript = getDataSource().getScript(StringCodec.INSTANCE);
        String shaDigests = sha1DigestAsHex(script);
        final List<Boolean> scriptExists = rScript.scriptExists(shaDigests);
        if (null != scriptExists && !scriptExists.isEmpty()) {
            final Boolean exists = scriptExists.get(0);
            if (exists) {
                try {
                    return rScript.evalShaAsync(RScript.Mode.READ_WRITE, shaDigests, RScript.ReturnType.VALUE, keys, values);
                } catch (RedisException e) {
                    return rScript.evalAsync(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values);
                }
            }
        }

        /* eval script */
        return rScript.evalAsync(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values);
    }

    @Override
    public long exists(String... keys) {
        log("exists", keys);
        return getRKeys().countExists(keys);
    }

    @Override
    public CompletableFuture<Long> existsAsync(String... keys) {
        log("existsAsync", keys);
        return existsRFuture(keys).toCompletableFuture();
    }

    private RFuture<Long> existsRFuture(String... keys) {
        return getRKeys().countExistsAsync(keys);
    }

    @Override
    public long del(String... keys) {
        log("del", keys);
        return getRKeys().delete(keys);
    }

    @Override
    public CompletableFuture<Long> delAsync(String... keys) {
        log("del", keys);
        return delRFuture(keys).toCompletableFuture();
    }

    private RFuture<Long> delRFuture(String... keys) {
        return getRKeys().deleteAsync(keys);
    }

    @Override
    public long ttl(String key) {
        log("ttl", key);
        final long remainTimeToLive = getDataSource().getBucket(key).remainTimeToLive();
        return remainTimeToLive < 0 ? remainTimeToLive : remainTimeToLive / 1000;
    }

    @Override
    public CompletableFuture<Long> ttlAsync(String key) {
        log("ttlAsync", key);
        return ttlRFuture(key)
                .handle((v, e) -> {
                    if (null != e) {
                        log("ttlAsync", key, e);
                        return 0L;
                    }
                    return v < 0 ? v : v / 1000;
                })
                .toCompletableFuture();
    }

    private RFuture<Long> ttlRFuture(String key) {
        return getDataSource().getBucket(key).remainTimeToLiveAsync();
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
    public CompletableFuture<KeyType> typeAsync(String key) {
        log("typeAsync", key);
        return typeRFuture(key)
                .toCompletableFuture()
                .handle((type, e) -> {
                    if (null != e) {
                        log("typeAsync", key, e);
                    }
                    if (type == RType.OBJECT) {
                        return KeyType.STRING;
                    } else if (type == RType.MAP) {
                        return KeyType.HASH;
                    } else {
                        return KeyType.valueOf(type.name());
                    }
                });
    }

    private RFuture<RType> typeRFuture(String key) {
        return getRKeys().getTypeAsync(key);
    }

    @Override
    public boolean trySetRateLimiter(String key, long rate, long rateInterval) {
        log("trySetRateLimiter", key, rate, rateInterval);
        return getRateLimiter(key).trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Boolean> trySetRateLimiterAsync(String key, long rate, long rateInterval) {
        log("trySetRateLimiterAsync", key, rate, rateInterval);
        return trySetRateLimiterRFuture(key, rate, rateInterval).toCompletableFuture();
    }

    private RFuture<Boolean> trySetRateLimiterRFuture(String key, long rate, long rateInterval) {
        return getRateLimiter(key).trySetRateAsync(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
    }

    @Override
    public boolean tryAcquire(String key) {
        log("tryAcquire", key);
        return getRateLimiter(key).tryAcquire();
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key) {
        log("tryAcquireAsync", key);
        return tryAcquireRFuture(key).toCompletableFuture();
    }

    private RFuture<Boolean> tryAcquireRFuture(String key) {
        return getRateLimiter(key).tryAcquireAsync();
    }

    @Override
    public boolean tryAcquire(String key, long permits) {
        log("tryAcquire", key, permits);
        return getRateLimiter(key).tryAcquire(permits);
    }

    @Override
    public CompletableFuture<Boolean> tryAcquireAsync(String key, long permits) {
        log("tryAcquireAsync", key, permits);
        return tryAcquireRFuture(key, permits).toCompletableFuture();
    }

    private RFuture<Boolean> tryAcquireRFuture(String key, long permits) {
        return getRateLimiter(key).tryAcquireAsync(permits);
    }

    private <T> RBucket<T> getRBucket(String key) {
        return getDataSource().getBucket(key);
    }

    private <T> RBucket<T> getRBucket(String key, Codec codec) {
        return getDataSource().getBucket(key, codec);
    }

    private RBuckets getRBuckets(Codec codec) {
        return getDataSource().getBuckets(codec);
    }

    private <K, V> RMap<K, V> getMap(String key) {
        return getDataSource().getMap(key, stringCodec);
    }

    private <V> RList<V> getList(String key) {
        return getDataSource().getList(key, stringCodec);
    }

    private <V> RBlockingDeque<V> getBlockingDeque(String key) {
        return getDataSource().getBlockingDeque(key, stringCodec);
    }

    private <V> RDeque<V> getDeque(String key) {
        return getDataSource().getDeque(key, stringCodec);
    }

    private <V> RSet<V> getSet(String key) {
        return getDataSource().getSet(key, stringCodec);
    }

    private <V> RScoredSortedSet<V> getRScoredSortedSet(String key) {
        return getDataSource().getScoredSortedSet(key, stringCodec);
    }

    private <V> RBloomFilter<V> getRBloomFilter(String key) {
        return getDataSource().getBloomFilter(key, stringCodec);
    }

    private RKeys getRKeys() {
        return getDataSource().getKeys();
    }

    private RBinaryStream getRBinaryStream(String key) {
        return getDataSource().getBinaryStream(key);
    }

    private <V> RHyperLogLog<V> getHyperLogLog(String key) {
        return getDataSource().getHyperLogLog(key, stringCodec);
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
        return getDataSource().getGeo(key, stringCodec);
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
