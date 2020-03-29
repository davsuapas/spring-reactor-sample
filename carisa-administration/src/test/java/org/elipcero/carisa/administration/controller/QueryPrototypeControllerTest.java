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

import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.general.StringResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * @author David Suárez
 */
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_query_prototype_controller" })
public class QueryPrototypeControllerTest extends DataAbstractControllerTest {

    private static final String QUERY_ID = "a985074c-796b-4ecb-9a8f-21f4b26aa11b"; // Look at query-prototype-controller
    private static final String SPACE_ID = "52107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
    private static final String QUERY_NAME = "Query type name";

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("space-controller.cql");
            this.executeCommands("space-query-prototype-controller.cql");
            this.executeCommands("query-prototype-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_query_should_return_ok_and_query_entity() {

        this.testClient
                .get()
                .uri("/api/queryprototypes/{id}", QUERY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queries-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_using_post_should_return_created_and_query_entity() {

        this.testClient
                .post()
                .uri("/api/queryprototypes").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQuery()), DynamicObjectPrototype.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryprototypes-post",
                        commonRequestFields(
                                Arrays.asList(
                                    fieldWithPath("parentId")
                                            .description("Space identifier (UUID) for this query prototype"))),
                        commonResponseFields()));
    }

    @Test
    public void create_query_using_put_should_return_created_and_query_entity() {

        String id = "4837648b-896b-4a91-b579-8a02cc6ce031";

        this.testClient
                .put()
                .uri("/api/queryprototypes/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQuery()), DynamicObjectPrototype.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryprototypes-put",
                        commonPathParameters(),
                        commonRequestFields(
                                Arrays.asList(
                                    fieldWithPath("parentId")
                                            .description("Space identifier (UUID) for this query prototype." +
                                                    "This property can not be updated"))),
                        commonResponseFields()));
    }

    @Test
    public void update_query_using_put_should_return_ok_and_query_entity() {

        String id = "b985074c-796b-4ecb-9a8f-21f4b26aa11b"; // Look at query-prototype-controller
        String newName = "Query type updated";

        DynamicObjectPrototype queryUpdated = DynamicObjectPrototype
                .builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                    .parentId(UUID.fromString(SPACE_ID))
                .build();

        queryUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/queryprototypes/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(queryUpdated), DynamicObjectPrototype.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_query_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/queryprototypes/{id}", QUERY_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='parentId')].name")
                        .isEqualTo("parentId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/queryprototypes")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='parentId')].name")
                        .isEqualTo("parentId");
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Query prototype name. Describe the query type"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("id").description("Query prototype id (UUID string format)")
        );
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Ente identifier (UUID)"),
                fieldWithPath("parentId").description("Space identifier (UUID) for this query prototype"),
                fieldWithPath("name").description("Query prototype name. Describe the query type"),
                subsectionWithPath("_links")
                        .description("The query prototype links. " + StringResource.METADATA_INFORMATION));
    }

    private static DynamicObjectPrototype createQuery() {
        return DynamicObjectPrototype.builder()
                    .name(QUERY_NAME)
                    .parentId(UUID.fromString(SPACE_ID))
                .build();
    }
}
