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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.service.InstanceService;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Instance controller
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/instance")
public class InstanceController {

    @NonNull
    private final InstanceService instanceService;

    @NonNull
    private final InstanceModelAssembler instanceModelAssembler;

    @GetMapping("/{id}")
    public Publisher<EntityModel<Instance>> getById(final @PathVariable("id") String id) {
        return this.instanceService.getById(UUID.fromString(id))
                .flatMap(instance -> this.instanceModelAssembler.toModel(instance, null));
    }

    @PostMapping
    public Publisher<ResponseEntity<EntityModel<Instance>>> create(final @RequestBody Instance instance) {
        return this.instanceService.create(instance)
                .flatMap(instanceCreated -> this.instanceModelAssembler.toModel(instanceCreated, null))
                .map(resourceCreated ->
                        ResponseEntity
                                .created(resourceCreated.getLink(IanaLinkRelations.SELF).get().toUri())
                                .body(resourceCreated));
    }
}
