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

package org.codeba.redis.keeper.core;

import java.util.Optional;

/**
 * The interface Load balance provider.
 *
 * @param <T> the type parameter
 * @author codeba
 */
public interface LoadBalanceProvider<T> extends Provider<T> {

    /**
     * Poll template optional.
     *
     * @param name the name
     * @return the optional
     */
    Optional<T> pollTemplate(String name);

    /**
     * Random template optional.
     *
     * @param name the name
     * @return the optional
     */
    Optional<T> randomTemplate(String name);

    /**
     * Poll template optional.
     *
     * @param name   the name
     * @param status the status
     * @return the optional
     */
    Optional<T> pollTemplate(String name, CacheDatasourceStatus status);

    /**
     * Random template optional.
     *
     * @param name   the name
     * @param status the status
     * @return the optional
     */
    Optional<T> randomTemplate(String name, CacheDatasourceStatus status);

}
