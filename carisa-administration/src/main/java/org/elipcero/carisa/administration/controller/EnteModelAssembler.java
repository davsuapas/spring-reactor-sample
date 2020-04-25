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
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Resources assembler for ente
 *
 * @author David Suárez
 */
@Component
public class EnteModelAssembler implements BasicReactiveRepresentationModelAssembler<Ente> {

    public static final String ENTE_REL_NAME = "ente";
    public static final String ENTES_REL_NAME = ENTE_REL_NAME + "s";

    @Override
    public Flux<Link> addLinks(Ente ente, ServerWebExchange exchange) {

        WebFluxLinkBuilder.WebFluxLink self = linkTo(
                methodOn(EnteController.class).getById(ente.getId().toString()))
                .withSelfRel()
                .andAffordance(methodOn(EnteController.class).updateOrCreate(ente.getId().toString(), ente));

        WebFluxLinkBuilder.WebFluxLink spaces = linkTo(
                methodOn(SpaceController.class).getById(ente.getSpaceId().toString()))
                .withRel(SpaceModelAssembler.SPACE_REL_NAME);

        WebFluxLinkBuilder.WebFluxLink properties = linkTo(
                methodOn(EnteController.class).getProperties(ente.getId().toString()))
                .withRel(EntePropertyModelAssembler.PROPERTIES_REL_NAME);

        return Flux.concat(self.toMono(), spaces.toMono(), properties.toMono());
    }
}
