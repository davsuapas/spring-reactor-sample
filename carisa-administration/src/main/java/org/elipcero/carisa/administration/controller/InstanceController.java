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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * Instance controller
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
@RequestMapping("api/instance")
public class InstanceController {

    @NonNull
    private final InstanceService instanceService;

    @NonNull
    private final InstanceModelAssembler instanceModelAssembler;

    @GetMapping("/{id}")
    public Publisher<EntityModel<Instance>> getById(UUID id) {
        return this.instanceService.getById(id)
                .flatMap(instance -> this.instanceModelAssembler.toModel(instance, null));
    }
}