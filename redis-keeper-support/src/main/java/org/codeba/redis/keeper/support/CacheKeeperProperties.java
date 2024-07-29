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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The type Cache keeper properties.
 *
 * @author codeba
 */
public class CacheKeeperProperties implements Serializable {

    private RedisKeeper redisKeeper;

    /**
     * Gets redis keeper.
     *
     * @return the redis keeper
     */
    public RedisKeeper getRedisKeeper() {
        return redisKeeper;
    }

    /**
     * Sets redis keeper.
     *
     * @param redisKeeper the redis keeper
     */
    public void setRedisKeeper(RedisKeeper redisKeeper) {
        this.redisKeeper = redisKeeper;
    }

    /**
     * From yaml cache keeper properties.
     *
     * @param content the content
     * @return the cache keeper properties
     * @throws IOException the io exception
     */
    public static CacheKeeperProperties fromYAML(String content) throws IOException {
        final YAMLSupport support = new YAMLSupport();
        return support.fromYAML(content, CacheKeeperProperties.class);
    }

    /**
     * From yaml cache keeper properties.
     *
     * @param inputStream the input stream
     * @return the cache keeper properties
     * @throws IOException the io exception
     */
    public static CacheKeeperProperties fromYAML(InputStream inputStream) throws IOException {
        final YAMLSupport support = new YAMLSupport();
        return support.fromYAML(inputStream, CacheKeeperProperties.class);
    }

    /**
     * The type Redis keeper.
     */
    public static class RedisKeeper {
        private Redisson redisson;

        /**
         * Gets redisson.
         *
         * @return the redisson
         */
        public Redisson getRedisson() {
            return redisson;
        }

        /**
         * Sets redisson.
         *
         * @param redisson the redisson
         */
        public void setRedisson(Redisson redisson) {
            this.redisson = redisson;
        }
    }

    /**
     * The type Redisson.
     */
    public static class Redisson {
        private Map<String, CacheKeeperConfig> datasource;
        private Map<String, List<CacheKeeperConfig>> datasources;

        /**
         * Gets datasource.
         *
         * @return the datasource
         */
        public Map<String, CacheKeeperConfig> getDatasource() {
            return datasource;
        }

        /**
         * Sets datasource.
         *
         * @param datasource the datasource
         */
        public void setDatasource(Map<String, CacheKeeperConfig> datasource) {
            this.datasource = datasource;
        }

        /**
         * Gets datasources.
         *
         * @return the datasources
         */
        public Map<String, List<CacheKeeperConfig>> getDatasources() {
            return datasources;
        }

        /**
         * Sets datasources.
         *
         * @param datasources the datasources
         */
        public void setDatasources(Map<String, List<CacheKeeperConfig>> datasources) {
            this.datasources = datasources;
        }
    }

}
