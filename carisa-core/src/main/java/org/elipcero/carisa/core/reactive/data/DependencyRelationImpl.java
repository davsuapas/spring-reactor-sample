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
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Manage dependency relations operations
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public abstract class DependencyRelationImpl<TParent, TRelation extends Relation, TRelationID, TParentID>
        implements DependencyRelation<TRelation> {

    @NonNull
    protected final ReactiveCrudRepository<TParent, TParentID> parentRepository;

    @NonNull
    protected final DependencyRelationRepository<TRelation, TRelationID> relationRepository;

    @NonNull
    protected final DependencyRelationIdentifierConvert<TRelation, TRelationID, TParentID> convertRelationId;

    /**
     * Create the relation. If the parent doesn't exist throw exception
     * @param relationEntity relation to create
     * @param errorMessage error message for user
     * @return the create relation
     */
    protected Mono<TRelation> createBasic(final TRelation relationEntity, final String errorMessage) {
        return this.parentRepository.findById(this.convertRelationId.convertForParent(relationEntity))
                .flatMap(__ -> relationRepository.save(relationEntity))
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format(errorMessage, relationEntity.getParentId()))));
    }

    /**
     * @see EmbeddedDependencyRelation
     */
    public Flux<TRelation> getRelationsByParent(UUID parentId) {
        return this.relationRepository.findAllByParentId(parentId);
    }

    /**
     * @see DependencyRelation
     */
    @Override
    public Mono<TRelation> getById(Map<String, Object> id) {
        return this.relationRepository.findById(this.convertRelationId.convertFromDictionary(id));
    }

    /**
     * @see DependencyRelation
     */
    @Override
    public Mono<EntityDataState<TRelation>> updateOrCreate(
            final TRelation relation, Consumer<TRelation> updateChange,
            final Mono<TRelation> monoCreatedEntity) {

        return this.relationRepository.updateCreate(
                this.convertRelationId.convert(relation), updateChange, monoCreatedEntity);
    }
}
