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

package org.elipcero.carisa.administration.repository;

import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Relations between Space and instance repository
 *
 * @author David Suárez
 */
public interface InstanceSpaceRepository extends ReactiveCrudRepository<InstanceSpace, MapId> {

    /**
     * Find all spaces by instance
     *
     * @param instanceId The instance id to find
     * @return InstanceSpace
     */
    Flux<InstanceSpace> findAllByInstanceId(UUID instanceId);
}
