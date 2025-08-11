package org.codeba.redis.keeper.spring;


import com.github.benmanes.caffeine.cache.Cache;
import org.codeba.redis.keeper.core.LocalCache;

import java.util.Map;
import java.util.Objects;

/**
 * The type Local cache manager.
 *
 * @author codeba
 */
public class LocalCacheManager implements LocalCache<Object, Object> {

    private final Map<String, Cache<Object, Object>> cacheManager;

    /**
     * Instantiates a new Local cache manager.
     *
     * @param cacheManager the cache manager
     */
    public LocalCacheManager(Map<String, Cache<Object, Object>> cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Object getNativeCache(String name) {
        return getCacheByName(name);
    }

    @Override
    public Object getIfPresent(String name, Object key) {
        final Cache<Object, Object> cache = getCacheByName(name);
        return cache.getIfPresent(key);
    }

    @Override
    public Map<Object, Object> getAllPresent(String name, Iterable<?> keys) {
        final Cache<Object, Object> cache = getCacheByName(name);
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(String name, Object key, Object value) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.put(key, value);
    }

    @Override
    public void invalidate(String name, Object key) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(String name, Iterable<?> keys) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.invalidateAll();
    }

    @Override
    public void invalidateAll(String name) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.invalidateAll();
    }

    private Cache<Object, Object> getCacheByName(String name) {
        final Cache<Object, Object> cache = cacheManager.get(name);
        Objects.requireNonNull(cache, "cacheManager.getCache(" + name + ") must not be null.");
        return cache;
    }
}
