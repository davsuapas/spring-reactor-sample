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

import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
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
import java.util.Collections;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_instanceproperty_controller" })
public class QueryInstancePropertyControllerTest extends DataAbstractControllerTest {

    // Look at query-instance-property-controller.cql
    private static final String INSTANCE_PROPERTY_ID = "c6b34eb0-e15e-4e5a-a20d-7548a6967085";
    private static final String INSTANCE_ID = "a985074c-796b-4ecb-9a8f-21f4b26aa11b"; // Look at query-instance-controller.cql

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("query-instance-controller.cql");
            this.executeCommands("query-instance-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_query_property_should_return_ok_and_query_property_entity() {

        this.testClient
                .get()
                .uri("/api/queryinstances/{instanceId}/properties/{propertyId}", INSTANCE_ID, INSTANCE_PROPERTY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$.id").isEqualTo(INSTANCE_PROPERTY_ID)
                    .jsonPath("$._links.queryinstance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryinstancepeproperties-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_property_using_post_should_return_created_and_query_property_entity() {

        this.testClient
                .post()
                .uri("/api/queryinstanceproperties").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQueryInstanceIntegerProperty()), DynamicObjectInstanceProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    //.jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    //.jsonPath("$.value").isEqualTo(1)
                    .jsonPath("$._links.queryinstance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryinstancepeproperties-post",
                        commonRequestFields(
                                Collections.singletonList(
                                        fieldWithPath("instanceId")
                                            .description("Query instance identifier (UUID) for this property"))),
                        commonResponseFields()));
    }

    @Test
    public void create_query_property_using_put_should_return_created_and_query_property_entity() {

        String propertyId = "361370a0-e3e5-45e5-b675-a55fe923873f";

        this.testClient
                .put()
                .uri("/api//queryinstances/{instanceId}/properties/{propertyId}", INSTANCE_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQueryInstanceIntegerProperty()), DynamicObjectInstanceProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$._links.queryinstance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryinstancepeproperties-put",
                        commonPathParameters(),
                        commonRequestFields(Collections.singletonList(fieldWithPath("instanceId").ignored())),
                        commonResponseFields()));
    }

    @Test
    public void update_query_property_using_put_should_return_ok_and_query_property_entity() {

        String propertyId = "d6b34eb0-e15e-4e5a-a20d-7548a6967085"; // Look at query-instance-property-controller.cql

        DynamicObjectInstanceProperty<?> updatedProperty = DynamicObjectInstanceProperty.builder()
                .build();

        this.testClient
                .put()
                .uri("/api//queryinstances/{instanceId}/properties/{propertyId}", INSTANCE_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(updatedProperty), DynamicObjectInstanceProperty.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$.id").isEqualTo(propertyId)
                    .jsonPath("$._links.queryinstance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_query_property_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api//queryinstances/{instanceId}/properties/{propertyId}", INSTANCE_ID, INSTANCE_PROPERTY_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='instanceId')].name")
                        .isEqualTo("instanceId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/queryinstanceproperties")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='instanceId')].name")
                        .isEqualTo("instanceId");
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("type")
                .description("The type of value: (Integer, Decimal, Boolean, DateTime, HierarchyBinding) "));
        fieldDescriptor.add(fieldWithPath("value").description("The value depending of the type"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("instanceId").description("Query instance identifier (UUID string format)"),
                parameterWithName("propertyId").description("Query instance property identifier (UUID string format)"));
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("instanceId").description("Query instance identifier (UUID) for this property"),
                fieldWithPath("id").description("Query instance property identifier (UUID)"),
                fieldWithPath("type").description("The type of value: (Integer, Decimal, Boolean, DateTime, HierarchyBinding) "),
                fieldWithPath("value").description("The value depending of the type"),
                subsectionWithPath("_links")
                        .description("Query instance property links. " + StringResource.METADATA_INFORMATION));
    }

    private static DynamicObjectInstanceProperty<?> createQueryInstanceIntegerProperty() {
        return DynamicObjectInstanceProperty.builder()
                    .parentId(UUID.fromString(INSTANCE_ID))
                    .value(new DynamicObjectInstanceProperty.IntegerValue(1))
                .build();
    }
}
