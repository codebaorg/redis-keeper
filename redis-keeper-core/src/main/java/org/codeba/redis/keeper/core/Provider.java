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

import java.util.Collection;
import java.util.Optional;

/**
 * The interface Provider.
 *
 * @param <T> the type parameter
 * @author codeba
 */
public interface Provider<T> {

    /**
     * Key with status string.
     *
     * @param name   the name
     * @param status the status
     * @return the string
     */
    static String keyWithStatus(String name, CacheDatasourceStatus status) {
        if (null == status) {
            return name;
        }
        return name + "-" + status;
    }

    /**
     * Gets template.
     *
     * @param name the name
     * @return the template
     */
    Optional<T> getTemplate(String name);

    /**
     * Gets templates.
     *
     * @param name the name
     * @return the templates
     */
    Collection<T> getTemplates(String name);

    /**
     * Gets template.
     *
     * @param name   the name
     * @param status the status
     * @return the template
     */
    Optional<T> getTemplate(String name, CacheDatasourceStatus status);

    /**
     * Gets templates.
     *
     * @param name   the name
     * @param status the status
     * @return the templates
     */
    Collection<T> getTemplates(String name, CacheDatasourceStatus status);

}
