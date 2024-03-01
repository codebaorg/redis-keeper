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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The type Application context util.
 *
 * @author codeba
 */
public class ApplicationContextUtil implements ApplicationContextAware {

    /**
     * The constant applicationContext.
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }

    /**
     * Gets bean.
     *
     * @param <T>    the type parameter
     * @param name   the name
     * @param tClass the t class
     * @return the bean
     * @throws BeansException the beans exception
     */
    public static <T> T getBean(String name, Class<T> tClass) throws BeansException {
        return applicationContext.getBean(name, tClass);
    }

    /**
     * Gets bean.
     *
     * @param <T>    the type parameter
     * @param tClass the t class
     * @return the bean
     * @throws BeansException the beans exception
     */
    public static <T> T getBean(Class<T> tClass) throws BeansException {
        return applicationContext.getBean(tClass);
    }

}
