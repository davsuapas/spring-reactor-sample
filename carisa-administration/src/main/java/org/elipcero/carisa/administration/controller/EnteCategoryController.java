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

package org.elipcero.carisa.administration.controller;

import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.projection.EnteCategoryChildName;
import org.elipcero.carisa.administration.service.EnteCategoryService;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Ente category controller. @see EnteCategory domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/entecategories")
public class EnteCategoryController {

    private final CrudHypermediaController<EnteCategory> crudHypermediaController;
    private final EnteCategoryService enteCategoryService;

    public EnteCategoryController(
            EnteCategoryService enteCategoryService, EnteCategoryModelAssembler enteCategoryModelAssembler) {

        Assert.notNull(enteCategoryModelAssembler, "The enteCategoryModelAssembler can not be null");
        Assert.notNull(enteCategoryService, "The enteCategoryService can not be null");
        this.enteCategoryService = enteCategoryService;
        this.crudHypermediaController = new CrudHypermediaController(enteCategoryModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(EnteCategoryController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(EnteCategoryController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get ente category by id
     * @param id the category ente identifier (UUID string)
     * @return category entity
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<EnteCategory>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.enteCategoryService.getById(UUID.fromString(id)));
    }

    /**
     * Create the ente category
     * @param enteCategory the category ente (Id == null)
     * @return
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<EnteCategory>>> create(final @RequestBody EnteCategory enteCategory) {
        return this.crudHypermediaController.create(this.enteCategoryService.create(enteCategory));
    }

    /**
     * Update or create ente category depending of the identifier if exists.
     * @param id the ente category identifier (UUID string)
     * @param enteCategory the ente category (Id == null)
     * @return
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<EnteCategory>>> updateOrCreate(
            final @PathVariable("id") String id, final @RequestBody EnteCategory enteCategory) {

        return this.crudHypermediaController
                .updateOrCreate(this.enteCategoryService.updateOrCreate(UUID.fromString(id), enteCategory));
    }

    @PutMapping("/{childId}/connectparent/{parentId}")
    public Publisher<ResponseEntity<EntityModel<EnteCategory>>> connectToParent(
            @PathVariable("childId") String childId, @PathVariable("parentId") String parentId) {

        return this.crudHypermediaController
                .connectToParent(this.enteCategoryService
                        .connectToParent(UUID.fromString(childId), UUID.fromString(parentId)));
    }

    /**
     * Get children (Ente category or Ente)
     * @param id the ente category identifier
     * @return Children collections with links
     */
    @GetMapping("/{id}/children")
    public Publisher<CollectionModel<EntityModel<EnteCategoryChildName>>> getChildren(
            final @PathVariable("id") String id) {

        return this.enteCategoryService.getChildren(UUID.fromString(id))
                .flatMap(child ->
                        Flux.concat(
                                linkTo(
                                        methodOn(EnteCategoryController.class)
                                                .getById(child.getChildId().toString()))
                                        .withRel(EnteCategoryModelAssembler.CATEGORY_REL_NAME).toMono())
                                .map(links -> new EntityModel<>(EnteCategoryChildName
                                        .builder()
                                            .id(child.getChildId())
                                            .name(child.getChildName())
                                        .build(), links)))
                .collectList()
                .flatMap(entities ->
                        linkTo(
                                methodOn(EnteCategoryController.class).getById(id))
                                .withRel(EnteCategoryModelAssembler.CATEGORY_REL_NAME).toMono()
                                .flatMap(link -> Mono.just(new CollectionModel<>(entities, link))));
    }
}
