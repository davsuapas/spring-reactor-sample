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
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @author David Suárez
 */
@CassandraDataSet(keyspace = DataConfiguration.CONST_KEY_SPACE_NAME, value = "cassandra/instance-controller.cql")
public class InstanceControllerTest extends AbstractControllerTest {

    private static final String INSTANCE_NAME = "test instance name";

    @Autowired
    private InstanceRepository instanceRepository;

    @Test
    public void find_instance_should_return_ok_and_instance_entity() {

        this.testClient
                .get()
                .uri("/api/instance/5b6962dd-3f90-4c93-8f61-eabfa4a803e2") // Look at instance-controller
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void create_instance_using_post_should_return_created_and_instance_entity() {

        Instance instance = createInstance();

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

    @Test
    public void create_instance_using_put_should_return_ok_and_instance_entity() {

        Instance instance = createInstance();

        String id = "8b6962dd-3f90-4c93-8f61-eabfa4a803e2";

        this.testClient
                .put()
                .uri("/api/instance/" + id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(instance), Instance.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                .jsonPath("$._links.self.href").value(containsString(id));
    }

    @Test
    public void update_instance_using_put_should_return_ok_and_instance_entity() {

        String id = "9b6962dd-3f90-4c93-8f61-eabfa4a803e2"; // Look at instance-controller
        String newName = "test instance name 1";

        Instance instanceUpdated = Instance.builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                .build();

        instanceUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/instance/" + id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(instanceUpdated), Instance.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(newName)
                .jsonPath("$._links.self.href").value(containsString(id));
    }

    private Instance createInstance() {
        return Instance.builder()
                .name(INSTANCE_NAME)
                .build();
    }
}
