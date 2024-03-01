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

package org.codeba.redis.keeper.example.standalone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.support.CacheKeeperConfig;
import org.codeba.redis.keeper.support.CacheKeeperProperties;
import org.codeba.redis.keeper.support.DefaultCacheDatasource;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
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

    private static CacheTemplateProvider<CacheTemplate> PROVIDER;

    static {
        // todo please set your address and password
        String yourAddress = "redis://localhost:6379";
        String yourPass = "yourPass";

        // method 1
//        initProvider1(yourAddress, yourPass);

        // method 2
         initProvider2(yourAddress, yourPass);

    }

    private static void initProvider1(String yourAddress, String yourPass) {
        String properties = "redisKeeper:\n" +
                "  redisson:\n" +
                "    datasource:\n" +
                "      ds1:\n" +
                "        invokeParamsPrint: true\n" +
                "        config:\n" +
                "          singleServerConfig:\n" +
                "            address: %1$s\n" +
                "            password: %2$s\n" +
                "    \n" +
                "    datasources:\n" +
                "      ds2:\n" +
                "        - invokeParamsPrint: true\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              database: 1\n" +
                "              password: %2$s\n" +
                "\n" +
                "        - invokeParamsPrint: true\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              database: 2\n" +
                "              password: %2$s\n" +
                "\n" +
                "        - invokeParamsPrint: true\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              database: 3\n" +
                "              password: %2$s\n" +
                "      \n" +
                "      ds3:\n" +
                "        - invokeParamsPrint: true\n" +
                "          status: RO\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              database: 1\n" +
                "              password: %2$s\n" +
                "\n" +
                "        - invokeParamsPrint: true\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              database: 2\n" +
                "              password: %2$s\n" +
                "\n" +
                "        - invokeParamsPrint: true\n" +
                "          status: RO\n" +
                "          config:\n" +
                "            singleServerConfig:\n" +
                "              address: %1$s\n" +
                "              database: 3\n" +
                "              password: %2$s";

        final String format = String.format(properties, yourAddress, yourPass);

        final CacheKeeperProperties fromYAML;
        try {
            fromYAML = CacheKeeperProperties.fromYAML(format);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final CacheKeeperProperties.Redisson redisson = fromYAML.getRedisKeeper().getRedisson();

        final DefaultCacheDatasource datasource = new DefaultCacheDatasource();
        datasource.configPostProcessor(v -> v.getConfig().setCodec(new JsonJacksonCodec()));
        final Map<String, CacheTemplate> dsMap = datasource.initialize(redisson.getDatasource());
        final Map<String, List<CacheTemplate>> dssMap = datasource.initializeMulti(redisson.getDatasources());

        PROVIDER = new CacheTemplateProvider<>(dsMap, dssMap);
    }

    private static void initProvider2(String yourAddress, String yourPass) {
        Config config = new Config();
        config.useSingleServer().setAddress(yourAddress).setPassword(yourPass);

        Config config1 = new Config();
        config1.useSingleServer().setAddress(yourAddress).setPassword(yourPass).setDatabase(1);

        Config config2 = new Config();
        config2.useSingleServer().setAddress(yourAddress).setPassword(yourPass).setDatabase(2);

        Config config3 = new Config();
        config3.useSingleServer().setAddress(yourAddress).setPassword(yourPass).setDatabase(3);

        final Map<String, CacheKeeperConfig> ds1 = new HashMap<String, CacheKeeperConfig>() {{
            put("ds1", new CacheKeeperConfig(config, true));
        }};

        final HashMap<String, List<CacheKeeperConfig>> ds23 = new HashMap<String, List<CacheKeeperConfig>>() {{
            putIfAbsent("ds2",
                    Arrays.asList(
                            new CacheKeeperConfig(config1, true),
                            new CacheKeeperConfig(config2, true),
                            new CacheKeeperConfig(config3, true)
                    )
            );
            putIfAbsent("ds3",
                    Arrays.asList(
                            new CacheKeeperConfig(CacheDatasourceStatus.RO.name(), config1, true),
                            new CacheKeeperConfig(config2, true),
                            new CacheKeeperConfig(CacheDatasourceStatus.RO.name(), config3, true)
                    )
            );
        }};

        final DefaultCacheDatasource datasource = new DefaultCacheDatasource();
        datasource.configPostProcessor(v -> v.getConfig().setCodec(new JsonJacksonCodec()));
        final Map<String, CacheTemplate> dsMap = datasource.initialize(ds1);
        final Map<String, List<CacheTemplate>> dssMap = datasource.initializeMulti(ds23);

        PROVIDER = new CacheTemplateProvider<>(dsMap, dssMap);
    }

    /**
     * Test.
     */
    public void test() {
        final Optional<CacheTemplate> templateOptional = PROVIDER.getTemplate("ds1");

        if (templateOptional.isPresent()) {
            final CacheTemplate cacheTemplate = templateOptional.get();

            String key = "foo";
            String value = "bar";
            cacheTemplate.set(key, value);

            final Optional<Object> optional = cacheTemplate.get(key);

            optional.ifPresent(el -> {
                assert value.equals(el);
            });

            cacheTemplate.del(key);

        }

    }

    /**
     * Test template with status.
     */
    public void testTemplateWithStatus() {
        final Optional<CacheTemplate> templateOptional = PROVIDER.getTemplate("ds1", CacheDatasourceStatus.RW);

        if (templateOptional.isPresent()) {
            final CacheTemplate cacheTemplate = templateOptional.get();

            String key = "foo";
            String value = "bar";
            cacheTemplate.set(key, value);

            final Optional<Object> optional = cacheTemplate.get(key);

            optional.ifPresent(el -> {
                assert value.equals(el);
            });

            cacheTemplate.del(key);

        }

        assert !PROVIDER.getTemplate("ds1", CacheDatasourceStatus.SKIP).isPresent();

    }

    /**
     * Test templates with status.
     */
    public void testTemplatesWithStatus() {
        final Collection<CacheTemplate> cacheTemplates = PROVIDER.getTemplates("ds2", CacheDatasourceStatus.RO);

        if (!cacheTemplates.isEmpty()) {
            for (CacheTemplate cacheTemplate : cacheTemplates) {
                String key = "foo";
                String value = "bar";
                cacheTemplate.set(key, value);

                final Optional<Object> optional = cacheTemplate.get(key);

                optional.ifPresent(el -> {
                    assert value.equals(el);
                });

                cacheTemplate.del(key);
            }

        }

        assert PROVIDER.getTemplates("ds2", CacheDatasourceStatus.SKIP).isEmpty();

    }

    /**
     * Test poll template.
     */
    public void testPollTemplate() {
        String key = "foo";

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = PROVIDER.pollTemplate("ds2");
            if (templateOptional.isPresent()) {
                final CacheTemplate template = templateOptional.get();

                final String str = String.valueOf(i);

                template.set(key, str);

                template.get(key).ifPresent(el -> {
                    assert str.equals(el);
                });

                template.del(key);
            }
        }

    }

    /**
     * Test poll template with status.
     */
    public void testPollTemplateWithStatus() {
        String key = "hello";
        final CacheDatasourceStatus status = CacheDatasourceStatus.RO;

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = PROVIDER.pollTemplate("ds3", status);
            if (templateOptional.isPresent()) {
                final CacheTemplate template = templateOptional.get();

                final String str = String.valueOf(i);

                template.set(key, str);

                template.get(key).ifPresent(el -> {
                    assert str.equals(el);
                });

                template.del(key);
            }
        }

    }

    /**
     * Test random template.
     */
    public void testRandomTemplate() {
        String key = "foo";

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = PROVIDER.randomTemplate("ds2");
            if (templateOptional.isPresent()) {
                final CacheTemplate template = templateOptional.get();

                final String str = String.valueOf(i);

                template.set(key, str);

                template.get(key).ifPresent(el -> {
                    assert str.equals(el);
                });

                template.del(key);
            }
        }

    }

    /**
     * Test random template with status.
     */
    public void testRandomTemplateWithStatus() {
        String key = "hello";
        final CacheDatasourceStatus status = CacheDatasourceStatus.RO;

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = PROVIDER.randomTemplate("ds3", status);
            if (templateOptional.isPresent()) {
                final CacheTemplate template = templateOptional.get();

                final String str = String.valueOf(i);

                template.set(key, str);

                template.get(key).ifPresent(el -> {
                    assert str.equals(el);
                });

                template.del(key);
            }
        }

    }


}
