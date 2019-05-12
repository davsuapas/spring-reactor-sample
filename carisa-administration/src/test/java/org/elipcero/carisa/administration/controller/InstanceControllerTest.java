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
import org.junit.Test;

/**
 * @author David Suárez
 */
@CassandraDataSet(keyspace = DataConfiguration.CONST_KEY_SPACE_NAME, value = "cassandra/instance-controller.cql")
public class InstanceControllerTest extends AbstractControllerTest {

    @Test
    public void find_Instance_should_return_ok_and_instance_entity() {

        this.testClient
                .get()
                .uri("api/instance/5b6962dd-3f90-4c93-8f61-eabfa4a803e2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo("test instance name");
    }
}
