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

import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.SpaceService;
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
 * Space controller. @see Space domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    private final CrudHypermediaController<Space> crudHypermediaController;
    private final SpaceService spaceService;

    public SpaceController(SpaceService spaceService, SpaceModelAssembler spaceModelAssembler) {
        Assert.notNull(spaceModelAssembler, "The spaceModelAssembler can not be null");
        Assert.notNull(spaceService, "The spaceService can not be null");
        this.spaceService = spaceService;
        this.crudHypermediaController = new CrudHypermediaController(spaceModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(SpaceController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(SpaceController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get space by id
     * @param id the space identifier (UUID string)
     * @return space entity
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<Space>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.spaceService.getById(UUID.fromString(id)));
    }

    /**
     * Create the space
     * @param space the space (Id == null)
     * @return
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<Space>>> create(final @RequestBody Space space) {
        return this.crudHypermediaController.create(this.spaceService.create(space));
    }

    /**
     * Update or create space depending of the identifier if exists.
     * @param id the space identifier (UUID string)
     * @param space the space (Id == null)
     * @return
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<Space>>> updateOrCreate(
            final @PathVariable("id") String id, final @RequestBody Space space) {

        return this.crudHypermediaController
                .updateOrCreate(this.spaceService.updateOrCreate(UUID.fromString(id), space));
    }
}
