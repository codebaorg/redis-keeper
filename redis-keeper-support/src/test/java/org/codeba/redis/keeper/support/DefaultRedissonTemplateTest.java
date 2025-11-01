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
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.core.KBatch;
import org.codeba.redis.keeper.core.KScript;
import org.codeba.redis.keeper.core.KeyType;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Unit test for DefaultRedissonTemplate.
 */
public class DefaultRedissonTemplateTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(DefaultRedissonTemplateTest.class);
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
        String yourPass = "0JBE7Xtf3LxwoIp7";
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

            final CacheDatasource<CacheTemplate> datasource = new CacheDatasource<CacheTemplate>() {
                @Override
                public CacheTemplate instantTemplate(CacheKeeperConfig config) {
                    return new DefaultRedissonTemplate(config);
                }

                @Override
                public Consumer<CacheKeeperConfig> configPostProcessor(Consumer<CacheKeeperConfig> consumer) {
                    return v -> v.getConfig().setCodec(new JsonJacksonCodec(getJacksonMapper()));
                }
            };
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
    public DefaultRedissonTemplateTest(String testName) {
        super(testName);
    }

    /**
     * Suite test.
     *
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DefaultRedissonTemplateTest.class);
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
     * Test append.
     */
    public void testAppend() {
        String key = "testAppend";

        CACHE_TEMPLATE.set(key, "Hello ");
        CACHE_TEMPLATE.append(key, "World");
        assertEquals("Hello World", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.append(key, "!");
        assertEquals("Hello World!", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test decr.
     */
    public void testDecr() {
        String key = "testDecr";

        CACHE_TEMPLATE.set(key, "10");
        assertEquals(9, CACHE_TEMPLATE.decr(key));
        assertEquals(8, CACHE_TEMPLATE.decr(key));

        assertEquals(7, CACHE_TEMPLATE.decrAsync(key).join().longValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test decr by.
     */
    public void testDecrBy() {
        String key = "testDecrBy";

        CACHE_TEMPLATE.set(key, "20");
        assertEquals(15, CACHE_TEMPLATE.decrBy(key, 5));
        assertEquals(10, CACHE_TEMPLATE.decrBy(key, 5));

        assertEquals(5, CACHE_TEMPLATE.decrByAsync(key, 5).join().longValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get.
     */
    public void testGet() {
        String key = "testGet";

        CACHE_TEMPLATE.set(key, "Hello World");
        assertEquals("Hello World", CACHE_TEMPLATE.get(key).get());
        assertEquals("Hello World", CACHE_TEMPLATE.getAsync(key).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get binary.
     */
    public void testGetBinary() {
        String key = "testGetBinary";

        CACHE_TEMPLATE.set(key, "Hello World");

        try (java.io.InputStream is = CACHE_TEMPLATE.getBinary(key)) {
            assertNotNull(is);
            byte[] bytes = is.readAllBytes();
            assertEquals("Hello World", new String(bytes));
        } catch (Exception e) {
            fail("Failed to read binary data");
        }

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get object.
     */
    public void testGetObject() {
        String key = "testGetObject";

        Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);

        CACHE_TEMPLATE.setObject(key, obj);

        Map<String, Object> retrieved = (Map<String, Object>) CACHE_TEMPLATE.getObject(key).get();
        assertEquals("John", retrieved.get("name"));
        assertEquals(30, retrieved.get("age"));

        Map<String, Object> asyncRetrieved = (Map<String, Object>) CACHE_TEMPLATE.getObjectAsync(key).join();
        assertEquals("John", asyncRetrieved.get("name"));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get del.
     */
    public void testGetDel() {
        String key = "testGetDel";

        CACHE_TEMPLATE.set(key, "Hello World");
        assertEquals("Hello World", CACHE_TEMPLATE.getDel(key).get());
        assertFalse(CACHE_TEMPLATE.get(key).isPresent());

        CACHE_TEMPLATE.set(key, "Another Value");
        assertEquals("Another Value", CACHE_TEMPLATE.getDelAsync(key).join());
        assertFalse(CACHE_TEMPLATE.get(key).isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get long.
     */
    public void testGetLong() {
        String key = "testGetLong";

        CACHE_TEMPLATE.set(key, "12345");
        assertEquals(12345L, CACHE_TEMPLATE.getLong(key));

        assertEquals(12345L, CACHE_TEMPLATE.getLongAsync(key).join().longValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr.
     */
    public void testIncr() {
        String key = "testIncr";

        CACHE_TEMPLATE.set(key, "10");
        assertEquals(11, CACHE_TEMPLATE.incr(key));
        assertEquals(12, CACHE_TEMPLATE.incr(key));

        assertEquals(13, CACHE_TEMPLATE.incrAsync(key).join().longValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr by.
     */
    public void testIncrBy() {
        String key = "testIncrBy";

        CACHE_TEMPLATE.set(key, "20");
        assertEquals(25, CACHE_TEMPLATE.incrBy(key, 5));
        assertEquals(30, CACHE_TEMPLATE.incrBy(key, 5));

        assertEquals(35, CACHE_TEMPLATE.incrByAsync(key, 5).join().longValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get double.
     */
    public void testGetDouble() {
        String key = "testGetDouble";

        CACHE_TEMPLATE.set(key, "123.45");
        assertEquals(123.45, CACHE_TEMPLATE.getDouble(key));

        assertEquals(123.45, CACHE_TEMPLATE.getDoubleAsync(key).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test incr by float.
     */
    public void testIncrByFloat() {
        String key = "testIncrByFloat";

        CACHE_TEMPLATE.set(key, "10.5");
        assertEquals(12.7, CACHE_TEMPLATE.incrByFloat(key, 2.2));

        assertEquals(15.0, CACHE_TEMPLATE.incrByFloatAsync(key, 2.3).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test compare and set.
     */
    public void testCompareAndSet() {
        String key = "testCompareAndSet";

        CACHE_TEMPLATE.set(key, "10");
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, 10L, 20L));
        assertEquals("20", CACHE_TEMPLATE.get(key).get());

        assertFalse(CACHE_TEMPLATE.compareAndSet(key, 10L, 30L));
        assertEquals("20", CACHE_TEMPLATE.get(key).get());

        assertTrue(CACHE_TEMPLATE.compareAndSetAsync(key, 20L, 40L).join());
        assertEquals("40", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test compare and set double.
     */
    public void testCompareAndSetDouble() {
        String key = "testCompareAndSetDouble";

        CACHE_TEMPLATE.set(key, "10.5");
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, 10.5, 20.5));
        assertEquals("20.5", CACHE_TEMPLATE.get(key).get());

        assertFalse(CACHE_TEMPLATE.compareAndSet(key, 10.5, 30.5));
        assertEquals("20.5", CACHE_TEMPLATE.get(key).get());

        assertTrue(CACHE_TEMPLATE.compareAndSetAsync(key, 20.5, 40.5).join());
        assertEquals("40.5", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test compare and set string.
     */
    public void testCompareAndSetString() {
        String key = "testCompareAndSetString";

        CACHE_TEMPLATE.set(key, "Hello");
        assertTrue(CACHE_TEMPLATE.compareAndSet(key, "Hello", "World"));
        assertEquals("World", CACHE_TEMPLATE.get(key).get());

        assertFalse(CACHE_TEMPLATE.compareAndSet(key, "Hello", "Test"));
        assertEquals("World", CACHE_TEMPLATE.get(key).get());

        assertTrue(CACHE_TEMPLATE.compareAndSetAsync(key, "World", "Test").join());
        assertEquals("Test", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test set object.
     */
    public void testSetObject() {
        String key = "testSetObject";
        String name = "John";

        Map<String, Object> obj = new HashMap<>();
        obj.put("name", name);
        obj.put("age", 30);

        CACHE_TEMPLATE.setObject(key, obj);
        Map<String, Object> retrieved = (Map<String, Object>) CACHE_TEMPLATE.getObject(key).get();
        assertEquals("John", retrieved.get("name").toString());

        CACHE_TEMPLATE.setObjectEx(key, obj, Duration.ofSeconds(3));
        Map<String, Object> retrieved2 = (Map<String, Object>) CACHE_TEMPLATE.getObject(key).get();
        assertEquals(30, retrieved2.get("age"));

        try {
            Thread.sleep(3100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertFalse(CACHE_TEMPLATE.getObject(key).isPresent());

        CACHE_TEMPLATE.setObjectAsync(key, obj).join();
        Map<String, Object> retrieved3 = (Map<String, Object>) CACHE_TEMPLATE.getObject(key).get();
        assertEquals("John", retrieved3.get("name").toString());

        CACHE_TEMPLATE.setObjectEXAsync(key, obj, Duration.ofSeconds(3)).join();
        Map<String, Object> retrieved4 = (Map<String, Object>) CACHE_TEMPLATE.getObject(key).get();
        assertEquals("John", retrieved4.get("name").toString());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test m get.
     */
    public void testMGet() {
        String key1 = "testMGet1";
        String key2 = "testMGet2";
        String key3 = "testMGet3";

        CACHE_TEMPLATE.set(key1, "value1");
        CACHE_TEMPLATE.set(key2, "value2");

        Map<String, Object> result = CACHE_TEMPLATE.mGet(key1, key2, key3);
        assertEquals("value1", result.get(key1));
        assertEquals("value2", result.get(key2));
        assertNull(result.get(key3));

        Map<String, Object> asyncResult = CACHE_TEMPLATE.mGetAsync(key1, key2, key3).join();
        assertEquals("value1", asyncResult.get(key1));
        assertEquals("value2", asyncResult.get(key2));

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test m set.
     */
    public void testMSet() {
        String key1 = "testMSet1";
        String key2 = "testMSet2";

        Map<String, String> values = new HashMap<>();
        values.put(key1, "value1");
        values.put(key2, "value2");

        CACHE_TEMPLATE.mSet(values);
        assertEquals("value1", CACHE_TEMPLATE.get(key1).get());
        assertEquals("value2", CACHE_TEMPLATE.get(key2).get());

        CACHE_TEMPLATE.mSetAsync(values).join();
        assertEquals("value1", CACHE_TEMPLATE.get(key1).get());
        assertEquals("value2", CACHE_TEMPLATE.get(key2).get());

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test m set nx.
     */
    public void testMSetNX() {
        String key1 = "testMSetNX1";
        String key2 = "testMSetNX2";

        Map<String, String> values = new HashMap<>();
        values.put(key1, "value1");
        values.put(key2, "value2");

        assertTrue(CACHE_TEMPLATE.mSetNX(values));
        assertEquals("value1", CACHE_TEMPLATE.get(key1).get());
        assertEquals("value2", CACHE_TEMPLATE.get(key2).get());

        assertFalse(CACHE_TEMPLATE.mSetNX(values));

        CACHE_TEMPLATE.del(key1, key2);

        assertTrue(CACHE_TEMPLATE.mSetNXAsync(values).join());

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test set.
     */
    public void testSet() {
        String key = "testSet";

        CACHE_TEMPLATE.set(key, "Hello World");
        assertEquals("Hello World", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.set(key, 123L);
        assertEquals("123", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.set(key, 45.67);
        assertEquals("45.67", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.setAsync(key, "Async Value").join();
        assertEquals("Async Value", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.setAsync(key, 999L).join();
        assertEquals("999", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.setAsync(key, 12.34).join();
        assertEquals("12.34", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test set ex.
     */
    public void testSetEX() {
        String key = "testSetEX";

        CACHE_TEMPLATE.setEX(key, "Hello World", Duration.ofSeconds(1));
        assertEquals("Hello World", CACHE_TEMPLATE.get(key).get());

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertFalse(CACHE_TEMPLATE.get(key).isPresent());

        CACHE_TEMPLATE.setEXAsync(key, "Async Value", Duration.ofSeconds(1)).join();
        assertEquals("Async Value", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test str len.
     */
    public void testStrLen() {
        String key = "testStrLen";

        CACHE_TEMPLATE.set(key, "Hello World");
        assertEquals(11, CACHE_TEMPLATE.strLen(key));

        assertEquals(11, CACHE_TEMPLATE.strLenAsync(key).join().longValue());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test bf add.
     */
    public void testBfAdd() {
        String key = "testBfAdd";

        assertTrue(CACHE_TEMPLATE.bfReserve(key, 100, 0.01));
        assertTrue(CACHE_TEMPLATE.bfAdd(key, "item1"));
        assertTrue(CACHE_TEMPLATE.bfAdd(key, "item2"));
        assertFalse(CACHE_TEMPLATE.bfAdd(key, "item1"));

        assertTrue(CACHE_TEMPLATE.bfExists(key, "item1"));
        assertTrue(CACHE_TEMPLATE.bfExists(key, "item2"));
        assertFalse(CACHE_TEMPLATE.bfExists(key, "item3"));

        assertTrue(CACHE_TEMPLATE.bfmAdd(key, "item3"));

        assertEquals(3, CACHE_TEMPLATE.bfCard(key));

        assertTrue(CACHE_TEMPLATE.deleteBf(key));

        assertTrue(CACHE_TEMPLATE.bfReserve(key, 100, 0.01));

        assertTrue(CACHE_TEMPLATE.deleteBfAsync(key).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try lock.
     */
    public void testTryLock() throws InterruptedException {
        String key = "testTryLock";

        assertTrue(CACHE_TEMPLATE.tryLock(key));
        assertTrue(CACHE_TEMPLATE.isLocked(key));
        assertTrue(CACHE_TEMPLATE.isHeldByCurrentThread(key));

        CACHE_TEMPLATE.tryLock(key, 1, 10, TimeUnit.SECONDS);

        CACHE_TEMPLATE.forceUnlock(key);
        Thread.sleep(1000);
        final boolean locked = CACHE_TEMPLATE.isLocked(key);
        assertFalse(locked);

        assertTrue(CACHE_TEMPLATE.tryLock(key, 1, TimeUnit.SECONDS));
        assertTrue(CACHE_TEMPLATE.isLocked(key));

        assertTrue(CACHE_TEMPLATE.tryLock(key, 1, TimeUnit.SECONDS));

        CACHE_TEMPLATE.unlock(key);

        assertTrue(CACHE_TEMPLATE.tryLockAsync(key).join());
        assertTrue(CACHE_TEMPLATE.tryLockAsync(key, 1, TimeUnit.SECONDS).join());

        CACHE_TEMPLATE.unlock(key);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test lock.
     */
    public void testLock() throws InterruptedException {
        String key = "testLock";

        CACHE_TEMPLATE.lock(key);
        assertTrue(CACHE_TEMPLATE.isLocked(key));

        CACHE_TEMPLATE.unlock(key);
        assertFalse(CACHE_TEMPLATE.isLocked(key));

        CACHE_TEMPLATE.lock(key, 1, TimeUnit.SECONDS);
        assertTrue(CACHE_TEMPLATE.isLocked(key));

        CACHE_TEMPLATE.unlock(key);

        CACHE_TEMPLATE.lockInterruptibly(key);
        assertTrue(CACHE_TEMPLATE.isLocked(key));

        CACHE_TEMPLATE.unlock(key);

        CACHE_TEMPLATE.lockAsync(key).join();
        assertTrue(CACHE_TEMPLATE.isLocked(key));

        CACHE_TEMPLATE.unlockAsync(key).join();

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test force unlock.
     */
    public void testForceUnlock() {
        String key = "testForceUnlock";

        CACHE_TEMPLATE.lock(key);
        assertTrue(CACHE_TEMPLATE.isLocked(key));

        assertTrue(CACHE_TEMPLATE.forceUnlock(key));
        assertFalse(CACHE_TEMPLATE.isLocked(key));

        assertFalse(CACHE_TEMPLATE.forceUnlockAsync(key).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test is held by thread.
     */
    public void testIsHeldByThread() {
        String key = "testIsHeldByThread";

        CACHE_TEMPLATE.lock(key);
        assertTrue(CACHE_TEMPLATE.isHeldByCurrentThread(key));
        assertTrue(CACHE_TEMPLATE.isHeldByThread(key, Thread.currentThread().getId()));

        assertTrue(CACHE_TEMPLATE.isHeldByThreadAsync(key, Thread.currentThread().getId()).join());

        CACHE_TEMPLATE.unlock(key);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test get hold count.
     */
    public void testGetHoldCount() {
        String key = "testGetHoldCount";

        assertEquals(0, CACHE_TEMPLATE.getHoldCount(key));

        CACHE_TEMPLATE.lock(key);
        assertEquals(1, CACHE_TEMPLATE.getHoldCount(key));

        CACHE_TEMPLATE.lock(key);
        assertEquals(2, CACHE_TEMPLATE.getHoldCount(key));

        assertEquals(2, CACHE_TEMPLATE.getHoldCountAsync(key).join().intValue());

        CACHE_TEMPLATE.unlock(key);
        CACHE_TEMPLATE.unlock(key);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test remain time to live.
     */
    public void testRemainTimeToLive() {
        String key = "testRemainTimeToLive";

        CACHE_TEMPLATE.lock(key, 5, TimeUnit.SECONDS);

        long ttl = CACHE_TEMPLATE.remainTimeToLive(key);
        assertTrue(ttl > 0 && ttl <= 5000);

        long asyncTtl = CACHE_TEMPLATE.remainTimeToLiveAsync(key).join();
        assertTrue(asyncTtl > 0 && asyncTtl <= 5000);

        CACHE_TEMPLATE.unlock(key);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test execute script.
     */
    public void testExecuteScript() throws NoSuchAlgorithmException {
        String key = "testExecuteScript";

        CACHE_TEMPLATE.set(key, "10");

        String script = "return redis.call('GET', KEYS[1])";
        List<Object> keys = Arrays.asList(key);

        Object result = CACHE_TEMPLATE.executeScript(script, keys).get();
        assertEquals("10", result);

        Object asyncResult = CACHE_TEMPLATE.executeScriptAsync(script, keys).join();
        assertEquals("10", asyncResult);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test exists.
     */
    public void testExists() {
        String key1 = "testExists1";
        String key2 = "testExists2";
        String key3 = "testExists3";

        CACHE_TEMPLATE.set(key1, "value1");
        CACHE_TEMPLATE.set(key2, "value2");

        assertEquals(2, CACHE_TEMPLATE.exists(key1, key2));
        assertEquals(0, CACHE_TEMPLATE.exists(key3));

        assertEquals(2, CACHE_TEMPLATE.existsAsync(key1, key2).join().longValue());

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test expire.
     */
    public void testExpire() {
        String key = "testExpire";

        CACHE_TEMPLATE.set(key, "value");

        assertTrue(CACHE_TEMPLATE.expire(key, 3, TimeUnit.SECONDS));
        assertTrue(CACHE_TEMPLATE.ttl(key) > 0);

        assertTrue(CACHE_TEMPLATE.expireAsync(key, 3, TimeUnit.SECONDS).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test expire at.
     */
    public void testExpireAt() {
        String key = "testExpireAt";

        CACHE_TEMPLATE.set(key, "value");

        long expireTime = System.currentTimeMillis() / 1000 + 5;
        final boolean expireAt = CACHE_TEMPLATE.expireAt(key, expireTime);
        assertTrue(expireAt);
        final long ttl = CACHE_TEMPLATE.ttl(key);
        assertTrue(ttl > 0);

        assertTrue(CACHE_TEMPLATE.expireAtAsync(key, expireTime + 5).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test del.
     */
    public void testDel() {
        String key1 = "testDel1";
        String key2 = "testDel2";

        CACHE_TEMPLATE.set(key1, "value1");
        CACHE_TEMPLATE.set(key2, "value2");

        assertEquals(2, CACHE_TEMPLATE.del(key1, key2));
        assertEquals(0, CACHE_TEMPLATE.del("nonexistent"));

        CACHE_TEMPLATE.set(key1, "value1");
        assertEquals(1, CACHE_TEMPLATE.delAsync(key1).join().longValue());

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test unlink.
     */
    public void testUnlink() {
        String key1 = "testUnlink1";
        String key2 = "testUnlink2";

        CACHE_TEMPLATE.set(key1, "value1");
        CACHE_TEMPLATE.set(key2, "value2");

        assertEquals(2, CACHE_TEMPLATE.unlink(key1, key2));
        assertEquals(0, CACHE_TEMPLATE.unlink("nonexistent"));

        CACHE_TEMPLATE.set(key1, "value1");
        assertEquals(1, CACHE_TEMPLATE.unlinkAsync(key1).join().longValue());

        CACHE_TEMPLATE.unlink(key1, key2);
    }

    /**
     * Test ttl.
     */
    public void testTtl() {
        String key = "testTtl";

        CACHE_TEMPLATE.set(key, "value");
        assertEquals(-1, CACHE_TEMPLATE.ttl(key));

        CACHE_TEMPLATE.expire(key, 5, TimeUnit.SECONDS);
        assertTrue(CACHE_TEMPLATE.ttl(key) > 0);

        assertTrue(CACHE_TEMPLATE.ttlAsync(key).join() > 0);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test p ttl.
     */
    public void testPTtl() {
        String key = "testPTtl";

        CACHE_TEMPLATE.set(key, "value");
        assertEquals(-1, CACHE_TEMPLATE.pTTL(key));

        CACHE_TEMPLATE.expire(key, 5, TimeUnit.SECONDS);
        assertTrue(CACHE_TEMPLATE.pTTL(key) > 0);

        assertTrue(CACHE_TEMPLATE.pTTLAsync(key).join() > 0);

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test scan.
     */
    public void testScan() {
        String prefix = "testScan:";

        for (int i = 0; i < 10; i++) {
            CACHE_TEMPLATE.set(prefix + i, "value" + i);
        }

        Iterator<String> iterator = CACHE_TEMPLATE.scan(prefix + "*").iterator();
        int count = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            assertTrue(key.startsWith(prefix));
            count++;
        }
        assertEquals(10, count);

        Iterator<String> iterator2 = CACHE_TEMPLATE.scan(prefix + "*", 5).iterator();
        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2++;
        }
        assertEquals(10, count2);

        for (int i = 0; i < 10; i++) {
            CACHE_TEMPLATE.del(prefix + i);
        }
    }

    /**
     * Test type.
     */
    public void testType() {
        String key = "testType";

        CACHE_TEMPLATE.set(key, "value");
        assertEquals(KeyType.STRING, CACHE_TEMPLATE.type(key));

        assertEquals(KeyType.STRING, CACHE_TEMPLATE.typeAsync(key).join());

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.hSet(key, "field", "value");
        assertEquals(KeyType.HASH, CACHE_TEMPLATE.type(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.rPush(key, "value");
        assertEquals(KeyType.LIST, CACHE_TEMPLATE.type(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.sAdd(key, "value");
        assertEquals(KeyType.SET, CACHE_TEMPLATE.type(key));

        CACHE_TEMPLATE.del(key);

        CACHE_TEMPLATE.zAdd(key, 1.0, "value");
        assertEquals(KeyType.ZSET, CACHE_TEMPLATE.type(key));

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try set rate limiter.
     */
    public void testTrySetRateLimiter() {
        String key = "testTrySetRateLimiter";

        assertTrue(CACHE_TEMPLATE.trySetRateLimiter(key, 10, 60));

        assertFalse(CACHE_TEMPLATE.trySetRateLimiterAsync(key, 10, 60).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test try acquire.
     */
    public void testTryAcquire() throws InterruptedException {
        String key = "testTryAcquire";

        assertTrue(CACHE_TEMPLATE.trySetRateLimiter(key, 3, 3));

        assertTrue(CACHE_TEMPLATE.tryAcquire(key));
        assertTrue(CACHE_TEMPLATE.tryAcquire(key));
        assertTrue(CACHE_TEMPLATE.tryAcquire(key));
        assertFalse(CACHE_TEMPLATE.tryAcquire(key));

        Thread.sleep(3000);

        assertTrue(CACHE_TEMPLATE.tryAcquireAsync(key).join());
        assertTrue(CACHE_TEMPLATE.tryAcquireAsync(key).join());
        assertTrue(CACHE_TEMPLATE.tryAcquireAsync(key).join());
        assertFalse(CACHE_TEMPLATE.tryAcquireAsync(key).join());

        Thread.sleep(3000);

        assertTrue(CACHE_TEMPLATE.tryAcquire(key, 2));
        assertFalse(CACHE_TEMPLATE.tryAcquire(key, 2));

        Thread.sleep(3000);

        assertTrue(CACHE_TEMPLATE.tryAcquireAsync(key, 2).join());
        assertFalse(CACHE_TEMPLATE.tryAcquireAsync(key, 2).join());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test pipeline.
     */
    public void testPipeline() {
        String key1 = "testPipeline1";
        String key2 = "testPipeline2";

        CACHE_TEMPLATE.pipeline(batch -> {
            batch.getString().setAsync(key1, "value1");
            batch.getString().setAsync(key2, "value2");
        });

        assertEquals("value1", CACHE_TEMPLATE.get(key1).get());
        assertEquals("value2", CACHE_TEMPLATE.get(key2).get());

        List<?> responses = CACHE_TEMPLATE.pipelineWithResponses(batch -> {
            batch.getString().getAsync(key1);
            batch.getString().getAsync(key2);
        });

        assertEquals("value1", responses.get(0));
        assertEquals("value2", responses.get(1));

        CACHE_TEMPLATE.pipelineAsync(batch -> {
            batch.getGeneric().delAsync(key1, key2);
        }).join();

        assertFalse(CACHE_TEMPLATE.get(key1).isPresent());
        assertFalse(CACHE_TEMPLATE.get(key2).isPresent());
    }

    /**
     * Test create batch.
     */
    public void testCreateBatch() {
        KBatch batch = CACHE_TEMPLATE.createBatch();
        assertNotNull(batch);
    }

    /**
     * Test get data source.
     */
    public void testGetDataSource() {
        RedissonClient client = ((DefaultRedissonTemplate) CACHE_TEMPLATE).getDataSource();
        assertNotNull(client);
    }

    /**
     * Test get connection info.
     */
    public void testGetConnectionInfo() {
        DefaultRedissonTemplate template = (DefaultRedissonTemplate) CACHE_TEMPLATE;
        String info = template.getConnectionInfo();
        assertNotNull(info);
    }

    /**
     * Test is invoke params print.
     */
    public void testIsInvokeParamsPrint() {
        DefaultRedissonTemplate template = (DefaultRedissonTemplate) CACHE_TEMPLATE;
        boolean print = template.isInvokeParamsPrint();
        assertTrue(print);
    }

    /**
     * Test setNX - set if not exists without expiration.
     */
    public void testSetNX() {
        String key = "testSetNX";

        // Test successful setNX on non-existent key
        assertTrue(CACHE_TEMPLATE.setNX(key, "value1"));
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        // Test failed setNX on existing key
        assertFalse(CACHE_TEMPLATE.setNX(key, "value2"));
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test setNX with duration - set if not exists with expiration.
     */
    public void testSetNXWithDuration() {
        String key = "testSetNXWithDuration";
        Duration duration = Duration.ofSeconds(2);

        // Test successful setNX with duration on non-existent key
        assertTrue(CACHE_TEMPLATE.setNX(key, "value1", duration));
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        // Test failed setNX with duration on existing key
        assertFalse(CACHE_TEMPLATE.setNX(key, "value2", duration));
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        // Wait for expiration
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertFalse(CACHE_TEMPLATE.get(key).isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test setNXAsync - async set if not exists without expiration.
     */
    public void testSetNXAsync() {
        String key = "testSetNXAsync";

        // Test successful setNXAsync on non-existent key
        assertTrue(CACHE_TEMPLATE.setNXAsync(key, "value1").join());
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        // Test failed setNXAsync on existing key
        assertFalse(CACHE_TEMPLATE.setNXAsync(key, "value2").join());
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test setNXAsync with duration - async set if not exists with expiration.
     */
    public void testSetNXAsyncWithDuration() {
        String key = "testSetNXAsyncWithDuration";
        Duration duration = Duration.ofSeconds(2);

        // Test successful setNXAsync with duration on non-existent key
        assertTrue(CACHE_TEMPLATE.setNXAsync(key, "value1", duration).join());
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        // Test failed setNXAsync with duration on existing key
        assertFalse(CACHE_TEMPLATE.setNXAsync(key, "value2", duration).join());
        assertEquals("value1", CACHE_TEMPLATE.get(key).get());

        // Wait for expiration
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertFalse(CACHE_TEMPLATE.get(key).isPresent());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test executeScript with ReturnType - execute Lua script with specified return type.
     */
    public void testExecuteScriptWithReturnType() throws NoSuchAlgorithmException {
        String key = "testExecuteScriptWithReturnType";

        CACHE_TEMPLATE.set(key, "testValue");

        // Test script that returns a string
        String script = "return redis.call('GET', KEYS[1])";
        List<Object> keys = Arrays.asList(key);

        Optional<Object> result = CACHE_TEMPLATE.executeScript(KScript.ReturnType.VALUE, script, keys);
        assertTrue(result.isPresent());
        assertEquals("testValue", result.get());

        // Test script with values parameter
        String scriptWithValues = "return redis.call('SET', KEYS[1], ARGV[1])";
        Optional<Object> setResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.VALUE, scriptWithValues, keys, "newValue");
        assertTrue(setResult.isPresent());
        assertEquals("newValue", CACHE_TEMPLATE.get(key).get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test executeScript with ReturnType and multiple keys/values.
     */
    public void testExecuteScriptWithMultipleKeysAndValues() throws NoSuchAlgorithmException {
        String key1 = "testExecuteScriptKey1";
        String key2 = "testExecuteScriptKey2";

        CACHE_TEMPLATE.set(key1, "value1");
        CACHE_TEMPLATE.set(key2, "value2");

        // Test script with multiple keys and values
        String script = "return {redis.call('GET', KEYS[1]), redis.call('GET', KEYS[2]), ARGV[1], ARGV[2]}";
        List<Object> keys = Arrays.asList(key1, key2);

        Optional<Object> result = CACHE_TEMPLATE.executeScript(KScript.ReturnType.MULTI, script, keys, "arg1", "arg2");
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof List);

        CACHE_TEMPLATE.del(key1, key2);
    }

    /**
     * Test executeScript with different ReturnType values.
     */
    public void testExecuteScriptDifferentReturnTypes() throws NoSuchAlgorithmException {
        String key = "testExecuteScriptReturnTypes";

        // Test with INTEGER return type
        CACHE_TEMPLATE.set(key, "42");
        String intScript = "return tonumber(redis.call('GET', KEYS[1]))";
        List<Object> keys = Arrays.asList(key);

        Optional<Object> intResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.INTEGER, intScript, keys);
        assertTrue(intResult.isPresent());
        assertEquals(42, Long.parseLong(intResult.get().toString()));

        // Test with BOOLEAN return type
        String boolScript = "return redis.call('EXISTS', KEYS[1]) == 1";
        Optional<Object> boolResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.BOOLEAN, boolScript, keys);
        assertTrue(boolResult.isPresent());
        assertEquals(true, boolResult.get());

        // Test with STATUS return type
        String statusScript = "return redis.call('SET', KEYS[1], 'test')";
        Optional<Object> statusResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.STATUS, statusScript, keys);
        assertTrue(statusResult.isPresent());
        assertEquals("OK", statusResult.get());

        CACHE_TEMPLATE.del(key);
    }

    /**
     * Test executeScript with MAPVALUE return type.
     */
//    public void testExecuteScriptWithMAPVALUEReturnType() throws NoSuchAlgorithmException {
//        String hashKey = "testExecuteScriptMAPVALUE";
//
//        // Setup hash data
//        CACHE_TEMPLATE.hSet(hashKey, "field1", "value1");
//        CACHE_TEMPLATE.hSet(hashKey, "field2", "value2");
//        CACHE_TEMPLATE.hSet(hashKey, "field3", "value3");
//
//        // Test script that returns hash data as map
//        String script = "return redis.call('HGETALL', KEYS[1])";
//        List<Object> keys = Arrays.asList(hashKey);
//
//        Optional<Object> result = CACHE_TEMPLATE.executeScript(KScript.ReturnType.MAPVALUE, script, keys);
//        assertTrue(result.isPresent());
//
//        // Verify the result contains the expected map structure
//        Object resultObj = result.get();
//        assertNotNull(resultObj);
//
//        CACHE_TEMPLATE.del(hashKey);
//    }

    /**
     * Test executeScript with MAPVALUELIST return type.
     */
    public void testExecuteScriptWithMAPVALUELISTReturnType() throws NoSuchAlgorithmException {
        String hashKey1 = "testExecuteScriptMAPVALUELIST1";
        String hashKey2 = "testExecuteScriptMAPVALUELIST2";

        // Setup multiple hash data
        CACHE_TEMPLATE.hSet(hashKey1, "field1", "value1");
        CACHE_TEMPLATE.hSet(hashKey1, "field2", "value2");

        CACHE_TEMPLATE.hSet(hashKey2, "fieldA", "valueA");
        CACHE_TEMPLATE.hSet(hashKey2, "fieldB", "valueB");

        // Test script that returns multiple hash maps
        String script = "return {redis.call('HGETALL', KEYS[1]), redis.call('HGETALL', KEYS[2])}";
        List<Object> keys = Arrays.asList(hashKey1, hashKey2);

        Optional<Object> result = CACHE_TEMPLATE.executeScript(KScript.ReturnType.MAPVALUELIST, script, keys);
        assertTrue(result.isPresent());

        // Verify the result contains a list of maps
        Object resultObj = result.get();
        assertNotNull(resultObj);

        CACHE_TEMPLATE.del(hashKey1, hashKey2);
    }

    /**
     * Enhanced testExecuteScriptDifferentReturnTypes with MAPVALUE and MAPVALUELIST.
     */
    public void testExecuteScriptAllDifferentReturnTypes() throws NoSuchAlgorithmException {
        String key = "testExecuteScriptReturnTypes";
        String hashKey = "testExecuteScriptReturnTypesHash";

        // Test with INTEGER return type
        CACHE_TEMPLATE.set(key, "42");
        String intScript = "return tonumber(redis.call('GET', KEYS[1]))";
        List<Object> keys = Arrays.asList(key);

        Optional<Object> intResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.INTEGER, intScript, keys);
        assertTrue(intResult.isPresent());

        // Test with BOOLEAN return type
        String boolScript = "return redis.call('EXISTS', KEYS[1]) == 1";
        Optional<Object> boolResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.BOOLEAN, boolScript, keys);
        assertTrue(boolResult.isPresent());
        assertEquals(true, boolResult.get());

        // Test with STATUS return type
        String statusScript = "return redis.call('SET', KEYS[1], 'test')";
        Optional<Object> statusResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.STATUS, statusScript, keys);
        assertTrue(statusResult.isPresent());

        // Test with MAPVALUE return type
        CACHE_TEMPLATE.hSet(hashKey, "field1", "value1");
        CACHE_TEMPLATE.hSet(hashKey, "field2", "value2");

//        String mapScript = "return redis.call('HGETALL', KEYS[1])";
//        List<Object> hashKeys = Arrays.asList(hashKey);
//        Optional<Object> mapResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.MAPVALUE, mapScript, hashKeys);
//        assertTrue(mapResult.isPresent());
//        assertNotNull(mapResult.get());

        // Test with MAPVALUELIST return type
        String hashKey2 = "testExecuteScriptReturnTypesHash2";
        CACHE_TEMPLATE.hSet(hashKey2, "fieldA", "valueA");

        String mapListScript = "return {redis.call('HGETALL', KEYS[1]), redis.call('HGETALL', KEYS[2])}";
        List<Object> multiHashKeys = Arrays.asList(hashKey, hashKey2);
        Optional<Object> mapListResult = CACHE_TEMPLATE.executeScript(KScript.ReturnType.MAPVALUELIST, mapListScript, multiHashKeys);
        assertTrue(mapListResult.isPresent());
        assertNotNull(mapListResult.get());

        CACHE_TEMPLATE.del(key, hashKey, hashKey2);
    }


}
