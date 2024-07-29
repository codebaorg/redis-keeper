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

import org.redisson.api.GeoOrder;
import org.redisson.api.GeoPosition;
import org.redisson.api.GeoUnit;
import org.redisson.api.RFuture;
import org.redisson.api.RGeo;
import org.redisson.api.RedissonClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
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
 * The type R geos.
 */
class RGeos {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Codec stringCodec = new StringCodec();
    private final String connectionInfo;
    private final RedissonClient redissonClient;
    private final boolean invokeParamsPrint;

    /**
     * Instantiates a new R geos.
     *
     * @param connectionInfo    the connection info
     * @param redissonClient    the redisson client
     * @param invokeParamsPrint the invoke params print
     */
    RGeos(String connectionInfo, RedissonClient redissonClient, boolean invokeParamsPrint) {
        this.connectionInfo = connectionInfo;
        this.redissonClient = redissonClient;
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Log.
     *
     * @param cmd    the cmd
     * @param params the params
     */
    void log(String cmd, Object... params) {
        if (this.invokeParamsPrint) {
            log.info("cmd:{}, params:{}, connectionInfo:[{}]", cmd, Arrays.toString(params), connectionInfo);
        }
    }

    /**
     * Geo add long.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the long
     */
    public long geoAdd(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).add(longitude, latitude, member);
    }


    /**
     * Geo add async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the completable future
     */
    public CompletableFuture<Long> geoAddAsync(String key, double longitude, double latitude, Object member) {
        return geoAddRFuture(key, longitude, latitude, member).toCompletableFuture();
    }

    private RFuture<Long> geoAddRFuture(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).addAsync(longitude, latitude, member);
    }


    /**
     * Geo add xx boolean.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the boolean
     */
    public boolean geoAddXX(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).addIfExists(longitude, latitude, member);
    }


    /**
     * Geo add xx async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the completable future
     */
    public CompletableFuture<Boolean> geoAddXXAsync(String key, double longitude, double latitude, Object member) {
        return geoAddXXRFuture(key, longitude, latitude, member).toCompletableFuture();
    }

    private RFuture<Boolean> geoAddXXRFuture(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).addIfExistsAsync(longitude, latitude, member);
    }


    /**
     * Geo dist double.
     *
     * @param key          the key
     * @param firstMember  the first member
     * @param secondMember the second member
     * @param geoUnit      the geo unit
     * @return the double
     */
    public Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit) {
        return getRGeo(key).dist(firstMember, secondMember, parseGeoUnit(geoUnit));
    }


    /**
     * Geo dist async completable future.
     *
     * @param key          the key
     * @param firstMember  the first member
     * @param secondMember the second member
     * @param geoUnit      the geo unit
     * @return the completable future
     */
    public CompletableFuture<Double> geoDistAsync(String key, Object firstMember, Object secondMember, String geoUnit) {
        return geoDistRFuture(key, firstMember, secondMember, geoUnit).toCompletableFuture();
    }

    private RFuture<Double> geoDistRFuture(String key, Object firstMember, Object secondMember, String geoUnit) {
        return getRGeo(key).distAsync(firstMember, secondMember, parseGeoUnit(geoUnit));
    }


    /**
     * Geo hash map.
     *
     * @param key     the key
     * @param members the members
     * @return the map
     */
    public Map<Object, String> geoHash(String key, Object... members) {
        return getRGeo(key).hash(members);
    }


    /**
     * Geo hash async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<Map<Object, String>> geoHashAsync(String key, Object... members) {
        return geoHashRFuture(key, members).toCompletableFuture();
    }

    private RFuture<Map<Object, String>> geoHashRFuture(String key, Object... members) {
        return getRGeo(key).hashAsync(members);
    }


    /**
     * Geo pos map.
     *
     * @param key     the key
     * @param members the members
     * @return the map
     */
    public Map<Object, double[]> geoPos(String key, Object... members) {
        final Map<Object, GeoPosition> pos = getRGeo(key).pos(members);
        return posMap(pos);
    }


    /**
     * Geo pos async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    public CompletableFuture<Map<Object, double[]>> geoPosAsync(String key, Object... members) {
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


    /**
     * Geo radius map.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @return the map
     */
    public Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit) {
        return getRGeo(key).searchWithDistance(GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
    }


    /**
     * Geo radius async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoRadiusAsync(String key, double longitude, double latitude, double radius, String geoUnit) {
        return geoRadiusRFuture(key, longitude, latitude, radius, geoUnit).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoRadiusRFuture(String key, double longitude, double latitude, double radius, String geoUnit) {
        return getRGeo(key).radiusWithDistanceAsync(longitude, latitude, radius, parseGeoUnit(geoUnit));
    }


    /**
     * Geo search list.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the list
     */
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(longitude, latitude, radius, geoUnit, order);
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        return geoSearchRFuture(key, longitude, latitude, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(longitude, latitude, radius, geoUnit, order);
        return getRGeo(key).searchAsync(search);
    }

    /**
     * Geo search list.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the list
     */
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        return geoSearchRFuture(key, longitude, latitude, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search list.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the list
     */
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        return geoSearchRFuture(key, longitude, latitude, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search list.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the list
     */
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        return geoSearchRFuture(key, longitude, latitude, width, height, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search list.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the list
     */
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, String order) {
        return geoSearchRFuture(key, member, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search list.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the list
     */
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        return geoSearchRFuture(key, member, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search list.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the list
     */
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        return geoSearchRFuture(key, member, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search list.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the list
     */
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }


    /**
     * Geo search async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        return geoSearchRFuture(key, member, width, height, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<List<Object>> geoSearchRFuture(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        return geoSearchWithDistanceRFuture(key, longitude, latitude, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param radius    the radius
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        return geoSearchWithDistanceRFuture(key, longitude, latitude, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        return geoSearchWithDistanceRFuture(key, longitude, latitude, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param width     the width
     * @param height    the height
     * @param geoUnit   the geo unit
     * @param count     the count
     * @param order     the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        return geoSearchWithDistanceRFuture(key, longitude, latitude, width, height, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, String order) {
        return geoSearchWithDistanceRFuture(key, member, radius, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param radius  the radius
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, int count, String order) {
        return geoSearchWithDistanceRFuture(key, member, radius, geoUnit, count, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }


    /**
     * Geo search with distance async completable future.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param order   the order
     * @return the completable future
     */
    public CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double width, double height, String geoUnit, String order) {
        return geoSearchWithDistanceRFuture(key, member, width, height, geoUnit, order).toCompletableFuture();
    }

    private RFuture<Map<Object, Double>> geoSearchWithDistanceRFuture(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistanceAsync(search);
    }


    /**
     * Geo search with distance map.
     *
     * @param key     the key
     * @param member  the member
     * @param width   the width
     * @param height  the height
     * @param geoUnit the geo unit
     * @param count   the count
     * @param order   the order
     * @return the map
     */
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order) {
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

    /**
     * Parse geo unit geo unit.
     *
     * @param geoUnit the geo unit
     * @return the geo unit
     */
    private GeoUnit parseGeoUnit(String geoUnit) {
        final GeoUnit[] values = GeoUnit.values();
        for (GeoUnit value : values) {
            if (value.toString().equalsIgnoreCase(geoUnit)) {
                return value;
            }
        }

        return null;
    }

    private <V> RGeo<V> getRGeo(String key) {
        return getDataSource().getGeo(key, stringCodec);
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
