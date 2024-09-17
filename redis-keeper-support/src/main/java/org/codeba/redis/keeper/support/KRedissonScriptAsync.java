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

import org.codeba.redis.keeper.core.KScriptAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RScript;
import org.redisson.api.RScriptAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type K redisson script async.
 */
class KRedissonScriptAsync extends BaseAsync implements KScriptAsync {
    /**
     * Instantiates a new K redisson script async.
     *
     * @param redissonClient the redisson client
     * @param codec          the codec
     */
    public KRedissonScriptAsync(RedissonClient redissonClient, Codec codec) {
        super(redissonClient, codec);
    }

    /**
     * Instantiates a new K redisson script async.
     *
     * @param batch the batch
     * @param codec the codec
     */
    public KRedissonScriptAsync(RBatch batch, Codec codec) {
        super(batch, codec);
    }

    @Override
    public CompletableFuture<Object> executeScriptAsync(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException {
        /* evalSha: 此处必须加上 StringCodec.INSTANCE */
        final RScriptAsync rScript = getRScript();
        String shaDigests = sha1DigestAsHex(script);
        try {
            return rScript.evalShaAsync(RScript.Mode.READ_WRITE, shaDigests, RScript.ReturnType.VALUE, keys, values).toCompletableFuture();
        } catch (RedisException e) {
            return rScript.evalAsync(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE, keys, values).toCompletableFuture();
        }
    }

    /**
     * Sha 1 digest as hex string.
     *
     * @param input the input
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    protected String sha1DigestAsHex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Gets r script.
     *
     * @return the r script
     */
    private RScriptAsync getRScript() {
        if (getBatch() != null) {
            return getBatch().getScript(getCodec());
        } else {
            return getRedissonClient().getScript(getCodec());
        }
    }

}
