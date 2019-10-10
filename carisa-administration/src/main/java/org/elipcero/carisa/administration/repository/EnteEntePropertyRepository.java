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

import org.elipcero.carisa.administration.domain.EnteEnteProperty;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Relations between Ente property and Ente repository
 *
 * @author David Suárez
 */
public interface EnteEntePropertyRepository extends ReactiveCrudRepository<EnteEnteProperty, MapId> {

    /**
     * Find all Ente properties by Ente
     *
     * @param enteId The ente id to find
     * @return EnteEnteProperty
     */
    Flux<EnteEnteProperty> findAllByEnteId(UUID enteId);
}
