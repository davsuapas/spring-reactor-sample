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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Ente property controller.
 * @see Ente domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/enteproperties")
public class EntePropertyController {

    private final CrudHypermediaController<EnteProperty> crudHypermediaController;
    private final EntePropertyService entePropertyService;

    public EntePropertyController(EntePropertyService entePropertyService, EntePropertyModelAssembler entePropertyModelAssembler) {
        Assert.notNull(entePropertyModelAssembler, "The entePropertyModelAssembler can not be null");
        Assert.notNull(entePropertyService, "The entePropertyService can not be null");
        this.entePropertyService = entePropertyService;
        this.crudHypermediaController = new CrudHypermediaController(entePropertyModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(EntePropertyController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(EntePropertyController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get Ente property by id
     * @param id the space identifier (UUID string)
     * @return ente property entity
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<EnteProperty>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.entePropertyService.getById(UUID.fromString(id)));
    }

    /**
     * Create the Ente property
     * @param enteProperty the Ente property (Id == null)
     * @return
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<EnteProperty>>> create(final @RequestBody EnteProperty enteProperty) {
        return this.crudHypermediaController.create(this.entePropertyService.create(enteProperty));
    }

    /**
     * Update or create the Ente property depending of the identifier if exists.
     * @param id the Ente property identifier (UUID string)
     * @param enteProperty the EnteProperty property (Id == null)
     * @return
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<EnteProperty>>> updateOrCreate(
            final @PathVariable("id") String id, final @RequestBody EnteProperty enteProperty) {

        return this.crudHypermediaController
                .updateOrCreate(this.entePropertyService.updateOrCreate(UUID.fromString(id), enteProperty));
    }
}
