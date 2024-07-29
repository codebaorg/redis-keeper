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
import java.util.LinkedHashMap;
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
        String yourPass = "youPass";
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

        assertFalse(CACHE_TEMPLATE.setBit(key, 1, true));
        CACHE_TEMPLATE.setBit(key, 2, true);
        CACHE_TEMPLATE.setBit(key, 4, true);

        final long bitCount = CACHE_TEMPLATE.bitCount(key);
        assertTrue(CACHE_TEMPLATE.getBit(key, 1));
        assertTrue(CACHE_TEMPLATE.getBit(key, 2));
        assertTrue(CACHE_TEMPLATE.getBit(key, 4));
        assertEquals(3, bitCount);

        CACHE_TEMPLATE.del(key);

        final CompletableFuture<Boolean> f1 = CACHE_TEMPLATE.setBitAsync(key, 1, true);
        final CompletableFuture<Boolean> f2 = CACHE_TEMPLATE.setBitAsync(key, 2, true);
        final CompletableFuture<Boolean> f3 = CACHE_TEMPLATE.setBitAsync(key, 4, true);
        CompletableFuture.allOf(f1, f2, f3).join();

        assertTrue(CACHE_TEMPLATE.getBitAsync(key, 1).join());
        assertTrue(CACHE_TEMPLATE.getBitAsync(key, 2).join());
        assertTrue(CACHE_TEMPLATE.getBitAsync(key, 4).join());
        assertEquals(3, CACHE_TEMPLATE.bitCountAsync(key).join().intValue());

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

        final CompletableFuture<Long> f1 = CACHE_TEMPLATE.bitFieldSetSignedAsync(key, 8, 8, value);
        assertEquals(0, f1.join().intValue());

        final CompletableFuture<Long> f2 = CACHE_TEMPLATE.bitFieldGetSignedAsync(key, 8, 8);
        assertEquals(value, f2.join().intValue());

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

        CACHE_TEMPLATE.bitFieldSetUnSignedAsync(key, 8, 8, oldValue)
                .handle((v, e) ->
                        CACHE_TEMPLATE.bitFieldSetUnSignedAsync(key, 8, 8, 97)
                )
                .thenAccept(v -> assertEquals(oldValue, v.join().intValue()))
                .join();

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bit field get signed.
     */
    public void testBitFieldGetSigned() {
        String key = "testBitFieldGetSigned";
        CACHE_TEMPLATE.setObject(key, "hello");

        assertEquals(6, CACHE_TEMPLATE.bitFieldGetUnSigned(key, 4, 0));
        assertEquals(5, CACHE_TEMPLATE.bitFieldGetUnSigned(key, 3, 2));
        assertEquals(6, CACHE_TEMPLATE.bitFieldGetSigned(key, 4, 0));
        assertEquals(-3, CACHE_TEMPLATE.bitFieldGetSigned(key, 3, 2));

        assertEquals(6, CACHE_TEMPLATE.bitFieldGetUnSignedAsync(key, 4, 0).join().intValue());
        assertEquals(5, CACHE_TEMPLATE.bitFieldGetUnSignedAsync(key, 3, 2).join().intValue());
        assertEquals(6, CACHE_TEMPLATE.bitFieldGetSignedAsync(key, 4, 0).join().intValue());
        assertEquals(-3, CACHE_TEMPLATE.bitFieldGetSignedAsync(key, 3, 2).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bit op or.
     */
    public void testBitOpOr() {
        String key1 = "key1";
        CACHE_TEMPLATE.setObject(key1, "foobar");

        String key2 = "key2";
        CACHE_TEMPLATE.set(key2, "abcdef");

        CACHE_TEMPLATE.bitOpOr("dest", key1, key2);
        assertEquals("goofev", CACHE_TEMPLATE.get("dest").orElse(null));

        CACHE_TEMPLATE.bitOpOrAsync("destAsync", key1, key2).join();
        assertEquals("goofev", CACHE_TEMPLATE.get("destAsync").orElse(null));

        CACHE_TEMPLATE.del(key1, key2, "dest", "destAsync");
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

        assertFalse(CACHE_TEMPLATE.getBitAsync(key, 0).join());
        assertTrue(CACHE_TEMPLATE.getBitAsync(key, 7).join());
        assertFalse(CACHE_TEMPLATE.getBitAsync(key, 100).join());

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

        assertEquals(1, CACHE_TEMPLATE.geoAddAsync(key, 13.361389, 38.115556, "Palermo").join().intValue());
        assertEquals(1, CACHE_TEMPLATE.geoAddAsync(key, 15.087269, 37.502669, "Catania").join().intValue());

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

        assertFalse(CACHE_TEMPLATE.geoAddXXAsync(key, 13.361389, 38.115556, "Palermo").join());

        CACHE_TEMPLATE.geoAddAsync(key, 13.361389, 38.115556, "Palermo").join();
        assertTrue(CACHE_TEMPLATE.geoAddXXAsync(key, 13.361389, 38.115556, "Palermo").join());

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test geo dist.
     */
    public void testGeoDist() {
        String key = "testGeoDist";
        CACHE_TEMPLATE.geoAdd(key, 13.361389, 38.115556, "Palermo");
        CACHE_TEMPLATE.geoAdd(key, 15.087269, 37.502669, "Catania");

        assertEquals(166.2742, CACHE_TEMPLATE.geoDist(key, "Palermo", "Catania", "km"));
        assertEquals(103.3182, CACHE_TEMPLATE.geoDist(key, "Palermo", "Catania", "mi"));
        assertNull(CACHE_TEMPLATE.geoDist(key, "Foo", "Bar", "mi"));

        assertEquals(166.2742, CACHE_TEMPLATE.geoDistAsync(key, "Palermo", "Catania", "km").join());
        assertEquals(103.3182, CACHE_TEMPLATE.geoDistAsync(key, "Palermo", "Catania", "mi").join());
        assertNull(CACHE_TEMPLATE.geoDistAsync(key, "Foo", "Bar", "mi").join());

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

        final Map<Object, String> map2 = CACHE_TEMPLATE.geoHashAsync(key, "Palermo", "Catania").join();
        assertEquals("sqc8b49rny0", map2.get("Palermo"));
        assertEquals("sqdtr74hyu0", map2.get("Catania"));

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

        final Map<Object, double[]> map2 = CACHE_TEMPLATE.geoPosAsync(key, "Palermo", "Catania", "NonExisting").join();
        assertEquals(13.36138933897018433, map2.get("Palermo")[0]);
        assertEquals(38.11555639549629859, map2.get("Palermo")[1]);
        assertEquals(15.08726745843887329, map2.get("Catania")[0]);
        assertEquals(37.50266842333162032, map2.get("Catania")[1]);
        assertNull(map2.get("NonExisting"));

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

        final Map<Object, Double> map2 = CACHE_TEMPLATE.geoRadiusAsync(key, 15, 37, 200, "km").join();
        assertEquals(190.4424, map2.get("Palermo"));
        assertEquals(56.4413, map2.get("Catania"));

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
     * Test geo search async.
     */
    public void testGeoSearchAsync() {
        String key = "testGeoSearchAsync";
        CACHE_TEMPLATE.geoAddAsync(key, 13.361389, 38.115556, "Palermo").join();
        CACHE_TEMPLATE.geoAddAsync(key, 15.087269, 37.502669, "Catania").join();
        CACHE_TEMPLATE.geoAddAsync(key, 12.758489, 38.788135, "edge1").join();
        CACHE_TEMPLATE.geoAddAsync(key, 17.241510, 38.788135, "edge2").join();

        final List<Object> objects = CACHE_TEMPLATE.geoSearchAsync(key, 15, 37, 200, "km", "asc").join();
        assertTrue(objects.containsAll(Arrays.asList("Catania", "Palermo")));

        final List<Object> objects2 = CACHE_TEMPLATE.geoSearchAsync(key, 15, 37, 200, "km", 4, "desc").join();
        assertEquals("Catania", objects2.get(1));
        assertEquals("Palermo", objects2.get(0));

        final List<Object> objects3 = CACHE_TEMPLATE.geoSearchAsync(key, 15, 37, 400, 400, "km", "asc").join();
        assertEquals("Catania", objects3.get(0));
        assertEquals("Palermo", objects3.get(1));
        assertEquals("edge2", objects3.get(2));
        assertEquals("edge1", objects3.get(3));

        final List<Object> objects5 = CACHE_TEMPLATE.geoSearchAsync(key, 15, 37, 400, 400, "km", 10, "desc").join();
        assertEquals("edge1", objects5.get(0));
        assertEquals("edge2", objects5.get(1));
        assertEquals("Palermo", objects5.get(2));
        assertEquals("Catania", objects5.get(3));

        final List<Object> objects6 = CACHE_TEMPLATE.geoSearchAsync(key, "Catania", 200, "km", "asc").join();
        assertEquals("Catania", objects6.get(0));
        assertEquals("Palermo", objects6.get(1));

        final List<Object> objects8 = CACHE_TEMPLATE.geoSearchAsync(key, "Catania", 200, "km", 1, "asc").join();
        assertEquals("Catania", objects8.get(0));

        final List<Object> objects9 = CACHE_TEMPLATE.geoSearchAsync(key, "Catania", 400, 400, "km", "asc").join();
        assertEquals("Catania", objects9.get(0));
        assertEquals("Palermo", objects9.get(1));
        assertEquals("edge2", objects9.get(2));

        final List<Object> objects11 = CACHE_TEMPLATE.geoSearchAsync(key, "Catania", 400, 400, "km", 2, "desc").join();
        assertEquals("edge2", objects11.get(0));
        assertEquals("Palermo", objects11.get(1));

        final Map<Object, Double> map1 = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, 15, 37, 200, "km", "asc").join();
        assertEquals(56.4413, map1.get("Catania"));
        assertEquals(190.4424, map1.get("Palermo"));

        final Map<Object, Double> map2 = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, 15, 37, 200, "km", 2, "asc").join();
        assertEquals(56.4413, map2.get("Catania"));
        assertEquals(190.4424, map2.get("Palermo"));

        final Map<Object, Double> map3 = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, 15, 37, 400, 400, "km", "asc").join();
        assertEquals(56.4413, map3.get("Catania"));
        assertEquals(190.4424, map3.get("Palermo"));
        assertEquals(279.7403, map3.get("edge2"));
        assertEquals(279.7405, map3.get("edge1"));

        final Map<Object, Double> map = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, 15, 37, 400, 400, "km", 2, "desc").join();
        assertEquals(279.7405, map.get("edge1"));
        assertEquals(279.7403, map.get("edge2"));

        final Map<Object, Double> map4 = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, "Palermo", 200, "km", "asc").join();
        assertEquals(0.0000, map4.get("Palermo"));
        assertEquals(91.4007, map4.get("edge1"));
        assertEquals(166.2742, map4.get("Catania"));

        final Map<Object, Double> map5 = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, "Palermo", 200, "km", 1, "asc").join();
        assertEquals(0.0000, map5.get("Palermo"));


        final Map<Object, Double> map6 = CACHE_TEMPLATE.geoSearchWithDistanceAsync(key, "Palermo", 400, 400, "km", "asc").join();
        assertEquals(0.0000, map6.get("Palermo"));
        assertEquals(91.4007, map6.get("edge1"));
        assertEquals(166.2742, map6.get("Catania"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h del.
     * hash
     */
    public void testHDel() {
        String key = "testHDel";
        String field = "field1";

        CACHE_TEMPLATE.hSet(key, field, "foo");
        final Map<String, Boolean> map = CACHE_TEMPLATE.hDel(key, field);
        assertTrue(map.get(field));

        final Map<String, Boolean> map1 = CACHE_TEMPLATE.hDel(key, field);
        assertFalse(map1.get(field));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.hSetAsync(key, field, "foo").join();
        assertEquals(1, (long) CACHE_TEMPLATE.hDelAsync(key, field).join());
        assertEquals(0, (long) CACHE_TEMPLATE.hDelAsync(key, field).join());

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test h del async.
     */
    public void testHDelAsync() {
        String key = "testHDelAsync";
        String field = "field1";

        CACHE_TEMPLATE.hSet(key, field, "foo");

        CACHE_TEMPLATE.hDelAsync(key, field).join();
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

        final Map<String, CompletableFuture<Boolean>> futureMap = CACHE_TEMPLATE.hExistsAsync(key, field1, field2);
        assertTrue(futureMap.get(field1).join());
        assertFalse(futureMap.get(field2).join());

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
        assertEquals("foo", CACHE_TEMPLATE.hGetAsync(key, field1).join());

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

        final Map<Object, Object> map2 = CACHE_TEMPLATE.hGetAllAsync(key).join();
        assertEquals("hello", map2.get(field1));
        assertEquals("world", map2.get(field2));

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

        CACHE_TEMPLATE.hSetAsync(key, field1, 5).join();
        assertEquals(6, Long.parseLong(CACHE_TEMPLATE.hIncrByAsync(key, field1, 1).join().toString()));
        assertEquals(5, Long.parseLong(CACHE_TEMPLATE.hIncrByAsync(key, field1, -1).join().toString()));
        assertEquals(-5, Long.parseLong(CACHE_TEMPLATE.hIncrByAsync(key, field1, -10).join().toString()));

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

        CACHE_TEMPLATE.hIncrByAsync(key, field1, 1).join();
        CACHE_TEMPLATE.hIncrByAsync(key, field1, -1).join();
        CACHE_TEMPLATE.hIncrByAsync(key, field1, -10).join();

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

        assertTrue(CACHE_TEMPLATE.hKeysAsync(key).join().containsAll(Arrays.asList("field1", "field2")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test h len.
     */
    public void testHLen() {
        String key = "testHLen";
        String field1 = "field1";
        String field2 = "field2";

        CACHE_TEMPLATE.hmSetAsync(key, new HashMap<String, String>() {{
            put(field1, "hello");
            put(field2, "world");
        }}).join();

        assertEquals(2, CACHE_TEMPLATE.hLen(key));
        assertEquals(2, CACHE_TEMPLATE.hLenAsync(key).join().intValue());

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

        final Map<Object, Object> map2 = CACHE_TEMPLATE.hmGetAsync(key, new HashSet<Object>() {{
            add(field1);
            add(field2);
            add("nofield");
        }}).join();
        assertEquals("hello", map2.get(field1));
        assertEquals("world", map2.get(field2));
        assertNull(map2.get("nofield"));

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
        }}).join();

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
        }}).join();

        final List<String> fields = Arrays.asList(field1, field2, field3);
        final Set<Object> objects = CACHE_TEMPLATE.hRandField(key, 1);
        assertTrue(fields.containsAll(objects));

        final Set<Object> objects1 = CACHE_TEMPLATE.hRandField(key, 1);
        assertTrue(fields.containsAll(objects1));

        assertTrue(fields.containsAll(CACHE_TEMPLATE.hRandFieldsAsync(key, 1).join()));
        assertTrue(fields.containsAll(CACHE_TEMPLATE.hRandFieldsAsync(key, 1).join()));

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
        CACHE_TEMPLATE.hmSetAsync(key, setMap).join();

        final Map<Object, Object> map = CACHE_TEMPLATE.hRandFieldWithValues(key, -5);
        assertTrue(setMap.keySet().containsAll(map.keySet()));
        assertTrue(setMap.values().containsAll(map.values()));

        final Map<Object, Object> map2 = CACHE_TEMPLATE.hRandFieldWithValuesAsync(key, -5).join();
        assertTrue(setMap.keySet().containsAll(map2.keySet()));
        assertTrue(setMap.values().containsAll(map2.values()));

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
     */
    public void testHSetNXAsync() {
        String key = "testHSetNXAsync";

        CACHE_TEMPLATE.hSetNXAsync(key, "field", "Hello").join();
        CACHE_TEMPLATE.hSetNXAsync(key, "field", "World").join();

        assertEquals("Hello", CACHE_TEMPLATE.hGetAsync(key, "field").join());

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

        assertEquals(CACHE_TEMPLATE.hStrLenAsync(key, filed1).join().intValue(), 10);
        assertEquals(CACHE_TEMPLATE.hStrLenAsync(key, filed2).join().intValue(), 2);
        assertEquals(CACHE_TEMPLATE.hStrLenAsync(key, filed3).join().intValue(), 4);

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

        final Collection<Object> objects2 = CACHE_TEMPLATE.hVALsAsync(key).join();
        assertTrue(objects2.containsAll(Arrays.asList("Hello", "World")));

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
     * Test bl move async.
     */
    public void testBlMoveAsync() {
        String key = "testBlMoveAsync";
        String destKey = "testBlMoveAsync-my-other-list";

        CACHE_TEMPLATE.rPushAsync(key, "one", "two", "three").join();

        final Object optional = CACHE_TEMPLATE.blMoveAsync(key, destKey, Duration.ofSeconds(3), false).join();
        assertEquals("three", optional);

        final Object optional1 = CACHE_TEMPLATE.blMoveAsync(key, destKey, Duration.ofSeconds(3), true).join();
        assertEquals("one", optional1);

        final List<Object> objects = CACHE_TEMPLATE.lRangeAsync(key, 0, -1).join();
        assertEquals("two", objects.get(0));

        final List<Object> objects1 = CACHE_TEMPLATE.lRangeAsync(destKey, 0, -1).join();
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
     * Test bl pop async.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testBlPopAsync() throws InterruptedException {
        String key = "testBlPopAsync";

        final boolean b = CACHE_TEMPLATE.rPushAsync(key, "one", "two", "three", "four", "five").join();
        assertTrue(b);

        final Object optional = CACHE_TEMPLATE.blPopAsync(key).join();
        assertEquals("one", optional);

        final Object optional1 = CACHE_TEMPLATE.blPopAsync(key, 10, TimeUnit.SECONDS).join();
        assertEquals("two", optional1);

        final List<Object> objects = CACHE_TEMPLATE.blPopAsync(key, 3).join();
        assertEquals("three", objects.get(0));
        assertEquals("four", objects.get(1));
        assertEquals("five", objects.get(2));

        final Object optional2 = CACHE_TEMPLATE.blPopAsync(key, 3, TimeUnit.SECONDS, "list3").join();
        assertNull(optional2);

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
     * Test br pop async.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testBrPopAsync() throws InterruptedException {
        String key = "testBrPopAsync";

        CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");

        final Object optional = CACHE_TEMPLATE.brPopAsync(key).join();
        assertEquals("one", optional);

        final Object optional1 = CACHE_TEMPLATE.brPopAsync(key, 10, TimeUnit.SECONDS).join();
        assertEquals("two", optional1);

        final List<Object> objects = CACHE_TEMPLATE.brPopAsync(key, 3).join();
        assertEquals("three", objects.get(0));
        assertEquals("four", objects.get(1));
        assertEquals("five", objects.get(2));

        final Object optional2 = CACHE_TEMPLATE.brPopAsync(key, 3, TimeUnit.SECONDS, "list3").join();
        assertNull(optional2);

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

        final Optional<Object> optional = CACHE_TEMPLATE.brPopLPush(key, key2, 3, TimeUnit.SECONDS);
        assertTrue(optional.isPresent());
        assertEquals("one", optional.get());

        CACHE_TEMPLATE.del(key, key2);

    }

    /**
     * Test br pop l push async.
     */
    public void testBrPopLPushAsync() {
        String key = "testBrPopLPushAsync";
        String key2 = "testBrPopLPushAsync-my-other-list";

        final int push1 = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, push1);

        final Object optional = CACHE_TEMPLATE.brPopLPushAsync(key, key2, 3, TimeUnit.SECONDS).join();
        assertEquals("one", optional);

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

        assertEquals("five", CACHE_TEMPLATE.lIndexAsync(key, 0).join());
        assertEquals("three", CACHE_TEMPLATE.lIndexAsync(key, 2).join());
        assertEquals("two", CACHE_TEMPLATE.lIndexAsync(key, 3).join());
        assertNull(CACHE_TEMPLATE.lIndexAsync("nonkey", 0).join());

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

        final Boolean there = CACHE_TEMPLATE.lRemAsync(key, "There").join();
        assertTrue(there);

        assertEquals(3, CACHE_TEMPLATE.lInsertAsync(key, true, "World", "There").join().intValue());

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
        assertEquals(2, CACHE_TEMPLATE.llenAsync(key).join().intValue());

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test l move.
     */
    public void testLMove() {
        String key = "testLMove";
        String destKey = "testLMove-my-other-list";

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
     * Test l move async.
     */
    public void testLMoveAsync() {
        String key = "testLMoveAsync";
        String destKey = "testLMoveAsync-my-other-list";

        CACHE_TEMPLATE.rPush(key, "one", "two", "three");

        final Object optional = CACHE_TEMPLATE.lMoveAsync(key, destKey, false).join();
        assertEquals("three", optional);

        final Object optional1 = CACHE_TEMPLATE.lMoveAsync(key, destKey, true).join();
        assertEquals("one", optional1);

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
     * Test l pop async.
     */
    public void testLPopAsync() {
        String key = "testLPopAsync";

        final boolean b = CACHE_TEMPLATE.rPush(key, "one", "two", "three", "four", "five");
        assertTrue(b);

        final List<Object> objects1 = CACHE_TEMPLATE.lPopAsync(key, 1).join();
        assertEquals("one", objects1.get(0));

        final List<Object> objects2 = CACHE_TEMPLATE.lPopAsync(key, 1).join();
        assertEquals("two", objects2.get(0));

        final List<Object> objects = CACHE_TEMPLATE.blPopAsync(key, 3).join();
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
        String key2 = "testLPushX-my-other-list";

        final int i = CACHE_TEMPLATE.lPush(key, "World");
        assertEquals(1, i);

        final int i1 = CACHE_TEMPLATE.lPushX(key, "Hello");
        assertEquals(2, i1);

        final int i2 = CACHE_TEMPLATE.lPushX(key2, "Hello");
        assertEquals(0, i2);

        CACHE_TEMPLATE.del(key, key2);

    }

    /**
     * Test l push x async.
     */
    public void testLPushXAsync() {
        String key = "testLPushXAsync";
        String key2 = "testLPushXAsync-my-other-list";

        CACHE_TEMPLATE.lPush(key, "World");

        final int i1 = CACHE_TEMPLATE.lPushXAsync(key, "Hello").join();
        assertEquals(2, i1);

        final int i2 = CACHE_TEMPLATE.lPushXAsync(key2, "Hello").join();
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

        CACHE_TEMPLATE.rPush(key, "hello", "hello", "foo", "hello");
        assertEquals("hello", CACHE_TEMPLATE.lRem(key, 0).get().toString());

        CACHE_TEMPLATE.del(key);

    }

    public void testLRemAll() {
        String key = "testLRemAll";

        final boolean b = CACHE_TEMPLATE.rPush(key, "hello", "hello", "foo", "hello");
        assertTrue(b);

        CACHE_TEMPLATE.lRemAll(key, "hello");

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("foo", objects.get(0));

        CACHE_TEMPLATE.del(key);

    }

    /**
     * Test l rem async.
     */
    public void testLRemAsync() {
        String key = "testLRemAsync";

        final boolean b = CACHE_TEMPLATE.rPush(key, "hello", "hello", "foo", "hello");
        assertTrue(b);

        final boolean b1 = CACHE_TEMPLATE.lRemAsync(key, "hello").join();
        assertTrue(b1);

        final boolean b2 = CACHE_TEMPLATE.lRemAsync(key, "hello").join();
        assertTrue(b2);

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("foo", objects.get(0));
        assertEquals("hello", objects.get(1));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.rPush(key, "hello", "hello", "foo", "hello");
        assertEquals("hello", CACHE_TEMPLATE.lRemAsync(key, 0).join().toString());

        CACHE_TEMPLATE.del(key);

    }

    public void testLRemAllAsync() {
        String key = "testLRemAllAsync";

        final boolean b = CACHE_TEMPLATE.rPush(key, "hello", "hello", "foo", "hello");
        assertTrue(b);

        CACHE_TEMPLATE.lRemAllAsync(key, "hello").join();

        final List<Object> objects = CACHE_TEMPLATE.lRange(key, 0, -1);
        assertEquals("foo", objects.get(0));

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
     * Test l set async.
     */
    public void testLSetAsync() {
        String key = "testLSetAsync";

        final boolean b = CACHE_TEMPLATE.rPush(key, "one", "two", "three");
        assertTrue(b);

        CACHE_TEMPLATE.lSetAsync(key, 0, "four").join();
        CACHE_TEMPLATE.lSetAsync(key, -2, "five").join();

        final List<Object> objects = CACHE_TEMPLATE.lRangeAsync(key, 0, -1).join();
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

        CACHE_TEMPLATE.lTrimAsync(key, 1, -1).join();
        assertEquals("three", CACHE_TEMPLATE.lRange(key, 0, -1).get(0));

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
     * Test r pop async.
     */
    public void testRPopAsync() {
        String key = "testRPopAsync";

        final int i = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, i);

        final List<Object> objects1 = CACHE_TEMPLATE.rPopAsync(key, 1).join();
        assertEquals("one", objects1.get(0));

        final List<Object> objects2 = CACHE_TEMPLATE.rPopAsync(key, 1).join();
        assertEquals("two", objects2.get(0));

        final List<Object> objects = CACHE_TEMPLATE.rPopAsync(key, 3).join();
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
        String key2 = "testRPopLPush-my-other-list";

        final int push1 = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, push1);

        final Optional<Object> optional = CACHE_TEMPLATE.rPopLPush(key, key2);
        assertTrue(optional.isPresent());
        assertEquals("one", optional.get());

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test r pop l push async.
     */
    public void testRPopLPushAsync() {
        String key = "testRPopLPushAsync";
        String key2 = "testRPopLPushAsync-my-other-list";

        final int push1 = CACHE_TEMPLATE.lPush(key, "one", "two", "three", "four", "five");
        assertEquals(5, push1);

        final Object optional = CACHE_TEMPLATE.rPopLPushAsync(key, key2).join();
        assertEquals("one", optional);

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test r push x.
     */
    public void testRPushX() {
        String key = "testRPushX";
        String key2 = "testRPushX-my-other-list";

        final boolean b = CACHE_TEMPLATE.rPush(key, "World");
        assertTrue(b);

        final int i1 = CACHE_TEMPLATE.rPushX(key, "Hello");
        assertEquals(2, i1);

        final int i2 = CACHE_TEMPLATE.rPushX(key2, "Hello");
        assertEquals(0, i2);

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test r push x async.
     */
    public void testRPushXAsync() {
        String key = "testRPushXAsync";
        String key2 = "testRPushXAsync-my-other-list";

        final boolean b = CACHE_TEMPLATE.rPush(key, "World");
        assertTrue(b);

        final int i1 = CACHE_TEMPLATE.rPushXAsync(key, "Hello").join();
        assertEquals(2, i1);

        final int i2 = CACHE_TEMPLATE.rPushXAsync(key2, "Hello").join();
        assertEquals(0, i2);

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test s add.
     * set
     */
    public void testSAdd() {
        String key = "testSAdd";

        final boolean sAdd = CACHE_TEMPLATE.sAdd(key, "hello");
        assertTrue(sAdd);

        final boolean b = CACHE_TEMPLATE.sAdd(key, "world");
        assertTrue(b);

        final boolean b1 = CACHE_TEMPLATE.sAdd(key, "world");
        assertFalse(b1);

        final boolean b2 = CACHE_TEMPLATE.sAdd(key, Arrays.asList("world", "one"));
        assertTrue(b2);

        CACHE_TEMPLATE.sAddAsync(key, "two").join();
        CACHE_TEMPLATE.sAddAsync(key, Arrays.asList("three", "four")).join();

        final List<String> values = Arrays.asList("hello", "world", "one", "two", "three", "four");

        final Set<Object> objects = CACHE_TEMPLATE.sMembers(key);
        assertTrue(objects.containsAll(values));

        assertTrue(CACHE_TEMPLATE.sMembersAsync(key).join().containsAll(values));

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
        assertEquals(2, CACHE_TEMPLATE.sCardAsync(key).join().intValue());

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
        assertTrue(CACHE_TEMPLATE.sIsMemberAsync(key, "one").join());

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

        assertTrue(CACHE_TEMPLATE.sDiffAsync(key1, key2, key3).join().containsAll(Arrays.asList("b", "d")));

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

        CACHE_TEMPLATE.del(destKey);
        assertEquals(2, CACHE_TEMPLATE.sDiffStoreAsync(destKey, key1, key2, key3).join().intValue());

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
        assertTrue(CACHE_TEMPLATE.sInterAsync(key1, key2, key3).join().contains("c"));

        CACHE_TEMPLATE.del(key1, key2, key3);
    }

    /**
     * Test s inter store.
     */
    public void testSInterStore() {
        String key1 = "testSInterStore1";
        String key2 = "testSInterStore2";
        String key3 = "testSInterStore3";
        String destKey = "testSInterStore-destKey";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final int i = CACHE_TEMPLATE.sInterStore(destKey, key1, key2, key3);
        assertEquals(1, i);

        CACHE_TEMPLATE.del(destKey);
        assertEquals(1, CACHE_TEMPLATE.sInterStoreAsync(destKey, key1, key2, key3).join().intValue());

        CACHE_TEMPLATE.del(key1, key2, key3, destKey);
    }

    /**
     * Test s move.
     */
    public void testSMove() {
        String key = "testSMove";
        String key2 = "testSMove-other-set";

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
     * Test s move async.
     */
    public void testSMoveAsync() {
        String key = "testSMoveAsync";
        String key2 = "testSMoveAsync-other-set";

        assertTrue(CACHE_TEMPLATE.sAdd(key, "one"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "two"));

        assertTrue(CACHE_TEMPLATE.sAdd(key2, "two"));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "three"));

        assertTrue(CACHE_TEMPLATE.sMoveAsync(key, key2, "one").join());
        assertTrue(CACHE_TEMPLATE.sMoveAsync(key, key2, "two").join());
        assertFalse(CACHE_TEMPLATE.sMoveAsync(key, key2, "four").join());

        final Set<Object> objects = CACHE_TEMPLATE.sMembersAsync(key).join();
        assertTrue(objects.isEmpty());

        final Set<Object> objects1 = CACHE_TEMPLATE.sMembersAsync(key2).join();
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
        assertNotNull(CACHE_TEMPLATE.sPopAsync(key).join());
        assertEquals(3, CACHE_TEMPLATE.sPop(key, 3).size());

        assertFalse(CACHE_TEMPLATE.sPop(key).isPresent());
        assertEquals(0, CACHE_TEMPLATE.sPop(key, 3).size());

        assertEquals(0, CACHE_TEMPLATE.sCard(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test s pop async.
     */
    public void testSPopAsync() {
        String key = "testSPopAsync";

        assertTrue(CACHE_TEMPLATE.sAdd(key, "one"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "two"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "three"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "four"));
        assertTrue(CACHE_TEMPLATE.sAdd(key, "five"));

        assertTrue(CACHE_TEMPLATE.sPop(key).isPresent());
        assertTrue(CACHE_TEMPLATE.sPop(key).isPresent());
        assertEquals(3, CACHE_TEMPLATE.sPopAsync(key, 3).join().size());

        assertFalse(CACHE_TEMPLATE.sPop(key).isPresent());
        assertEquals(0, CACHE_TEMPLATE.sPopAsync(key, 3).join().size());

        assertEquals(0, CACHE_TEMPLATE.sCardAsync(key).join().intValue());

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
     * Test s rand member async.
     */
    public void testSRandMemberAsync() {
        String key = "testSRandMemberAsync";
        final List<String> list = Arrays.asList("one", "two", "three", "four", "five");

        assertTrue(CACHE_TEMPLATE.sAdd(key, list));

        assertTrue(list.contains(CACHE_TEMPLATE.sRandMemberAsync(key).join()));
        assertTrue(list.contains(CACHE_TEMPLATE.sRandMemberAsync(key).join()));
        assertTrue(list.containsAll(CACHE_TEMPLATE.sRandMemberAsync(key, 4).join()));
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
     * Test s rem async.
     */
    public void testSRemAsync() {
        String key = "testSRemAsync";
        final List<String> list = Arrays.asList("one", "two", "three");

        assertTrue(CACHE_TEMPLATE.sAdd(key, list));

        assertTrue(CACHE_TEMPLATE.sRemAsync(key, Collections.singletonList("one")).join());
        assertTrue(CACHE_TEMPLATE.sRemAsync(key, Arrays.asList("two", "four")).join());
        assertFalse(CACHE_TEMPLATE.sRemAsync(key, Collections.singletonList("five")).join());

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

        assertTrue(CACHE_TEMPLATE.sUnionAsync(key1, key2, key3).join().containsAll(Arrays.asList("a", "b", "c", "d", "e")));

        CACHE_TEMPLATE.del(key1, key2, key3);
    }

    /**
     * Test s union store.
     */
    public void testSUnionStore() {
        String key1 = "testSUnionStore1";
        String key2 = "testSUnionStore2";
        String key3 = "testSUnionStore3";
        String destKey = "testSUnionStore-destKey";

        assertTrue(CACHE_TEMPLATE.sAdd(key1, Arrays.asList("a", "b", "c", "d")));
        assertTrue(CACHE_TEMPLATE.sAdd(key2, "c"));
        assertTrue(CACHE_TEMPLATE.sAdd(key3, Arrays.asList("a", "c", "e")));

        final int i = CACHE_TEMPLATE.sUnionStore(destKey, key1, key2, key3);
        assertEquals(5, i);

        CACHE_TEMPLATE.del(destKey);
        assertEquals(5, CACHE_TEMPLATE.sUnionStoreAsync(destKey, key1, key2, key3).join().intValue());

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

        assertTrue(CACHE_TEMPLATE.zAddAsync(key2, 4D, "four").join());
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
     * Test bzm pop async.
     */
    public void testBzmPopAsync() {
        final Object optional = CACHE_TEMPLATE.bzmPopAsync(3, TimeUnit.SECONDS, "notsuchkey", true).join();
        assertNull(optional);

        String key = "testBzmPopAsync";
        String key2 = "testBzmPopAsync2";

        final int zAdd = CACHE_TEMPLATE.zAddAsync(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }}).join();
        assertEquals(3, zAdd);

        final Object optional2 = CACHE_TEMPLATE.bzmPopAsync(3, TimeUnit.SECONDS, key, true).join();
        assertEquals("one", optional2.toString());

        final Collection<Object> objects = CACHE_TEMPLATE.zRangeAsync(key, 0, -1).join();
        final List<String> list = new ArrayList<String>() {{
            add("one");
            add("two");
            add("three");
        }};
        list.remove(optional2.toString());
        assertTrue(objects.containsAll(list));

        final Collection<Object> objects1 = CACHE_TEMPLATE.bzmPopAsync(key, false, 10).join();
        assertTrue(list.containsAll(objects1));

        assertTrue(CACHE_TEMPLATE.zAdd(key2, 4D, "four"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 5D, "five"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 6D, "six"));

        final List<String> list2 = new ArrayList<String>() {{
            add("four");
            add("five");
            add("six");
        }};

        final Object objects2 = CACHE_TEMPLATE.bzmPopAsync(3, TimeUnit.SECONDS, key, true, key2).join();
        assertTrue(list2.contains(objects2));

        final Collection<Object> objects3 = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertTrue(objects3.isEmpty());

        final Object objects4 = CACHE_TEMPLATE.bzmPopAsync(3, TimeUnit.SECONDS, key, false, key2).join();
        assertTrue(list2.contains(objects4));

        final Object objects42 = CACHE_TEMPLATE.bzmPopAsync(3, TimeUnit.SECONDS, key, false, key2).join();
        assertTrue(list2.contains(objects42));

        final Collection<Object> objects5 = CACHE_TEMPLATE.zRange(key2, 0, -1);
        assertTrue(objects5.isEmpty());

        assertEquals(0, CACHE_TEMPLATE.existsAsync(key, key2).join().intValue());

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
     * Test bz pop max async.
     */
    public void testBzPopMaxAsync() {
        String key = "testBzPopMaxAsync";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Object optional = CACHE_TEMPLATE.bzPopMaxAsync(key, 3, TimeUnit.SECONDS).join();
        assertEquals("three", optional);

        final Collection<Object> objects = CACHE_TEMPLATE.bzPopMaxAsync(key, 3).join();
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
     * Test bz pop min async.
     */
    public void testBzPopMinAsync() {
        String key = "testBzPopMinAsync";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Object optional = CACHE_TEMPLATE.bzPopMinAsync(key, 3, TimeUnit.SECONDS).join();
        assertEquals("one", optional);

        final Collection<Object> objects = CACHE_TEMPLATE.bzPopMinAsync(key, 3).join();
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
        assertEquals(2, CACHE_TEMPLATE.zCardAsync(key).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z count.
     */
    public void testZCount() {
        String key = "testZCount";

        final int zAdd = CACHE_TEMPLATE.zAddAsync(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }}).join();
        assertEquals(3, zAdd);

        final int zCount = CACHE_TEMPLATE.zCount(key, Double.MIN_VALUE, true, Double.MAX_VALUE, true);
        assertEquals(3, zCount);

        final int zCount2 = CACHE_TEMPLATE.zCount(key, 1, false, 3, true);
        assertEquals(2, zCount2);

        assertEquals(3, CACHE_TEMPLATE.zCountAsync(key, Double.MIN_VALUE, true, Double.MAX_VALUE, true).join().intValue());
        assertEquals(2, CACHE_TEMPLATE.zCountAsync(key, 1, false, 3, true).join().intValue());

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
        assertTrue(CACHE_TEMPLATE.zDiffAsync(key, key2).join().contains("three"));

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

        CACHE_TEMPLATE.del(key3);
        assertEquals(1, CACHE_TEMPLATE.zDiffStoreAsync(key3, key, key2).join().intValue());

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

        assertEquals(1.0, CACHE_TEMPLATE.zIncrByAsync(key, -2, "one").join());
        assertEquals(4.0, CACHE_TEMPLATE.zIncrByAsync(key, 2, "two").join());

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

        assertTrue(CACHE_TEMPLATE.zInterAsync(key1, key2).join().containsAll(Arrays.asList("one", "two")));

        CACHE_TEMPLATE.del(key1, key2);

    }

    /**
     * Test z inter store.
     */
    public void testZInterStore() {
        String key1 = "testZInterStore1";
        String key2 = "testZInterStore2";
        String dest = "testZInterStore-out";

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

        CACHE_TEMPLATE.del(dest);
        final int zInterStore2 = CACHE_TEMPLATE.zInterStoreAsync(dest, new HashMap<String, Double>() {{
            put(key1, 2.0);
            put(key2, 3.0);
        }}).join();
        assertEquals(2, zInterStore2);

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(dest, 0, -1);
        assertTrue(objects.containsAll(Arrays.asList("one", "two")));
        assertTrue(CACHE_TEMPLATE.zRangeAsync(dest, 0, -1).join().containsAll(Arrays.asList("one", "two")));


        final List<Double> doubles = CACHE_TEMPLATE.zScore(dest, new ArrayList<>(objects));
        assertEquals(5.0, doubles.get(0));
        assertEquals(10.0, doubles.get(1));

        final List<Double> doubles2 = CACHE_TEMPLATE.zScoreAsync(dest, new ArrayList<>(objects)).join();
        assertEquals(5.0, doubles2.get(0));
        assertEquals(10.0, doubles2.get(1));

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(2, CACHE_TEMPLATE.zInterStoreAsync(dest, key1, key2).join().intValue());

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(2, CACHE_TEMPLATE.zInterStoreAggregateAsync(dest, "min", key1, key2).join().intValue());

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(2, CACHE_TEMPLATE.zInterStoreAggregateAsync(dest, "max", key1, key2).join().intValue());


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

        CACHE_TEMPLATE.del(dest);
        assertEquals(2,
                CACHE_TEMPLATE.zInterStoreAsync(dest, "min", new HashMap<String, Double>() {{
                    put(key1, 2.0);
                    put(key2, 3.0);
                }}).join().intValue()
        );

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(2,
                CACHE_TEMPLATE.zInterStoreAsync(dest, "max", new HashMap<String, Double>() {{
                    put(key1, 2.0);
                    put(key2, 3.0);
                }}).join().intValue()
        );

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

        assertEquals(7, CACHE_TEMPLATE.zLexCountHeadAsync(key, "g", true).join().intValue());
        assertEquals(5, CACHE_TEMPLATE.zLexCountAsync(key, "b", true, "f", true).join().intValue());
        assertEquals(4, CACHE_TEMPLATE.zLexCountTailAsync(key, "c", false).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test zm pop.
     */
    public void testZmPop() {
        final Optional<Object> optional = CACHE_TEMPLATE.zmPop("notsuchkey", true);
        assertFalse(optional.isPresent());

        String key = "testZmPop";
        final int zAdd = CACHE_TEMPLATE.zAdd(key, new LinkedHashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Optional<Object> optional2 = CACHE_TEMPLATE.zmPop(key, true);
        assertEquals("one", optional2.get());
        final Optional<Object> optional3 = CACHE_TEMPLATE.zmPop(key, false);
        assertEquals("three", optional3.get());

        final Collection<Object> objects = CACHE_TEMPLATE.zRange(key, 0, -1);
        assertEquals("two", objects.iterator().next());

        String key2 = "testZmPop2";
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 4D, "four"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 5D, "five"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 6D, "six"));

        CACHE_TEMPLATE.zmPop(key, true);
        CACHE_TEMPLATE.zmPop(key, false);

        final Optional<Object> objects2 = CACHE_TEMPLATE.zmPop(key, true, 10, TimeUnit.SECONDS, key2);
        assertEquals("four", objects2.get());

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
     * Test zm pop async.
     */
    public void testZmPopAsync() {
        final Optional<Object> optional = CACHE_TEMPLATE.zmPop("notsuchkey", true);
        assertFalse(optional.isPresent());

        String key = "testZmPopAsync";
        final int zAdd = CACHE_TEMPLATE.zAdd(key, new LinkedHashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Object optional2 = CACHE_TEMPLATE.zmPopAsync(key, true).join();
        assertEquals("one", optional2);
        final Object optional3 = CACHE_TEMPLATE.zmPopAsync(key, false).join();
        assertEquals("three", optional3);

        final Collection<Object> objects = CACHE_TEMPLATE.zRangeAsync(key, 0, -1).join();
        assertEquals("two", objects.iterator().next());

        String key2 = "testZmPop2";
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 4D, "four"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 5D, "five"));
        assertTrue(CACHE_TEMPLATE.zAdd(key2, 6D, "six"));

        CACHE_TEMPLATE.zmPop(key, true);
        CACHE_TEMPLATE.zmPop(key, false);

        final Object objects2 = CACHE_TEMPLATE.zmPopAsync(key, true, 10, TimeUnit.SECONDS, key2).join();
        assertEquals("four", objects2);

        final Collection<Object> objects3 = CACHE_TEMPLATE.zRangeAsync(key, 0, -1).join();
        assertTrue(objects3.isEmpty());

        final Object objects4 = CACHE_TEMPLATE.zmPopAsync(key, false, 10, TimeUnit.SECONDS, key2).join();
        assertNotNull(objects4);

        final Object objects42 = CACHE_TEMPLATE.zmPopAsync(key, false, 10, TimeUnit.SECONDS, key2).join();
        assertNotNull(objects42);

        final Collection<Object> objects5 = CACHE_TEMPLATE.zRangeAsync(key2, 0, -1).join();
        assertTrue(objects5.isEmpty());

        assertEquals(0, CACHE_TEMPLATE.existsAsync(key, key2).join().intValue());

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
     * Test z pop max async.
     */
    public void testZPopMaxAsync() {
        String key = "testZPopMaxAsync";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Object optional = CACHE_TEMPLATE.zPopMaxAsync(key).join();
        assertEquals("three", optional);

        final Collection<Object> objects = CACHE_TEMPLATE.zPopMaxAsync(key, 3).join();
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
     * Test z pop min async.
     */
    public void testZPopMinAsync() {
        String key = "testZPopMinAsync";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Object optional = CACHE_TEMPLATE.zPopMinAsync(key).join();
        assertEquals("one", optional);

        final Collection<Object> objects = CACHE_TEMPLATE.zPopMinAsync(key, 3).join();
        assertTrue(objects.containsAll(Arrays.asList("three", "two")));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test z rand member.
     */
    public void testZRandMember() {
        String key = "testZRandMember";

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

        assertTrue(list.contains(CACHE_TEMPLATE.zRandMemberAsync(key).join()));
        assertTrue(list.contains(CACHE_TEMPLATE.zRandMemberAsync(key).join()));
        assertTrue(list.contains(CACHE_TEMPLATE.zRandMemberAsync(key).join()));

        final Collection<Object> objects = CACHE_TEMPLATE.zRandMember(key, 5);
        assertEquals(5, objects.size());
        assertTrue(list.containsAll(objects));

        final Collection<Object> objects2 = CACHE_TEMPLATE.zRandMemberAsync(key, 5).join();
        assertEquals(5, objects2.size());
        assertTrue(list.containsAll(objects2));

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

        final ArrayList<Object> objects13 = new ArrayList<>(CACHE_TEMPLATE.zRangeAsync(key, 1.0, false, Double.MAX_VALUE, true).join());
        assertEquals("two", objects13.get(0));
        assertEquals("three", objects13.get(1));

        final Collection<Object> objects2 = CACHE_TEMPLATE.zRangeAsync(key, 1.0, false, Double.MAX_VALUE, true, 1, 1).join();
        assertEquals("three", objects2.iterator().next());

        final Collection<Object> objects23 = CACHE_TEMPLATE.zRange(key, 1.0, false, Double.MAX_VALUE, true, 1, 1);
        assertEquals("three", objects23.iterator().next());

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
     * Test z range reversed async.
     */
    public void testZRangeReversedAsync() {
        String key = "testZRangeReversedAsync";

        final int zAdd = CACHE_TEMPLATE.zAddAsync(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }}).join();
        assertEquals(3, zAdd);

        final Collection<Object> objects1 = CACHE_TEMPLATE.zRangeReversedAsync(key, 0, -1).join();
        final ArrayList<Object> objects3 = new ArrayList<>(objects1);
        assertEquals("three", objects3.get(0));
        assertEquals("two", objects3.get(1));
        assertEquals("one", objects3.get(2));

        final Collection<Object> objects = CACHE_TEMPLATE.zRangeReversedAsync(key, 1.0, false, Double.MAX_VALUE, true).join();
        final ArrayList<Object> objects4 = new ArrayList<>(objects);
        assertEquals("three", objects4.get(0));
        assertEquals("two", objects4.get(1));

        final Collection<Object> objects2 = CACHE_TEMPLATE.zRangeReversedAsync(key, 1.0, false, Double.MAX_VALUE, true, 1, 1).join();
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
        assertEquals(2, CACHE_TEMPLATE.zRankAsync(key, "three").join().intValue());

        final Optional<Integer> four = CACHE_TEMPLATE.zRank(key, "four");
        assertFalse(four.isPresent());
        assertNull(CACHE_TEMPLATE.zRankAsync(key, "four").join());

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

        CACHE_TEMPLATE.zAddAsync(key, 2D, "two").join();
        assertTrue(CACHE_TEMPLATE.zRemAsync(key, Collections.singletonList("two")).join());

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
     * Test z rem range by lex async.
     */
    public void testZRemRangeByLexAsync() {
        String key = "testZRemRangeByLexAsync";

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

        final Integer optional = CACHE_TEMPLATE.zRemRangeByLexAsync(key, "alpha", true, "omega", true).join();
        assertEquals(6, optional.intValue());

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
     * Test z rem range by rank async.
     */
    public void testZRemRangeByRankAsync() {
        String key = "testZRemRangeByRankAsync";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Integer optional = CACHE_TEMPLATE.zRemRangeByRankAsync(key, 0, 1).join();
        assertEquals(2, optional.intValue());

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
     * Test z rem range by score async.
     */
    public void testZRemRangeByScoreAsync() {
        String key = "testZRemRangeByScoreAsync";

        final int zAdd = CACHE_TEMPLATE.zAdd(key, new HashMap<Object, Double>() {{
            put("one", 1D);
            put("two", 2D);
            put("three", 3D);
        }});
        assertEquals(3, zAdd);

        final Integer optional = CACHE_TEMPLATE.zRemRangeByScoreAsync(key, Double.MIN_VALUE, true, 2, false).join();
        assertEquals(1, optional.intValue());

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

        final Integer integer2 = CACHE_TEMPLATE.zRevRankAsync(key, "two").join();
        assertEquals(1, integer2.intValue());

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
        assertTrue(CACHE_TEMPLATE.zUnionAsync(key1, key2).join().containsAll(Arrays.asList("one", "two", "three")));

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(3,
                CACHE_TEMPLATE.zUnionStoreAsync(dest, new HashMap<String, Double>() {{
                    put(key1, 2.0);
                    put(key2, 3.0);
                }}).join().intValue()
        );

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

        CACHE_TEMPLATE.delAsync(dest).join();
        assertEquals(3, CACHE_TEMPLATE.zUnionStoreAsync(dest, key1, key2).join().intValue());

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(3, CACHE_TEMPLATE.zUnionStoreAggregateAsync(dest, "min", key1, key2).join().intValue());

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(3, CACHE_TEMPLATE.zUnionStoreAggregateAsync(dest, "max", key1, key2).join().intValue());

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(3,
                CACHE_TEMPLATE.zUnionStoreAsync(dest, "min", new HashMap<String, Double>() {{
                    put(key1, 2.0);
                    put(key2, 3.0);
                }}).join().intValue()
        );

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

        CACHE_TEMPLATE.del(dest);
        assertEquals(3,
                CACHE_TEMPLATE.zUnionStoreAsync(dest, "max", new HashMap<String, Double>() {{
                    put(key1, 2.0);
                    put(key2, 3.0);
                }}).join().intValue()
        );

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


    /**
     * Test pf add.
     * <p>
     * hyberloglog
     */
    public void testPfAdd() {
        String key = "hll";

        final boolean b = CACHE_TEMPLATE.pfAdd(key, Arrays.asList("a", "b", "c", "d", "e", "f", "g"));
        assertTrue(b);
        assertEquals(7, CACHE_TEMPLATE.pfCount(key));

        CACHE_TEMPLATE.del(key);

        assertTrue(CACHE_TEMPLATE.pfAddAsync(key, Arrays.asList("a", "b", "c", "d", "e", "f", "g")).join());
        assertEquals(7, CACHE_TEMPLATE.pfCountAsync(key).join().intValue());

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
        assertEquals(6, CACHE_TEMPLATE.pfCountAsync(key, key1).join().intValue());

        CACHE_TEMPLATE.del(key, key1);
    }

    /**
     * Test pf merge.
     */
    public void testPfMerge() {
        String key1 = "hll1";
        String key2 = "hll2";
        String key3 = "hll3";
        String key4 = "hll4";

        CACHE_TEMPLATE.pfAdd(key1, Arrays.asList("foo", "bar", "zap", "a"));
        CACHE_TEMPLATE.pfAdd(key2, Arrays.asList("a", "b", "c", "foo"));

        CACHE_TEMPLATE.pfMerge(key3, key1, key2);
        CACHE_TEMPLATE.pfMergeAsync(key4, key1, key2).join();

        assertEquals(6, CACHE_TEMPLATE.pfCount(key3));
        assertEquals(6, CACHE_TEMPLATE.pfCountAsync(key4).join().intValue());

        CACHE_TEMPLATE.del(key1, key2, key3, key4);
    }

    /**
     * Test append.
     * string
     */
    public void testAppend() {
        String key = "my-key-append";

        CACHE_TEMPLATE.append(key, "Hello");
        CACHE_TEMPLATE.append(key, " World");
        assertEquals("Hello World", CACHE_TEMPLATE.get(key).orElse(null));
        assertEquals("Hello World", CACHE_TEMPLATE.getAsync(key).join());
        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test decr.
     */
    public void testDecr() {
        String key = "my-key-desr";

        assertEquals(-1, CACHE_TEMPLATE.decr(key));
        assertEquals(-2, CACHE_TEMPLATE.decrAsync(key).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test decr by.
     */
    public void testDecrBy() {
        String key = "my-key-desc-br";

        assertEquals(-10, CACHE_TEMPLATE.decrBy(key, 10));
        assertEquals(0, CACHE_TEMPLATE.decrBy(key, -10));
        assertEquals(-10, CACHE_TEMPLATE.decrByAsync(key, 10).join().intValue());
        assertEquals(0, CACHE_TEMPLATE.decrByAsync(key, -10).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get del.
     */
    public void testGetDel() {
        String key = "testGetDel";

        CACHE_TEMPLATE.setObject(key, "hello");
        assertEquals("hello", CACHE_TEMPLATE.getDel(key).get());
        assertFalse(1 == CACHE_TEMPLATE.exists(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.setObjectAsync(key, "hello").join();
        assertEquals("hello", CACHE_TEMPLATE.getDelAsync(key).join().toString());
        assertFalse(1 == CACHE_TEMPLATE.existsAsync(key).join());

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
        String key = "testIncr";

        assertEquals(1, CACHE_TEMPLATE.incr(key));
        assertEquals(2, CACHE_TEMPLATE.incrAsync(key).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr by.
     */
    public void testIncrBy() {
        String key = "testIncrBy";

        assertEquals(10, CACHE_TEMPLATE.incrBy(key, 10));
        assertEquals(0, CACHE_TEMPLATE.incrBy(key, -10));
        assertEquals(10, CACHE_TEMPLATE.incrByAsync(key, 10).join().intValue());
        assertEquals(0, CACHE_TEMPLATE.incrByAsync(key, -10).join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr by float.
     */
    public void testIncrByFloat() {
        String key = "testIncrByFloat";

        assertEquals(10.01D, CACHE_TEMPLATE.incrByFloat(key, 10.01D));
        assertEquals(0.0, CACHE_TEMPLATE.incrByFloat(key, -10.01D));
        assertEquals(10.01D, CACHE_TEMPLATE.incrByFloatAsync(key, 10.01D).join());
        assertEquals(0.0, CACHE_TEMPLATE.incrByFloatAsync(key, -10.01D).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test compare and set.
     */
    public void testCompareAndSet() {
        String key = "testCompareAndSet";

        CACHE_TEMPLATE.incr(key);
        assertFalse(CACHE_TEMPLATE.compareAndSet(key, 0, 2));
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, 1, 3));
        assertEquals(3, CACHE_TEMPLATE.getLong(key));
        assertEquals(3, CACHE_TEMPLATE.getLongAsync(key).join().longValue());

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.incrByFloat(key, 1);
        assertFalse(CACHE_TEMPLATE.compareAndSet(key, 0.0, 2.0));
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, 1.0, 3.0));
        assertEquals(3.0, CACHE_TEMPLATE.getDouble(key));
        assertEquals(3.0, CACHE_TEMPLATE.getDoubleAsync(key).join());

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.set(key, "foo");
        assertFalse(CACHE_TEMPLATE.compareAndSet(key, "hello", "world"));
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, "foo", "bar"));
        assertEquals("bar", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test compare and set async.
     */
    public void testCompareAndSetAsync() {
        String key = "testCompareAndSetAsync";

        CACHE_TEMPLATE.incr(key);
        assertFalse(CACHE_TEMPLATE.compareAndSetAsync(key, 0, 2).join());
        assertTrue(CACHE_TEMPLATE.compareAndSetAsync(key, 1, 3).join());
        assertEquals(3, CACHE_TEMPLATE.getLong(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.incrByFloat(key, 1);
        assertFalse(CACHE_TEMPLATE.compareAndSetAsync(key, 0.0, 2.0).join());
        assertTrue(CACHE_TEMPLATE.compareAndSetAsync(key, 1.0, 3.0).join());
        assertEquals(3.0, CACHE_TEMPLATE.getDouble(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.set(key, "foo");
        assertFalse(CACHE_TEMPLATE.compareAndSetAsync(key, "hello", "world").join());
        assertTrue(CACHE_TEMPLATE.compareAndSetAsync(key, "foo", "bar").join());
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
        CACHE_TEMPLATE.setObject(key2, "World");

        final Map<String, Object> map = CACHE_TEMPLATE.mGet(key1, key2, "nonexisting");
        assertEquals("Hello", map.get(key1));
        assertEquals("World", map.get(key2));
        assertNull(map.get("nonexisting"));

        final Map<String, Object> map2 = CACHE_TEMPLATE.mGetAsync(key1, key2, "nonexisting").join();
        assertEquals("Hello", map2.get(key1));
        assertEquals("World", map2.get(key2));
        assertNull(map2.get("nonexisting"));

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test set.
     */
    public void testSet() {
        String key1 = "testSet1";
        String key2 = "testSet2";
        String key3 = "testSet3";
        String key4 = "testSet4";
        String key5 = "testSet5";
        String key6 = "testSet6";

        CACHE_TEMPLATE.setObject(key1, "Hello");
        CACHE_TEMPLATE.setObject(key2, 1L);
        CACHE_TEMPLATE.setObject(key3, 2.0D);
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("hello");
        strings.add("world");
        CACHE_TEMPLATE.setObject(key4, strings);
        CACHE_TEMPLATE.setObject(key5, 5);
        CACHE_TEMPLATE.setObject(key6, 6.0F);

        assertEquals("Hello", CACHE_TEMPLATE.get(key1).get());
        assertEquals("Hello", CACHE_TEMPLATE.getObject(key1).get());
        assertEquals("Hello", CACHE_TEMPLATE.getAsync(key1).join());
        assertEquals("Hello", CACHE_TEMPLATE.getObjectAsync(key1).join());

        assertEquals(1L, CACHE_TEMPLATE.getLong(key2));
        assertEquals(1L, Long.parseLong(CACHE_TEMPLATE.getObject(key2).get().toString()));
        assertEquals(1L, CACHE_TEMPLATE.getLongAsync(key2).join().longValue());
        assertEquals(1L, Long.parseLong(CACHE_TEMPLATE.getObjectAsync(key2).join().toString()));

        assertEquals(2.0D, CACHE_TEMPLATE.getDouble(key3));
        assertEquals(2.0D, Double.parseDouble(CACHE_TEMPLATE.getObject(key3).get().toString()));
        assertEquals(2.0D, CACHE_TEMPLATE.getDoubleAsync(key3).join());
        assertEquals(2.0D, Double.parseDouble(CACHE_TEMPLATE.getObjectAsync(key3).join().toString()));

        List<String> list = (List<String>) CACHE_TEMPLATE.getObject(key4).get();
        assertEquals(2, list.size());

        List<String> list2 = (List<String>) CACHE_TEMPLATE.getObjectAsync(key4).join();
        assertEquals(2, list2.size());

        assertEquals(5, CACHE_TEMPLATE.getLong(key5));
        assertEquals(5, Integer.parseInt(CACHE_TEMPLATE.getObject(key5).get().toString()));
        assertEquals(5, CACHE_TEMPLATE.getLongAsync(key5).join().intValue());
        assertEquals(5, Integer.parseInt(CACHE_TEMPLATE.getObjectAsync(key5).join().toString()));

        assertEquals(6.0F, (float) CACHE_TEMPLATE.getDouble(key6));
        assertEquals(6.0F, Float.parseFloat(CACHE_TEMPLATE.getObject(key6).get().toString()));
        assertEquals(6.0F, CACHE_TEMPLATE.getDoubleAsync(key6).join().floatValue());
        assertEquals(6.0F, Float.parseFloat(CACHE_TEMPLATE.getObjectAsync(key6).join().toString()));

        CACHE_TEMPLATE.del(key1, key2, key3, key4, key5, key6);
    }

    /**
     * Test set async.
     */
    public void testSetAsync() {
        String key1 = "testSetAsync1";
        String key2 = "testSetAsync2";
        String key3 = "testSetAsync3";
        String key4 = "testSetAsync4";

        CACHE_TEMPLATE.setAsync(key1, "Hello").join();
        CACHE_TEMPLATE.setAsync(key2, 1L).join();
        CACHE_TEMPLATE.setAsync(key3, 2.0D).join();
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("hello");
        strings.add("world");
        CACHE_TEMPLATE.setObjectAsync(key4, strings).join();

        assertEquals("Hello", CACHE_TEMPLATE.getAsync(key1).join());
        assertEquals(1L, CACHE_TEMPLATE.getLongAsync(key2).join().longValue());
        assertEquals(2.0D, CACHE_TEMPLATE.getDoubleAsync(key3).join());
        List<String> list = (List<String>) CACHE_TEMPLATE.getObjectAsync(key4).join();
        assertTrue(list.containsAll(Arrays.asList("hello", "world")));

        CACHE_TEMPLATE.del(key1, key2, key3, key4);
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
        CACHE_TEMPLATE.mSetAsync(new HashMap<String, String>() {{
            put(key1, "Hello");
            put(key2, "World");
        }}).join();

        final Map<String, Object> map2 = CACHE_TEMPLATE.mGetAsync(key1, key2, "nonexisting").join();
        assertEquals("Hello", map2.get(key1));
        assertEquals("World", map2.get(key2));
        assertNull(map2.get("nonexisting"));

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
     * Test m set nx async.
     */
    public void testMSetNxAsync() {
        String key1 = "testMSetNxAsync1";
        String key2 = "testMSetNxAsync2";

        final boolean msetnx = CACHE_TEMPLATE.mSetNXAsync(new HashMap<String, String>() {{
            put(key1, "Hello");
            put(key2, "World");
        }}).join();
        assertTrue(msetnx);

        final boolean msetnx2 = CACHE_TEMPLATE.mSetNXAsync(new HashMap<String, String>() {{
            put(key1, "Hello");
            put(key2, "World");
        }}).join();
        assertFalse(msetnx2);


        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test set ex.
     */
    public void testSetEX() {
        String key1 = "key1";
        CACHE_TEMPLATE.setObject(key1, "hello world");
        assertEquals("hello world", CACHE_TEMPLATE.get(key1).get());

        CACHE_TEMPLATE.setEX(key1, "foo bar", Duration.ofSeconds(10));
        assertEquals("foo bar", CACHE_TEMPLATE.get(key1).get());

        CACHE_TEMPLATE.del(key1);
    }

    /**
     * Test set ex async.
     */
    public void testSetEXAsync() {
        String key1 = "testSetEXAsync1";
        CACHE_TEMPLATE.setObjectAsync(key1, "hello world").join();
        assertEquals("hello world", CACHE_TEMPLATE.get(key1).get());

        CACHE_TEMPLATE.setEXAsync(key1, "foo bar", Duration.ofSeconds(10)).join();
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
        String key = "testStrLen";
        CACHE_TEMPLATE.setObject(key, "Hello world");

        assertEquals(11, CACHE_TEMPLATE.strLen(key));
        assertEquals(0, CACHE_TEMPLATE.strLen("nonexisting"));
        assertEquals(11, CACHE_TEMPLATE.strLenAsync(key).join().intValue());
        assertEquals(0, CACHE_TEMPLATE.strLenAsync("nonexisting").join().intValue());

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
        assertTrue(CACHE_TEMPLATE.deleteBf(key));

        CACHE_TEMPLATE.bfReserve(key, 100, 0.01);
        assertTrue(CACHE_TEMPLATE.bfAdd(key, "item1"));
        assertFalse(CACHE_TEMPLATE.bfAdd(key, "item1"));
        assertTrue(CACHE_TEMPLATE.deleteBfAsync(key).join());

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

        final boolean bfmAdd = CACHE_TEMPLATE.bfmAdd(key, Arrays.asList("item1", "item2", "item2"));
        assertTrue(bfmAdd);

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
     * lock
     */
    public void testTryLock() {
        String key = "testTryLock";

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
     * Test try lock async.
     */
    public void testTryLockAsync() {
        String key = "testTryLockAsync";

        final boolean tryLock = CACHE_TEMPLATE.tryLockAsync(key, 3, 5, TimeUnit.SECONDS).join();
        assertTrue(tryLock);

        CompletableFuture.runAsync(() -> {
            final boolean b = CACHE_TEMPLATE.tryLockAsync(key, 3, 3, TimeUnit.SECONDS).join();
            assertFalse(b);
        }).join();

        CompletableFuture.runAsync(() -> {
            final boolean b = CACHE_TEMPLATE.tryLockAsync(key, 10, 1, TimeUnit.SECONDS).join();
            assertTrue(b);
        }).join();


        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try lock 2.
     */
    public void testTryLock2() {
        String key = "testTryLock2";

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
     * Test try lock 2 async.
     */
    public void testTryLock2Async() {
        String key = "testTryLock2Async";

        final boolean tryLock = CACHE_TEMPLATE.tryLockAsync(key, 3, TimeUnit.SECONDS).join();
        assertTrue(tryLock);

        CompletableFuture.runAsync(() -> {
            final boolean b = CACHE_TEMPLATE.tryLockAsync(key, 3, TimeUnit.SECONDS).join();
            assertFalse(b);
        }).join();

        CACHE_TEMPLATE.unlock(key);

        CompletableFuture.runAsync(() -> {
            final boolean b = CACHE_TEMPLATE.tryLockAsync(key, 10, TimeUnit.SECONDS).join();
            assertTrue(b);
        }).join();


        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test unlock async.
     */
    public void testUnlockAsync() {
        String key = "testUnlockAsync";

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
                CACHE_TEMPLATE.unlockAsync(key).join();
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
            CACHE_TEMPLATE.unlockAsync(key, id).join();
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
        String key = "testForceUnlock";

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
     * Test force unlock async.
     */
    public void testForceUnlockAsync() {
        String key = "testForceUnlockAsync";

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
                final boolean forceUnlock = CACHE_TEMPLATE.forceUnlockAsync(key).join();
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

        final List<Object> keys = Collections.singletonList("my-lua-key");
        final Optional<Object> optional = CACHE_TEMPLATE.executeScript(saddNxLua, keys, "hello", "world");
        assertEquals(2, (long) optional.get());

        final Optional<Object> optional2 = CACHE_TEMPLATE.executeScript(saddNxLua, keys, "hello", "world");
        assertEquals(0, (long) optional2.get());

        CACHE_TEMPLATE.del(keys.toArray(new String[]{}));
    }

    /**
     * Test execute script async.
     *
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public void testExecuteScriptAsync() throws NoSuchAlgorithmException {
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

        final List<Object> keys = Collections.singletonList("my-lua-key-async");
        final Object optional = CACHE_TEMPLATE.executeScriptAsync(saddNxLua, keys, "hello", "world").join();
        assertEquals(2, (long) optional);

        final Object optional2 = CACHE_TEMPLATE.executeScriptAsync(saddNxLua, keys, "hello", "world").join();
        assertEquals(0, (long) optional2);

        CACHE_TEMPLATE.del(keys.toArray(new String[]{}));
    }

    /**
     * Test exists.
     */
    public void testExists() {
        String key = "testExists";
        CACHE_TEMPLATE.setObject(key, "hello world");

        assertEquals(1, CACHE_TEMPLATE.exists(key));
        assertFalse(1 == CACHE_TEMPLATE.exists("noExistKey"));

        assertEquals(1, CACHE_TEMPLATE.existsAsync(key).join().intValue());
        assertFalse(1 == CACHE_TEMPLATE.existsAsync("noExistKey").join().intValue());

        CACHE_TEMPLATE.del(key);
    }

    public void testExpire() {
        String key = "testExpire";
        CACHE_TEMPLATE.setObject(key, "hello world");

        assertTrue(CACHE_TEMPLATE.expire(key, 60, TimeUnit.SECONDS));
        assertTrue(CACHE_TEMPLATE.ttl(key) > 55 && CACHE_TEMPLATE.ttl(key) < 60);

        assertTrue(CACHE_TEMPLATE.expireAt(key, System.currentTimeMillis() + 15000));
        assertTrue(CACHE_TEMPLATE.ttl(key) > 10 && CACHE_TEMPLATE.ttl(key) < 15);

        assertTrue(CACHE_TEMPLATE.expireAsync(key, 22, TimeUnit.SECONDS).join());
        assertTrue(CACHE_TEMPLATE.ttl(key) > 18 && CACHE_TEMPLATE.ttl(key) < 22);

        assertTrue(CACHE_TEMPLATE.expireAtAsync(key, System.currentTimeMillis() + 55000).join());
        assertTrue(CACHE_TEMPLATE.ttl(key) > 50 && CACHE_TEMPLATE.ttl(key) < 55);

    }

    /**
     * Test del.
     */
    public void testDel() {
        String key = "testExists";

        CACHE_TEMPLATE.setObject(key, "hello world");

        final long del = CACHE_TEMPLATE.del(key);
        assertEquals(1, del);
        assertFalse(1 == CACHE_TEMPLATE.del(key));
    }

    public void testUnlink() {
        String key = "testUnlink";
        String key2 = "testUnlink2";
        String key3 = "testUnlink3";
        String key4 = "testUnlink4";

        CACHE_TEMPLATE.setObject(key, "hello world");
        CACHE_TEMPLATE.setObject(key2, "foo");
        CACHE_TEMPLATE.setObject(key3, "foo");
        CACHE_TEMPLATE.setObject(key4, "foo");

        assertEquals(2, CACHE_TEMPLATE.unlink(key, key2));
        assertEquals(2, CACHE_TEMPLATE.unlinkAsync(key3, key4).join().intValue());
        assertFalse(1 == CACHE_TEMPLATE.unlink(key));
        assertFalse(1 == CACHE_TEMPLATE.unlinkAsync(key).join().intValue());
    }

    /**
     * Test ttl.
     */
    public void testTTL() {
        String key = "testTTL";
        String key2 = "testTTL2";
        CACHE_TEMPLATE.setObject(key, "hello world");
        CACHE_TEMPLATE.setEX(key2, "hello world", Duration.ofSeconds(30));

        assertEquals(-1, CACHE_TEMPLATE.ttl(key));
        assertEquals(-2, CACHE_TEMPLATE.ttl("noExistKey"));
        assertTrue(CACHE_TEMPLATE.ttl(key2) > 26 && CACHE_TEMPLATE.ttl(key2) <= 30);

        assertEquals(-1, CACHE_TEMPLATE.ttlAsync(key).join().intValue());
        assertEquals(-2, CACHE_TEMPLATE.ttlAsync("noExistKey").join().intValue());
        assertTrue(CACHE_TEMPLATE.ttlAsync(key2).join().intValue() > 20 &&
                CACHE_TEMPLATE.ttlAsync(key2).join().intValue() < 30);

        CACHE_TEMPLATE.del(key);
    }

    public void testPTTL() {
        String key = "testPTTL";
        String key2 = "testPTTL2";
        CACHE_TEMPLATE.setObject(key, "hello world");
        CACHE_TEMPLATE.setEX(key2, "hello world", Duration.ofSeconds(30));

        assertEquals(-1, CACHE_TEMPLATE.pTTL(key));
        assertEquals(-2, CACHE_TEMPLATE.pTTL("noExistKey"));
        assertTrue(CACHE_TEMPLATE.pTTL(key2) > 26000 && CACHE_TEMPLATE.pTTL(key2) <= 30000);

        assertEquals(-1, CACHE_TEMPLATE.pTTLAsync(key).join().intValue());
        assertEquals(-2, CACHE_TEMPLATE.pTTLAsync("noExistKey").join().intValue());
        assertTrue(CACHE_TEMPLATE.pTTLAsync(key2).join().intValue() > 20000 &&
                CACHE_TEMPLATE.pTTLAsync(key2).join().intValue() < 30000);

        CACHE_TEMPLATE.del(key, key2);
    }

    /**
     * Test scan.
     */
    public void testScan() {
        String key = "myObj";
        CACHE_TEMPLATE.setObject(key, "hello world");

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
        CACHE_TEMPLATE.setObject(key, "hello world");
        assertSame(KeyType.STRING, CACHE_TEMPLATE.type(key));

        String key2 = "myHash";
        CACHE_TEMPLATE.hSet(key2, "field", "hello world");
        assertSame(KeyType.HASH, CACHE_TEMPLATE.type(key2));

        String key3 = "myList";
        CACHE_TEMPLATE.lPush(key3, "hello world", "foo bar");
        assertSame(KeyType.LIST, CACHE_TEMPLATE.typeAsync(key3).join());

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
     * rateLimiter
     */
    public void testTrySetRateLimiter() {
        String key = "testTrySetRateLimiter";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiter(key, 3, 10);
        assertTrue(b);

        CACHE_TEMPLATE.del(key);
        assertTrue(CACHE_TEMPLATE.trySetRateLimiterAsync(key, 3, 10).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try acquire.
     */
    public void testTryAcquire() {
        String key = "testTryAcquire";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiter(key, 3, 10);
        assertTrue(b);

        for (int i = 0; i < 3; i++) {
            assertTrue(CACHE_TEMPLATE.tryAcquire(key));
        }

        assertFalse(CACHE_TEMPLATE.tryAcquire(key));

        CACHE_TEMPLATE.del(key, "{" + key + "}:permits", "{" + key + "}:value");
    }

    /**
     * Test try acquire async.
     */
    public void testTryAcquireAsync() {
        String key = "testTryAcquireAsync";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiterAsync(key, 3, 10).join();
        assertTrue(b);

        for (int i = 0; i < 3; i++) {
            assertTrue(CACHE_TEMPLATE.tryAcquireAsync(key).join());
        }

        assertFalse(CACHE_TEMPLATE.tryAcquireAsync(key).join());

        CACHE_TEMPLATE.del(key, "{" + key + "}:permits", "{" + key + "}:value");
    }

    /**
     * Test try acquire 2.
     */
    public void testTryAcquire2() {
        String key = "testTryAcquire2";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiter(key, 3, 10);
        assertTrue(b);

        assertTrue(CACHE_TEMPLATE.tryAcquire(key, 3));
        assertFalse(CACHE_TEMPLATE.tryAcquire(key, 3));

        CACHE_TEMPLATE.del(key, "{" + key + "}:permits", "{" + key + "}:value");
    }

    /**
     * Test try acquire 2 async.
     */
    public void testTryAcquire2Async() {
        String key = "testTryAcquire2Async";

        final boolean b = CACHE_TEMPLATE.trySetRateLimiterAsync(key, 3, 10).join();
        assertTrue(b);

        assertTrue(CACHE_TEMPLATE.tryAcquireAsync(key, 3).join());
        assertFalse(CACHE_TEMPLATE.tryAcquireAsync(key, 3).join());

        CACHE_TEMPLATE.del(key, "{" + key + "}:permits", "{" + key + "}:value");
    }

}
