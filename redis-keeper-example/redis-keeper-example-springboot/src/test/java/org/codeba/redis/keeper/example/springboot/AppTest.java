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

package org.codeba.redis.keeper.example.springboot;

import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.spring.CacheTemplateProxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.Optional;

/**
 * Unit test for simple ExampleApplication.
 */
@SpringBootTest
public class AppTest {

    @Autowired
    private CacheTemplateProvider<CacheTemplate> provider;

    @Autowired
    private CacheTemplateProvider<MyCacheTemplate> myProvider;

    /**
     * Test.
     */
    @Test
    public void test() {
        final Optional<CacheTemplate> templateOptional = provider.getTemplate("ds1");

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
     * Test my provider.
     */
    @Test
    public void testMyProvider() {
        final Optional<MyCacheTemplate> templateOptional = myProvider.getTemplate("ds1");

        if (templateOptional.isPresent()) {
            final MyCacheTemplate cacheTemplate = templateOptional.get();

            // Custom Methods
            cacheTemplate.test();

        }

    }

    /**
     * Test template with status.
     */
    @Test
    public void testTemplateWithStatus() {
        final Optional<CacheTemplate> templateOptional = provider.getTemplate("ds1", CacheDatasourceStatus.RW);

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

        assert !provider.getTemplate("ds1", CacheDatasourceStatus.SKIP).isPresent();

    }

    /**
     * Test templates with status.
     */
    @Test
    public void testTemplatesWithStatus() {
        final Collection<CacheTemplate> cacheTemplates = provider.getTemplates("ds2", CacheDatasourceStatus.RO);

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

        assert provider.getTemplates("ds2", CacheDatasourceStatus.SKIP).isEmpty();

    }

    /**
     * Test proxy.
     */
    @Test
    public void testProxy() {
        final CacheTemplate cacheTemplate = CacheTemplateProxy.asTemplate("ds1");

        String key = "foo";
        String value = "bar";
        cacheTemplate.set(key, value);

        final Optional<Object> optional = cacheTemplate.get(key);

        optional.ifPresent(el -> {
            assert value.equals(el);
        });

        cacheTemplate.del(key);
    }

    /**
     * Test poll template.
     */
    @Test
    public void testPollTemplate() {
        String key = "foo";

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = provider.pollTemplate("ds2");
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
    @Test
    public void testPollTemplateWithStatus() {
        String key = "hello";
        final CacheDatasourceStatus status = CacheDatasourceStatus.RO;

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = provider.pollTemplate("ds3", status);
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
    @Test
    public void testRandomTemplate() {
        String key = "foo";

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = provider.randomTemplate("ds2");
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
    @Test
    public void testRandomTemplateWithStatus() {
        String key = "hello";
        final CacheDatasourceStatus status = CacheDatasourceStatus.RO;

        for (int i = 1; i <= 10; i++) {
            final Optional<CacheTemplate> templateOptional = provider.randomTemplate("ds3", status);
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
