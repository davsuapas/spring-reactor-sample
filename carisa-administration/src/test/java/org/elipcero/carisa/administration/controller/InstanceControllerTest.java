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

import org.cassandraunit.spring.CassandraDataSet;
import org.elipcero.carisa.administration.configuration.DataConfiguration;
import org.elipcero.carisa.administration.domain.Instance;
import org.junit.Test;
import org.springframework.hateoas.MediaTypes;
import reactor.core.publisher.Mono;

/**
 * @author David Suárez
 */
@CassandraDataSet(keyspace = DataConfiguration.CONST_KEY_SPACE_NAME, value = "cassandra/instance-controller.cql")
public class InstanceControllerTest extends AbstractControllerTest {

    public static final String INSTANCE_NAME = "test instance name";

    @Test
    public void Find_Instance_Should_Return_Ok_And_Instance_Entity() {

        this.testClient
                .get()
                .uri("/api/instance/5b6962dd-3f90-4c93-8f61-eabfa4a803e2")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void create_Instance_Should_Return_Created_and_Instance_Entity() {

        Instance instance =
                Instance.builder()
                    .name(INSTANCE_NAME)
                .build();

        this.testClient
                .post()
                .uri("/api/instance").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(instance), Instance.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                .jsonPath("$._links.self.href").hasJsonPath();
    }
}
