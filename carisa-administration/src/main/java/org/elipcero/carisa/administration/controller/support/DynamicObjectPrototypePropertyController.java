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

package org.elipcero.carisa.administration.controller.support;

import lombok.NonNull;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.DynamicObjectPrototypePropertyService;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.elipcero.carisa.core.reactive.web.BiKeyChildControllerHypermedia;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Dynamic object prototype property controller.
 * @see EnteProperty domain
 *
 * @author David Suárez
 */
public abstract class DynamicObjectPrototypePropertyController
        implements BiKeyChildControllerHypermedia<DynamicObjectPrototypeProperty> {

    private final CrudHypermediaController<DynamicObjectPrototypeProperty> crudHypermediaController;
    private final DynamicObjectPrototypePropertyService service;

    public DynamicObjectPrototypePropertyController(
            @NonNull final DynamicObjectPrototypePropertyService service,
            @NonNull final BasicReactiveRepresentationModelAssembler<DynamicObjectPrototypeProperty> modelAssembler) {

        this.service = service;
        this.crudHypermediaController = new CrudHypermediaController<>(modelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping("/prototypeproperties")
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(this.getClass()).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(this.getClass()).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get prototype property by id
     * @param prototypeId the prototype identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @return ente property entity
     */
    @GetMapping("/prototypes/{prototypeId}/properties/{propertyId}")
    public Publisher<EntityModel<DynamicObjectPrototypeProperty>> getById(
            final @PathVariable("prototypeId") String prototypeId,
            final @PathVariable("propertyId") String propertyId) {

        return this.crudHypermediaController.get(
                this.service.getById(
                        DynamicObjectPrototypeProperty.GetMapId(UUID.fromString(prototypeId), UUID.fromString(propertyId))));
    }

    /**
     * Create the prototype property
     * @param property the prototype property (Id == null)
     * @return the created property
     */
    @PostMapping("/prototypeproperties")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototypeProperty>>> create(
            final @RequestBody DynamicObjectPrototypeProperty property) {

        return this.crudHypermediaController.create(this.service.create(property));
    }

    /**
     * Update or create the prototype property depending of the identifier if exists.
     * @param prototypeId the prototype identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @param property the EnteProperty property (Id == null)
     * @return the created or updated property
     */
    @PutMapping("/prototypes/{prototypeId}/properties/{propertyId}")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototypeProperty>>> updateOrCreate(
            final @PathVariable("prototypeId") String prototypeId,
            final @PathVariable("propertyId") String propertyId,
            final @RequestBody DynamicObjectPrototypeProperty property) {

        property.setParentId(UUID.fromString(prototypeId));
        property.setId(UUID.fromString(propertyId));

        return this.crudHypermediaController.updateOrCreate(this.service.updateOrCreate(property));
    }
}
