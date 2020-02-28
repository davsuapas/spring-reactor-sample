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

import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_entecategoryproperty_controller" })
public class EnteCategoryPropertyControllerTest extends DataAbstractControllerTest {

    // Look at ente-category-property-controller.cql
    private static final String ENTE_PROPERTY_NAME = "Ente category property name";
    private static final String ENTE_PROPERTY_ID = "b9439ad1-4419-4765-acca-55ce69179c0f";
    // Look at ente-category-controller.cql
    private static final String ENTE_CATEGORY_ID = "83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("ente-category-controller.cql");
            this.executeCommands("ente-category-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_ente_category_property_should_return_ok_and_ente_category_property_entity() {

        this.testClient
                .get()
                .uri("/api/entecategories/{enteCategoryId}/properties/{propertyId}", ENTE_CATEGORY_ID, ENTE_PROPERTY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.id").isEqualTo(ENTE_PROPERTY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entecategoryproperties-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_ente_category_property_using_post_should_return_created_and_ente_category_property_entity() {

        this.testClient
                .post()
                .uri("/api/entecategoryproperties").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entecategoryproperties-post",
                        commonRequestFields(
                           Arrays.asList(
                              fieldWithPath("enteCategoryId")
                                      .description("Ente category identifier (UUID) for this ente category property"))),
                        commonResponseFields()));
    }

    @Test
    public void create_entecategoryproperty_using_put_should_return_created_and_entecategoryproperty_entity() {

        String propertyId = "361370a0-e3e5-45e5-b675-a55fe923873f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{propertyId}", ENTE_CATEGORY_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(propertyId)
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("enteproperties-put",
                        commonPathParameters(),
                        commonRequestFields(Arrays.asList(fieldWithPath("enteCategoryId").ignored())),
                        commonResponseFields()));
    }

    @Test
    public void update_entecategoryproperty_using_put_should_return_ok_and_entecategoryproperty_entity() {

        String propertyId = "c9439ad1-4419-4765-acca-55ce69179c0f"; // Look at entecategoryproperties-controller
        String newName = "Ente category property name updated";

        EnteProperty entePropertyUpdated = EnteProperty.builder().name(newName).build();

        entePropertyUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{propertyId}", ENTE_CATEGORY_ID, propertyId)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(entePropertyUpdated), EnteProperty.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(propertyId)
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_enteproperty_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/entecategories/{enteCategoryId}/properties/{propertyId}",
                        ENTE_CATEGORY_ID, ENTE_PROPERTY_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='enteCategoryId')].name")
                        .isEqualTo("enteCategoryId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/entecategoryproperties")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='enteCategoryId')].name")
                        .isEqualTo("enteCategoryId");
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Ente category property name"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("enteCategoryId").description("Ente category identifier (UUID string format)"),
                parameterWithName("propertyId").description("Ente property identifier (UUID string format)"));
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("enteCategoryId").description("Ente category identifier (UUID) for this ente property"),
                fieldWithPath("id").description("Ente property identifier (UUID)"),
                fieldWithPath("name").description("Ente property name"),
                subsectionWithPath("_links")
                        .description("The ente links. " + StringResource.METADATA_INFORMATION));
    }

    private static EnteCategoryProperty createEnteCategoryProperty() {
        return EnteCategoryProperty.builder()
                    .name(ENTE_PROPERTY_NAME)
                    .parentId(UUID.fromString(ENTE_CATEGORY_ID))
                .build();
    }
}
