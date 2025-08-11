package org.codeba.redis.keeper.spring.boot;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.codeba.redis.keeper.spring.LocalCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Local cache autoconfiguration.
 *
 * @author codeba
 */
@Slf4j
@Configuration
@ConditionalOnClass(Caffeine.class)
@EnableConfigurationProperties(CaffeineSpecsProperties.class)
public class LocalCacheAutoConfiguration {

    private final CaffeineSpecsProperties props;

    /**
     * Instantiates a new Local cache auto configuration.
     *
     * @param props the props
     */
    public LocalCacheAutoConfiguration(CaffeineSpecsProperties props) {
        this.props = props;
    }

    /**
     * Cache manager local cache manager.
     *
     * @return the local cache manager
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalCacheManager localCacheManager() {
        final Map<String, String> specsMap = props.getSpecs();
        if (null == specsMap || specsMap.isEmpty()) {
            return new LocalCacheManager(Collections.emptyMap());
        }

        final HashMap<String, Cache<Object, Object>> map = new HashMap<>();
        for (Map.Entry<String, String> entry : specsMap.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (StringUtils.hasText(value)) {
                map.put(key, Caffeine.from(value).build());
            }
        }

        return new LocalCacheManager(map);
    }

}
