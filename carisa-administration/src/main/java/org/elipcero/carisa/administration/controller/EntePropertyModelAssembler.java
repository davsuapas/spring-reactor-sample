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

import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Resources assembler for Ente property
 *
 * @author David Suárez
 */
@Component
public class EntePropertyModelAssembler implements BasicReactiveRepresentationModelAssembler<EnteProperty> {

    public static final String PROPERTY_REL_NAME = "property";

    @Override
    public Flux<Link> addLinks(EnteProperty enteProperty, ServerWebExchange exchange) {

        WebFluxLinkBuilder.WebFluxLink self = linkTo(
                methodOn(EntePropertyController.class)
                        .getById(enteProperty.getEnteId().toString(), enteProperty.getId().toString()))
                .withSelfRel()
                .andAffordance(methodOn(EntePropertyController.class)
                        .updateOrCreate(
                                enteProperty.getSpaceId().toString(),
                                enteProperty.getEnteId().toString(),
                                enteProperty.getId().toString(),
                                enteProperty));

        WebFluxLinkBuilder.WebFluxLink entes = linkTo(
                methodOn(EnteController.class)
                        .getById(enteProperty.getEnteId().toString(), enteProperty.getId().toString()))
                .withRel(EnteModelAssembler.ENTE_REL_NAME);

        return Flux.concat(self.toMono(), entes.toMono());
    }
}
