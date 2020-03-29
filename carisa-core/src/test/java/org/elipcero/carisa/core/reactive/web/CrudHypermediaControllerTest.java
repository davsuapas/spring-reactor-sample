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

import org.elipcero.carisa.core.data.ChildName;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.ParentChildName;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.elipcero.carisa.core.reactive.data.DependencyRelationChildNotFoundException;
import org.elipcero.carisa.core.reactive.data.DependencyRelationParentNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;

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
    public void controller_creation_operation_should_return_status_404() {

        StepVerifier
                .create(crudHypermediaController.create(
                        Mono.error(new DependencyRelationParentNotFoundException("error"))))
                .expectErrorMessage("404 NOT_FOUND \"error\"")
                .verify();
    }

    @Test
    public void controller_creation_operation_should_return_status_500() {

        StepVerifier
                .create(crudHypermediaController.create(
                        Mono.error(new ArithmeticException())))
                .expectErrorMessage("500 INTERNAL_SERVER_ERROR")
                .verify();
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

    @Test
    public void controller_get_operation_should_return_status_200_and_entity_and_link() {

        StepVerifier
                .create(crudHypermediaController.get(Mono.just(ENTITY)))
                .expectNextMatches(result -> {
                    assertThat(result.getContent()).isEqualTo(ENTITY).as("Check the content is ok");
                    assertThat(result.hasLinks()).isTrue().as("Check the resource has link");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void controller_connectToParent_should_return_status_200_and_entity_and_link() {

        StepVerifier
                .create(crudHypermediaController.connectToParent(Mono.just(ENTITY)))
                .expectNextMatches(result -> {
                    assertThat(result.getStatusCodeValue()).isEqualTo(200).as("Check code status is 200");
                    assertThat(result.getBody().getContent()).isEqualTo(ENTITY).as("Check the body is ok");
                    assertThat(result.getBody().hasLinks()).isTrue().as("Check the resource has link");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void controller_connectToParent_child_not_found_should_return_status_204() {

        StepVerifier
                .create(crudHypermediaController.connectToParent(
                        Mono.error(new DependencyRelationChildNotFoundException("error"))))
                .expectErrorMessage("404 NOT_FOUND \"error\"")
                .verify();
    }

    @Test
    public void controller_connectToParent_parent_not_found_should_return_status_404() {

        StepVerifier
                .create(crudHypermediaController.connectToParent(
                        Mono.error(new DependencyRelationParentNotFoundException("error"))))
                .expectErrorMessage("404 NOT_FOUND \"error\"")
                .verify();
    }

    @Test
    public void controller_connectToParent_any_exception_should_return_status_500() {

        StepVerifier
                .create(crudHypermediaController.connectToParent(Mono.error(new ArithmeticException())))
                .expectErrorMessage("500 INTERNAL_SERVER_ERROR")
                .verify();
    }

    @Test
    public void controller_connectToParent_customized_exception_should_return_status_400() {

        StepVerifier
                .create(crudHypermediaController.connectToParent(
                        Mono.error(new ArithmeticException()),
                        e -> {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
                        }))
                .expectErrorMessage("400 BAD_REQUEST \"error\"")
                .verify();
    }

    @Test
    public void controller_get_children_by_parent_operation_should_return_entity_model() {

        ParentChildName parentChildName =
                ParentChildName
                        .builder()
                            .parentId(UUID.randomUUID())
                            .childId(UUID.randomUUID())
                            .name("name")
                        .build();

        StepVerifier
                .create(crudHypermediaController.childrenByParent(
                        parentChildName.getParentId().toString(), Flux.just(parentChildName),
                        ParentChildController.class, "parentRelName",
                        ParentChildController.class, "childRelName"))
                .expectNextMatches(result -> {
                    Optional<EntityModel<ChildName>> r = result.getContent().stream().findFirst();
                    assertThat(r.get().getContent().getId()).isEqualTo(parentChildName.getChildId())
                            .as("Check child identifier");
                    assertThat(r.get().getContent().getName()).isEqualTo(parentChildName.getName())
                            .as("Check name");
                    assertThat(r.get().getLinks().hasSingleLink()).isTrue().as("Check child links");
                    assertThat(result.getLinks().hasSingleLink()).isTrue().as("Check parent links");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void controller_get_children_by_parent_operation_with_bi_key_should_return_entity_model() {

        ParentChildName parentChildName =
                ParentChildName
                        .builder()
                            .parentId(UUID.randomUUID())
                            .childId(UUID.randomUUID())
                            .name("name")
                        .build();

        StepVerifier
                .create(crudHypermediaController.childrenByParentWithBiKey(
                        parentChildName.getParentId().toString(), Flux.just(parentChildName),
                        ParentChildController.class, "parentRelName",
                        ParentChildController.class, "childRelName"))
                .expectNextMatches(result -> {
                    Optional<EntityModel<ChildName>> r = result.getContent().stream().findFirst();
                    assertThat(r.get().getContent().getId()).isEqualTo(parentChildName.getChildId())
                            .as("Check child identifier");
                    assertThat(r.get().getContent().getName()).isEqualTo(parentChildName.getName())
                            .as("Check name");
                    assertThat(r.get().getLinks().hasSingleLink()).isTrue().as("Check child links");
                    assertThat(result.getLinks().hasSingleLink()).isTrue().as("Check parent links");
                    return true;
                })
                .verifyComplete();
    }

    public static class ParentChildController
            implements ChildControllerHypermedia<String>, BiKeyChildControllerHypermedia<String> {

        @Override
        public Publisher<EntityModel<String>> getById(String id) {
            return null;
        }

        @Override
        public Publisher<EntityModel<String>> getById(String parentId, String childId) {
            return null;
        }
    }

    private static class Test1BasicReactiveRepresentationModelAssembler
            implements BasicReactiveRepresentationModelAssembler<String> {

        public Flux<Link> addLinks(String entity, ServerWebExchange exchange) {
            return Flux.just(new Link(HREF));
        }
    }
}
