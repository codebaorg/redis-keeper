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

package org.codeba.redis.keeper.core;

/**
 * The interface K bit map.
 */
public interface KBitSet extends KBitSetAsync {
    /**
     * Bit count long.
     *
     * @param key the key
     * @return the long
     */
    long bitCount(String key);

    /**
     * Bit field set signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the long
     */
    long bitFieldSetSigned(String key, int size, long offset, long value);

    /**
     * Bit field set un signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @param value  the value
     * @return the long
     */
    long bitFieldSetUnSigned(String key, int size, long offset, long value);

    /**
     * Bit field get signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the long
     */
    long bitFieldGetSigned(String key, int size, long offset);

    /**
     * Bit field get un signed long.
     *
     * @param key    the key
     * @param size   the size
     * @param offset the offset
     * @return the long
     */
    long bitFieldGetUnSigned(String key, int size, long offset);

    /**
     * Bit op or.
     *
     * @param destKey the dest key
     * @param keys    the keys
     */
    void bitOpOr(String destKey, String... keys);

    /**
     * Gets bit.
     *
     * @param key      the key
     * @param bitIndex the bit index
     * @return the bit
     */
    boolean getBit(String key, long bitIndex);

    /**
     * Sets bit.
     *
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the bit: the original bit value stored at offset.
     */
    boolean setBit(String key, long offset, boolean value);

}
