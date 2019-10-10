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
import org.elipcero.carisa.administration.domain.EnteEnteProperty;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.projection.EntePropertyName;
import org.elipcero.carisa.administration.service.EnteService;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
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
        this.crudHypermediaController = new CrudHypermediaController(enteModelAssembler);
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
     * @param id the space identifier (UUID string)
     * @return ente entity
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<Ente>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.enteService.getById(UUID.fromString(id)));
    }

    /**
     * Create the Ente
     * @param ente the Ente (Id == null)
     * @return
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<Ente>>> create(final @RequestBody Ente ente) {
        return this.crudHypermediaController.create(this.enteService.create(ente));
    }

    /**
     * Update or create the Ente depending of the identifier if exists.
     * @param id the Ente identifier (UUID string)
     * @param ente the Ente (Id == null)
     * @return
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<Ente>>> updateOrCreate(
            final @PathVariable("id") String id, final @RequestBody Ente ente) {

        return this.crudHypermediaController
                .updateOrCreate(this.enteService.updateOrCreate(UUID.fromString(id), ente));
    }

    /**
     * Remove ente-properties. Validate that the property doesn't exist
     * @param entePropertyId the Ente identifier
     * @param entePropertyId the enteProperty identifier
     * @return the ente-properties removed
     */
    @DeleteMapping("/{id}/properties/{entePropertyId}")
    public Publisher<ResponseEntity<?>> purgeSpaceEnte(
            final @PathVariable("id") String enteId,
            final @PathVariable("entePropertyId") String entePropertyId) {

        UUID uuidEnteId = UUID.fromString(enteId);
        UUID uuidEntePropertyId = UUID.fromString(entePropertyId);

        return this.enteService.removeEnteEnteProperty(uuidEnteId, uuidEntePropertyId)
                .flatMap(completed ->
                        linkTo(
                                methodOn(EnteController.class).getById(enteId))
                                .withRel(EnteModelAssembler.ENTE_REL_NAME).toMono()
                                .map(link -> {
                                    if (completed) {
                                        return ResponseEntity.ok(
                                                new EntityModel<>(EnteEnteProperty.builder()
                                                        .enteId(uuidEnteId)
                                                        .entePropertyId(uuidEntePropertyId)
                                                        .build(), link));
                                    }
                                    else {
                                        return new ResponseEntity<>(
                                                new EntityModel<>(
                                                        "The ente-property is just removed " +
                                                                "if the ente property doesn't exist", link),
                                                HttpStatus.NOT_ACCEPTABLE);
                                    }
                                })
                );
    }

    /**
     * Get ente properties by enteId
     * @param id the enteId
     * @return Ente property collections with links
     */
    @GetMapping("/{id}/properties")
    public Publisher<CollectionModel<EntityModel<EntePropertyName>>> getProperties(final @PathVariable("id") String id) {
        return this.enteService.getEntePropertiesByEnte(UUID.fromString(id))
                .flatMap(enteEnteProperty ->
                        Flux.concat(
                                linkTo(
                                        methodOn(EntePropertyController.class).getById(enteEnteProperty.getEntePropertyId().toString()))
                                        .withRel(EntePropertyModelAssembler.PROPERTY_REL_NAME).toMono())
                                .map(links -> new EntityModel<>(EntePropertyName
                                        .builder()
                                            .entePropertyId(enteEnteProperty.getEntePropertyId())
                                            .name(enteEnteProperty.getEntePropertyName())
                                        .build(), links)))
                .collectList()
                .flatMap(entities ->
                        linkTo(
                                methodOn(EnteController.class).getById(id))
                                .withRel(EnteModelAssembler.ENTE_REL_NAME).toMono()
                                .flatMap(link -> Mono.just(new CollectionModel<>(entities, link))));
    }
}
