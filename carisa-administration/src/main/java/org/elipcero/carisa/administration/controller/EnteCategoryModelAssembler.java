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

import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Resources assembler for ente category
 *
 * @author David Suárez
 */
@Component
public class EnteCategoryModelAssembler implements BasicReactiveRepresentationModelAssembler<EnteCategory> {

    public static final String CATEGORY_REL_NAME = "category";
    public static final String CATEGORIES_REL_NAME = "enteCategories";

    @Override
    public Flux<Link> addLinks(EnteCategory enteCategory, ServerWebExchange exchange) {

        WebFluxLinkBuilder.WebFluxLink self = linkTo(
                methodOn(EnteCategoryController.class).getById(enteCategory.getId().toString()))
                .withSelfRel()
                .andAffordance(methodOn(EnteCategoryController.class)
                        .updateOrCreate(enteCategory.getId().toString(), enteCategory));

        WebFluxLinkBuilder.WebFluxLink children = linkTo(
                methodOn(EnteCategoryController.class).getChildren(enteCategory.getId().toString()))
                .withRel("children");

        return Flux.concat(self.toMono(), children.toMono());
    }
}
