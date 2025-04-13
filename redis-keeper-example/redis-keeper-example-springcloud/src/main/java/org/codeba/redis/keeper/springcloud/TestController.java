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

package org.codeba.redis.keeper.springcloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The type Test controller.
 *
 * @author codeba
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    /**
     * The Provider.
     */
    private final CacheTemplateProvider<CacheTemplate> provider;

    /**
     * Test refresh.
     */
    @RequestMapping("/refresh")
    public boolean testRefresh() {
        final long start = System.currentTimeMillis();

        final CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100000; i++) {
                final Optional<CacheTemplate> templateOptional = provider.getTemplate("ds4");
                templateOptional.ifPresent(cacheTemplate -> {
                    cacheTemplate.incr("testRefresh");
                });
            }
        });
        final CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100000; i++) {
                final Optional<CacheTemplate> templateOptional = provider.getTemplate("ds4");
                templateOptional.ifPresent(cacheTemplate -> {
                    cacheTemplate.incr("testRefresh");
                });
            }
        });
        final CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100000; i++) {
                final Optional<CacheTemplate> templateOptional = provider.getTemplate("ds4");
                templateOptional.ifPresent(cacheTemplate -> {
                    cacheTemplate.incr("testRefresh");
                });
            }
        });

        CompletableFuture.allOf(f1, f2, f3).join();
        log.info("cost time: {}", (System.currentTimeMillis() - start));

        return true;
    }


}
