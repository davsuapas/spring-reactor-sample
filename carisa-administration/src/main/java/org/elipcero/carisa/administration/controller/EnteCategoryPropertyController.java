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

import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.exception.NotMatchingTypeException;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.projection.ChildName;
import org.elipcero.carisa.administration.service.EnteCategoryPropertyService;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Ente category property controller.
 * @see Ente domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api")
public class EnteCategoryPropertyController {

    private final CrudHypermediaController<EnteCategoryProperty> crudHypermediaController;
    private final EnteCategoryPropertyService enteCategoryPropertyService;

    public EnteCategoryPropertyController(final EnteCategoryPropertyService enteCategoryPropertyService,
                                          final EnteCategoryPropertyModelAssembler enteCategoryPropertyModelAssembler) {

        Assert.notNull(enteCategoryPropertyModelAssembler,
                "The enteCategoryPropertyModelAssembler can not be null");
        Assert.notNull(enteCategoryPropertyService, "The enteCategoryPropertyService can not be null");

        this.enteCategoryPropertyService = enteCategoryPropertyService;
        this.crudHypermediaController = new CrudHypermediaController<>(enteCategoryPropertyModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping("/entecategoryproperties")
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(EnteCategoryPropertyController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(EnteCategoryPropertyController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get Ente category property by id
     * @param enteCategoryId the Ente identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @return ente category property entity
     */
    @GetMapping("/entecategories/{enteCategoryId}/properties/{propertyId}")
    public Publisher<EntityModel<EnteCategoryProperty>> getById(
            final @PathVariable("enteCategoryId") String enteCategoryId,
            final @PathVariable("propertyId") String propertyId) {

        return this.crudHypermediaController.get(
                this.enteCategoryPropertyService
                        .getById(EnteCategoryProperty.GetMapId(
                                UUID.fromString(enteCategoryId), UUID.fromString(propertyId))));
    }

    /**
     * Create the Ente category property
     * @param enteCategoryProperty the Ente category property (Id == null)
     * @return
     */
    @PostMapping("/entecategoryproperties")
    public Publisher<ResponseEntity<EntityModel<EnteCategoryProperty>>> create(
            final @RequestBody EnteCategoryProperty enteCategoryProperty) {

        enteCategoryProperty.setType(EnteProperty.Type.None); // Only is updated from connect
        return this.crudHypermediaController.create(this.enteCategoryPropertyService.create(enteCategoryProperty));
    }

    /**
     * Update or create the ente property depending of the identifier if exists.
     * @param enteCategoryId the ente identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @param enteCategoryProperty the EnteProperty property (Id == null)
     * @return
     */
    @PutMapping("/entecategories/{enteCategoryId}/properties/{propertyId}")
    public Publisher<ResponseEntity<EntityModel<EnteCategoryProperty>>> updateOrCreate(
            final @PathVariable("enteCategoryId") String enteCategoryId,
            final @PathVariable("propertyId") String propertyId,
            final @RequestBody EnteCategoryProperty enteCategoryProperty) {

        enteCategoryProperty.setParentId(UUID.fromString(enteCategoryId));
        enteCategoryProperty.setId(UUID.fromString(propertyId));
        enteCategoryProperty.setType(EnteProperty.Type.None); // Only is updated from connect

        return this.crudHypermediaController.updateOrCreate(
                this.enteCategoryPropertyService.updateOrCreate(enteCategoryProperty, false));
    }

    /**
     * Get children (Ente category property or Ente property)
     * @param enteCategoryId  the Ente category identifier
     * @param propertyId the ente category property identifier
     * @return Children collections with links
     */
    @GetMapping("/entecategories/{enteCategoryId}/properties/{propertyId}/children")
    public Publisher<CollectionModel<EntityModel<ChildName>>> getChildren(
            final @PathVariable("enteCategoryId") String enteCategoryId,
            final @PathVariable("propertyId") String propertyId) {

        return this.enteCategoryPropertyService.getChildren(UUID.fromString(propertyId))
                .flatMap(child -> {
                    WebFluxLinkBuilder.WebFluxLink link;
                    if (child.isCategory()) { // Category property
                        link = linkTo(
                                methodOn(EnteCategoryPropertyController.class)
                                        .getById(child.getParentId().toString(), child.getChildId().toString()))
                                .withRel(EnteCategoryPropertyModelAssembler.PROPERTY_REL_NAME);
                    }
                    else { // Ente property
                        link = linkTo(
                                methodOn(EntePropertyController.class)
                                        .getById(child.getParentId().toString(), child.getChildId().toString()))
                                .withRel(EntePropertyModelAssembler.PROPERTY_REL_NAME);
                    }
                    return Flux.concat(link.toMono())
                            .map(links -> new EntityModel<>(ChildName
                                    .builder()
                                        .id(child.getChildId())
                                        .name(child.getChildName())
                                    .build(), links));
                })
                .collectList()
                .flatMap(entities ->
                        linkTo(
                                methodOn(EnteCategoryPropertyController.class).getById(enteCategoryId, propertyId))
                                .withRel(EnteCategoryPropertyModelAssembler.PROPERTY_REL_NAME).toMono()
                                .flatMap(link -> Mono.just(new CollectionModel<>(entities, link))));
    }

    /**
     * Connect the Ente to Ente category property as inheritance.
     * @param enteCategoryId the Ente category identifier
     * @param categoryPropertyId the Ente category property identifier
     * @param enteId the connected Ente identifier
     * @param entePropertyId the connected Ente property identifier
     * @return
     */
    @PutMapping("/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
            "/connectente/{enteId}/properties/{entePropertyId}")
    public Publisher<ResponseEntity<EntityModel<EnteCategoryProperty>>> connectToEnte(
            final @PathVariable("enteCategoryId") String enteCategoryId,
            final @PathVariable("categoryPropertyId") String categoryPropertyId,
            final @PathVariable("enteId") String enteId,
            final @PathVariable("entePropertyId") String entePropertyId) {

        return this.crudHypermediaController
                .connectToParent(this.enteCategoryPropertyService
                        .connectToEnte(
                                UUID.fromString(enteCategoryId), UUID.fromString(categoryPropertyId),
                                UUID.fromString(enteId), UUID.fromString(entePropertyId)),
                        tryThrowConflictException());
    }

    /**
     * Connect the Ente category property to Ente category property as inheritance.
     * @param enteCategoryId the Ente category identifier
     * @param categoryPropertyId the Ente category property identifier
     * @param linkedEnteCategoryId the connected Ente category identifier
     * @param linkedCategoryPropertyId the connected Ente category property identifier
     * @return
     */
    @PutMapping("/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
            "/connectpropertycategory/{linkedEnteCategoryId}/properties/{linkedCategoryPropertyId}")
    public Publisher<ResponseEntity<EntityModel<EnteCategoryProperty>>> connectToCategoryProperty(
            final @PathVariable("enteCategoryId") String enteCategoryId,
            final @PathVariable("categoryPropertyId") String categoryPropertyId,
            final @PathVariable("linkedEnteCategoryId") String linkedEnteCategoryId,
            final @PathVariable("linkedCategoryPropertyId") String linkedCategoryPropertyId) {

        return this.crudHypermediaController
                .connectToParent(this.enteCategoryPropertyService
                                .connectToCategoryProperty(
                                        UUID.fromString(enteCategoryId), UUID.fromString(categoryPropertyId),
                                        UUID.fromString(linkedEnteCategoryId),
                                        UUID.fromString(linkedCategoryPropertyId)),
                        tryThrowConflictException());
    }

    private static Consumer<Throwable> tryThrowConflictException() {
        return e -> {
            if (e instanceof NotMatchingTypeException) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
        };
    }
}
