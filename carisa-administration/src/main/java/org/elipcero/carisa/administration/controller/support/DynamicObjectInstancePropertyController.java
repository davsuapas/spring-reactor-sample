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
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.projection.RawDynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.service.DynamicObjectInstancePropertyService;
import org.elipcero.carisa.core.data.EntityDataState;
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
 * Dynamic object instance property controller.
 * @see DynamicObjectInstanceProperty
 *
 * @author David Suárez
 */
public abstract class DynamicObjectInstancePropertyController
        implements BiKeyChildControllerHypermedia<RawDynamicObjectInstanceProperty<?>> {

    private final CrudHypermediaController<RawDynamicObjectInstanceProperty<?>> crudHypermediaController;
    private final DynamicObjectInstancePropertyService service;

    public DynamicObjectInstancePropertyController(
            @NonNull final DynamicObjectInstancePropertyService service,
            @NonNull final BasicReactiveRepresentationModelAssembler<RawDynamicObjectInstanceProperty<?>> modelAssembler) {

        this.service = service;
        this.crudHypermediaController = new CrudHypermediaController<>(modelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping("/instanceproperties")
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(this.getClass()).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(this.getClass()).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get instance property by id
     * @param instanceId the instance identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @return instance property entity
     */
    @GetMapping("/instances/{instanceId}/properties/{propertyId}")
    public Publisher<EntityModel<RawDynamicObjectInstanceProperty<?>>> getById(
            final @PathVariable("instanceId") String instanceId,
            final @PathVariable("propertyId") String propertyId) {

        return this.crudHypermediaController.get(
                this.service
                        .getById(DynamicObjectInstanceProperty.GetMapId(UUID.fromString(instanceId), UUID.fromString(propertyId)))
                        .map(DynamicObjectInstancePropertyController::convertToResponse)
        );
    }

    /**
     * Create the instance property
     * @param property the instance property (Id == null)
     * @return the created property
     */
    @PostMapping("/instanceproperties")
    public Publisher<ResponseEntity<EntityModel<RawDynamicObjectInstanceProperty<?>>>> create(
            final @RequestBody DynamicObjectInstanceProperty<?> property) {

        return this.crudHypermediaController.create(
                this.service
                        .create(property)
                        .map(DynamicObjectInstancePropertyController::convertToResponse));
    }

    /**
     * Update or create the instance property depending of the identifier if exists.
     * @param instanceId the instance identifier (UUID string)
     * @param propertyId the property identifier (UUID string)
     * @param property the instance property entity (Id == null)
     * @return the created or updated property
     */
    @PutMapping("/instances/{instanceId}/properties/{propertyId}")
    public Publisher<ResponseEntity<EntityModel<RawDynamicObjectInstanceProperty<?>>>> updateOrCreate(
            final @PathVariable("instanceId") String instanceId,
            final @PathVariable("propertyId") String propertyId,
            final @RequestBody DynamicObjectInstanceProperty<?> property) {

        property.setParentId(UUID.fromString(instanceId));
        property.setId(UUID.fromString(propertyId));

        return this.crudHypermediaController.updateOrCreate(
                this.service
                        .updateOrCreate(property)
                        .map(entityDataState -> EntityDataState.<RawDynamicObjectInstanceProperty<?>>builder()
                                    .domainState(entityDataState.getDomainState())
                                    .entity(convertToResponse(entityDataState.getEntity()))
                                .build())
        );
    }

    private static RawDynamicObjectInstanceProperty<?> convertToResponse(DynamicObjectInstanceProperty<?> property) {
        return RawDynamicObjectInstanceProperty.builder()
                    .id(property.getId())
                    .instanceId(property.getParentId())
                    .value(property.getValue().getRawValue())
                .build();
    }
}
