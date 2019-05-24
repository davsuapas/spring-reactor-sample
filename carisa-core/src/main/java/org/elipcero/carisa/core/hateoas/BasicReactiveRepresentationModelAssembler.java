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

package org.elipcero.carisa.core.hateoas;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Create reactive hypermedia model from resources. Allow add hypermedia links
 *
 * @author David Suárez
 */
public interface BasicReactiveRepresentationModelAssembler<T>
        extends ReactiveRepresentationModelAssembler<T, EntityModel<T>> {

    default Mono<EntityModel<T>> toModel(T entity, ServerWebExchange exchange) {
        return this.addLinks(entity, exchange).collectList().map(links -> new EntityModel<>(entity, links));
    }

    default Flux<Link> addLinks(T entity, ServerWebExchange exchange) {
        return Flux.empty();
    }

    default Mono<CollectionModel<EntityModel<T>>> toCollectionModel(
            Flux<? extends T> entities, ServerWebExchange exchange) {

        return entities
                .flatMap((entity) -> this.toModel(entity, exchange)).collectList()
                .flatMap((x$0) -> this.addLinks(exchange).collectList()
                        .map(links -> new CollectionModel<>(x$0, links)));

    }

    default Flux<Link> addLinks(ServerWebExchange exchange) {
        return Flux.empty();
    }
}
