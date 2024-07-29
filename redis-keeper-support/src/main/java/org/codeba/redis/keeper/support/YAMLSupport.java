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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.netty.channel.EventLoopGroup;
import org.redisson.api.NameMapper;
import org.redisson.api.NatMapper;
import org.redisson.api.RedissonNodeInitializer;
import org.redisson.client.NettyHook;
import org.redisson.client.codec.Codec;
import org.redisson.codec.ReferenceCodecProvider;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.ReplicatedServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.connection.AddressResolverGroupFactory;
import org.redisson.connection.ConnectionListener;
import org.redisson.connection.balancer.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type support.
 *
 * @author codeba
 */
public class YAMLSupport {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * The type Ignore mix in.
     */
    @JsonIgnoreType
    public static class IgnoreMixIn {

    }

    /**
     * The type Class mix in.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    @JsonFilter("classFilter")
    public static class ClassMixIn {

    }

    /**
     * The type Config mix in.
     */
    @JsonIgnoreProperties({"clusterConfig", "sentinelConfig"})
    public static class ConfigMixIn {

        /**
         * The Sentinel servers config.
         */
        @JsonProperty
        SentinelServersConfig sentinelServersConfig;

        /**
         * The Master slave servers config.
         */
        @JsonProperty
        MasterSlaveServersConfig masterSlaveServersConfig;

        /**
         * The Single server config.
         */
        @JsonProperty
        SingleServerConfig singleServerConfig;

        /**
         * The Cluster servers config.
         */
        @JsonProperty
        ClusterServersConfig clusterServersConfig;

        /**
         * The Replicated servers config.
         */
        @JsonProperty
        ReplicatedServersConfig replicatedServersConfig;

    }

    private ObjectMapper yamlMapper = createMapper(new YAMLFactory(), null);

    /**
     * From yaml t.
     *
     * @param <T>        the type parameter
     * @param content    the content
     * @param configType the config type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T fromYAML(String content, Class<T> configType) throws IOException {
        content = resolveEnvParams(content);
        return yamlMapper.readValue(content, configType);
    }

    /**
     * From yaml t.
     *
     * @param <T>        the type parameter
     * @param file       the file
     * @param configType the config type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T fromYAML(File file, Class<T> configType) throws IOException {
        return fromYAML(file, configType, null);
    }

    /**
     * From yaml t.
     *
     * @param <T>         the type parameter
     * @param file        the file
     * @param configType  the config type
     * @param classLoader the class loader
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T fromYAML(File file, Class<T> configType, ClassLoader classLoader) throws IOException {
        yamlMapper = createMapper(new YAMLFactory(), classLoader);
        String content = resolveEnvParams(new FileReader(file));
        return yamlMapper.readValue(content, configType);
    }

    /**
     * From yaml t.
     *
     * @param <T>        the type parameter
     * @param url        the url
     * @param configType the config type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T fromYAML(URL url, Class<T> configType) throws IOException {
        String content = resolveEnvParams(new InputStreamReader(url.openStream()));
        return yamlMapper.readValue(content, configType);
    }

    /**
     * From yaml t.
     *
     * @param <T>        the type parameter
     * @param reader     the reader
     * @param configType the config type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T fromYAML(Reader reader, Class<T> configType) throws IOException {
        String content = resolveEnvParams(reader);
        return yamlMapper.readValue(content, configType);
    }

    /**
     * From yaml t.
     *
     * @param <T>         the type parameter
     * @param inputStream the input stream
     * @param configType  the config type
     * @return the t
     * @throws IOException the io exception
     */
    public <T> T fromYAML(InputStream inputStream, Class<T> configType) throws IOException {
        String content = resolveEnvParams(new InputStreamReader(inputStream));
        return yamlMapper.readValue(content, configType);
    }

    private String resolveEnvParams(Readable in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        try {
            if (s.hasNext()) {
                return resolveEnvParams(s.next());
            }
            return "";
        } finally {
            s.close();
        }
    }

    private String resolveEnvParams(String content) {
        Pattern pattern = Pattern.compile("\\$\\{([\\w\\.]+(:-.+?)?)\\}");
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            String[] parts = m.group(1).split(":-");
            String v = System.getenv(parts[0]);
            if (v == null) {
                v = System.getProperty(parts[0]);
            }
            if (v != null) {
                content = content.replace(m.group(), v);
            } else if (parts.length == 2) {
                content = content.replace(m.group(), parts[1]);
            }
        }
        return content;
    }

    private ObjectMapper createMapper(JsonFactory mapping, ClassLoader classLoader) {
        ObjectMapper mapper = new ObjectMapper(mapping);

        mapper.addMixIn(Config.class, ConfigSupport.ConfigMixIn.class);
        mapper.addMixIn(ReferenceCodecProvider.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(AddressResolverGroupFactory.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(Codec.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(RedissonNodeInitializer.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(LoadBalancer.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(NatMapper.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(NameMapper.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(NettyHook.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(EventLoopGroup.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(ConnectionListener.class, ConfigSupport.ClassMixIn.class);
        mapper.addMixIn(ExecutorService.class, ConfigSupport.ClassMixIn.class);

        try {
            final Class<?> resolver = Class.forName("org.redisson.config.CredentialsResolver");
            final Class<?> mixIn = Class.forName("org.redisson.config.ConfigSupport$ClassMixIn");
            mapper.addMixIn(resolver, mixIn);
        } catch (Exception e) {
            log.warn("org.codeba.redis.keeper.support.YAMLSupport.createMapper(JsonFactory mapping, ClassLoader classLoader) addMixIn", e);
        }
        try {
            final Class<?> factory = Class.forName("javax.net.ssl.KeyManagerFactory");
            final Class<?> ignoreMixIn = Class.forName("org.redisson.config.ConfigSupport$IgnoreMixIn");
            mapper.addMixIn(factory, ignoreMixIn);
        } catch (Exception e) {
            log.warn("org.codeba.redis.keeper.support.YAMLSupport.createMapper(JsonFactory mapping, ClassLoader classLoader) addMixIn", e);
        }
        try {
            final Class<?> factory = Class.forName("javax.net.ssl.TrustManagerFactory");
            final Class<?> ignoreMixIn = Class.forName("org.redisson.config.ConfigSupport$IgnoreMixIn");
            mapper.addMixIn(factory, ignoreMixIn);
        } catch (Exception e) {
            log.warn("org.codeba.redis.keeper.support.YAMLSupport.createMapper(JsonFactory mapping, ClassLoader classLoader) addMixIn", e);
        }
        try {
            final Class<?> command = Class.forName("org.redisson.config.CommandMapper");
            final Class<?> mixIn = Class.forName("org.redisson.config.ConfigSupport$ClassMixIn");
            mapper.addMixIn(command, mixIn);
        } catch (Exception e) {
            log.warn("org.codeba.redis.keeper.support.YAMLSupport.createMapper(JsonFactory mapping, ClassLoader classLoader) addMixIn", e);
        }

        FilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("classFilter", SimpleBeanPropertyFilter.filterOutAllExcept());
        mapper.setFilterProvider(filterProvider);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        if (classLoader != null) {
            TypeFactory tf = TypeFactory.defaultInstance()
                    .withClassLoader(classLoader);
            mapper.setTypeFactory(tf);
        }

        return mapper;
    }

}
