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

package org.codeba.redis.keeper.core;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The interface Cache template.
 *
 * @author codeba
 */
public interface CacheTemplate {

    /**
     * Bit count long.
     *
     * @param key the key
     * @return the long
     */
    long bitCount(String key);

    /**
     * Bit field set signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the long
     */
    long bitFieldSetSigned(String key, int size, long offset, long value);

    /**
     * Bit field set un signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the long
     */
    long bitFieldSetUnSigned(String key, int size, long offset, long value);

    /**
     * Bit field get signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the long
     */
    long bitFieldGetSigned(String key, int size, long offset);

    /**
     * Bit field get un signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the long
     */
    long bitFieldGetUnSigned(String key, int size, long offset);

    /**
     * Bit op or.
     *
     * @param destKey the dest key
     * @param keys    the keys
     */
    void bitOpOr(String destKey, String... keys);

    /**
     * Gets bit.
     *
     * @param key      the key
     * @param bitIndex the bit index
     * @return the bit
     */
    boolean getBit(String key, long bitIndex);

    /**
     * Sets bit.
     *
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the bit
     */
    boolean setBit(String key, long offset, boolean value);

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

    /**
     * H del map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<String, Boolean> hDel(String key, String... fields);

    /**
     * H del async.
     *
     * @param key    the key
     * @param fields the fields
     */
    void hDelAsync(String key, String... fields);

    /**
     * H exists map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<String, Boolean> hExists(String key, String... fields);

    /**
     * H get optional.
     *
     * @param key   the key
     * @param field the field
     * @return the optional
     */
    Optional<Object> hGet(String key, String field);

    /**
     * H get all map.
     *
     * @param key the key
     * @return the map
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * H incr by object.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the object
     */
    Object hIncrBy(String key, String field, Number value);

    /**
     * H incr by async.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hIncrByAsync(String key, String field, Number value);

    /**
     * H keys collection.
     *
     * @param key the key
     * @return the collection
     */
    Collection<Object> hKeys(String key);

    /**
     * H len int.
     *
     * @param key the key
     * @return the int
     */
    int hLen(String key);

    /**
     * Hm get map.
     *
     * @param key    the key
     * @param fields the fields
     * @return the map
     */
    Map<Object, Object> hmGet(String key, Set<Object> fields);

    /**
     * Hm set.
     *
     * @param key   the key
     * @param kvMap the kv map
     */
    void hmSet(String key, Map<?, ?> kvMap);

    /**
     * Hm set async.
     *
     * @param key   the key
     * @param kvMap the kv map
     */
    void hmSetAsync(String key, Map<?, ?> kvMap);

    /**
     * H set.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hSet(String key, String field, Object value);

    /**
     * H set async.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hSetAsync(String key, String field, Object value);

    /**
     * H rand field set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> hRandField(String key, int count);

    /**
     * H rand field with values map.
     *
     * @param key   the key
     * @param count the count
     * @return the map
     */
    Map<Object, Object> hRandFieldWithValues(String key, int count);

    /**
     * H scan iterator.
     *
     * @param key        the key
     * @param keyPattern the key pattern
     * @return the iterator
     */
    Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern);

    /**
     * H scan iterator.
     *
     * @param key        the key
     * @param keyPattern the key pattern
     * @param count      the count
     * @return the iterator
     */
    Iterator<Map.Entry<Object, Object>> hScan(String key, String keyPattern, int count);

    /**
     * H set nx.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hSetNX(String key, String field, Object value);

    /**
     * H set nx async.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     */
    void hSetNXAsync(String key, String field, Object value);

    /**
     * H str len int.
     *
     * @param key   the key
     * @param field the field
     * @return the int
     */
    int hStrLen(String key, String field);

    /**
     * H va ls collection.
     *
     * @param key the key
     * @return the collection
     */
    Collection<Object> hVALs(String key);

    /**
     * Pf add boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    boolean pfAdd(String key, Collection<Object> elements);

    /**
     * Pf count long.
     *
     * @param key the key
     * @return the long
     */
    long pfCount(String key);

    /**
     * Pf count long.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the long
     */
    long pfCount(String key, String... otherKeys);

    /**
     * Pf merge.
     *
     * @param destKey    the dest key
     * @param sourceKeys the source keys
     */
    void pfMerge(String destKey, String... sourceKeys);

    /**
     * Bl move optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param pollLeft    the poll left
     * @return the optional
     */
    Optional<Object> blMove(String source, String destination, Duration timeout, boolean pollLeft);

    /**
     * Bl pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> blPop(String key);

    /**
     * Bl pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> blPop(String key, int count);

    /**
     * Bl pop optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> blPop(String key, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Bl pop optional.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> blPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException;

    /**
     * Br pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> brPop(String key);

    /**
     * Br pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> brPop(String key, int count);

    /**
     * Br pop optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> brPop(String key, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Br pop optional.
     *
     * @param key       the key
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> brPop(String key, long timeout, TimeUnit unit, String... otherKeys) throws InterruptedException;

    /**
     * Br popl push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param timeout     the timeout
     * @param unit        the unit
     * @return the optional
     * @throws InterruptedException the interrupted exception
     */
    Optional<Object> brPoplPush(String source, String destination, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * L index optional.
     *
     * @param key   the key
     * @param index the index
     * @return the optional
     */
    Optional<Object> lIndex(String key, int index);

    /**
     * L insert int.
     *
     * @param key     the key
     * @param before  the before
     * @param pivot   the pivot
     * @param element the element
     * @return the int
     */
    int lInsert(String key, boolean before, Object pivot, Object element);

    /**
     * Llen int.
     *
     * @param key the key
     * @return the int
     */
    int llen(String key);

    /**
     * L move optional.
     *
     * @param source      the source
     * @param destination the destination
     * @param pollLeft    the poll left
     * @return the optional
     */
    Optional<Object> lMove(String source, String destination, boolean pollLeft);

    /**
     * L pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> lPop(String key, int count);

    /**
     * L push int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int lPush(String key, Object... elements);

    /**
     * L push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int lPushX(String key, Object... elements);

    /**
     * L range list.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     * @return the list
     */
    List<Object> lRange(String key, int fromIndex, int toIndex);

    /**
     * L rem boolean.
     *
     * @param key     the key
     * @param element the element
     * @return the boolean
     */
    boolean lRem(String key, Object element);

    /**
     * L set.
     *
     * @param key     the key
     * @param index   the index
     * @param element the element
     */
    void lSet(String key, int index, Object element);

    /**
     * L trim.
     *
     * @param key       the key
     * @param fromIndex the from index
     * @param toIndex   the to index
     */
    void lTrim(String key, int fromIndex, int toIndex);

    /**
     * R pop list.
     *
     * @param key   the key
     * @param count the count
     * @return the list
     */
    List<Object> rPop(String key, int count);

    /**
     * R popl push optional.
     *
     * @param source      the source
     * @param destination the destination
     * @return the optional
     */
    Optional<Object> rPoplPush(String source, String destination);

    /**
     * R push boolean.
     *
     * @param key      the key
     * @param elements the elements
     * @return the boolean
     */
    boolean rPush(String key, Object... elements);

    /**
     * R push x int.
     *
     * @param key      the key
     * @param elements the elements
     * @return the int
     */
    int rPushX(String key, Object... elements);

    /**
     * S add boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    boolean sAdd(String key, Object member);

    /**
     * S add async.
     *
     * @param key    the key
     * @param member the member
     */
    void sAddAsync(String key, Object member);

    /**
     * S add boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean sAdd(String key, Collection<?> members);

    /**
     * S add async.
     *
     * @param key     the key
     * @param members the members
     */
    void sAddAsync(String key, Collection<?> members);

    /**
     * S card int.
     *
     * @param key the key
     * @return the int
     */
    int sCard(String key);

    /**
     * S diff set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sDiff(String key, String... otherKeys);

    /**
     * S diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sDiffStore(String destination, String... keys);

    /**
     * S inter set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sInter(String key, String... otherKeys);

    /**
     * S inter store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sInterStore(String destination, String... keys);

    /**
     * S is member boolean.
     *
     * @param key    the key
     * @param member the member
     * @return the boolean
     */
    boolean sIsMember(String key, Object member);

    /**
     * S members set.
     *
     * @param key the key
     * @return the set
     */
    Set<Object> sMembers(String key);

    /**
     * S move boolean.
     *
     * @param source      the source
     * @param destination the destination
     * @param member      the member
     * @return the boolean
     */
    boolean sMove(String source, String destination, Object member);

    /**
     * S pop optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> sPop(String key);

    /**
     * S pop set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> sPop(String key, int count);

    /**
     * S rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> sRandMember(String key);

    /**
     * S rand member set.
     *
     * @param key   the key
     * @param count the count
     * @return the set
     */
    Set<Object> sRandMember(String key, int count);

    /**
     * S rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean sRem(String key, Collection<?> members);

    /**
     * S scan iterator.
     *
     * @param key the key
     * @return the iterator
     */
    Iterator<Object> sScan(String key);

    /**
     * S scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @return the iterator
     */
    Iterator<Object> sScan(String key, String pattern);

    /**
     * S scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @param count   the count
     * @return the iterator
     */
    Iterator<Object> sScan(String key, String pattern, int count);

    /**
     * S union set.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the set
     */
    Set<Object> sUnion(String key, String... otherKeys);

    /**
     * S union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int sUnionStore(String destination, String... keys);

    /**
     * Bzm pop optional.
     *
     * @param timeout the timeout
     * @param unit    the unit
     * @param key     the key
     * @param min     the min
     * @return the optional
     */
    Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min);

    /**
     * Bzm pop collection.
     *
     * @param key   the key
     * @param min   the min
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzmPop(String key, boolean min, int count);

    /**
     * Bzm pop optional.
     *
     * @param timeout   the timeout
     * @param unit      the unit
     * @param key       the key
     * @param min       the min
     * @param otherKeys the other keys
     * @return the optional
     */
    Optional<Object> bzmPop(long timeout, TimeUnit unit, String key, boolean min, String... otherKeys);

    /**
     * Bz pop max optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    Optional<Object> bzPopMax(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzPopMax(String key, int count);

    /**
     * Bz pop min optional.
     *
     * @param key     the key
     * @param timeout the timeout
     * @param unit    the unit
     * @return the optional
     */
    Optional<Object> bzPopMin(String key, long timeout, TimeUnit unit);

    /**
     * Bz pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> bzPopMin(String key, int count);

    /**
     * Z add boolean.
     *
     * @param key    the key
     * @param score  the score
     * @param member the member
     * @return the boolean
     */
    boolean zAdd(String key, double score, Object member);

    /**
     * Z add int.
     *
     * @param key     the key
     * @param members the members
     * @return the int
     */
    int zAdd(String key, Map<Object, Double> members);

    /**
     * Z card int.
     *
     * @param key the key
     * @return the int
     */
    int zCard(String key);

    /**
     * Z count int.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the int
     */
    int zCount(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z diff collection.
     *
     * @param key  the key
     * @param keys the keys
     * @return the collection
     */
    Collection<Object> zDiff(String key, String... keys);

    /**
     * Z diff store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int zDiffStore(String destination, String... keys);

    /**
     * Z incr by double.
     *
     * @param key       the key
     * @param increment the increment
     * @param member    the member
     * @return the double
     */
    Double zIncrBy(String key, Number increment, Object member);

    /**
     * Z inter collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    Collection<Object> zInter(String key, String... otherKeys);

    /**
     * Z inter store int.
     *
     * @param destination the destination
     * @param otherKeys   the other keys
     * @return the int
     */
    int zInterStore(String destination, String... otherKeys);

    /**
     * Z inter store aggregate int.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param otherKeys   the other keys
     * @return the int
     */
    int zInterStoreAggregate(String destination, String aggregate, String... otherKeys);

    /**
     * Z inter store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zInterStore(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z inter store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zInterStore(String destination, String aggregate, Map<String, Double> keyWithWeight);

    /**
     * Z lex count int.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @param toElement     the to element
     * @param toInclusive   the to inclusive
     * @return the int
     */
    int zLexCount(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive);

    /**
     * Z lex count head int.
     *
     * @param key         the key
     * @param toElement   the to element
     * @param toInclusive the to inclusive
     * @return the int
     */
    int zLexCountHead(String key, String toElement, boolean toInclusive);

    /**
     * Z lex count tail int.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @return the int
     */
    int zLexCountTail(String key, String fromElement, boolean fromInclusive);

    /**
     * Zm pop optional.
     *
     * @param key the key
     * @param min the min
     * @return the optional
     */
    Optional<Object> zmPop(String key, boolean min);


    /**
     * Zm pop optional.
     *
     * @param key       the key
     * @param min       the min
     * @param timeout   the timeout
     * @param unit      the unit
     * @param otherKeys the other keys
     * @return the optional
     */
    Optional<Object> zmPop(String key, boolean min, long timeout, TimeUnit unit, String... otherKeys);

    /**
     * Z pop max optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zPopMax(String key);

    /**
     * Z pop max collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zPopMax(String key, int count);

    /**
     * Z pop min optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zPopMin(String key);

    /**
     * Z pop min collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zPopMin(String key, int count);

    /**
     * Z rand member optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> zRandMember(String key);

    /**
     * Z rand member collection.
     *
     * @param key   the key
     * @param count the count
     * @return the collection
     */
    Collection<Object> zRandMember(String key, int count);

    /**
     * Z range collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    Collection<Object> zRange(String key, int startIndex, int endIndex);

    /**
     * Z range collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the collection
     */
    Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z range collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @param offset              the offset
     * @param count               the count
     * @return the collection
     */
    Collection<Object> zRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count);

    /**
     * Z range reversed collection.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, int startIndex, int endIndex);

    /**
     * Z range reversed collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z range reversed collection.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @param offset              the offset
     * @param count               the count
     * @return the collection
     */
    Collection<Object> zRangeReversed(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int count);

    /**
     * Z rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    Optional<Integer> zRank(String key, Object member);

    /**
     * Z rem boolean.
     *
     * @param key     the key
     * @param members the members
     * @return the boolean
     */
    boolean zRem(String key, Collection<?> members);

    /**
     * Z rem range by lex optional.
     *
     * @param key           the key
     * @param fromElement   the from element
     * @param fromInclusive the from inclusive
     * @param toElement     the to element
     * @param toInclusive   the to inclusive
     * @return the optional
     */
    Optional<Integer> zRemRangeByLex(String key, String fromElement, boolean fromInclusive, String toElement, boolean toInclusive);

    /**
     * Z rem range by rank optional.
     *
     * @param key        the key
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the optional
     */
    Optional<Integer> zRemRangeByRank(String key, int startIndex, int endIndex);

    /**
     * Z rem range by score optional.
     *
     * @param key                 the key
     * @param startScore          the start score
     * @param startScoreInclusive the start score inclusive
     * @param endScore            the end score
     * @param endScoreInclusive   the end score inclusive
     * @return the optional
     */
    Optional<Integer> zRemRangeByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * Z rev rank optional.
     *
     * @param key    the key
     * @param member the member
     * @return the optional
     */
    Optional<Integer> zRevRank(String key, Object member);

    /**
     * Z scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @return the iterator
     */
    Iterator<Object> zScan(String key, String pattern);

    /**
     * Z scan iterator.
     *
     * @param key     the key
     * @param pattern the pattern
     * @param count   the count
     * @return the iterator
     */
    Iterator<Object> zScan(String key, String pattern, int count);

    /**
     * Z score list.
     *
     * @param key     the key
     * @param members the members
     * @return the list
     */
    List<Double> zScore(String key, List<Object> members);

    /**
     * Z union collection.
     *
     * @param key       the key
     * @param otherKeys the other keys
     * @return the collection
     */
    Collection<Object> zUnion(String key, String... otherKeys);

    /**
     * Z union store int.
     *
     * @param destination the destination
     * @param keys        the keys
     * @return the int
     */
    int zUnionStore(String destination, String... keys);

    /**
     * Z union store aggregate int.
     *
     * @param destination the destination
     * @param aggregate   the aggregate
     * @param keys        the keys
     * @return the int
     */
    int zUnionStoreAggregate(String destination, String aggregate, String... keys);

    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zUnionStore(String destination, Map<String, Double> keyWithWeight);

    /**
     * Z union store int.
     *
     * @param destination   the destination
     * @param aggregate     the aggregate
     * @param keyWithWeight the key with weight
     * @return the int
     */
    int zUnionStore(String destination, String aggregate, Map<String, Double> keyWithWeight);

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     */
    void append(String key, Object value);

    /**
     * Decr long.
     *
     * @param key the key
     * @return the long
     */
    long decr(String key);

    /**
     * Decr by long.
     *
     * @param key       the key
     * @param decrement the decrement
     * @return the long
     */
    long decrBy(String key, long decrement);

    /**
     * Get optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<Object> get(String key);

    /**
     * Gets del.
     *
     * @param key the key
     * @return the del
     */
    Optional<Object> getDel(String key);

    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     */
    long getLong(String key);

    /**
     * Incr long.
     *
     * @param key the key
     * @return the long
     */
    long incr(String key);

    /**
     * Incr by long.
     *
     * @param key       the key
     * @param increment the increment
     * @return the long
     */
    long incrBy(String key, long increment);

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     */
    double getDouble(String key);

    /**
     * Incr by float double.
     *
     * @param key       the key
     * @param increment the increment
     * @return the double
     */
    double incrByFloat(String key, double increment);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, long expect, long update);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, double expect, double update);

    /**
     * M get map.
     *
     * @param keys the keys
     * @return the map
     */
    Map<String, Object> mGet(String... keys);

    /**
     * M set.
     *
     * @param kvMap the kv map
     */
    void mSet(Map<String, String> kvMap);

    /**
     * M set nx boolean.
     *
     * @param kvMap the kv map
     * @return the boolean
     */
    boolean mSetNX(Map<String, String> kvMap);

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    void set(String key, String value);

    /**
     * Compare and set boolean.
     *
     * @param key    the key
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    boolean compareAndSet(String key, String expect, String update);

    /**
     * Sets ex.
     *
     * @param key      the key
     * @param value    the value
     * @param duration the duration
     */
    void setEX(String key, String value, Duration duration);

    /**
     * Str len long.
     *
     * @param key the key
     * @return the long
     */
    long strLen(String key);

    /**
     * Bf add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfAdd(String key, Object item);

    /**
     * Bf card long.
     *
     * @param key the key
     * @return the long
     */
    long bfCard(String key);

    /**
     * Bf exists boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfExists(String key, Object item);

    /**
     * Bfm add boolean.
     *
     * @param key  the key
     * @param item the item
     * @return the boolean
     */
    boolean bfmAdd(String key, Object item);

    /**
     * Bf reserve boolean.
     *
     * @param key                the key
     * @param expectedInsertions the expected insertions
     * @param falseProbability   the false probability
     * @return the boolean
     */
    boolean bfReserve(String key, long expectedInsertions, double falseProbability);

    /**
     * Try lock boolean.
     *
     * @param key       the key
     * @param waitTime  the wait time
     * @param leaseTime the lease time
     * @param unit      the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * Try lock boolean.
     *
     * @param key      the key
     * @param waitTime the wait time
     * @param unit     the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException;

    /**
     * Unlock.
     *
     * @param key the key
     */
    void unlock(String key);

    /**
     * Unlock async.
     *
     * @param key the key
     */
    void unlockAsync(String key);

    /**
     * Unlock async.
     *
     * @param key      the key
     * @param threadId the thread id
     */
    void unlockAsync(String key, long threadId);

    /**
     * Force unlock boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean forceUnlock(String key);

    /**
     * Execute script optional.
     *
     * @param script the script
     * @param keys   the keys
     * @param values the values
     * @return the optional
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException;

    /**
     * Exists long.
     *
     * @param keys the keys
     * @return the long
     */
    long exists(String... keys);

    /**
     * Del long.
     *
     * @param keys the keys
     * @return the long
     */
    long del(String... keys);

    /**
     * Ttl long.
     *
     * @param key the key
     * @return the long
     */
    long ttl(String key);

    /**
     * Scan iterable.
     *
     * @param keyPattern the key pattern
     * @return the iterable
     */
    Iterable<String> scan(String keyPattern);

    /**
     * Scan iterable.
     *
     * @param keyPattern the key pattern
     * @param count      the count
     * @return the iterable
     */
    Iterable<String> scan(String keyPattern, int count);

    /**
     * Type key type.
     *
     * @param key the key
     * @return the key type
     */
    KeyType type(String key);

    /**
     * Try set rate limiter boolean.
     *
     * @param key          the key
     * @param rate         the rate
     * @param rateInterval the rate interval
     * @return the boolean
     */
    boolean trySetRateLimiter(String key, long rate, long rateInterval);

    /**
     * Try acquire boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean tryAcquire(String key);

    /**
     * Try acquire boolean.
     *
     * @param key     the key
     * @param permits the permits
     * @return the boolean
     */
    boolean tryAcquire(String key, long permits);

}
