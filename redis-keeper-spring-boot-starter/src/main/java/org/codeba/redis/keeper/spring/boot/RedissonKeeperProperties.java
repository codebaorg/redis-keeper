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

package org.codeba.redis.keeper.spring.boot;

import lombok.Data;
import org.codeba.redis.keeper.core.CacheDatasourceStatus;


/**
 * The type Redisson keeper properties.
 *
 * @author codeba
 */
@Data
public class RedissonKeeperProperties {
    /**
     * The Status is the identity of the data source, you can specify the identity through the cacheProvider class to get the corresponding data source
     * <p>
     * Value range: RO, WO, RW, SKIP
     * RO: Read-only cache datasource status
     * WO: Write-only cache datasource status.
     * RW: Read-write cache datasource status.
     * SKIP: Skip cache datasource status.
     */
    private String status = CacheDatasourceStatus.RW.name();
    /**
     * The Invoke params print, means whether to print an entry log when executing the methods of the cacheTemplate class.
     * <p>
     * true : print entry log
     * false: not print entry log
     */
    private boolean invokeParamsPrint;
    /**
     * The Config, the same as redisson properties.
     */
    private String config;
    /**
     * The File, the same as redisson properties.
     */
    private String file;
}
