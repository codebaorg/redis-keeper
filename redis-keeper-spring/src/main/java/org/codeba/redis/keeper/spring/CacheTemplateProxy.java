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

package org.codeba.redis.keeper.spring;


import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.core.Provider;

/**
 * The type Cache template proxy.
 *
 * @author codeba
 */
public class CacheTemplateProxy {

    /**
     * As template cache template.
     *
     * @param datasource the datasource
     * @return the cache template
     */
    public static CacheTemplate asTemplate(final String datasource) {
        return CacheTemplateProxy.asTemplate(datasource, CacheTemplate.class);
    }

    /**
     * As template cache template.
     *
     * @param datasource the datasource
     * @param status     the status
     * @return the cache template
     */
    public static CacheTemplate asTemplate(final String datasource, final CacheDatasourceStatus status) {
        return CacheTemplateProxy.asTemplate(datasource, status, CacheTemplate.class);
    }

    /**
     * As template t.
     *
     * @param <T>           the type parameter
     * @param datasource    the datasource
     * @param templateClass the template class
     * @return the t
     */
    public static <T> T asTemplate(final String datasource, Class<T> templateClass) {
        return (T) getInvokeTemplate(datasource, null);
    }

    /**
     * As template t.
     *
     * @param <T>           the type parameter
     * @param datasource    the datasource
     * @param status        the status
     * @param templateClass the template class
     * @return the t
     */
    public static <T> T asTemplate(final String datasource, final CacheDatasourceStatus status, Class<T> templateClass) {
        return (T) getInvokeTemplate(datasource, status);
    }

    private static Object getInvokeTemplate(final String datasource, final CacheDatasourceStatus status) {
        final CacheTemplateProvider provider = ApplicationContextUtil.getBean(CacheTemplateProvider.class);
        final String key = Provider.keyWithStatus(datasource, status);
        return provider.getTemplate(key).get();
    }

}
