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

import org.codeba.redis.keeper.core.KGeoAsync;
import org.redisson.api.GeoOrder;
import org.redisson.api.GeoPosition;
import org.redisson.api.GeoUnit;
import org.redisson.api.RBatch;
import org.redisson.api.RFuture;
import org.redisson.api.RGeoAsync;
import org.redisson.api.RedissonClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.client.codec.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The type K redisson geo async.
 */
class KRedissonGeoAsync extends BaseAsync implements KGeoAsync {
    /**
     * The Log.
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Instantiates a new K redisson geo async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonGeoAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson geo async.
     *
     * @param rBatch the r batch
     * @param codec  the codec
     */
    public KRedissonGeoAsync(RBatch rBatch, Codec codec) {
        super(rBatch, codec);
    }

    @Override
    public CompletableFuture<Long> geoAddAsync(String key, double longitude, double latitude, Object member) {
        return getGeo(key).addAsync(longitude, latitude, member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Boolean> geoAddXXAsync(String key, double longitude, double latitude, Object member) {
        return getGeo(key).addIfExistsAsync(longitude, latitude, member).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Double> geoDistAsync(String key, Object firstMember, Object secondMember, String geoUnit) {
        return getGeo(key).distAsync(firstMember, secondMember, parseGeoUnit(geoUnit)).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, String>> geoHashAsync(String key, Object... members) {
        return getGeo(key).hashAsync(members).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, double[]>> geoPosAsync(String key, Object... members) {
        final RFuture<Map<Object, GeoPosition>> mapRFuture = getGeo(key).posAsync(members);
        return mapRFuture.handle((pos, e) -> {
            if (null != e) {
                log.error("cmd:" + key + ", params:" + Arrays.toString(members), e);
                return Collections.<Object, double[]>emptyMap();
            }
            return posMap(pos);
        }).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoRadiusAsync(String key, double longitude, double latitude, double radius, String geoUnit) {
        return getGeo(key).radiusWithDistanceAsync(longitude, latitude, radius, parseGeoUnit(geoUnit)).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(longitude, latitude, radius, geoUnit, order);
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
    }

    @Override
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getGeo(key).searchWithDistanceAsync(search).toCompletableFuture();
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

    /**
     * Pos map map.
     *
     * @param pos the pos
     * @return the map
     */
    protected Map<Object, double[]> posMap(Map<Object, GeoPosition> pos) {
        return pos.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new double[]{entry.getValue().getLongitude(), entry.getValue().getLatitude()}
                )
        );
    }

    /**
     * Gets optional geo search.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the optional geo search
     */
    protected OptionalGeoSearch getOptionalGeoSearch(double longitude, double latitude, double radius, String geoUnit, String order) {
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        return GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).order(geoOrder);
    }

    /**
     * Gets optional geo search.
     *
     * @param order     the order
     * @param longitude the longitude
     * @return the optional geo search
     */
    protected OptionalGeoSearch getOptionalGeoSearch(String order, OptionalGeoSearch longitude) {
        final GeoOrder geoOrder = GeoOrder.valueOf(order.toUpperCase(Locale.ROOT));
        return longitude.order(geoOrder);
    }

    /**
     * Gets geo async.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the geo async
     */
    private <V> RGeoAsync<V> getGeo(String key) {
        if (null != getBatch()) {
            return super.getBatch().getGeo(key, getCodec());
        } else {
            return super.getRedissonClient().getGeo(key, getCodec());
        }
    }

}
