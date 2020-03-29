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
import org.elipcero.carisa.core.data.ChildName;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.ParentChildName;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.elipcero.carisa.core.reactive.data.DependencyRelationChildNotFoundException;
import org.elipcero.carisa.core.reactive.data.DependencyRelationParentNotFoundException;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

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
                                .body(resourceCreated))
                .onErrorResume(error -> {
                    if (error instanceof DependencyRelationParentNotFoundException) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, error.getMessage());
                    }
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                });
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

    /**
     * Hypermedia resource when is connected a child with a parent in a relation.
     * @param entity relation entity
     * @return model representation
     */
    public Publisher<ResponseEntity<EntityModel<T>>> connectToParent(final Mono<T> entity) {
        return this.connectToParent(entity, null);
    }

    /**
     * Hypermedia resource when is connected a child with a parent in a relation.
     * @param entity relation entity
     * @param onError customized error event
     * @return model representation
     */
    public Publisher<ResponseEntity<EntityModel<T>>> connectToParent(
            final Mono<T> entity, final Consumer<Throwable> onError) {

        return entity
                .flatMap(child -> this.assembler.toModel(child, null))
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error instanceof DependencyRelationChildNotFoundException ||
                            error instanceof DependencyRelationParentNotFoundException) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, error.getMessage());
                    }
                    if (onError != null) {
                        onError.accept(error);
                    }
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    /**
     * Hypermedia resource when getting the children by parent.
     * @param parentId the parent identifier
     * @param parentChildNameFlux the flux children
     * @param controllerParent the parent controller
     * @param parentRelName the parent relation tag
     * @param controllerChild the child controller
     * @param childRelName the child relation tag
     * @param <TParent> the parent class
     * @return the entity model
     */
    public <TParent> Publisher<CollectionModel<EntityModel<ChildName>>> childrenByParent(
            final String parentId,
            final Flux<ParentChildName> parentChildNameFlux,
            final Class<? extends ChildControllerHypermedia<T>> controllerParent,
            final String parentRelName,
            final Class<? extends ChildControllerHypermedia<TParent>> controllerChild,
            final String childRelName) {

        return functionChildrenByParent(
                parentId, parentChildNameFlux, controllerParent, parentRelName,
                children -> methodOn(controllerChild).getById(children.getChildId().toString()),
                childRelName);
    }

    /**
     * Hypermedia resource when getting the children by parent where the child has bi key.
     * @param parentId the parent identifier
     * @param parentChildNameFlux the flux children
     * @param controllerParent the parent controller
     * @param parentRelName the parent relation tag
     * @param controllerChild the child controller with Bi key
     * @param childRelName the child relation tag
     * @param <TParent> the parent class
     * @return the entity model
     */
    public <TParent> Publisher<CollectionModel<EntityModel<ChildName>>> childrenByParentWithBiKey(
            final String parentId,
            final Flux<ParentChildName> parentChildNameFlux,
            final Class<? extends ChildControllerHypermedia<T>> controllerParent,
            final String parentRelName,
            final Class<? extends BiKeyChildControllerHypermedia<TParent>> controllerChild,
            final String childRelName) {

        return functionChildrenByParent(
                parentId, parentChildNameFlux, controllerParent, parentRelName,
                children -> methodOn(controllerChild).getById(
                        children.getParentId().toString(), children.getChildId().toString()),
                childRelName);
    }

    // Build hypermedia resource when getting the children by parent.
    private Publisher<CollectionModel<EntityModel<ChildName>>> functionChildrenByParent(
            final String parentId,
            final Flux<ParentChildName> parentChildNameFlux,
            final Class<? extends ChildControllerHypermedia<T>> controllerParent,
            final String parentRelName,
            final Function<ParentChildName, Object> childLinkTo,
            final String childRelName) {

        return parentChildNameFlux.flatMap(children ->
                Flux.concat(
                        linkTo(childLinkTo.apply(children)).withRel(childRelName).toMono())
                        .map(links ->
                                new EntityModel<>(
                                        ChildName.builder()
                                            .id(children.getChildId())
                                            .name(children.getName())
                                       .build(), links)
                        ))
                .collectList()
                .flatMap(entities ->
                        linkTo(
                                methodOn(controllerParent).getById(parentId))
                                .withRel(parentRelName).toMono()
                                .flatMap(link -> Mono.just(new CollectionModel<>(entities, link))));
    }
}


