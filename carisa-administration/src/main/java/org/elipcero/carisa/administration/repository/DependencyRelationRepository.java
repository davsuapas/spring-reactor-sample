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

import org.elipcero.carisa.administration.domain.DependencyRelation;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Relations between parent and child
 *
 * @author David Suárez
 */
public interface DependencyRelationRepository extends ReactiveCrudRepository<DependencyRelation, MapId> {

    /**
     * Find all children by parent
     *
     * @param parentId The parent id to find
     * @return DependencyRelation relations
     */
    Flux<DependencyRelation> findAllByParentIdAndRelation(UUID parentId, int relation);
}
