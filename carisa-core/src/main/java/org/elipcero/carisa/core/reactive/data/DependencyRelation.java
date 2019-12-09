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

package org.elipcero.carisa.core.reactive.data;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Manage dependency relations operations
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public abstract class DependencyRelation<TParent, TRelation extends Relation, TRelationID> {

    @NonNull
    private final ReactiveCrudRepository<TParent, UUID> parentRepository;

    @NonNull
    protected final DependencyRelationRepository<TRelation, TRelationID> relationRepository;

    /**
     * Create the relation. If the parent doesn't exist throw exception
     * @param relationEntity relation to create
     * @param errorMessage error message for user
     * @return the create relation
     */
    protected Mono<TRelation> create(final TRelation relationEntity, final String errorMessage) {
        return this.parentRepository.findById(relationEntity.getParentId())
                .flatMap(__ -> relationRepository.save(relationEntity))
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format(errorMessage, relationEntity.getParentId()))));
    }

    /**
     * Get children by parent.
     *
     * @param parentId parent identifier
     * @return
     */
    public Flux<TRelation> getRelationsByParent(UUID parentId) {
        return this.relationRepository.findAllByParentId(parentId);
    }
}
