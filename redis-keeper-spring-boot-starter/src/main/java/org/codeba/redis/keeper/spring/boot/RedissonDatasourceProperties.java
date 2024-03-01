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

package org.codeba.redis.keeper.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * The type Redisson datasource properties.
 *
 * @author codeba
 */
@Data
@ConfigurationProperties(prefix = "redis-keeper.redisson")
public class RedissonDatasourceProperties {

    private Map<String, RedissonKeeperProperties> datasource;

    private Map<String, List<RedissonKeeperProperties>> datasources;

}
