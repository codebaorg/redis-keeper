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

/**
 * The interface K geo.
 */
public interface KGeo extends KGeoAsync {
    /**
     * Geo add long.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the long
     */
    long geoAdd(String key, double longitude, double latitude, Object member);

    /**
     * Geo add xx boolean.
     *
     * @param key       the key
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param member    the member
     * @return the boolean
     */
    boolean geoAddXX(String key, double longitude, double latitude, Object member);

    /**
     * Geo dist double.
     *
     * @param key          the key
     * @param firstMember  the first member
     * @param secondMember the second member
     * @param geoUnit      the geo unit
     * @return the double
     */
    Double geoDist(String key, Object firstMember, Object secondMember, String geoUnit);

    /**
     * Geo hash map.
     *
     * @param key     the key
     * @param members the members
     * @return the map
     */
    Map<Object, String> geoHash(String key, Object... members);

    /**
     * Geo pos map.
     *
     * @param key     the key
     * @param members the members
     * @return the map
     */
    Map<Object, double[]> geoPos(String key, Object... members);

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
    Map<Object, Double> geoRadius(String key, double longitude, double latitude, double radius, String geoUnit);

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
    List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, String order);

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
    List<Object> geoSearch(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order);

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
    List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, String order);

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
    List<Object> geoSearch(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order);

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
    List<Object> geoSearch(String key, Object member, double radius, String geoUnit, String order);

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
    List<Object> geoSearch(String key, Object member, double radius, String geoUnit, int count, String order);

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
    List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, String order);

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
    List<Object> geoSearch(String key, Object member, double width, double height, String geoUnit, int count, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double radius, String geoUnit, int count, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, double longitude, double latitude, double width, double height, String geoUnit, int count, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, Object member, double radius, String geoUnit, int count, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, String order);

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
    Map<Object, Double> geoSearchWithDistance(String key, Object member, double width, double height, String geoUnit, int count, String order);

}
