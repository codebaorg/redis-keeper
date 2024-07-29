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

import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Utils.
 *
 * @author codeba
 */
public class Utils {

    /**
     * The constant REDIS_PROTOCOL_PREFIX.
     */
    public static final String REDIS_PROTOCOL_PREFIX = "redis://";
    /**
     * The constant REDISS_PROTOCOL_PREFIX.
     */
    public static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    /**
     * Convert string [ ].
     *
     * @param nodesObject the nodes object
     * @return the string [ ]
     */
    public static String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(REDIS_PROTOCOL_PREFIX + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }


    /**
     * Gets connection info.
     *
     * @param config the config
     * @return the connection info
     */
    public static String getConnectionInfo(Config config) {
        if (null == config) {
            return "";
        }

        StringBuilder connectInfoBuilder = new StringBuilder();
        if (config.isSentinelConfig()) {
            final SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            final String master = sentinelServersConfig.getMasterName();
            final List<String> nodes = sentinelServersConfig.getSentinelAddresses();
            final int database = sentinelServersConfig.getDatabase();
            connectInfoBuilder.append("Sentinel:{")
                    .append("Master=").append(master).append(",")
                    .append("SentinelAddress=").append(Arrays.toString(nodes.toArray())).append(",")
                    .append("Database=").append(database)
                    .append("}");
        } else if (config.isClusterConfig()) {
            final ClusterServersConfig clusterServersConfig = config.useClusterServers();
            final List<String> nodes = clusterServersConfig.getNodeAddresses();
            connectInfoBuilder.append("ClusterServers:{")
                    .append("NodeAddress=").append(Arrays.toString(nodes.toArray()))
                    .append("}");
        } else {
            final SingleServerConfig singleServerConfig = config.useSingleServer();
            final String address = singleServerConfig.getAddress();
            final int database = singleServerConfig.getDatabase();
            connectInfoBuilder.append("SingleServer:{")
                    .append("Address=").append(address).append(",")
                    .append("Database=").append(database)
                    .append("}");
        }

        return connectInfoBuilder.toString();

    }

    /**
     * Find annotation a.
     *
     * @param <A>              the type parameter
     * @param targetAnnotation the target annotation
     * @param annotatedType    the annotated type
     * @return the a
     */
    public static <A extends Annotation> A findAnnotation(final Class<A> targetAnnotation, final Class<?> annotatedType) {
        A foundAnnotation = annotatedType.getAnnotation(targetAnnotation);
        if (foundAnnotation == null) {
            for (Annotation annotation : annotatedType.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType.isAnnotationPresent(targetAnnotation)) {
                    foundAnnotation = annotationType.getAnnotation(targetAnnotation);
                    break;
                }
            }
        }
        return foundAnnotation;
    }

    /**
     * Is annotation present boolean.
     *
     * @param targetAnnotation the target annotation
     * @param annotatedType    the annotated type
     * @return the boolean
     */
    public static boolean isAnnotationPresent(final Class<? extends Annotation> targetAnnotation, final Class<?> annotatedType) {
        return findAnnotation(targetAnnotation, annotatedType) != null;
    }

}
