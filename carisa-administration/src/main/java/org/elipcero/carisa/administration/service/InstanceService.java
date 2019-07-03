/*
 *  Copyright 2019-2022 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.elipcero.carisa.administration.service;

import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Operations for instance
 *
 * @author David Suárez
 */
public interface InstanceService {

    /**
     * Get instance by id
     * @param id
     * @return instance found
     */
    Mono<Instance> getById(UUID id);

    /**
     * Create the instance
     * @param instance instance for creating
     * @return instance created
     */
    Mono<Instance> create(Instance instance);

    /**
     * Update or create the instance. If the id exits is updated
     * otherwise is created
     * @param id
     * @param instance instance for updating or creating
     * @return instance created or updated
     */
    Mono<EntityDataState<Instance>> updateOrCreate(UUID id, Instance instance);

    /**
     * Deploy instance into platform. Change state of the instance
     * @param id the instance id
     * @return instance deployed
     */
    Mono<Instance> deploy(final UUID id);
}
