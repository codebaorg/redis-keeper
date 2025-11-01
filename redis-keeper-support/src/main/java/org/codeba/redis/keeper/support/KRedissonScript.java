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

import org.codeba.redis.keeper.core.KScript;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

/**
 * The type K redisson script.
 */
class KRedissonScript extends KRedissonScriptAsync implements KScript {
    /**
     * Instantiates a new K redisson script.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonScript(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    @Override
    public Optional<Object> executeScript(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        /* evalSha: 此处必须加上 StringCodec.INSTANCE */
        final RScript rScript = getRScript();
        String shaDigests = sha1DigestAsHex(script);
        try {
            return Optional.ofNullable(rScript.evalSha(RScript.Mode.READ_WRITE, shaDigests, RScript.ReturnType.VALUE, keys, values));
        } catch (RedisException e) {
            return Optional.ofNullable(rScript.eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values));
        }
    }

    @Override
    public Optional<Object> executeScript(ReturnType returnType, String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        final RScript rScript = getRScript();
        String shaDigests = sha1DigestAsHex(script);
        final RScript.ReturnType convertReturnType = convertReturnType(returnType);
        try {
            return Optional.ofNullable(rScript.evalSha(RScript.Mode.READ_WRITE, shaDigests, convertReturnType, keys, values));
        } catch (RedisException e) {
            return Optional.ofNullable(rScript.eval(RScript.Mode.READ_WRITE, script, convertReturnType, keys, values));
        }
    }

    /**
     * Convert return type r script . return type.
     *
     * @param returnType the return type
     * @return the r script . return type
     */
    private RScript.ReturnType convertReturnType(ReturnType returnType) {
        switch (returnType) {
            case BOOLEAN:
                return RScript.ReturnType.BOOLEAN;
            case INTEGER:
                return RScript.ReturnType.INTEGER;
            case MULTI:
                return RScript.ReturnType.MULTI;
            case STATUS:
                return RScript.ReturnType.STATUS;
            case VALUE:
                return RScript.ReturnType.VALUE;
//            case MAPVALUE:
//                return RScript.ReturnType.MAPVALUE;
            case MAPVALUELIST:
                return RScript.ReturnType.MAPVALUELIST;
            default:
                return RScript.ReturnType.VALUE;
        }
    }

    /**
     * Gets r script.
     *
     * @return the r script
     */
    private RScript getRScript() {
        return getRedissonClient().getScript(getCodec());
    }

}
