package org.codeba.redis.keeper.spring;


import com.github.benmanes.caffeine.cache.Cache;
import org.codeba.redis.keeper.core.LocalCache;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

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
    public Object get(String name, Object key, Function<? super Object, ?> mappingFunction) {
        final Cache<Object, Object> cache = getCacheByName(name);
        return cache.get(key, mappingFunction);
    }

    @Override
    public Map<Object, Object> getAllPresent(String name, Iterable<?> keys) {
        final Cache<Object, Object> cache = getCacheByName(name);
        return cache.getAllPresent(keys);
    }

    @Override
    public Map<Object, Object> getAll(String name, Iterable<?> keys, Function<? super Set<?>, ? extends Map<?, ?>> mappingFunction) {
        final Cache<Object, Object> cache = getCacheByName(name);
        return cache.getAll(keys, mappingFunction);
    }

    @Override
    public void put(String name, Object key, Object value) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.put(key, value);
    }

    @Override
    public void putAll(String name, Map<?, ?> map) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.putAll(map);
    }

    @Override
    public void invalidate(String name, Object key) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(String name, Iterable<?> keys) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll(String name) {
        final Cache<Object, Object> cache = getCacheByName(name);
        cache.invalidateAll();
    }

    @Override
    public ConcurrentMap<Object, Object> asMap(String name) {
        final Cache<Object, Object> cache = getCacheByName(name);
        return cache.asMap();
    }

    private Cache<Object, Object> getCacheByName(String name) {
        final Cache<Object, Object> cache = cacheManager.get(name);
        Objects.requireNonNull(cache, "cacheManager.getCache(" + name + ") must not be null.");
        return cache;
    }
}
