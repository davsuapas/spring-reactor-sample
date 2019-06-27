/*
 * Copyright 2019-2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.elipcero.carisa.core.reactive.misc;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Controller to critical section lock between process
 *
 * @author David Suárez
 */
public interface DataLockController {

    /**
     * Lock a resource
     * @param id the resource id
     * @param secondsExpired when expire the resource in seconds
     * @return if it can be locked return true otherwise false
     */
    Mono<Boolean> lock(UUID id, int secondsExpired);

    /**
     * Unlock a resource
     * @param id the resource id
     * @return if it can be unlocked return true otherwise false
     */
    Mono<Boolean> unLock(UUID id);
}
