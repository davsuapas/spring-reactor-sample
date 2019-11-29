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
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.EntePropertyService;
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

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Ente property controller.
 * @see Ente domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api")
public class EntePropertyController {

    private final CrudHypermediaController<EnteProperty> crudHypermediaController;
    private final EntePropertyService entePropertyService;

    public EntePropertyController(EntePropertyService entePropertyService,
                                  EntePropertyModelAssembler entePropertyModelAssembler) {
        Assert.notNull(entePropertyModelAssembler, "The entePropertyModelAssembler can not be null");
        Assert.notNull(entePropertyService, "The entePropertyService can not be null");
        this.entePropertyService = entePropertyService;
        this.crudHypermediaController = new CrudHypermediaController(entePropertyModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping("/enteproperties")
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(EntePropertyController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(EntePropertyController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get ente property by id
     * @param enteId the ente identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @return ente property entity
     */
    @GetMapping("/entes/{enteId}/properties/{propertyId}")
    public Publisher<EntityModel<EnteProperty>> getById(
            final @PathVariable("enteId") String enteId,
            final @PathVariable("propertyId") String propertyId) {

        return this.crudHypermediaController.get(
                this.entePropertyService.getById(EnteProperty.getId(enteId, propertyId)));
    }

    /**
     * Create the Ente property
     * @param enteProperty the Ente property (Id == null)
     * @return
     */
    @PostMapping("/enteproperties")
    public Publisher<ResponseEntity<EntityModel<EnteProperty>>> create(final @RequestBody EnteProperty enteProperty) {
        return this.crudHypermediaController.create(this.entePropertyService.create(enteProperty));
    }

    /**
     * Update or create the ente property depending of the identifier if exists.
     * @param enteId the ente identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @param enteProperty the EnteProperty property (Id == null)
     * @return
     */
    @PutMapping("/entes/{enteId}/properties/{propertyId}")
    public Publisher<ResponseEntity<EntityModel<EnteProperty>>> updateOrCreate(
            final @PathVariable("enteId") String enteId,
            final @PathVariable("propertyId") String propertyId,
            final @RequestBody EnteProperty enteProperty) {

        return this.crudHypermediaController
                .updateOrCreate(this.entePropertyService.updateOrCreate(
                        EnteProperty.getId(enteId, propertyId), enteProperty));
    }
}
