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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.codec.JsonJacksonCodec;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * The constant CACHE_TEMPLATE.
     */
    public static CacheTemplate CACHE_TEMPLATE;
    /**
     * The Provider.
     */
    public static CacheTemplateProvider<CacheTemplate> PROVIDER;

    static {
        // todo please set your address and password
        String yourAddress = "redis://localhost:6379";
        String yourPass = "yourPass";
        String properties = "redisKeeper:\n" +
                "  redisson:\n" +
                "    datasource:\n" +
                "      ds1:\n" +
                "        invokeParamsPrint: true\n" +
                "        config: \n" +
                "          singleServerConfig:\n" +
                "            address: %1$s\n" +
                "            password: %2$s\n" +
                "      \n" +
                "      ds2:\n" +
                "        invokeParamsPrint: true\n" +
                "        status: RO\n" +
                "        config:\n" +
                "          singleServerConfig:\n" +
                "            address: %1$s\n" +
                "            password: %2$s\n" +
                "            database: 1\n" +
                "      \n" +
                "      ds3:\n" +
                "        invokeParamsPrint: true\n" +
                "        config:\n" +
                "          singleServerConfig:\n" +
                "            address: %1$s\n" +
                "            password: %2$s\n" +
                "            database: 2\n" +
                "\n" +
                "    datasources:\n" +
                "      ds4:\n" +
                "        - invokeParamsPrint: true\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              password: %2$s\n" +
                "\n" +
                "        - invokeParamsPrint: true\n" +
                "          status: RO\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              password: %2$s\n" +
                "              database: 1\n" +
                "\n" +
                "        - invokeParamsPrint: true\n" +
                "          config: \n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              password: %2$s\n" +
                "              database: 2";

        final String format = String.format(properties, yourAddress, yourPass);

        try {
            final CacheKeeperProperties fromYAML = CacheKeeperProperties.fromYAML(format);
            final CacheKeeperProperties.Redisson redisson = fromYAML.getRedisKeeper().getRedisson();

            final DefaultCacheDatasource datasource = new DefaultCacheDatasource();
            datasource.configPostProcessor(v -> v.getConfig().setCodec(new JsonJacksonCodec(getJacksonMapper())));
            final Map<String, CacheTemplate> dsMap = datasource.initialize(redisson.getDatasource());
            final Map<String, List<CacheTemplate>> dssMap = datasource.initializeMulti(redisson.getDatasources());

            PROVIDER = new CacheTemplateProvider<>(dsMap, dssMap);
            CACHE_TEMPLATE = PROVIDER.getTemplate("ds1").orElse(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * Suite test.
     *
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Gets jackson mapper.
     *
     * @return the jackson mapper
     */
    public static ObjectMapper getJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());
        return mapper;
    }

    /**
     * Test poll template.
     */
    public void testPollTemplate() {
        final Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

        final ArrayList<Object> resultList = new ArrayList<>();

        int loop = 900000;
        for (int i = 0; i < loop; i++) {
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                final Optional<CacheTemplate> optional = PROVIDER.pollTemplate("ds4");
                if (optional.isPresent()) {
                    final CacheTemplate cacheTemplate = optional.get();
                    map.computeIfAbsent(cacheTemplate.toString(), k -> new AtomicInteger()).incrementAndGet();
                }
            });

            resultList.add(future);
        }

        CompletableFuture.allOf(resultList.toArray(new CompletableFuture[]{})).join();

        double gap = 0.01;
        final ArrayList<AtomicInteger> atomicIntegers = new ArrayList<>(map.values());
        assertTrue(Math.abs(atomicIntegers.get(0).get() - atomicIntegers.get(1).get()) * 1.0 / 3 / loop < gap);
        assertTrue(Math.abs(atomicIntegers.get(0).get() - atomicIntegers.get(2).get()) * 1.0 / 3 / loop < gap);
        assertTrue(Math.abs(atomicIntegers.get(1).get() - atomicIntegers.get(2).get()) * 1.0 / 3 / loop < gap);

    }

    /**
     * Test poll template 2.
     */
    public void testPollTemplate2() {
        final Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

        final ArrayList<Object> resultList = new ArrayList<>();

        int loop = 900000;
        for (int i = 0; i < loop; i++) {
            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                final Optional<CacheTemplate> optional = PROVIDER.pollTemplate("ds4", CacheDatasourceStatus.RW);
                if (optional.isPresent()) {
                    final CacheTemplate cacheTemplate = optional.get();
                    map.computeIfAbsent(cacheTemplate.toString(), k -> new AtomicInteger()).incrementAndGet();
                }
            });

            resultList.add(future);
        }

        CompletableFuture.allOf(resultList.toArray(new CompletableFuture[]{})).join();

        double gap = 0.01;
        final ArrayList<AtomicInteger> atomicIntegers = new ArrayList<>(map.values());
        assertTrue(Math.abs(atomicIntegers.get(0).get() - atomicIntegers.get(1).get()) * 1.0 / 2 / loop < gap);

    }

    /**
     * Test bit count.
     */
    public void testBitCount() {
        String key = "testBitCount";

        CACHE_TEMPLATE.setBit(key, 1, true);
        CACHE_TEMPLATE.setBit(key, 2, true);
        CACHE_TEMPLATE.setBit(key, 4, true);

        final long bitCount = CACHE_TEMPLATE.bitCount(key);
        assertEquals(3, bitCount);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bit field set signed.
     */
    public void testBitFieldSetSigned() {
        String key = "testBitFieldSetSigned";
        long value = 97;

        final long setSigned = CACHE_TEMPLATE.bitFieldSetSigned(key, 8, 8, value);
        assertEquals(0, setSigned);

        final long getSigned = CACHE_TEMPLATE.bitFieldGetSigned(key, 8, 8);
        assertEquals(value, getSigned);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bit field set un signed.
     */
    public void testBitFieldSetUnSigned() {
        String key = "testBitFieldSetUnSigned";
        long oldValue = 101;

        CACHE_TEMPLATE.bitFieldSetUnSigned(key, 8, 8, oldValue);
        final long setUnSigned = CACHE_TEMPLATE.bitFieldSetUnSigned(key, 8, 8, 97);
        assertEquals(oldValue, setUnSigned);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bit field get signed.
     */
    public void testBitFieldGetSigned() {
        String key = "testBitFieldGetSigned";
        CACHE_TEMPLATE.set(key, "hello");

        assertEquals(6, CACHE_TEMPLATE.bitFieldGetUnSigned(key, 4, 0));
        assertEquals(5, CACHE_TEMPLATE.bitFieldGetUnSigned(key, 3, 2));
        assertEquals(6, CACHE_TEMPLATE.bitFieldGetSigned(key, 4, 0));
        assertEquals(-3, CACHE_TEMPLATE.bitFieldGetSigned(key, 3, 2));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bit op or.
     */
    public void testBitOpOr() {
        String key1 = "key1";
        CACHE_TEMPLATE.set(key1, "foobar");

        String key2 = "key2";
        CACHE_TEMPLATE.set(key2, "abcdef");

        CACHE_TEMPLATE.bitOpOr("dest", key1, key2);
        final Object object = CACHE_TEMPLATE.get("dest").orElse(null);
        assertEquals("goofev", object);

        CACHE_TEMPLATE.del(key1, key2, "dest");
    }

    /**
     * Test get bit.
     */
    public void testGetBit() {
        String key = "mykeybit";
        CACHE_TEMPLATE.setBit(key, 7, true);

        assertFalse(CACHE_TEMPLATE.getBit(key, 0));
        assertTrue(CACHE_TEMPLATE.getBit(key, 7));
        assertFalse(CACHE_TEMPLATE.getBit(key, 100));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo add.
     */
    public void testGeoAdd() {
        String key = "testGeoAdd";
        final long palermo = CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        assertEquals(1, palermo);

        final long catania = CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");
        assertEquals(1, catania);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo add xx.
     */
    public void testGeoAddXX() {
        String key = "testGeoAddXX";

        final boolean palermo = CACHE_TEMPLATE.geoAddXX(key, 13.361389, 38.115556, "Palermo");
        assertFalse(palermo);

        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        final boolean palermo1 = CACHE_TEMPLATE.geoAddXX(key, 13.361389, 38.115556, "Palermo");
        assertTrue(palermo1);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo dist.
     */
    public void testGeoDist() {
        String key = "testGeoDist";
        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");

        final Double km = CACHE_TEMPLATE.geoDist(key, "Palermo", "Catania", "km");
        assertEquals(166.2742, km);

        final Double mi = CACHE_TEMPLATE.geoDist(key, "Palermo", "Catania", "mi");
        assertEquals(103.3182, mi);

        final Double fooBar = CACHE_TEMPLATE.geoDist(key, "Foo", "Bar", "mi");
        assertNull(fooBar);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo hash.
     */
    public void testGeoHash() {
        String key = "testGeoHash";
        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");

        final Map<Object, String> map = CACHE_TEMPLATE.geoHash(key, "Palermo", "Catania");
        assertEquals("sqc8b49rny0", map.get("Palermo"));
        assertEquals("sqdtr74hyu0", map.get("Catania"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo pos.
     */
    public void testGeoPos() {
        String key = "testGeoPos";
        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");

        final Map<Object, double[]> map = CACHE_TEMPLATE.geoPos(key, "Palermo", "Catania", "NonExisting");
        assertEquals(13.36138933897018433, map.get("Palermo")[0]);
        assertEquals(38.11555639549629859, map.get("Palermo")[1]);
        assertEquals(15.08726745843887329, map.get("Catania")[0]);
        assertEquals(37.50266842333162032, map.get("Catania")[1]);
        assertNull(map.get("NonExisting"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo radius.
     */
    public void testGeoRadius() {
        String key = "testGeoRadius";
        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");

        final Map<Object, Double> map = CACHE_TEMPLATE.geoRadius(key, 15, 37, 200, "km");
        assertEquals(190.4424, map.get("Palermo"));
        assertEquals(56.4413, map.get("Catania"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test geo search.
     */
    public void testGeoSearch() {
        String key = "testGeoSearch";
        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");
        CACHE_TEMPLATE.geoAdd(key, 12.758489, 38.788135, "edge1");
        CACHE_TEMPLATE.geoAdd(key, 17.241510, 38.788135, "edge2");

        final List<Object> objects = CACHE_TEMPLATE.geoSearch(key, 15, 37, 200, "km", "asc");
        assertTrue(objects.containsAll(Arrays.asList("Catania", "Palermo")));

        final List<Object> objects2 = CACHE_TEMPLATE.geoSearch(key, 15, 37, 200, "km", 4, "desc");
        assertEquals("Catania", objects2.get(1));
        assertEquals("Palermo", objects2.get(0));

        final List<Object> objects3 = CACHE_TEMPLATE.geoSearch(key, 15, 37, 400, 400, "km", "asc");
        assertEquals("Catania", objects3.get(0));
        assertEquals("Palermo", objects3.get(1));
        assertEquals("edge2", objects3.get(2));
        assertEquals("edge1", objects3.get(3));

        final List<Object> objects5 = CACHE_TEMPLATE.geoSearch(key, 15, 37, 400, 400, "km", 10, "desc");
        assertEquals("edge1", objects5.get(0));
        assertEquals("edge2", objects5.get(1));
        assertEquals("Palermo", objects5.get(2));
        assertEquals("Catania", objects5.get(3));

        final List<Object> objects6 = CACHE_TEMPLATE.geoSearch(key, "Catania", 200, "km", "asc");
        assertEquals("Catania", objects6.get(0));
        assertEquals("Palermo", objects6.get(1));

        final List<Object> objects8 = CACHE_TEMPLATE.geoSearch(key, "Catania", 200, "km", 1, "asc");
        assertEquals("Catania", objects8.get(0));

        final List<Object> objects9 = CACHE_TEMPLATE.geoSearch(key, "Catania", 400, 400, "km", "asc");
        assertEquals("Catania", objects9.get(0));
        assertEquals("Palermo", objects9.get(1));
        assertEquals("edge2", objects9.get(2));

        final List<Object> objects11 = CACHE_TEMPLATE.geoSearch(key, "Catania", 400, 400, "km", 2, "desc");
        assertEquals("edge2", objects11.get(0));
        assertEquals("Palermo", objects11.get(1));

        final Map<Object, Double> map1 = CACHE_TEMPLATE.geoSearchWithDistance(key, 15, 37, 200, "km", "asc");
        assertEquals(56.4413, map1.get("Catania"));
        assertEquals(190.4424, map1.get("Palermo"));

        final Map<Object, Double> map2 = CACHE_TEMPLATE.geoSearchWithDistance(key, 15, 37, 200, "km", 2, "asc");
        assertEquals(56.4413, map2.get("Catania"));
        assertEquals(190.4424, map2.get("Palermo"));

        final Map<Object, Double> map3 = CACHE_TEMPLATE.geoSearchWithDistance(key, 15, 37, 400, 400, "km", "asc");
        assertEquals(56.4413, map3.get("Catania"));
        assertEquals(190.4424, map3.get("Palermo"));
        assertEquals(279.7403, map3.get("edge2"));
        assertEquals(279.7405, map3.get("edge1"));

        final Map<Object, Double> map = CACHE_TEMPLATE.geoSearchWithDistance(key, 15, 37, 400, 400, "km", 2, "desc");
        assertEquals(279.7405, map.get("edge1"));
        assertEquals(279.7403, map.get("edge2"));

        final Map<Object, Double> map4 = CACHE_TEMPLATE.geoSearchWithDistance(key, "Palermo", 200, "km", "asc");
        assertEquals(0.0000, map4.get("Palermo"));
        assertEquals(91.4007, map4.get("edge1"));
        assertEquals(166.2742, map4.get("Catania"));

        final Map<Object, Double> map5 = CACHE_TEMPLATE.geoSearchWithDistance(key, "Palermo", 200, "km", 1, "asc");
        assertEquals(0.0000, map5.get("Palermo"));


        final Map<Object, Double> map6 = CACHE_TEMPLATE.geoSearchWithDistance(key, "Palermo", 400, 400, "km", "asc");
        assertEquals(0.0000, map6.get("Palermo"));
        assertEquals(91.4007, map6.get("edge1"));
        assertEquals(166.2742, map6.get("Catania"));


        final Map<Object, Double> map7 = CACHE_TEMPLATE.geoSearchWithDistance(key, "Palermo", 400, 400, "km", 1, "asc");
        assertEquals(0.0000, map7.get("Palermo"));
        assertNull(map7.get("edge1"));
        assertNull(map7.get("Catania"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h del.
     */
// hash
    public void testHDel() {
        String key = "testHDel";
        String field = "field1";

        CACHE_TEMPLATE.hSet(key, field, "foo");
        final Map<String, Boolean> map = CACHE_TEMPLATE.hDel(key, field);
        assertTrue(map.get(field));

        final Map<String, Boolean> map1 = CACHE_TEMPLATE.hDel(key, field);
        assertFalse(map1.get(field));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h del async.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testHDelAsync() throws InterruptedException {
        String key = "testHDelAsync";
        String field = "field1";

        CACHE_TEMPLATE.hSet(key, field, "foo");

        CACHE_TEMPLATE.hDelAsync(key, field);

        Thread.sleep(1000);

        final Optional<Object> optional = CACHE_TEMPLATE.hGet(key, field);
        assertFalse(optional.isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h exists.
     */
    public void testHExists() {
        String key = "testHExists";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hSet(key, field1, "foo");

        final Map<String, Boolean> map = CACHE_TEMPLATE.hExists(key, field1, field2);
        assertTrue(map.get(field1));
        assertFalse(map.get(field2));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h get.
     */
    public void testHGet() {
        String key = "testHGet";
        String field1 = "field1";

        CACHE_TEMPLATE.hSet(key, field1, "foo");

        final Optional<Object> optional = CACHE_TEMPLATE.hGet(key, field1);
        assertEquals("foo", optional.orElse(null));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h get all.
     */
    public void testHGetAll() {
        String key = "testHGetAll";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hSet(key, field1, "hello");
        CACHE_TEMPLATE.hSet(key, field2, "world");

        final Map<Object, Object> map = CACHE_TEMPLATE.hGetAll(key);
        assertEquals("hello", map.get(field1));
        assertEquals("world", map.get(field2));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h incr by.
     */
    public void testHIncrBy() {
        String key = "testHIncrBy";
        String field1 = "field";

        CACHE_TEMPLATE.hSet(key, field1, 5);

        final Object object = CACHE_TEMPLATE.hIncrBy(key, field1, 1);
        assertEquals(6, Long.parseLong(object.toString()));

        final Object object1 = CACHE_TEMPLATE.hIncrBy(key, field1, -1);
        assertEquals(5, Long.parseLong(object1.toString()));

        final Object object2 = CACHE_TEMPLATE.hIncrBy(key, field1, -10);
        assertEquals(-5, Long.parseLong(object2.toString()));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h incr by async.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testHIncrByAsync() throws InterruptedException {
        String key = "testHIncrByAsync";
        String field1 = "field";

        CACHE_TEMPLATE.hSet(key, field1, 5);

        CACHE_TEMPLATE.hIncrByAsync(key, field1, 1);
        Thread.sleep(200);

        CACHE_TEMPLATE.hIncrByAsync(key, field1, -1);
        Thread.sleep(200);

        CACHE_TEMPLATE.hIncrByAsync(key, field1, -10);
        Thread.sleep(200);

        final Optional<Object> optional = CACHE_TEMPLATE.hGet(key, field1);
        assertEquals(-5, Long.parseLong(optional.get().toString()));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h keys.
     */
    public void testHKeys() {
        String key = "testHKeys";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hSet(key, field1, "hello");
        CACHE_TEMPLATE.hSet(key, field2, "world");

        final Collection<Object> objects = CACHE_TEMPLATE.hKeys(key);
        assertTrue(objects.containsAll(Arrays.asList("field1", "field2")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h len.
     */
    public void testHLen() {
        String key = "testHLen";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hSet(key, field1, "hello");
        CACHE_TEMPLATE.hSet(key, field2, "world");

        final int hLen = CACHE_TEMPLATE.hLen(key);
        assertEquals(2, hLen);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test hm get.
     */
    public void testHmGet() {
        String key = "testHmGet";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hSet(key, field1, "hello");
        CACHE_TEMPLATE.hSet(key, field2, "world");

        final Map<Object, Object> map = CACHE_TEMPLATE.hmGet(key, new HashSet<Object>() {{
            add(field1);
            add(field2);
            add("nofield");
        }});
        assertEquals("hello", map.get(field1));
        assertEquals("world", map.get(field2));
        assertNull(map.get("nofield"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test hm set.
     */
    public void testHmSet() {
        String key = "testHmSet";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hmSet(key, new HashMap<String, Object>() {{
            put(field1, "Hello");
            put(field2, "World");
        }});

        assertEquals("Hello", CACHE_TEMPLATE.hGet(key, field1).get().toString());
        assertEquals("World", CACHE_TEMPLATE.hGet(key, field2).get().toString());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test hm set async.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testHmSetAsync() throws InterruptedException {
        String key = "testHmSetAsync";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hmSetAsync(key, new HashMap<String, Object>() {{
            put(field1, "Hello");
            put(field2, "World");
        }});

        Thread.sleep(1000);

        assertEquals("Hello", CACHE_TEMPLATE.hGet(key, field1).get().toString());
        assertEquals("World", CACHE_TEMPLATE.hGet(key, field2).get().toString());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h rand field.
     */
    public void testHRandField() {
        String key = "testHRandField";
        String field1 = "heads";
        String field2 = "tails";
        String field3 = "edge";

        CACHE_TEMPLATE.hmSetAsync(key, new HashMap<String, Object>() {{
            put(field1, "obverse");
            put(field2, "reverse");
            put(field3, "null");
        }});

        final Set<Object> objects = CACHE_TEMPLATE.hRandField(key, 1);
        assertTrue(Arrays.asList(field1, field2, field3).containsAll(objects));

        final Set<Object> objects1 = CACHE_TEMPLATE.hRandField(key, 1);
        assertTrue(Arrays.asList(field1, field2, field3).containsAll(objects1));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h rand field with values.
     */
    public void testHRandFieldWithValues() {
        String key = "testHRandFieldWithValues";
        String field1 = "heads";
        String field2 = "tails";
        String field3 = "edge";

        final HashMap<String, Object> setMap = new HashMap<String, Object>() {{
            put(field1, "obverse");
            put(field2, "reverse");
            put(field3, "null");
        }};
        CACHE_TEMPLATE.hmSetAsync(key, setMap);

        final Map<Object, Object> map = CACHE_TEMPLATE.hRandFieldWithValues(key, -5);
        assertTrue(setMap.keySet().containsAll(map.keySet()));
        assertTrue(setMap.values().containsAll(map.values()));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h scan.
     */
    public void testHScan() {
        String key = "myhscan";

        CACHE_TEMPLATE.hSet(key, "field1", "bar");
        CACHE_TEMPLATE.hSet(key, "field2", "bar");
        CACHE_TEMPLATE.hSet(key, "field3", "bar");
        CACHE_TEMPLATE.hSet(key, "field4", "bar");
        CACHE_TEMPLATE.hSet(key, "field5", "bar");
        CACHE_TEMPLATE.hSet(key, "field55", "bar");

        final Iterator<Map.Entry<Object, Object>> entryIterator = CACHE_TEMPLATE.hScan(key, "my*");
        assertFalse(entryIterator.hasNext());

        final Iterator<Map.Entry<Object, Object>> entryIterator1 = CACHE_TEMPLATE.hScan(key, "field*", 2);
        while (entryIterator1.hasNext()) {
            final Map.Entry<Object, Object> next = entryIterator1.next();
            final Object key1 = next.getKey();
            assertTrue(key1.toString().startsWith("field"));
        }

        final Iterator<Map.Entry<Object, Object>> entryIterator2 = CACHE_TEMPLATE.hScan(key, "field5*", 10);
        while (entryIterator2.hasNext()) {
            final Map.Entry<Object, Object> next = entryIterator2.next();
            final Object key1 = next.getKey();
            assertTrue(key1.toString().startsWith("field5"));
        }

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test h set nx.
     */
    public void testHSetNX() {
        String key = "testHSetNX";

        CACHE_TEMPLATE.hSetNX(key, "field", "Hello");
        CACHE_TEMPLATE.hSetNX(key, "field", "World");

        assertEquals("Hello", CACHE_TEMPLATE.hGet(key, "field").get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h set nx async.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testHSetNXAsync() throws InterruptedException {
        String key = "testHSetNXAsync";

        CACHE_TEMPLATE.hSetNXAsync(key, "field", "Hello");
        Thread.sleep(200);

        CACHE_TEMPLATE.hSetNXAsync(key, "field", "World");

        Thread.sleep(200);

        assertEquals("Hello", CACHE_TEMPLATE.hGet(key, "field").get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h str len.
     */
    public void testHStrLen() {
        String key = "testHStrLen";
        String filed1 = "f1";
        String filed2 = "f2";
        String filed3 = "f3";

        CACHE_TEMPLATE.hmSet(key, new HashMap<String, Object>() {{
            put(filed1, "HelloWorld");
            put(filed2, 99);
            put(filed3, -256);
        }});

        final int i = CACHE_TEMPLATE.hStrLen(key, filed1);
        assertEquals(i, 10);

        final int i1 = CACHE_TEMPLATE.hStrLen(key, filed2);
        assertEquals(i1, 2);

        final int i2 = CACHE_TEMPLATE.hStrLen(key, filed3);
        assertEquals(i2, 4);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h vals.
     */
    public void testHVals() {
        String key = "testHVals";

        CACHE_TEMPLATE.hSet(key, "field1", "Hello");
        CACHE_TEMPLATE.hSet(key, "field2", "World");

        final Collection<Object> objects = CACHE_TEMPLATE.hVALs(key);
        assertTrue(objects.containsAll(Arrays.asList("Hello", "World")));

        CACHE_TEMPLATE.del(key);

    }

    // list

    /**
     * Test bl move.
     */
    public void testBlMove() {
        String key = "testBlMove";
        String destKey = "testBlMovemyotherlist";

        CACHE_TEMPLATE.rPush(key, "one", "two", "three");

        final Optional<Object> optional = CACHE_TEMPLATE.blMove(key, destKey, Duration.ofSeconds(3), false);
        assertTrue(optional.isPresent());
        assertEquals("three", optional.get());

        final Optional<Object> optional1 = CACHE_TEMPLATE.blMove(key, destKey, Duration.ofSeconds(3), true);
        assertTrue(optional1.isPresent());
        assertEquals("one", optional1.get());

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("two", objects.get(0));

        final List<Object> objects1 = CACHE_TEMPLATE.lRange(destKey, 0, -1);
        assertEquals("three", objects1.get(0));
        assertEquals("one", objects1.get(1));

        CACHE_TEMPLATE.del(key, destKey);

    }

    /**
     * Test bl pop.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testBlPop() throws InterruptedException {
        String key = "testBlPop";

        final boolean b = CACHE_TEMPLATE.rPush(key, "one", "two", "three", "four", "five");
        assertTrue(b);

        final Optional<Object> optional = CACHE_TEMPLATE.blPop(key);
        assertTrue(optional.isPresent());
        assertEquals("one", optional.get());

        final Optional<Object> optional1 = CACHE_TEMPLATE.blPop(key, 10, TimeUnit.SECONDS);
        assertTrue(optional1.isPresent());
        assertEquals("two", optional1.get());

        final List<Object> objects = CACHE_TEMPLATE.blPop(key, 3);
        assertEquals("three", objects.get(0));
        assertEquals("four", objects.get(1));
        assertEquals("five", objects.get(2));

        final Optional<Object> optional2 = CACHE_TEMPLATE.blPop(key, 3, TimeUnit.SECONDS, "list3");
        assertFalse(optional2.isPresent());

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test br pop.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testBrPop() throws InterruptedException {
        String key = "testBrPop";

        final int i = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, i);

        final Optional<Object> optional = CACHE_TEMPLATE.brPop(key);
        assertTrue(optional.isPresent());
        assertEquals("one", optional.get());

        final Optional<Object> optional1 = CACHE_TEMPLATE.brPop(key, 10, TimeUnit.SECONDS);
        assertTrue(optional1.isPresent());
        assertEquals("two", optional1.get());

        final List<Object> objects = CACHE_TEMPLATE.brPop(key, 3);
        assertEquals("three", objects.get(0));
        assertEquals("four", objects.get(1));
        assertEquals("five", objects.get(2));

        final Optional<Object> optional2 = CACHE_TEMPLATE.brPop(key, 3, TimeUnit.SECONDS, "list3");
        assertFalse(optional2.isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test br pop l push.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testBrPopLPush() throws InterruptedException {
        String key = "testBrPopLPush";
        String key2 = "testBrPopLPushmyotherlist";

        final int push1 = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, push1);

        final Optional<Object> optional = CACHE_TEMPLATE.brPoplPush(key, key2, 3, TimeUnit.SECONDS);
        assertTrue(optional.isPresent());
        assertEquals("one", optional.get());

        CACHE_TEMPLATE.del(key, key2);

    }

    /**
     * Test l index.
     */
    public void testLIndex() {
        String key = "testLIndex";

        final int i = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, i);

        assertEquals("five", CACHE_TEMPLATE.lIndex(key, 0).get());
        assertEquals("three", CACHE_TEMPLATE.lIndex(key, 2).get());
        assertEquals("two", CACHE_TEMPLATE.lIndex(key, 3).get());
        assertFalse(CACHE_TEMPLATE.lIndex("nonkey", 0).isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test l insert.
     */
    public void testLInsert() {
        String key = "testLInsert";

        final boolean b = CACHE_TEMPLATE.rPush(key, "Hello", "World");
        assertTrue(b);

        final int i = CACHE_TEMPLATE.lInsert(key, true, "World", "There");
        assertEquals(3, i);

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("Hello", objects.get(0));
        assertEquals("There", objects.get(1));
        assertEquals("World", objects.get(2));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test l len.
     */
    public void testLLen() {
        String key = "testLLen";

        CACHE_TEMPLATE.lPush(key, "World", "Hello");
        assertEquals(2, CACHE_TEMPLATE.llen(key));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test l move.
     */
    public void testLMove() {
        String key = "testLMove";
        String destKey = "testLMovemyotherlist";

        CACHE_TEMPLATE.rPush(key, "one", "two", "three");

        final Optional<Object> optional = CACHE_TEMPLATE.lMove(key, destKey, false);
        assertTrue(optional.isPresent());
        assertEquals("three", optional.get());

        final Optional<Object> optional1 = CACHE_TEMPLATE.lMove(key, destKey, true);
        assertTrue(optional1.isPresent());
        assertEquals("one", optional1.get());

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("two", objects.get(0));

        final List<Object> objects1 = CACHE_TEMPLATE.lRange(destKey, 0, -1);
        assertEquals("three", objects1.get(0));
        assertEquals("one", objects1.get(1));

        CACHE_TEMPLATE.del(key, destKey);
    }


    /**
     * Test lm pop.
     *
     * @throws InterruptedException the interrupted exception
     */
//    public void testLmPop() throws InterruptedException {
//        final Optional<Map<String, List<Object>>> optional = CACHE_TEMPLATE.lmPop("non1", true, 10, "non2");
//        assertFalse(optional.isPresent());
//
//        String key = "testLmPop";
//        String key2 = "testLmPop2";
//
//        final int push = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
//        assertEquals(5, push);
//
//        final Optional<Map<String, List<Object>>> optional1 = CACHE_TEMPLATE.lmPop(key, true, 1);
//        assertEquals("five", optional1.get().get(key).get(0));
//
//        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
//        assertEquals("four", objects.get(0));
//        assertEquals("three", objects.get(1));
//        assertEquals("two", objects.get(2));
//        assertEquals("one", objects.get(3));
//
//        final Optional<Map<String, List<Object>>> optional2 = CACHE_TEMPLATE.lmPop(key, false, 10);
//        assertEquals("one", optional2.get().get(key).get(0));
//        assertEquals("two", optional2.get().get(key).get(1));
//        assertEquals("three", optional2.get().get(key).get(2));
//        assertEquals("four", optional2.get().get(key).get(3));
//
//        final List<Object> objects1 = CACHE_TEMPLATE.lRange(key, 0, -1);
//        assertTrue(objects1.isEmpty());
//
//        final int push1 = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
//        assertEquals(5, push1);
//
//        final int push2 = CACHE_TEMPLATE.lPush(key2, "a", "b", "c", "d", "e");
//        assertEquals(5, push2);
//
//        final Optional<Map<String, List<Object>>> optional3 = CACHE_TEMPLATE.lmPop(key, false, 3, key2);
//        assertEquals("one", optional3.get().get(key).get(0));
//        assertEquals("two", optional3.get().get(key).get(1));
//        assertEquals("three", optional3.get().get(key).get(2));
//
//        final List<Object> objects2 = CACHE_TEMPLATE.lRange(key, 0, -1);
//        assertEquals("five", objects2.get(0));
//        assertEquals("four", objects2.get(1));
//
//        final Optional<Map<String, List<Object>>> optional5 = CACHE_TEMPLATE.lmPop(key, false, 5, key2);
//        assertEquals("four", optional5.get().get(key).get(0));
//        assertEquals("five", optional5.get().get(key).get(1));
//
//        final Optional<Map<String, List<Object>>> optional6 = CACHE_TEMPLATE.lmPop(key, false, 10, key2);
//        assertEquals("a", optional6.get().get(key2).get(0));
//        assertEquals("b", optional6.get().get(key2).get(1));
//        assertEquals("c", optional6.get().get(key2).get(2));
//        assertEquals("d", optional6.get().get(key2).get(3));
//        assertEquals("e", optional6.get().get(key2).get(4));
//
//        final long exists = CACHE_TEMPLATE.exists(key, key2);
//        assertEquals(0, exists);
//
//        CACHE_TEMPLATE.del(key, key2);
//    }

    /**
     * Test l pop.
     */
    public void testLPop() {
        String key = "testLPop";

        final boolean b = CACHE_TEMPLATE.rPush(key, "one", "two", "three", "four", "five");
        assertTrue(b);

        final List<Object> objects1 = CACHE_TEMPLATE.lPop(key, 1);
        assertEquals("one", objects1.get(0));

        final List<Object> objects2 = CACHE_TEMPLATE.lPop(key, 1);
        assertEquals("two", objects2.get(0));

        final List<Object> objects = CACHE_TEMPLATE.blPop(key, 3);
        assertEquals("three", objects.get(0));
        assertEquals("four", objects.get(1));
        assertEquals("five", objects.get(2));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test l push x.
     */
    public void testLPushX() {
        String key = "testLPushX";
        String key2 = "testLPushXmyotherlist";

        final int i = CACHE_TEMPLATE.lPush(key, "World");
        assertEquals(1, i);

        final int i1 = CACHE_TEMPLATE.lPushX(key, "Hello");
        assertEquals(2, i1);

        final int i2 = CACHE_TEMPLATE.lPushX(key2, "Hello");
        assertEquals(0, i2);

        CACHE_TEMPLATE.del(key, key2);

    }

    /**
     * Test l rem.
     */
    public void testLRem() {
        String key = "testLRem";

        final boolean b = CACHE_TEMPLATE.rPush(key, "hello", "hello", "foo", "hello");
        assertTrue(b);

        final boolean b1 = CACHE_TEMPLATE.lRem(key, "hello");
        assertTrue(b1);

        final boolean b2 = CACHE_TEMPLATE.lRem(key, "hello");
        assertTrue(b2);

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("foo", objects.get(0));
        assertEquals("hello", objects.get(1));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test l set.
     */
    public void testLSet() {
        String key = "testLSet";

        final boolean b = CACHE_TEMPLATE.rPush(key, "one", "two", "three");
        assertTrue(b);

        CACHE_TEMPLATE.lSet(key, 0, "four");
        CACHE_TEMPLATE.lSet(key, -2, "five");

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("four", objects.get(0));
        assertEquals("five", objects.get(1));
        assertEquals("three", objects.get(2));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test l trim.
     */
    public void testLTrim() {
        String key = "testLTrim";

        final boolean b = CACHE_TEMPLATE.rPush(key, "one", "two", "three");
        assertTrue(b);

        CACHE_TEMPLATE.lTrim(key, 1, -1);

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("two", objects.get(0));
        assertEquals("three", objects.get(1));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test r pop.
     */
    public void testRPop() {
        String key = "testRPop";

        final int i = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, i);

        final List<Object> objects1 = CACHE_TEMPLATE.rPop(key, 1);
        assertEquals("one", objects1.get(0));

        final List<Object> objects2 = CACHE_TEMPLATE.rPop(key, 1);
        assertEquals("two", objects2.get(0));

        final List<Object> objects = CACHE_TEMPLATE.rPop(key, 3);
        assertEquals("three", objects.get(0));
        assertEquals("four", objects.get(1));
        assertEquals("five", objects.get(2));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test r pop l push.
     */
    public void testRPopLPush() {
        String key = "testRPopLPush";
        String key2 = "myotherlist";

        final int push1 = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, push1);

        final Optional<Object> optional = CACHE_TEMPLATE.rPoplPush(key, key2);
        assertTrue(optional.isPresent());
        assertEquals("one", optional.get());

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test r push x.
     */
    public void testRPushX() {
        String key = "testRPushX";
        String key2 = "testRPushXmyotherlist";

        final boolean b = CACHE_TEMPLATE.rPush(key, "World");
        assertTrue(b);

        final int i1 = CACHE_TEMPLATE.rPushX(key, "Hello");
        assertEquals(2, i1);

        final int i2 = CACHE_TEMPLATE.rPushX(key2, "Hello");
        assertEquals(0, i2);

        CACHE_TEMPLATE.del(key, key2);
    }


    // set

    /**
     * Test s add.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testSAdd() throws InterruptedException {
        String key = "testSAdd";

        final boolean sAdd = CACHE_TEMPLATE.sAdd(key, "hello");
        assertTrue(sAdd);

        final boolean b = CACHE_TEMPLATE.sAdd(key, "world");
        assertTrue(b);

        final boolean b1 = CACHE_TEMPLATE.sAdd(key, "world");
        assertFalse(b1);

        final boolean b2 = CACHE_TEMPLATE.sAdd(key, Arrays.asList("world", "one"));
        assertTrue(b2);

        CACHE_TEMPLATE.sAddAsync(key, "two");
        CACHE_TEMPLATE.sAddAsync(key, Arrays.asList("three", "four"));

        Thread.sleep(1000);

        final Set<Object> objects = CACHE_TEMPLATE.sMembers(key);
        assertTrue(objects.containsAll(Arrays.asList("hello", "world", "one", "two", "three", "four")));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test s card.
     */
    public void testSCard() {
        String key = "testSCard";

        final boolean b2 = CACHE_TEMPLATE.sAdd(key, Arrays.asList("world", "one"));
        assertTrue(b2);

        assertEquals(2, CACHE_TEMPLATE.sCard(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test sis member.
     */
    public void testSisMember() {
        String key = "testSisMember";

        final boolean b2 = CACHE_TEMPLATE.sAdd(key, Arrays.asList("world", "one"));
        assertTrue(b2);

        assertTrue(CACHE_TEMPLATE.sIsMember(key, "one"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test s diff.
     */
    public void testSDiff() {
        String key1 = "testSDiff1";
        String key2 = "testSDiff2";
        String key3 = "testSDiff3";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final Set<Object> objects = CACHE_TEMPLATE.sDiff(key1, key2, key3);
        assertTrue(objects.containsAll(Arrays.asList("b", "d")));

        CACHE_TEMPLATE.del(key1, key2, key3);

    }

    /**
     * Test s diff store.
     */
    public void testSDiffStore() {
        String key1 = "testSDiffStore1";
        String key2 = "testSDiffStore2";
        String key3 = "testSDiffStore3";
        String destKey = "destKey";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final int i = CACHE_TEMPLATE.sDiffStore(destKey, key1, key2, key3);
        assertEquals(2, i);

        CACHE_TEMPLATE.del(key1, key2, key3, destKey);
    }

    /**
     * Test s inter.
     */
    public void testSInter() {
        String key1 = "testSInter1";
        String key2 = "testSInter2";
        String key3 = "testSInter3";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final Set<Object> objects = CACHE_TEMPLATE.sInter(key1, key2, key3);
        assertTrue(objects.contains("c"));

        CACHE_TEMPLATE.del(key1, key2, key3);
    }

    /**
     * Test s inter store.
     */
    public void testSInterStore() {
        String key1 = "testSInterStore1";
        String key2 = "testSInterStore2";
        String key3 = "testSInterStore3";
        String destKey = "testSInterStoredestKey";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final int i = CACHE_TEMPLATE.sInterStore(destKey, key1, key2, key3);
        assertEquals(1, i);

        CACHE_TEMPLATE.del(key1, key2, key3, destKey);
    }

    /**
     * Test s move.
     */
    public void testSMove() {
        String key = "testSMove";
        String key2 = "testSMoveotherset";

        assertTrue(CACHE_TEMPLATE.sAdd(key, "one"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "two"));

        assertTrue(CACHE_TEMPLATE.sAdd(key2, "two"));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "three"));

        assertTrue(CACHE_TEMPLATE.sMove(key, key2, "one"));
        assertTrue(CACHE_TEMPLATE.sMove(key, key2, "two"));
        assertFalse(CACHE_TEMPLATE.sMove(key, key2, "four"));

        final Set<Object> objects = CACHE_TEMPLATE.sMembers(key);
        assertTrue(objects.isEmpty());

        final Set<Object> objects1 = CACHE_TEMPLATE.sMembers(key2);
        assertTrue(objects1.containsAll(Arrays.asList("one", "two", "three")));

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test s pop.
     */
    public void testSPop() {
        String key = "testSPop";

        assertTrue(CACHE_TEMPLATE.sAdd(key, "one"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "two"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "three"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "four"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "five"));

        assertTrue(CACHE_TEMPLATE.sPop(key).isPresent());
        assertTrue(CACHE_TEMPLATE.sPop(key).isPresent());
        assertEquals(3, CACHE_TEMPLATE.sPop(key, 3).size());

        assertFalse(CACHE_TEMPLATE.sPop(key).isPresent());
        assertEquals(0, CACHE_TEMPLATE.sPop(key, 3).size());

        assertEquals(0, CACHE_TEMPLATE.sCard(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test s rand member.
     */
    public void testSRandMember() {
        String key = "testSRandMember";
        final List<String> list = Arrays.asList("one", "two", "three", "four", "five");

        assertTrue(CACHE_TEMPLATE.sAdd(key, list));

        assertTrue(list.contains(CACHE_TEMPLATE.sRandMember(key).get()));
        assertTrue(list.contains(CACHE_TEMPLATE.sRandMember(key).get()));
        assertTrue(list.containsAll(CACHE_TEMPLATE.sRandMember(key, 4)));
        assertEquals(5, CACHE_TEMPLATE.sCard(key));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test s rem.
     */
    public void testSRem() {
        String key = "testSRem";
        final List<String> list = Arrays.asList("one", "two", "three");

        assertTrue(CACHE_TEMPLATE.sAdd(key, list));

        assertTrue(CACHE_TEMPLATE.sRem(key, Collections.singletonList("one")));
        assertTrue(CACHE_TEMPLATE.sRem(key, Arrays.asList("two", "four")));
        assertFalse(CACHE_TEMPLATE.sRem(key, Collections.singletonList("five")));

        final Set<Object> objects = CACHE_TEMPLATE.sMembers(key);
        assertTrue(objects.contains("three"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test s scan.
     */
    public void testSScan() {
        String key = "testSScan";
        final List<String> list = Arrays.asList("one", "two", "three", "four", "five");
        assertTrue(CACHE_TEMPLATE.sAdd(key, list));

        final Iterator<Object> sscan = CACHE_TEMPLATE.sScan(key);
        while (sscan.hasNext()) {
            final Object next = sscan.next();
            assertTrue(list.contains(next.toString()));
        }

        final Iterator<Object> sscan1 = CACHE_TEMPLATE.sScan(key, "t*");
        while (sscan1.hasNext()) {
            final Object next = sscan1.next();
            assertTrue(next.toString().startsWith("t"));
        }

        final Iterator<Object> sscan2 = CACHE_TEMPLATE.sScan(key, "*o*", 20);
        while (sscan2.hasNext()) {
            final Object next = sscan2.next();
            assertTrue(next.toString().contains("o"));
        }

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test s union.
     */
    public void testSUnion() {
        String key1 = "testSUnion1";
        String key2 = "testSUnion2";
        String key3 = "testSUnion3";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final Set<Object> objects = CACHE_TEMPLATE.sUnion(key1, key2, key3);
        assertTrue(objects.containsAll(Arrays.asList("a", "b", "c", "d", "e")));

        CACHE_TEMPLATE.del(key1, key2, key3);
    }

    /**
     * Test s union store.
     */
    public void testSUnionStore() {
        String key1 = "testSUnionStore1";
        String key2 = "testSUnionStore2";
        String key3 = "testSUnionStore3";
        String destKey = "testSUnionStoredestKey";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final int i = CACHE_TEMPLATE.sUnionStore(destKey, key1, key2, key3);
        assertEquals(5, i);

        CACHE_TEMPLATE.del(key1, key2, key3, destKey);
    }

    // sorted set

    /**
     * Test bzm pop.
     */
    public void testBzmPop() {
        final Optional<Object> optional = CACHE_TEMPLATE.bzmPop(3, TimeUnit.SECONDS, "notsuchkey", true);
        assertFalse(optional.isPresent());

        String key = "testBzmPop";
        String key2 = "testBzmPop2";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional2 = CACHE_TEMPLATE.bzmPop(3, TimeUnit.SECONDS, key, true);
        assertTrue(optional2.isPresent());

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        final List<String> list = new ArrayList<String>() {{
            add("one");
            add("two");
            add("three");
        }};
        list.remove(optional2.get().toString());
        assertTrue(objects.containsAll(list));

        final Collection<Object> objects1 = CACHE_TEMPLATE.bzmPop(key, false, 10);
        assertTrue(list.containsAll(objects1));

        assertTrue(CACHE_TEMPLATE.zAdd(key2, 4D, "four"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 5D, "five"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 6D, "six"));

        final List<String> list2 = new ArrayList<String>() {{
            add("four");
            add("five");
            add("six");
        }};

        final Optional<Object> objects2 = CACHE_TEMPLATE.bzmPop(3, TimeUnit.SECONDS, key, true, key2);
        assertTrue(list2.contains(objects2.get()));

        final Collection<Object> objects3 = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertTrue(objects3.isEmpty());

        final Optional<Object> objects4 = CACHE_TEMPLATE.bzmPop(3, TimeUnit.SECONDS, key, false, key2);
        assertTrue(list2.contains(objects4.get()));

        final Optional<Object> objects42 = CACHE_TEMPLATE.bzmPop(3, TimeUnit.SECONDS, key, false, key2);
        assertTrue(list2.contains(objects42.get()));

        final Collection<Object> objects5 = CACHE_TEMPLATE.zRange(key2, 0, -1);
        assertTrue(objects5.isEmpty());

        assertEquals(0, CACHE_TEMPLATE.exists(key, key2));

        CACHE_TEMPLATE.del(key, key2);

    }

    /**
     * Test bz pop max.
     */
    public void testBzPopMax() {
        String key = "testBzPopMax";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional = CACHE_TEMPLATE.bzPopMax(key, 3, TimeUnit.SECONDS);
        assertEquals("three", optional.get());

        final Collection<Object> objects = CACHE_TEMPLATE.bzPopMax(key, 3);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test bz pop min.
     */
    public void testBzPopMin() {
        String key = "testBzPopMin";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional = CACHE_TEMPLATE.bzPopMin(key, 3, TimeUnit.SECONDS);
        assertEquals("one", optional.get());

        final Collection<Object> objects = CACHE_TEMPLATE.bzPopMin(key, 3);
        assertTrue(objects.containsAll(Arrays.asList("three", "two")));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test z card.
     */
    public void testZCard() {
        String key = "testZCard";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd);

        assertEquals(2, CACHE_TEMPLATE.zCard(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z count.
     */
    public void testZCount() {
        String key = "testZCount";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zCount = CACHE_TEMPLATE.zCount(key, Double.MIN_VALUE, true, Double.MAX_VALUE, true);
        assertEquals(3, zCount);

        final int zCount2 = CACHE_TEMPLATE.zCount(key, 1, false, 3, true);
        assertEquals(2, zCount2);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z diff.
     */
    public void testZDiff() {
        String key = "testZDiff1";
        String key2 = "testZDiff2";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final Collection<Object> objects = CACHE_TEMPLATE.zDiff(key, key2);
        assertTrue(objects.contains("three"));

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test z diff store.
     */
    public void testZDiffStore() {
        String key = "zset1";
        String key2 = "zset2";
        String key3 = "out";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zDiffStore = CACHE_TEMPLATE.zDiffStore(key3, key, key2);
        assertEquals(1, zDiffStore);

        CACHE_TEMPLATE.del(key, key2, key3);
    }

    /**
     * Test z incr by.
     */
    public void testZIncrBy() {
        String key = "testZIncrBy";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd);

        final Double zIncrBy = CACHE_TEMPLATE.zIncrBy(key, 2, "one");
        assertEquals(3.0, zIncrBy);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z inter.
     */
    public void testZInter() {
        String key1 = "testZInter1";
        String key2 = "testZInter2";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Collection<Object> objects = CACHE_TEMPLATE.zInter(key1, key2);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        CACHE_TEMPLATE.del(key1, key2);

    }

    /**
     * Test z inter store.
     */
    public void testZInterStore() {
        String key1 = "testZInterStore1";
        String key2 = "testZInterStore2";
        String dest = "testZInterStoreout";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zInterStore(dest, new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }});
        assertEquals(2, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(5.0, doubles.get(0));
        assertEquals(10.0, doubles.get(1));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z inter store 2.
     */
    public void testZInterStore2() {
        String key1 = "zset1";
        String key2 = "zset2";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zInterStore(dest, key1, key2);
        assertEquals(2, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(2.0, doubles.get(0));
        assertEquals(4.0, doubles.get(1));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z inter store 3.
     */
    public void testZInterStore3() {
        String key1 = "zset1";
        String key2 = "zset2";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zInterStoreAggregate(dest, "min", key1, key2);
        assertEquals(2, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(1.0, doubles.get(0));
        assertEquals(2.0, doubles.get(1));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z inter store 4.
     */
    public void testZInterStore4() {
        String key1 = "zset1";
        String key2 = "zset2";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zInterStoreAggregate(dest, "max", key1, key2);
        assertEquals(2, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(4.0, doubles.get(0));
        assertEquals(5.0, doubles.get(1));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z inter store 5.
     */
    public void testZInterStore5() {
        String key1 = "zset1";
        String key2 = "zset2";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zInterStore(dest, "min", new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }});
        assertEquals(2, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(2.0, doubles.get(0));
        assertEquals(4.0, doubles.get(1));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z inter store 6.
     */
    public void testZInterStore6() {
        String key1 = "zset1";
        String key2 = "zset2";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zInterStore(dest, "max", new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }});
        assertEquals(2, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(12.0, doubles.get(0));
        assertEquals(15.0, doubles.get(1));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z lex count.
     */
    public void testZLexCount() {
        String key = "testZLexCount";

        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "a"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "b"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "c"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "d"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "e"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "f"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "g"));

        final int zLexCountHead = CACHE_TEMPLATE.zLexCountHead(key, "g", true);
        assertEquals(7, zLexCountHead);

        final int zLexCount1 = CACHE_TEMPLATE.zLexCount(key, "b", true, "f", true);
        assertEquals(5, zLexCount1);

        final int zLexCountTail = CACHE_TEMPLATE.zLexCountTail(key, "c", false);
        assertEquals(4, zLexCountTail);

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test zm pop.
     */
    public void testZmPop() {
        final Optional<Object> optional = CACHE_TEMPLATE.zmPop("notsuchkey", true);
        assertFalse(optional.isPresent());

        String key = "testZmPop";
        String key2 = "testZmPop2";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional2 = CACHE_TEMPLATE.zmPop(key, true);
        assertTrue(optional2.isPresent());

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        final List<String> list = new ArrayList<String>() {{
            add("one");
            add("two");
            add("three");
        }};
        list.remove(optional2.get().toString());
        assertTrue(objects.containsAll(list));

        assertTrue(CACHE_TEMPLATE.zAdd(key2, 4D, "four"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 5D, "five"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 6D, "six"));

        final List<String> list2 = new ArrayList<String>() {{
            add("four");
            add("five");
            add("six");
        }};

        CACHE_TEMPLATE.zmPop(key, true);
        CACHE_TEMPLATE.zmPop(key, true);

        final Optional<Object> objects2 = CACHE_TEMPLATE.zmPop(key, true, 10, TimeUnit.SECONDS, key2);
        assertTrue(list2.contains(objects2.get()));

        final Collection<Object> objects3 = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertTrue(objects3.isEmpty());

        final Optional<Object> objects4 = CACHE_TEMPLATE.zmPop(key, false, 10, TimeUnit.SECONDS, key2);
        assertTrue(objects4.isPresent());

        final Optional<Object> objects42 = CACHE_TEMPLATE.zmPop(key, false, 10, TimeUnit.SECONDS, key2);
        assertTrue(objects42.isPresent());

        final Collection<Object> objects5 = CACHE_TEMPLATE.zRange(key2, 0, -1);
        assertTrue(objects5.isEmpty());

        assertEquals(0, CACHE_TEMPLATE.exists(key, key2));

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test z pop max.
     */
    public void testZPopMax() {
        String key = "testZPopMax";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional = CACHE_TEMPLATE.zPopMax(key);
        assertEquals("three", optional.get());

        final Collection<Object> objects = CACHE_TEMPLATE.zPopMax(key, 3);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z pop min.
     */
    public void testZPopMin() {
        String key = "testZPopMin";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional = CACHE_TEMPLATE.zPopMin(key);
        assertEquals("one", optional.get());

        final Collection<Object> objects = CACHE_TEMPLATE.zPopMin(key, 3);
        assertTrue(objects.containsAll(Arrays.asList("three", "two")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rand member.
     */
    public void testZRandMember() {
        String key = "dadid";

        final List<String> list = Arrays.asList("uno", "due", "tre", "quattro", "cinque", "sei");

        assertTrue(CACHE_TEMPLATE.zAdd(key, 1D, "uno"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 2D, "due"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 3D, "tre"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 4D, "quattro"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 5D, "cinque"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 6D, "sei"));

        assertTrue(list.contains(CACHE_TEMPLATE.zRandMember(key).get()));
        assertTrue(list.contains(CACHE_TEMPLATE.zRandMember(key).get()));
        assertTrue(list.contains(CACHE_TEMPLATE.zRandMember(key).get()));

        final Collection<Object> objects = CACHE_TEMPLATE.zRandMember(key, 5);
        assertEquals(5, objects.size());
        assertTrue(list.containsAll(objects));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test z range.
     */
    public void testZRange() {
        String key = "testZRange";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 1.0, false, Double.MAX_VALUE, true);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("two", objects1.get(0));
        assertEquals("three", objects1.get(1));

        final Collection<Object> objects2 = CACHE_TEMPLATE.zRange(key, 1.0, false, Double.MAX_VALUE, true, 1, 1);
        assertEquals("three", objects2.iterator().next());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z range reversed.
     */
    public void testZRangeReversed() {
        String key = "testZRangeReversed";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Collection<Object> objects1 = CACHE_TEMPLATE.zRangeReversed(key, 0, -1);
        final ArrayList<Object> objects3 = new ArrayList<>(objects1);
        assertEquals("three", objects3.get(0));
        assertEquals("two", objects3.get(1));
        assertEquals("one", objects3.get(2));

        final Collection<Object> objects = CACHE_TEMPLATE.zRangeReversed(key, 1.0, false, Double.MAX_VALUE, true);
        final ArrayList<Object> objects4 = new ArrayList<>(objects);
        assertEquals("three", objects4.get(0));
        assertEquals("two", objects4.get(1));

        final Collection<Object> objects2 = CACHE_TEMPLATE.zRangeReversed(key, 1.0, false, Double.MAX_VALUE, true, 1, 1);
        assertEquals("two", objects2.iterator().next());

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test z rank.
     */
    public void testZRank() {
        String key = "testZRank";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Integer> three = CACHE_TEMPLATE.zRank(key, "three");
        assertEquals(2, three.get().intValue());

        final Optional<Integer> four = CACHE_TEMPLATE.zRank(key, "four");
        assertFalse(four.isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rem.
     */
    public void testZRem() {
        String key = "testZRem";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final boolean b = CACHE_TEMPLATE.zRem(key, Collections.singletonList("two"));
        assertTrue(b);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "three")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rem range by lex.
     */
    public void testZRemRangeByLex() {
        String key = "testZRemRangeByLex";

        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "aaaa"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "b"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "c"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "d"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "e"));

        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "foo"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "zap"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "zip"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "ALPHA"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "alpha"));

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("ALPHA", objects1.get(0));
        assertEquals("aaaa", objects1.get(1));
        assertEquals("alpha", objects1.get(2));
        assertEquals("b", objects1.get(3));
        assertEquals("c", objects1.get(4));
        assertEquals("d", objects1.get(5));
        assertEquals("e", objects1.get(6));
        assertEquals("foo", objects1.get(7));
        assertEquals("zap", objects1.get(8));
        assertEquals("zip", objects1.get(9));

        final Optional<Integer> optional = CACHE_TEMPLATE.zRemRangeByLex(key, "alpha", true, "omega", true);
        assertEquals(6, optional.get().intValue());

        final Collection<Object> objects2 = CACHE_TEMPLATE.zRange(key, 0, -1);
        final ArrayList<Object> objects3 = new ArrayList<>(objects2);
        assertEquals("ALPHA", objects3.get(0));
        assertEquals("aaaa", objects3.get(1));
        assertEquals("zap", objects3.get(2));
        assertEquals("zip", objects3.get(3));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rem range by rank.
     */
    public void testZRemRangeByRank() {
        String key = "testZRemRangeByRank";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Integer> optional = CACHE_TEMPLATE.zRemRangeByRank(key, 0, 1);
        assertEquals(2, optional.get().intValue());

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertTrue(objects.contains("three"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rem range by score.
     */
    public void testZRemRangeByScore() {
        String key = "testZRemRangeByScore";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Integer> optional = CACHE_TEMPLATE.zRemRangeByScore(key, Double.MIN_VALUE, true, 2, false);
        assertEquals(1, optional.get().intValue());

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("three", "two")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rev rank.
     */
    public void testZRevRank() {
        String key = "testZRevRank";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Integer> integer = CACHE_TEMPLATE.zRevRank(key, "one");
        assertEquals(2, integer.get().intValue());

        final Optional<Integer> integer2 = CACHE_TEMPLATE.zRevRank(key, "two");
        assertEquals(1, integer2.get().intValue());

        final Optional<Integer> integer3 = CACHE_TEMPLATE.zRevRank(key, "three");
        assertEquals(0, integer3.get().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z scan.
     */
    public void testZScan() {
        String key = "testZScan";

        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "aaaa"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "b"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "c"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "d"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "e"));

        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "foo"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "zap"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "zip"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "ALPHA"));
        assertTrue(CACHE_TEMPLATE.zAdd(key, 0, "alpha"));

        final Iterator<Object> objectIterator = CACHE_TEMPLATE.zScan(key, "a*");
        while (objectIterator.hasNext()) {
            final Object next = objectIterator.next();
            assertTrue(next.toString().startsWith("a"));
        }

        final Iterator<Object> objectIterator1 = CACHE_TEMPLATE.zScan(key, "*a", 20);
        while (objectIterator1.hasNext()) {
            final Object next = objectIterator1.next();
            assertTrue(next.toString().endsWith("a"));
        }

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z union.
     */
    public void testZUnion() {
        String key1 = "testZUnion";
        String key2 = "testZUnion2";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Collection<Object> objects = CACHE_TEMPLATE.zUnion(key1, key2);
        assertTrue(objects.containsAll(Arrays.asList("one", "two", "three")));

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test z union store.
     */
    public void testZUnionStore() {
        String key1 = "testZUnionStore1";
        String key2 = "testZUnionStore2";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zUnionStore(dest, new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }});
        assertEquals(3, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("one", objects1.get(0));
        assertEquals("three", objects1.get(1));
        assertEquals("two", objects1.get(2));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(5.0, doubles.get(0));
        assertEquals(9.0, doubles.get(1));
        assertEquals(10.0, doubles.get(2));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z union store 2.
     */
    public void testZUnionStore2() {
        String key1 = "testZUnionStore21";
        String key2 = "testZUnionStore22";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zUnionStore(dest, key1, key2);
        assertEquals(3, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("one", objects1.get(0));
        assertEquals("three", objects1.get(1));
        assertEquals("two", objects1.get(2));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(2.0, doubles.get(0));
        assertEquals(3.0, doubles.get(1));
        assertEquals(4.0, doubles.get(2));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z union store 3.
     */
    public void testZUnionStore3() {
        String key1 = "testZUnionStore31";
        String key2 = "testZUnionStore32";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zUnionStoreAggregate(dest, "min", key1, key2);
        assertEquals(3, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("one", objects1.get(0));
        assertEquals("two", objects1.get(1));
        assertEquals("three", objects1.get(2));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(1.0, doubles.get(0));
        assertEquals(2.0, doubles.get(1));
        assertEquals(3.0, doubles.get(2));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z union store 4.
     */
    public void testZUnionStore4() {
        String key1 = "testZUnionStore41";
        String key2 = "testZUnionStore42";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zUnionStoreAggregate(dest, "max", key1, key2);
        assertEquals(3, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("three", objects1.get(0));
        assertEquals("one", objects1.get(1));
        assertEquals("two", objects1.get(2));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(3.0, doubles.get(0));
        assertEquals(4.0, doubles.get(1));
        assertEquals(5.0, doubles.get(2));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z union store 5.
     */
    public void testZUnionStore5() {
        String key1 = "testZUnionStore51";
        String key2 = "testZUnionStore52";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zUnionStore(dest, "min", new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }});
        assertEquals(3, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("one", objects1.get(0));
        assertEquals("two", objects1.get(1));
        assertEquals("three", objects1.get(2));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(2.0, doubles.get(0));
        assertEquals(4.0, doubles.get(1));
        assertEquals(9.0, doubles.get(2));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }

    /**
     * Test z union store 6.
     */
    public void testZUnionStore6() {
        String key1 = "testZUnionStore61";
        String key2 = "testZUnionStore62";
        String dest = "out";

        final int zAdd2 = CACHE_TEMPLATE.zAdd(key1, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
        }});
        assertEquals(2, zAdd2);

        final int zAdd = CACHE_TEMPLATE.zAdd(key2, new HashMap<Object, Double>() {{
            put("one", 4D);
            put("two", 5D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final int zInterStore = CACHE_TEMPLATE.zUnionStore(dest, "max", new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }});
        assertEquals(3, zInterStore);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        final ArrayList<Object> objects1 = new ArrayList<>(objects);
        assertEquals("three", objects1.get(0));
        assertEquals("one", objects1.get(1));
        assertEquals("two", objects1.get(2));

        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(9.0, doubles.get(0));
        assertEquals(12.0, doubles.get(1));
        assertEquals(15.0, doubles.get(2));

        CACHE_TEMPLATE.del(key1, key2, dest);
    }


    // hyberloglog

    /**
     * Test pf add.
     */
    public void testPfAdd() {
        String key = "hll";

        final boolean b = CACHE_TEMPLATE.pfAdd(key, Arrays.asList("a", "b", "c", "d", "e", "f", "g"));
        assertTrue(b);
        assertEquals(7, CACHE_TEMPLATE.pfCount(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test pf count.
     */
    public void testPfCount() {
        String key = "hll";
        String key1 = "some-other-hll";

        final boolean b = CACHE_TEMPLATE.pfAdd(key, Arrays.asList("foo", "bar", "zap"));
        assertTrue(b);

        final boolean b1 = CACHE_TEMPLATE.pfAdd(key, Arrays.asList("zap", "zap", "zap"));
        assertFalse(b1);

        final boolean b2 = CACHE_TEMPLATE.pfAdd(key, Arrays.asList("foo", "bar"));
        assertFalse(b2);

        final boolean b3 = CACHE_TEMPLATE.pfAdd(key1, Arrays.asList(1, 2, 3));
        assertTrue(b3);

        assertEquals(6, CACHE_TEMPLATE.pfCount(key, key1));

        CACHE_TEMPLATE.del(key, key1);
    }

    /**
     * Test pf merge.
     */
    public void testPfMerge() {
        String key1 = "hll1";
        String key2 = "hll2";
        String key3 = "hll3";

        CACHE_TEMPLATE.pfAdd(key1, Arrays.asList("foo", "bar", "zap", "a"));
        CACHE_TEMPLATE.pfAdd(key2, Arrays.asList("a", "b", "c", "foo"));

        CACHE_TEMPLATE.pfMerge(key3, key1, key2);
        assertEquals(6, CACHE_TEMPLATE.pfCount(key3));

        CACHE_TEMPLATE.del(key1, key2, key3);
    }

    // string

    /**
     * Test append.
     */
    public void testAppend() {
        String key = "mykeyappend";

        CACHE_TEMPLATE.append(key, "Hello");
        CACHE_TEMPLATE.append(key, " World");

        assertEquals("Hello World", CACHE_TEMPLATE.get(key).orElse(null));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test decr.
     */
    public void testDecr() {
        String key = "mykeydesr";

        assertEquals(-1, CACHE_TEMPLATE.decr(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test decr by.
     */
    public void testDecrBy() {
        String key = "mykeydescbr";

        assertEquals(-10, CACHE_TEMPLATE.decrBy(key, 10));
        assertEquals(0, CACHE_TEMPLATE.decrBy(key, -10));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get del.
     */
    public void testGetDel() {
        String key = "mykey";

        CACHE_TEMPLATE.set(key, "hello");
        final Optional<Object> optional = CACHE_TEMPLATE.getDel(key);
        assertEquals("hello", optional.get());
        assertFalse(1 == CACHE_TEMPLATE.exists(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get ex.
     *
     * @throws InterruptedException the interrupted exception
     */
//    public void testGetEx() throws InterruptedException {
//        String key = "mykey";
//        int expire = 3;
//
//        CACHE_TEMPLATE.set(key, "hello");
//        final Optional<Object> optional = CACHE_TEMPLATE.getEx(key, Duration.ofSeconds(expire));
//        assertEquals("hello", optional.get());
//        assertEquals(1, CACHE_TEMPLATE.exists(key));
//
//        Thread.sleep(expire * 1000);
//        assertEquals(0, CACHE_TEMPLATE.exists(key));
//
//    }

    /**
     * Test get ex 2.
     *
     * @throws InterruptedException the interrupted exception
     */
//    public void testGetEx2() throws InterruptedException {
//        String key = "mykey";
//
//        CACHE_TEMPLATE.set(key, "hello");
//        final Optional<Object> optional = CACHE_TEMPLATE.getEx(key, Instant.now());
//        assertEquals("hello", optional.get());
//
//        Thread.sleep(1000);
//        assertEquals(0, CACHE_TEMPLATE.exists(key));
//
//    }

    /**
     * Test incr.
     */
    public void testIncr() {
        String key = "mykey";

        assertEquals(1, CACHE_TEMPLATE.incr(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr by.
     */
    public void testIncrBy() {
        String key = "mykey";

        assertEquals(10, CACHE_TEMPLATE.incrBy(key, 10));
        assertEquals(0, CACHE_TEMPLATE.incrBy(key, -10));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr by float.
     */
    public void testIncrByFloat() {
        String key = "mykeyFloat";

        assertEquals(10.01D, CACHE_TEMPLATE.incrByFloat(key, 10.01D));
        assertEquals(0.0, CACHE_TEMPLATE.incrByFloat(key, -10.01D));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test compare and set.
     */
    public void testCompareAndSet() {
        String key = "mykeyCompareAndSet";

        CACHE_TEMPLATE.incr(key);
        assertFalse(CACHE_TEMPLATE.compareAndSet(key, 0, 2));
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, 1, 3));
        assertEquals(3, CACHE_TEMPLATE.getLong(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.incrByFloat(key, 1);
        assertFalse(CACHE_TEMPLATE.compareAndSet(key, 0.0, 2.0));
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, 1.0, 3.0));
        assertEquals(3.0, CACHE_TEMPLATE.getDouble(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.set(key, "foo");
        assertFalse(CACHE_TEMPLATE.compareAndSet(key, "hello", "world"));
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, "foo", "bar"));
        assertEquals("bar", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test m get.
     */
    public void testMGet() {
        String key1 = "key1";
        String key2 = "key2";

        CACHE_TEMPLATE.set(key1, "Hello");
        CACHE_TEMPLATE.set(key2, "World");

        final Map<String, Object> map = CACHE_TEMPLATE.mGet(key1, key2, "nonexisting");
        assertEquals("Hello", map.get(key1));
        assertEquals("World", map.get(key2));
        assertNull(map.get("nonexisting"));

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test m set.
     */
    public void testMSet() {
        String key1 = "key1";
        String key2 = "key2";

        CACHE_TEMPLATE.mSet(new HashMap<String, String>() {{
            put(key1, "Hello");
            put(key2, "World");
        }});

        final Map<String, Object> map = CACHE_TEMPLATE.mGet(key1, key2, "nonexisting");
        assertEquals("Hello", map.get(key1));
        assertEquals("World", map.get(key2));
        assertNull(map.get("nonexisting"));

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test m set nx.
     */
    public void testMSetNx() {
        String key1 = "key1";
        String key2 = "key2";

        final boolean msetnx = CACHE_TEMPLATE.mSetNX(new HashMap<String, String>() {{
            put(key1, "Hello");
            put(key2, "World");
        }});
        assertTrue(msetnx);

        final boolean msetnx2 = CACHE_TEMPLATE.mSetNX(new HashMap<String, String>() {{
            put(key1, "Hello");
            put(key2, "World");
        }});
        assertFalse(msetnx2);


        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test set ex.
     */
    public void testSetEX() {
        String key1 = "key1";
        CACHE_TEMPLATE.set(key1, "hello world");
        assertEquals("hello world", CACHE_TEMPLATE.get(key1).get());

        CACHE_TEMPLATE.setEX(key1, "foo bar", Duration.ofSeconds(10));
        assertEquals("foo bar", CACHE_TEMPLATE.get(key1).get());

        CACHE_TEMPLATE.del(key1);
    }

    /**
     * Test set nx.
     */
//    public void testSetNX() {
//        String key1 = "key1";
//        assertTrue(CACHE_TEMPLATE.setNX(key1, "hello"));
//        assertFalse(CACHE_TEMPLATE.setNX(key1, "world"));
//        assertEquals("hello", CACHE_TEMPLATE.get(key1).get());
//
//        CACHE_TEMPLATE.del(key1);
//    }

    /**
     * Test set nxex.
     */
//    public void testSetNXEX() {
//        String key1 = "key1";
//        assertTrue(CACHE_TEMPLATE.setNxEx(key1, "hello", Duration.ofSeconds(10)));
//        assertFalse(CACHE_TEMPLATE.setNxEx(key1, "world", Duration.ofSeconds(10)));
//        assertEquals("hello", CACHE_TEMPLATE.get(key1).get());
//
//        CACHE_TEMPLATE.del(key1);
//    }

    /**
     * Test str len.
     */
    public void testStrLen() {
        String key = "mykey";
        CACHE_TEMPLATE.set(key, "Hello world");

        assertEquals(11, CACHE_TEMPLATE.strLen(key));
        assertEquals(0, CACHE_TEMPLATE.strLen("nonexisting"));

        CACHE_TEMPLATE.del(key);
    }

    // bloom filter

    /**
     * Test bf add.
     */
    public void testBfAdd() {
        String key = "bf";

        CACHE_TEMPLATE.bfReserve(key, 100, 0.01);

        assertTrue(CACHE_TEMPLATE.bfAdd(key, "item1"));
        assertFalse(CACHE_TEMPLATE.bfAdd(key, "item1"));

        CACHE_TEMPLATE.del(key, "{" + key + "}:config");
    }

    /**
     * Test bf card.
     */
    public void testBfCard() {
        String key = "bf";

        CACHE_TEMPLATE.bfReserve(key, 100, 0.01);

        assertTrue(CACHE_TEMPLATE.bfAdd(key, "item1"));

        assertEquals(1, CACHE_TEMPLATE.bfCard(key));

        CACHE_TEMPLATE.del(key, "{" + key + "}:config");
    }

    /**
     * Test bfm add.
     */
    public void testBfmAdd() {
        String key = "bf";

        CACHE_TEMPLATE.bfReserve(key, 100, 0.01);

        final Object bfmAdd = CACHE_TEMPLATE.bfmAdd(key, Arrays.asList("item1", "item2", "item2"));
        if (bfmAdd instanceof Boolean) {
            assertTrue((Boolean) bfmAdd);
        } else {
            assertEquals(2, bfmAdd);
        }

        CACHE_TEMPLATE.del(key, "{" + key + "}:config");
    }

    /**
     * Test bfm exists.
     */
    public void testBfmExists() {
        String key = "testBfmExists";

        final boolean bfReserve = CACHE_TEMPLATE.bfReserve(key, 100, 0.000001);
        assertTrue(bfReserve);

        assertTrue(CACHE_TEMPLATE.bfmAdd(key, "item1"));
        assertTrue(CACHE_TEMPLATE.bfmAdd(key, "item2"));

        final boolean b1 = CACHE_TEMPLATE.bfExists(key, "item1");
        assertTrue(b1);

        final boolean b2 = CACHE_TEMPLATE.bfExists(key, "item2");
        assertTrue(b2);

        final boolean b3 = CACHE_TEMPLATE.bfExists(key, "item3");
        assertFalse(b3);

        CACHE_TEMPLATE.del(key, "{" + key + "}:config");

    }

    /**
     * Test try lock.
     */
// lock
    public void testTryLock() {
        String key = "mylock";

        try {
            final boolean tryLock = CACHE_TEMPLATE.tryLock(key, 3, 5, TimeUnit.SECONDS);
            assertTrue(tryLock);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture.runAsync(() -> {
            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 3, 3, TimeUnit.SECONDS);
                assertFalse(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();

        CompletableFuture.runAsync(() -> {
            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 10, 1, TimeUnit.SECONDS);
                assertTrue(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();


        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try lock 2.
     */
    public void testTryLock2() {
        String key = "mylock";

        try {
            final boolean tryLock = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
            assertTrue(tryLock);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture.runAsync(() -> {
            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
                assertFalse(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();

        CACHE_TEMPLATE.unlock(key);

        CompletableFuture.runAsync(() -> {
            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 10, TimeUnit.SECONDS);
                assertTrue(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();


        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test unlock async.
     */
    public void testUnlockAsync() {
        String key = "mylock";

        final long id = Thread.currentThread().getId();

        try {
            final boolean tryLock = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
            assertTrue(tryLock);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // expect unlock fail
        CompletableFuture.runAsync(() -> {
            assertFalse(id == Thread.currentThread().getId());
            try {
                CACHE_TEMPLATE.unlockAsync(key);
            } catch (Exception e) {
            }
        }).join();

        CompletableFuture.runAsync(() -> {
            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
                assertFalse(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();

        CompletableFuture.runAsync(() -> {
            CACHE_TEMPLATE.unlockAsync(key, id);
            assertFalse(id == Thread.currentThread().getId());

            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
                assertTrue(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test force unlock.
     */
    public void testForceUnlock() {
        String key = "mylock";

        final long id = Thread.currentThread().getId();

        try {
            final boolean tryLock = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
            assertTrue(tryLock);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture.runAsync(() -> {
            assertFalse(id == Thread.currentThread().getId());
            try {
                final boolean forceUnlock = CACHE_TEMPLATE.forceUnlock(key);
                assertTrue(forceUnlock);
            } catch (Exception e) {
            }
        }).join();

        CompletableFuture.runAsync(() -> {
            try {
                final boolean b = CACHE_TEMPLATE.tryLock(key, 3, TimeUnit.SECONDS);
                assertTrue(b);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test execute script.
     *
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public void testExecuteScript() throws NoSuchAlgorithmException {
        String saddNxLua = "local added = {};" +
                "for i, v in ipairs(ARGV) do" +
                "    local addResult = redis.call('SADD', KEYS[1], ARGV[i]);" +
                "    if( addResult < 1 ) then" +
                "        for i, v in ipairs(added) do" +
                "            redis.call('SREM', KEYS[1], v);" +
                "        end;" +
                "        added = {};" +
                "        break;" +
                "    end;" +
                "    added[i] = ARGV[i];" +
                "end;" +
                "return #added;";

        final List<Object> keys = Collections.singletonList("myluakey");
        final Optional<Object> optional = CACHE_TEMPLATE.executeScript(saddNxLua, keys, "hello", "world");
        assertEquals(2, (long) optional.get());

        final Optional<Object> optional2 = CACHE_TEMPLATE.executeScript(saddNxLua, keys, "hello", "world");
        assertEquals(0, (long) optional2.get());

        CACHE_TEMPLATE.del(keys.toArray(new String[]{}));
    }

    /**
     * Test exists.
     */
    public void testExists() {
        String key = "myKey";
        CACHE_TEMPLATE.set(key, "hello world");

        assertEquals(1, CACHE_TEMPLATE.exists(key));
        assertFalse(1 == CACHE_TEMPLATE.exists("noExistKey"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test del.
     */
    public void testDel() {
        String key = "myKey";

        CACHE_TEMPLATE.set(key, "hello world");

        final long del = CACHE_TEMPLATE.del(key);
        assertEquals(1, del);
        assertFalse(1 == CACHE_TEMPLATE.del(key));
    }

    /**
     * Test ttl.
     */
    public void testTTL() {
        String key = "myKey";
        CACHE_TEMPLATE.set(key, "hello world");

        assertEquals(-1, CACHE_TEMPLATE.ttl(key));
        assertEquals(-2, CACHE_TEMPLATE.ttl("noExistKey"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test scan.
     */
    public void testScan() {
        String key = "myObj";
        CACHE_TEMPLATE.set(key, "hello world");

        String key2 = "myHash";
        CACHE_TEMPLATE.hSet(key2, "field", "hello world");

        String key3 = "myList";
        CACHE_TEMPLATE.lPush(key3, "hello world", "foo bar");

        String key4 = "mySet";
        CACHE_TEMPLATE.sAdd(key4, "hello world");

        String key5 = "mySortedSet";
        CACHE_TEMPLATE.zAdd(key5, 1.0, "hello world");

        final Iterable<String> scan = CACHE_TEMPLATE.scan("my*");
        for (String s : scan) {
            assertTrue(s.startsWith("my"));
        }

        final Iterable<String> scan1 = CACHE_TEMPLATE.scan("myS*", 4);
        for (String s : scan1) {
            assertTrue(s.startsWith("myS"));
        }

        CACHE_TEMPLATE.del(key, key2, key3, key4, key5);
    }

    /**
     * Test type.
     */
    public void testType() {
        String key = "myObj";
        CACHE_TEMPLATE.set(key, "hello world");
        assertSame(KeyType.STRING, CACHE_TEMPLATE.type(key));

        String key2 = "myHash";
        CACHE_TEMPLATE.hSet(key2, "field", "hello world");
        assertSame(KeyType.HASH, CACHE_TEMPLATE.type(key2));

        String key3 = "myList";
        CACHE_TEMPLATE.lPush(key3, "hello world", "foo bar");
        assertSame(KeyType.LIST, CACHE_TEMPLATE.type(key3));

        String key4 = "mySet";
        CACHE_TEMPLATE.sAdd(key4, "hello world");
        assertSame(KeyType.SET, CACHE_TEMPLATE.type(key4));

        String key5 = "mySortedSet";
        CACHE_TEMPLATE.zAdd(key5, 1.0, "hello world");
        assertSame(KeyType.ZSET, CACHE_TEMPLATE.type(key5));

        CACHE_TEMPLATE.del(key, key2, key3, key4, key5);
    }

    /**
     * Test try set rate limiter.
     */
// rateLimiter
    public void testTrySetRateLimiter() {
        String key = "myRateLimiter";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiter(key, 3, 10);
        assertTrue(b);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try acquire.
     */
    public void testTryAcquire() {
        String key = "myRateLimiter";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiter(key, 3, 10);
        assertTrue(b);

        for (int i = 0; i < 3; i++) {
            assertTrue(CACHE_TEMPLATE.tryAcquire(key));
        }

        assertFalse(CACHE_TEMPLATE.tryAcquire(key));

        CACHE_TEMPLATE.del(key, "{" + key + "}:permits", "{" + key + "}:value");
    }

    /**
     * Test try acquire 2.
     */
    public void testTryAcquire2() {
        String key = "myRateLimiter";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiter(key, 3, 10);
        assertTrue(b);

        assertTrue(CACHE_TEMPLATE.tryAcquire(key, 3));
        assertFalse(CACHE_TEMPLATE.tryAcquire(key, 3));

        CACHE_TEMPLATE.del(key, "{" + key + "}:permits", "{" + key + "}:value");
    }

}
