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

package org.elipcero.carisa.core.reactive.web;

import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.hateoas.Link;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
public class CrudHypermediaControllerTest {

    public static final String HREF = "href";
    public static final String ENTITY = "Hola";

    private final CrudHypermediaController<String> crudHypermediaController =
            new CrudHypermediaController<>(new Test1BasicReactiveRepresentationModelAssembler());

    @Test
    public void controller_creation_operation_should_return_status_201_and_entity_and_link() {

        StepVerifier
                .create(crudHypermediaController.create(Mono.just(ENTITY)))
                .expectNextMatches(result -> {
                    assertThat(result.getStatusCodeValue()).isEqualTo(201).as("Check code status is 201");
                    assertThat(result.getBody().getContent()).isEqualTo(ENTITY).as("Check the body is ok");
                    assertThat(result.getBody().hasLinks()).isTrue().as("Check the resource has link");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void controller_post_creation_operation_should_return_status_201_and_entity_and_link() {

        EntityDataState<String> entityDataState = EntityDataState.<String>builder()
                    .entity(ENTITY)
                    .domainState(EntityDataState.State.created)
                .build();

        StepVerifier
                .create(crudHypermediaController.updateOrCreate(Mono.just(entityDataState)))
                .expectNextMatches(result -> {
                    assertThat(result.getStatusCodeValue()).isEqualTo(201).as("Check code status is 201");
                    assertThat(result.getBody().getContent()).isEqualTo(ENTITY).as("Check the body is ok");
                    assertThat(result.getBody().hasLinks()).isTrue().as("Check the resource has link");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void controller_put_creation_operation_should_return_status_200_and_entity_and_link() {

        EntityDataState<String> entityDataState = EntityDataState.<String>builder()
                .entity(ENTITY)
                .domainState(EntityDataState.State.updated)
                .build();

        StepVerifier
                .create(crudHypermediaController.updateOrCreate(Mono.just(entityDataState)))
                .expectNextMatches(result -> {
                    assertThat(result.getStatusCodeValue()).isEqualTo(200).as("Check code status is 200");
                    assertThat(result.getBody().getContent()).isEqualTo(ENTITY).as("Check the body is ok");
                    assertThat(result.getBody().hasLinks()).isTrue().as("Check the resource has link");
                    return true;
                })
                .verifyComplete();
    }

    private static class Test1BasicReactiveRepresentationModelAssembler
            implements BasicReactiveRepresentationModelAssembler<String> {

        public Flux<Link> addLinks(String entity, ServerWebExchange exchange) {
            return Flux.just(new Link(HREF));
        }
    }
}
