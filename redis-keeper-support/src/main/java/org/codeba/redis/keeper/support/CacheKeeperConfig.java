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

package org.codeba.redis.keeper.support;

import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.redisson.config.Config;

/**
 * The type Cache keeper config.
 *
 * @author codeba
 */
public class CacheKeeperConfig {
    private String status = CacheDatasourceStatus.RW.name();

    private boolean invokeParamsPrint;

    private Config config;

    /**
     * Instantiates a new Cache keeper config.
     */
    public CacheKeeperConfig() {
    }

    /**
     * Instantiates a new Cache keeper config.
     *
     * @param config the config
     */
    public CacheKeeperConfig(Config config) {
        this.config = config;
    }

    /**
     * Instantiates a new Cache keeper config.
     *
     * @param config            the config
     * @param invokeParamsPrint the invoke params print
     */
    public CacheKeeperConfig(Config config, boolean invokeParamsPrint) {
        this.invokeParamsPrint = invokeParamsPrint;
        this.config = config;
    }

    /**
     * Instantiates a new Cache keeper config.
     *
     * @param status            the status
     * @param config            the config
     * @param invokeParamsPrint the invoke params print
     */
    public CacheKeeperConfig(String status, Config config, boolean invokeParamsPrint) {
        this.status = status;
        this.invokeParamsPrint = invokeParamsPrint;
        this.config = config;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Is invoke params print boolean.
     *
     * @return the boolean
     */
    public boolean isInvokeParamsPrint() {
        return invokeParamsPrint;
    }

    /**
     * Sets invoke params print.
     *
     * @param invokeParamsPrint the invoke params print
     */
    public void setInvokeParamsPrint(boolean invokeParamsPrint) {
        this.invokeParamsPrint = invokeParamsPrint;
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     */
    public void setConfig(Config config) {
        this.config = config;
    }
}
