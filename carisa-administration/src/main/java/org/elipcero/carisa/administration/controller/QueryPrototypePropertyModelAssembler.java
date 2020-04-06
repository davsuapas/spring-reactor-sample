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

import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Resources assembler for query prototype property
 *
 * @author David Suárez
 */
@Component
public class QueryPrototypePropertyModelAssembler
        implements BasicReactiveRepresentationModelAssembler<DynamicObjectPrototypeProperty> {

    public static final String QUERY_PROTOTYPE_PROP_REL_NAME = "property";
    public static final String QUERY_PROTOTYPES_PROP_REL_NAME = "querypluginproperties";

    @Override
    public Flux<Link> addLinks(DynamicObjectPrototypeProperty queryProperty, ServerWebExchange exchange) {

        WebFluxLinkBuilder.WebFluxLink self = linkTo(
                methodOn(QueryPrototypePropertyController.class).getById(
                        queryProperty.getPrototypeId().toString(),
                        queryProperty.getId().toString()))
                .withSelfRel()
                .andAffordance(methodOn(QueryPrototypePropertyController.class)
                        .updateOrCreate(
                                queryProperty.getPrototypeId().toString(),
                                queryProperty.getId().toString(),
                                queryProperty));

        WebFluxLinkBuilder.WebFluxLink queryPrototype = linkTo(
                methodOn(QueryPrototypeController.class).getById(queryProperty.getPrototypeId().toString()))
                .withRel(QueryPrototypeModelAssembler.QUERY_PROTOTYPE_REL_NAME);

        return Flux.concat(self.toMono(), queryPrototype.toMono());
    }
}
