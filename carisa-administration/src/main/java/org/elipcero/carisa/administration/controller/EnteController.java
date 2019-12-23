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
@RequestMapping("/api")
public class EnteController {

    private final CrudHypermediaController<Ente> crudHypermediaController;
    private final EnteService enteService;

    public EnteController(EnteService enteService, EnteModelAssembler enteModelAssembler) {
        Assert.notNull(enteModelAssembler, "The enteModelAssembler can not be null");
        Assert.notNull(enteService, "The enteService can not be null");
        this.enteService = enteService;
        this.crudHypermediaController = new CrudHypermediaController(enteModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping("/entes")
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(EnteController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(EnteController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get Ente by id
     * @param spaceId the space identifier (UUID string)
     * @param enteId the ente identifier (UUID string)
     * @return ente entity
     */
    @GetMapping("/spaces/{spaceId}/entes/{enteId}")
    public Publisher<EntityModel<Ente>> getById(
            final @PathVariable("spaceId") String spaceId, final @PathVariable("enteId") String enteId) {
        return this.crudHypermediaController.get(
                this.enteService.getById(Ente.GetMapId(UUID.fromString(spaceId), UUID.fromString(enteId))));
    }

    /**
     * Create the Ente
     * @param ente the Ente (Id == null)
     * @return ente
     */
    @PostMapping("/entes")
    public Publisher<ResponseEntity<EntityModel<Ente>>> create(final @RequestBody Ente ente) {
        return this.crudHypermediaController.create(this.enteService.create(ente));
    }

    /**
     * Update or create the Ente depending of the identifier if exists.
     * @param spaceId the space identifier (UUID string)
     * @param enteId the ente identifier (UUID string)
     * @param ente the Ente (Id == null)
     * @return ente
     */
    @PutMapping("/spaces/{spaceId}/entes/{enteId}")
    public Publisher<ResponseEntity<EntityModel<Ente>>> updateOrCreate(
            final @PathVariable("spaceId") String spaceId,
            final @PathVariable("enteId") String enteId,
            final @RequestBody Ente ente) {

        ente.setParentId(UUID.fromString(spaceId));
        ente.setId(UUID.fromString(enteId));

        return this.crudHypermediaController.updateOrCreate(this.enteService.updateOrCreate(ente));
    }

    /**
     * Get ente properties by enteId
     * @param spaceId the space identifier (UUID string)
     * @param enteId the ente identifier (UUID string)
     * @return Ente property collections with links
     */
    @GetMapping("/spaces/{spaceId}/entes/{enteId}/properties")
    public Publisher<CollectionModel<EntityModel<EntePropertyName>>> getProperties(
            final @PathVariable("spaceId") String spaceId,
            final @PathVariable("enteId") String enteId) {

        return this.enteService.getEntePropertiesByEnte(UUID.fromString(enteId))
                .flatMap(enteProperty ->
                        Flux.concat(
                                linkTo(
                                        methodOn(EntePropertyController.class)
                                                .getById(
                                                        enteProperty.getSpaceId().toString(),
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
                                methodOn(EnteController.class).getById(spaceId, enteId))
                                .withRel(EnteModelAssembler.ENTE_REL_NAME).toMono()
                                .flatMap(link -> Mono.just(new CollectionModel<>(entities, link))));
    }
}
