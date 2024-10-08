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

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The interface K script async.
 */
public interface KScriptAsync {
    /**
     * Execute script async completable future.
     *
     * @param script the script
     * @param keys   the keys
     * @param values the values
     * @return the completable future
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    CompletableFuture<Object> executeScriptAsync(String script, List<Object> keys, Object... values) throws NoSuchAlgorithmException;
}
