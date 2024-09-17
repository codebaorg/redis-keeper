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

import org.codeba.redis.keeper.core.KGeo;
import org.redisson.api.GeoPosition;
import org.redisson.api.RGeo;
import org.redisson.api.RedissonClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.client.codec.Codec;

import java.util.List;
import java.util.Map;

/**
 * The type K redisson geo.
 */
class KRedissonGeo extends KRedissonGeoAsync implements KGeo {
    /**
     * The Redisson client.
     */
    private final RedissonClient redissonClient;
    /**
     * The Codec.
     */
    private final Codec codec;

    /**
     * Instantiates a new K redisson geo.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonGeo(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
        this.redissonClient = redissonClient;
        this.codec = codec;
    }

    @Override
    public long geoAdd(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).add(longitude, latitude, member);
    }

    @Override
    public boolean geoAddXX(String key, double longitude, double latitude, Object member) {
        return getRGeo(key).addIfExists(longitude, latitude, member);
    }

    @Override
    public Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit) {
        return getRGeo(key).dist(firstMember, secondMember, parseGeoUnit(geoUnit));
    }

    @Override
    public Map<Object, String> geoHash(String key, Object... members) {
        return getRGeo(key).hash(members);
    }

    @Override
    public Map<Object, double[]> geoPos(String key, Object... members) {
        final Map<Object, GeoPosition> pos = getRGeo(key).pos(members);
        return posMap(pos);
    }

    @Override
    public Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit) {
        return getRGeo(key).searchWithDistance(GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(longitude, latitude, radius, geoUnit, order);
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).search(search);
    }

    @Override
    public List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).search(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(longitude, latitude).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).radius(radius, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)));
        return getRGeo(key).searchWithDistance(search);
    }

    @Override
    public Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order) {
        final OptionalGeoSearch search = getOptionalGeoSearch(order, GeoSearchArgs.from(member).box(width, height, parseGeoUnit(geoUnit)).count(count));
        return getRGeo(key).searchWithDistance(search);
    }

    /**
     * Gets r geo.
     *
     * @param <V> the type parameter
     * @param key the key
     * @return the r geo
     */
    private <V> RGeo<V> getRGeo(String key) {
        return redissonClient.getGeo(key, codec);
    }

}
