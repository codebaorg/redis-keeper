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

package org.codeba.redis.keeper.spring;


import org.codeba.redis.keeper.core.CacheDatasourceStatus;
import org.codeba.redis.keeper.core.CacheTemplate;
import org.codeba.redis.keeper.core.CacheTemplateProvider;
import org.codeba.redis.keeper.core.Provider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

/**
 * The type Cache template proxy.
 *
 * @author codeba
 */
public class CacheTemplateProxy implements InvocationHandler {
    private String datasource;
    private CacheDatasourceStatus status;
    private CacheTemplateProvider<?> templateProvider;

    /**
     * Instantiates a new Cache template proxy.
     *
     * @param datasource the datasource
     */
    public CacheTemplateProxy(String datasource) {
        this.datasource = datasource;
    }

    /**
     * Instantiates a new Cache template proxy.
     *
     * @param datasource the datasource
     * @param status     the status
     */
    public CacheTemplateProxy(String datasource, CacheDatasourceStatus status) {
        this.datasource = datasource;
        this.status = status;
    }

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
        return (T) Proxy.newProxyInstance(
                templateClass.getClassLoader(),
                new Class[]{templateClass},
                new CacheTemplateProxy(datasource));
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
        return (T) Proxy.newProxyInstance(
                templateClass.getClassLoader(),
                new Class[]{templateClass},
                new CacheTemplateProxy(datasource, status));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String key = Provider.keyWithStatus(this.datasource, this.status);
        final Optional<?> template = getTemplateProvider().getTemplate(key);
        return method.invoke(template.get(), args);
    }

    /**
     * Gets template provider.
     *
     * @return the template provider
     */
    private CacheTemplateProvider<?> getTemplateProvider() {
        if (this.templateProvider == null) {
            templateProvider = ApplicationContextUtil.getBean(CacheTemplateProvider.class);
        }
        return this.templateProvider;
    }


}
