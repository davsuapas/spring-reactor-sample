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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.hateoas.Link;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
public class BasicReactiveRepresentationModelAssemblerTest {

    public static final String HREF = "href";
    public static final String ENTITY = "Hola";

    @Test
    public void assembler_entity_toModel_should_return_entityModel_with_link() {

        Test1BasicReactiveRepresentationModelAssembler assembler = new Test1BasicReactiveRepresentationModelAssembler();

        StepVerifier
                .create(assembler.toModel(ENTITY, null))
                    .expectNextMatches(result -> {
                        assertThat(result.getContent()).isEqualTo(ENTITY).as("Check entity name is ok");
                        assertThat(result.hasLinks()).isTrue().as("Check the resource has link");
                        return true;
                    })
                .verifyComplete();
    }

    @Test
    public void assembler_entity_toModel_should_return_entityModel_noLink() {

        Test2BasicReactiveRepresentationModelAssembler assembler = new Test2BasicReactiveRepresentationModelAssembler();

        StepVerifier
                .create(assembler.toModel(ENTITY, null))
                .expectNextMatches(result -> {
                    assertThat(result.getContent()).isEqualTo(ENTITY).as("Check entity name is ok");
                    assertThat(result.hasLinks()).isFalse().as("Check the resource has not link");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void assembler_entity_toCollectionModel_should_return_entityModel_with_link() {

        Test1BasicReactiveRepresentationModelAssembler assembler = new Test1BasicReactiveRepresentationModelAssembler();

        StepVerifier
                .create(assembler.toCollectionModel(Flux.just(ENTITY, ENTITY), null))
                .expectNextMatches(result -> {
                    assertThat(result.getContent().size()).isEqualTo(2).as("Check collection model size is 2");
                    assertThat(result.getContent().stream().findFirst().get().getContent()).isEqualTo(ENTITY)
                            .as("Check item1 of collection model is ok");
                    assertThat(result.getContent().stream().findFirst().get().hasLinks()).isTrue()
                            .as("Check item1 of collection model has links");
                    assertThat(result.hasLinks()).isTrue().as("Check the resources has link");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void assembler_entity_toCollectionModel_should_return_entityModel_with_noLink() {

        Test2BasicReactiveRepresentationModelAssembler assembler = new Test2BasicReactiveRepresentationModelAssembler();

        StepVerifier
                .create(assembler.toCollectionModel(Flux.just(ENTITY, ENTITY), null))
                .expectNextMatches(result -> {
                    assertThat(result.getContent().size()).isEqualTo(2).as("Check collection model size is 2");
                    assertThat(result.getContent().stream().findFirst().get().getContent()).isEqualTo(ENTITY)
                            .as("Check item1 of collection model is ok");
                    assertThat(result.getContent().stream().findFirst().get().hasLinks()).isFalse()
                            .as("Check item1 of collection model has not links");
                    assertThat(result.hasLinks()).isFalse().as("Check the resources has not link");
                    return true;
                })
                .verifyComplete();

    }

    private static class Test1BasicReactiveRepresentationModelAssembler
            implements BasicReactiveRepresentationModelAssembler<String> {

        public Flux<Link> addLinks(String entity, ServerWebExchange exchange) {
            return Flux.just(new Link(HREF));
        }

        public Flux<Link> addLinks(ServerWebExchange exchange) {
            return Flux.just(new Link(HREF));
        }
    }

    private static class Test2BasicReactiveRepresentationModelAssembler
            implements BasicReactiveRepresentationModelAssembler<String> {
    }
}
