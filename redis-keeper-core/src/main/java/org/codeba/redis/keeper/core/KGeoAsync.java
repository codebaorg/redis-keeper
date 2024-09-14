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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The interface K geo async.
 */
public interface KGeoAsync {

    /**
     * Geo add async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the completable future
     */
    CompletableFuture<Long> geoAddAsync(String key, double longitude, double latitude, Object member);

    /**
     * Geo add xx async completable future.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the completable future
     */
    CompletableFuture<Boolean> geoAddXXAsync(String key, double longitude, double latitude, Object member);

    /**
     * Geo dist async completable future.
     *
     * @param key          the key
     * @param firstMember  the first member
     * @param secondMember the second member
     * @param geoUnit      the geo unit
     * @return the completable future
     */
    CompletableFuture<Double> geoDistAsync(String key, Object firstMember, Object secondMember, String geoUnit);

    /**
     * Geo hash async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Map<Object, String>> geoHashAsync(String key, Object... members);

    /**
     * Geo pos async completable future.
     *
     * @param key     the key
     * @param members the members
     * @return the completable future
     */
    CompletableFuture<Map<Object, double[]>> geoPosAsync(String key, Object... members);

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
    CompletableFuture<Map<Object, Double>> geoRadiusAsync(String key, double longitude, double latitude, double radius, String geoUnit);

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
    CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order);

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
    CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order);

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
    CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order);

    /**
     * Geos search async completable future.
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
    CompletableFuture<List<Object>> geoSearchAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order);

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
    CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, String order);

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
    CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double radius, String geoUnit, int count, String order);

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
    CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, String order);

    /**
     * Geos search async completable future.
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
    CompletableFuture<List<Object>> geoSearchAsync(String key, Object member, double width, double height, String geoUnit, int count, String order);

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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, String order);

    /**
     * Geos search with distance async completable future.
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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order);

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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, String order);

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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order);

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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, String order);

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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double radius, String geoUnit, int count, String order);

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
    CompletableFuture<Map<Object, Double>> geoSearchWithDistanceAsync(String key, Object member, double width, double height, String geoUnit, String order);

}
