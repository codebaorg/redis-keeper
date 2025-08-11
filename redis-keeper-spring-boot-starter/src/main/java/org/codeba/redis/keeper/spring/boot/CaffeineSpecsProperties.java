package org.codeba.redis.keeper.spring.boot;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author codeba
 *
 */
@Data
@ConfigurationProperties(prefix = "redis-keeper.cache.caffeine")
public class CaffeineSpecsProperties {
    /**
     * key = cacheName, value = CaffeineSpec 字符串
     */
    private Map<String, String> specs = new HashMap<>();
}
