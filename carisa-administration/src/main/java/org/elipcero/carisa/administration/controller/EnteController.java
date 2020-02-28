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
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.projection.EntePropertyName;
import org.elipcero.carisa.administration.service.EnteService;
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
 * Ente controller.
 * @see Ente domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/entes")
public class EnteController {

    private final CrudHypermediaController<Ente> crudHypermediaController;
    private final EnteService enteService;

    public EnteController(EnteService enteService, EnteModelAssembler enteModelAssembler) {
        Assert.notNull(enteModelAssembler, "The enteModelAssembler can not be null");
        Assert.notNull(enteService, "The enteService can not be null");
        this.enteService = enteService;
        this.crudHypermediaController = new CrudHypermediaController<>(enteModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(EnteController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(EnteController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get Ente by id
     * @param id the ente identifier (UUID string)
     * @return ente entity
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<Ente>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.enteService.getById(UUID.fromString(id)));
    }

    /**
     * Create the Ente
     * @param ente the Ente (Id == null)
     * @return ente
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<Ente>>> create(final @RequestBody Ente ente) {
        return this.crudHypermediaController.create(this.enteService.create(ente));
    }

    /**
     * Update or create the Ente depending of the identifier if exists.
     * @param id the ente identifier (UUID string)
     * @param ente the ente (Id == null)
     * @return the ente
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<Ente>>> updateOrCreate(
            final @PathVariable("id") String id,
            final @RequestBody Ente ente) {

        return this.crudHypermediaController.updateOrCreate(this.enteService.updateOrCreate(UUID.fromString(id), ente));
    }

    /**
     * Connect the ente to the category
     * @param enteId ente identifier to connect
     * @param categoryId category connected
     * @return the ente connected
     */
    @PutMapping("/{enteId}/connectcategory/{categoryId}")
    public Publisher<ResponseEntity<EntityModel<Ente>>> connectToCategory(
            @PathVariable("enteId") String enteId, @PathVariable("categoryId") String categoryId) {

        return this.crudHypermediaController
                .connectToParent(this.enteService
                        .connectToCategory(UUID.fromString(enteId), UUID.fromString(categoryId)));

    }

    /**
     * Get ente properties by enteId
     * @param id the ente identifier (UUID string)
     * @return the ente property collections with links
     */
    @GetMapping("/{id}/properties")
    public Publisher<CollectionModel<EntityModel<EntePropertyName>>> getProperties(
            final @PathVariable("id") String id) {

        return this.enteService.getEntePropertiesByEnte(UUID.fromString(id))
                .flatMap(enteProperty ->
                        Flux.concat(
                                linkTo(
                                        methodOn(EntePropertyController.class)
                                                .getById(
                                                        enteProperty.getParentId().toString(),
                                                        enteProperty.getId().toString()))
                                        .withRel(EntePropertyModelAssembler.PROPERTY_REL_NAME).toMono())
                                .map(links -> new EntityModel<>(EntePropertyName
                                        .builder()
                                            .entePropertyId(enteProperty.getId())
                                            .name(enteProperty.getName())
                                        .build(), links)))
                .collectList()
                .flatMap(entities ->
                        linkTo(
                                methodOn(EnteController.class).getById(id))
                                .withRel(EnteModelAssembler.ENTE_REL_NAME).toMono()
                                .flatMap(link -> Mono.just(new CollectionModel<>(entities, link))));
    }
}
