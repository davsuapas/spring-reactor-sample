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

import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_prototypeproperty_controller" })
public class QueryPluginPrototypePropertyControllerTest extends DataAbstractControllerTest {

    // Look at query-prototype-property-controller.cql
    private static final String PROTOTYPE_PROPERTY_ID = "249f1073-3164-4ed0-9ad5-4415945b273f";
    // Look at query-prototype-property-controller.cql
    private static final String PROTOTYPE_PROPERTY_NAME = "Query property name";
    private static final String PROTOTYPE_PROPERTY_DESCRIPTION = "Query property description";
    private static final String PROTOTYPE_ID = "5d191729-1f4c-4b7e-b573-b90cf3457df8"; // Look at query-prototype-controller.cql

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("query-prototype-controller.cql");
            this.executeCommands("query-prototype-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_query_property_should_return_ok_and_query_property_entity() {

        this.testClient
                .get()
                .uri("/api/queriesplugin/{prototypeId}/properties/{propertyId}", PROTOTYPE_ID, PROTOTYPE_PROPERTY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.prototypeId").isEqualTo(PROTOTYPE_ID)
                    .jsonPath("$.id").isEqualTo(PROTOTYPE_PROPERTY_ID)
                    .jsonPath("$.name").isEqualTo(PROTOTYPE_PROPERTY_NAME)
                    .jsonPath("$.description").isEqualTo(PROTOTYPE_PROPERTY_DESCRIPTION)
                    .jsonPath("$.type").isEqualTo(DynamicObjectPrototypeProperty.Type.HierarchyBinding.toString())
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryprototypeproperties-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_property_using_post_should_return_created_and_query_property_entity() {

        this.testClient
                .post()
                .uri("/api/querypluginproperties").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQueryProtoypeProperty()), DynamicObjectPrototypeProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.prototypeId").isEqualTo(PROTOTYPE_ID)
                    .jsonPath("$.name").isEqualTo(PROTOTYPE_PROPERTY_NAME)
                    .jsonPath("$.description").isEqualTo(PROTOTYPE_PROPERTY_DESCRIPTION)
                    .jsonPath("$.type").isEqualTo(DynamicObjectPrototypeProperty.Type.HierarchyBinding.toString())
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryprototypeproperties-post",
                        commonRequestFields(
                           Arrays.asList(
                              fieldWithPath("prototypeId").description("Query plugin identifier (UUID) for this property"))),
                        commonResponseFields()));
    }

    @Test
    public void create_query_property_using_put_should_return_created_and_query_property_entity() {

        String propertyId = "361370a0-e3e5-45e5-b675-a55fe923873f";

        this.testClient
                .put()
                .uri("/api/queriesplugin/{prototypeId}/properties/{propertyId}", PROTOTYPE_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQueryProtoypeProperty()), DynamicObjectPrototypeProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.prototypeId").isEqualTo(PROTOTYPE_ID)
                    .jsonPath("$.name").isEqualTo(PROTOTYPE_PROPERTY_NAME)
                    .jsonPath("$.description").isEqualTo(PROTOTYPE_PROPERTY_DESCRIPTION)
                    .jsonPath("$.type").isEqualTo(DynamicObjectPrototypeProperty.Type.HierarchyBinding.toString())
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryprototypeproperties-put",
                        commonPathParameters(),
                        commonRequestFields(Arrays.asList(fieldWithPath("prototypeId").ignored())),
                        commonResponseFields()));
    }

    @Test
    public void update_query_property_using_put_should_return_ok_and_query_property_entity() {

        String propertyId = "349f1073-3164-4ed0-9ad5-4415945b273f"; // Look at query-prototype-property-controller.cql
        String newName = "Updated query property name";
        String newDescription = "Updated query property description";

        DynamicObjectPrototypeProperty updatedProperty = DynamicObjectPrototypeProperty
                .builder()
                    .name(newName)
                    .description(newDescription)
                .build();

        this.testClient
                .put()
                .uri("/api/queriesplugin/{prototypeId}/properties/{propertyId}", PROTOTYPE_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(updatedProperty), DynamicObjectPrototypeProperty.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.prototypeId").isEqualTo(PROTOTYPE_ID)
                    .jsonPath("$.id").isEqualTo(propertyId)
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.description").isEqualTo(newDescription)
                    .jsonPath("$.type").isEqualTo(DynamicObjectPrototypeProperty.Type.HierarchyBinding.toString())
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_query_property_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/queriesplugin/{prototypeId}/properties/{propertyId}", PROTOTYPE_ID, PROTOTYPE_PROPERTY_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='prototypeId')].name")
                        .isEqualTo("prototypeId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/querypluginproperties")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='prototypeId')].name")
                        .isEqualTo("prototypeId");
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Query plugin prototype property name"));
        fieldDescriptor.add(fieldWithPath("description").description("Query plugin prototype property description"));
        fieldDescriptor.add(fieldWithPath("type")
                .description("Query prototype property type (Integer, Decimal, Boolean, DateTime, HierarchyBinding)"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("prototypeId").description("Query plugin identifier (UUID string format)"),
                parameterWithName("propertyId").description("Query plugin prototype property identifier (UUID string format)"));
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("prototypeId").description("Query plugin identifier (UUID) for this property"),
                fieldWithPath("id").description("Query plugin prototype property identifier (UUID)"),
                fieldWithPath("name").description("Query plugin prototype property name"),
                fieldWithPath("description").description("Query plugin prototype property description"),
                fieldWithPath("type")
                        .description("Query prototype property type (Integer, Decimal, Boolean, DateTime, HierarchyBinding)"),
                subsectionWithPath("_links")
                        .description("Query plugin prototype property links. " + StringResource.METADATA_INFORMATION));
    }

    private static DynamicObjectPrototypeProperty createQueryProtoypeProperty() {
        return DynamicObjectPrototypeProperty.builder()
                    .name(PROTOTYPE_PROPERTY_NAME)
                    .description(PROTOTYPE_PROPERTY_DESCRIPTION)
                    .parentId(UUID.fromString(PROTOTYPE_ID))
                    .type(DynamicObjectPrototypeProperty.Type.HierarchyBinding)
                .build();
    }
}
