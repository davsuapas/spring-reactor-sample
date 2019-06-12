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

import org.elipcero.carisa.administration.domain.Index;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Index for all controllers
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/")
public class IndexController {

    /**
     * Return schema like index
     * @return schema
     */
    @GetMapping
    public Publisher<EntityModel<Index>> index() {
        return Flux.concat(
                linkTo(methodOn(InstanceController.class).getMetadata()).withRel("instances").toMono())
                .collectList().map(links -> new EntityModel<>(new Index(), links));
    }
}