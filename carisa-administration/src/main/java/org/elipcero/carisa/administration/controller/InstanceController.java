/*
 *  Copyright 2019-2022 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.elipcero.carisa.administration.controller;

import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.InstanceService;
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
 * Instance controller. @see Instance domain
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/instances")
public class InstanceController {

    private final InstanceService instanceService;
    private final InstanceModelAssembler instanceModelAssembler;
    private final CrudHypermediaController<Instance> crudHypermediaController;

    public InstanceController(InstanceService instanceService, InstanceModelAssembler instanceModelAssembler) {
        Assert.notNull(instanceService, "The instanceService can not be null");
        Assert.notNull(instanceModelAssembler, "The instanceModelAssembler can not be null");
        this.instanceService = instanceService;
        this.instanceModelAssembler = instanceModelAssembler;
        this.crudHypermediaController = new CrudHypermediaController(instanceModelAssembler);
    }

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(InstanceController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(InstanceController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get instance by id
     * @param id the instance identifier (UUID string)
     * @return
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<Instance>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.instanceService.getById(UUID.fromString(id)));
    }

    /**
     * Create the instance
     * @param instance the instance (Id == null)
     * @return
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<Instance>>> create(final @RequestBody Instance instance) {
        return this.crudHypermediaController.create(this.instanceService.create(instance));
    }

    /**
     * Update or create instance depending of the identifier if exists
     * @param id the instance identifier (UUID string)
     * @param instance the instance (Id == null)
     * @return
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<Instance>>> updateOrCreate(
            final @PathVariable("id") String id, final @RequestBody Instance instance) {

        return this.crudHypermediaController
                .updateOrCreate(this.instanceService.updateOrCreate(UUID.fromString(id), instance));
    }

    /**
     * Deploy de instance into the platform
     * @return the instance. instance.state report of the state of the instance
     */
    @PutMapping("/{id}/deploy")
    public Publisher<EntityModel<Instance>> deploy(@PathVariable("id") String id) {
        return this.instanceService.deploy(UUID.fromString(id))
                .flatMap(instance -> this.instanceModelAssembler.toModel(instance, null));
    }
}
