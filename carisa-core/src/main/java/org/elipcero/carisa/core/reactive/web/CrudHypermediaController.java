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

package org.elipcero.carisa.core.reactive.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Crud operations for controller
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class CrudHypermediaController<T> {

    @NonNull
    private BasicReactiveRepresentationModelAssembler<T> assembler;

    /**
     * Hypermedia resource (Get) to return web client
     * @param entity entity to get
     * @return model representation
     */
    public Publisher<EntityModel<T>> get(final Mono<T> entity) {
        return entity.flatMap(instance -> this.assembler.toModel(instance, null));
    }

    /**
     * Hypermedia resource (Create) to return web client
     * Mandatory self rel
     *
     * @param entity entity to create
     * @return model representation
     */
    public Publisher<ResponseEntity<EntityModel<T>>> create(final Mono<T> entity) {
        return entity
                .flatMap(entityCreated -> this.assembler.toModel(entityCreated, null))
                .map(resourceCreated ->
                        ResponseEntity
                                .created(resourceCreated.getLink(IanaLinkRelations.SELF).get().toUri())
                                .body(resourceCreated));
    }

    /**
     * Hypermedia resource (Create or update) to return web client
     * Mandatory self rel
     *
     * @param entityWrapper entity to create or update
     * @return model representation
     */
    public Publisher<ResponseEntity<EntityModel<T>>> updateOrCreate(final Mono<EntityDataState<T>> entityWrapper) {
        return entityWrapper
            .flatMap(entityDataState ->
                    this.assembler.toModel(entityDataState.getEntity(), null)
                            .map(resource -> {
                                ResponseEntity.BodyBuilder builder =
                                    entityDataState.getDomainState() == EntityDataState.State.created ?
                                        ResponseEntity.created(resource.getLink(IanaLinkRelations.SELF).get().toUri()) :
                                        ResponseEntity.ok();
                                return builder.body(resource);
                            }));
    }
}


