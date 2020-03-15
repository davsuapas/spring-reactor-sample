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
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.EnteCategoryPropertyService;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
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

import java.util.UUID;

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
                        .getById(EnteProperty.GetMapId(UUID.fromString(enteCategoryId), UUID.fromString(propertyId))));
    }

    /**
     * Create the Ente category property
     * @param enteCategoryProperty the Ente category property (Id == null)
     * @return
     */
    @PostMapping("/entecategoryproperties")
    public Publisher<ResponseEntity<EntityModel<EnteCategoryProperty>>> create(
            final @RequestBody EnteCategoryProperty enteCategoryProperty) {
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

        return this.crudHypermediaController.updateOrCreate(
                this.enteCategoryPropertyService.updateOrCreate(enteCategoryProperty));
    }

    /**
     * Connect the Ente to Ente category property
     * @param enteCategoryId the Ente category identifier
     * @param categoryPropertyId the Ente category property identifier
     * @param enteId the Ente identifier connected
     * @param entePropertyId the Ente property identifier
     * @return
     */
    @PutMapping("/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
            "/connectente/{enteId}/properties/{entePropertyId}")
    public Publisher<ResponseEntity<EntityModel<EnteCategoryProperty>>> connectToCategoryProperty(
            @PathVariable("enteCategoryId") String enteCategoryId,
            @PathVariable("categoryPropertyId") String categoryPropertyId, @PathVariable("enteId") String enteId,
            @PathVariable("entePropertyId") String entePropertyId) {

        return this.crudHypermediaController
                .connectToParent(this.enteCategoryPropertyService
                        .connectEnte(
                                UUID.fromString(enteCategoryId), UUID.fromString(categoryPropertyId),
                                UUID.fromString(enteId), UUID.fromString(entePropertyId)));
    }
}
