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

import org.elipcero.carisa.administration.domain.EnteProperty;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_enteproperty_controller" })
public class EntePropertyControllerTest extends DataAbstractControllerTest {

    private static final String ENTE_PROPERTY_NAME = "Ente property name"; // Look at ente-property-controller.cql
    private static final String ENTE_PROPERTY_ID = "c0838415-6ae2-4914-b202-f1b3adbf0353";
    private static final String ENTE_ID = "7acdac69-fdf8-45e5-a189-2b2b4beb1c26"; // Look at ente-controller.cql

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("ente-controller.cql");
            this.executeCommands("ente-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_ente_property_should_return_ok_and_ente_property_entity() {

        this.testClient
                .get()
                .uri("/api/entes/{enteId}/properties/{propertyId}", ENTE_ID, ENTE_PROPERTY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.enteId").isEqualTo(ENTE_ID)
                    .jsonPath("$.id").isEqualTo(ENTE_PROPERTY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.Integer.toString())
                    .jsonPath("$._links.ente.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("enteproperties-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_ente_property_using_post_should_return_created_and_ente_property_entity() {

        this.testClient
                .post()
                .uri("/api/enteproperties").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteProperty()), EnteProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.enteId").isEqualTo(ENTE_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.Boolean.toString())
                    .jsonPath("$._links.ente.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("enteproperties-post",
                        commonRequestFields(
                           Arrays.asList(
                              fieldWithPath("enteId").description("Ente identifier (UUID) for this ente property"))),
                        commonResponseFields()));
    }

    @Test
    public void create_enteproperty_using_put_should_return_created_and_enteproperty_entity() {

        String propertyId = "361370a0-e3e5-45e5-b675-a55fe923873f";

        this.testClient
                .put()
                .uri("/api/entes/{enteId}/properties/{propertyId}", ENTE_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteProperty()), EnteProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.enteId").isEqualTo(ENTE_ID)
                    .jsonPath("$.id").isEqualTo(propertyId)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.Boolean.toString())
                    .jsonPath("$._links.ente.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("enteproperties-put",
                        commonPathParameters(),
                        commonRequestFields(Arrays.asList(fieldWithPath("enteId").ignored())),
                        commonResponseFields()));
    }

    @Test
    public void update_enteproperty_using_put_should_return_ok_and_enteproperty_entity() {

        String propertyId = "d0838415-6ae2-4914-b202-f1b3adbf0353"; // Look at enteproperties-controller
        String newName = "Ente property name updated";

        EnteProperty entePropertyUpdated = EnteProperty
                .builder()
                    .name(newName)
                    .type(EnteProperty.Type.DateTime)
                .build();

        entePropertyUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/entes/{enteId}/properties/{propertyId}", ENTE_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(entePropertyUpdated), EnteProperty.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.enteId").isEqualTo(ENTE_ID)
                    .jsonPath("$.id").isEqualTo(propertyId)
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.DateTime.toString())
                    .jsonPath("$._links.ente.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_enteproperty_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/entes/{enteId}/properties/{propertyId}", ENTE_ID, ENTE_PROPERTY_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='enteId')].name")
                        .isEqualTo("enteId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/enteproperties")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='enteId')].name")
                        .isEqualTo("enteId");
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Ente property name"));
        fieldDescriptor.add(fieldWithPath("type")
                .description("Ente property type (Integer, Decimal, Boolean, DateTime)"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("enteId").description("Ente identifier (UUID string format)"),
                parameterWithName("propertyId").description("Ente property identifier (UUID string format)"));
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("enteId").description("Ente identifier (UUID) for this ente property"),
                fieldWithPath("id").description("Ente property identifier (UUID)"),
                fieldWithPath("name").description("Ente property name"),
                fieldWithPath("type").description("Ente property type (Integer, Decimal, Boolean, DateTime)"),
                subsectionWithPath("_links")
                        .description("The ente links. " + StringResource.METADATA_INFORMATION));
    }

    private static EnteProperty createEnteProperty() {
        return EnteProperty.builder()
                    .name(ENTE_PROPERTY_NAME)
                    .parentId(UUID.fromString(ENTE_ID))
                    .type(EnteProperty.Type.Boolean)
                .build();
    }
}
