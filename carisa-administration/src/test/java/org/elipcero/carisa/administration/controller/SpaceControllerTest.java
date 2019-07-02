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

import org.cassandraunit.spring.CassandraDataSet;
import org.elipcero.carisa.administration.configuration.DataConfiguration;
import org.elipcero.carisa.administration.general.StringResource;
import org.junit.Test;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * @author David Suárez
 */
@AutoConfigureWireMock
@CassandraDataSet(keyspace = DataConfiguration.CONST_KEY_SPACE_NAME, value = "cassandra/space-controller.cql")
public class SpaceControllerTest extends CassandraAbstractControllerTest {

    private static final String SPACE_ID = "52107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
    private static final String INSTANCE_ID = "523170e2-cd49-4261-913b-1a4c0c7652cf"; // Look at space-controller
    private static final String SPACE_NAME = "Space name"; // Look at space-controller

    @Test
    public void find_space_should_return_ok_and_space_entity() {

        this.testClient
                .get()
                .uri("/api/spaces/{id}", SPACE_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(SPACE_NAME)
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("spaces-get",
                        commonPathParamters(),
                        commonResponseFields()));
    }


    private static PathParametersSnippet commonPathParamters() {
        return pathParameters(
                parameterWithName("id").description("Space id (UUID string format)")
        );
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Space identifier (UUID)"),
                fieldWithPath("instanceId").description("Instance identifier (UUID) for this space"),
                fieldWithPath("name").description("Space name"),
                subsectionWithPath("_links")
                        .description("The space links. " + StringResource.METADATA_INFORMATION));
    }
}
