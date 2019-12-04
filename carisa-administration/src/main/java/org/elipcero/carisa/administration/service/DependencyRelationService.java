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

package org.elipcero.carisa.administration.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.domain.ChildRelation;
import org.elipcero.carisa.administration.domain.DependencyRelation;
import org.elipcero.carisa.administration.repository.DependencyRelationRepository;
import org.springframework.data.cassandra.core.mapping.MapId;
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
public class DependencyRelationService<TParent, TChild extends ChildRelation> {

    @NonNull
    private final DependencyRelationRepository dependencyRelationRepository;

    @NonNull
    private final ReactiveCrudRepository<TParent, UUID> parentRepository;

    @NonNull
    private final ReactiveCrudRepository<TChild, UUID> childRepository;

    public Mono<TChild> create(final TChild child, final String errorMessage) {
        return this.parentRepository.findById(child.getParentId())
                .flatMap(__ ->
                        dependencyRelationRepository
                                .save(DependencyRelation
                                        .builder()
                                            .parentId(child.getParentId())
                                            .childId(child.getChildId())
                                        .build()))
                .flatMap(__ -> this.childRepository.save(child))
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format(errorMessage, child.getParentId()))));
    }

    public Flux<TChild> getChildrenByParent(UUID parentId) {
        return this.dependencyRelationRepository.findAllByParentId(parentId)
                .flatMap(relation -> this.childRepository
                            .findById(relation.getChildId())
                            .switchIfEmpty(this.purge(relation.getId())));
    }

    private Mono<TChild> purge(MapId id) {
        return this.dependencyRelationRepository.deleteById(id).then(Mono.empty());
    }
}
