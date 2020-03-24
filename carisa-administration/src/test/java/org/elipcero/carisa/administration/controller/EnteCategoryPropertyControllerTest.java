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
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
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
    private static final String CATEGORY_PROPERTY_ID = "b9439ad1-4419-4765-acca-55ce69179c0f";
    // Look at ente-category-controller.cql
    private static final String ENTE_CATEGORY_ID = "83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";

    // Look at ente-controller.cql
    public static final String CONNECT_ENTE_ID = "7acdac69-fdf8-45e5-a189-2b2b4beb1c26";
    // Look at ente-property-controller.cql
    public static final String CONNECT_ENTE_PROPERTY_ID = "c0838415-6ae2-4914-b202-f1b3adbf0353";

    // Look at ente-category-controller.cql
    public static final String CONNECT_CATEGORY_CATEGORY_ID = "33ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";
    public static final String CONNECT_CATEGORY_LINKED_CATEGORY_ID = "23ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";
    // Look at ente-category-property-controller.cql
    public static final String CONNECT_CATEGORY_CATEGORY_PROP_ID = "f9439ad1-4419-4765-acca-55ce69179c0f";
    public static final String CONNECT_CATEGORY_LINKED_CATEGORY_PROP_ID = "a9439ad1-4419-4765-acca-55ce69179c0f";

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("ente-category-controller.cql");
            this.executeCommands("ente-category-property-controller.cql");
            this.executeCommands("ente-category-property-link-controller.cql");
            this.executeCommands("ente-hierarchy-controller.cql");
            this.executeCommands("ente-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_ente_category_property_should_return_ok_and_ente_category_property_entity() {

        this.testClient
                .get()
                .uri("/api/entecategories/{enteCategoryId}/properties/{propertyId}",
                        ENTE_CATEGORY_ID, CATEGORY_PROPERTY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.id").isEqualTo(CATEGORY_PROPERTY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.None.toString())
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath()
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
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.None.toString())
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath()
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
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.None.toString())
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath()
                .consumeWith(document("entecategoryproperties-put",
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
        entePropertyUpdated.setType(EnteProperty.Type.Integer);

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
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.None.toString())
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath();
    }

    @Test
    public void connect_ente_to_category_property_using_put_should_return_linked_entecategoryproperty() {

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                        "/connectente/{enteId}/properties/{entePropertyId}",
                        ENTE_CATEGORY_ID, CATEGORY_PROPERTY_ID, CONNECT_ENTE_ID, CONNECT_ENTE_PROPERTY_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(CATEGORY_PROPERTY_ID)
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.Integer.toString())
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath()
                .consumeWith(document("entecategoryproperties-connectoente-put",
                        commonConnectPathParameters(Arrays.asList(
                                parameterWithName("enteId")
                                        .description("Connected Ente identifier (UUID string format)"),
                                parameterWithName("entePropertyId")
                                        .description("Connected Ente property identifier (UUID string format)"))
                        ),
                        commonResponseFields()));
    }

    @Test
    public void connect_ente_to_category_property_the_types_are_same_should_return_propertyCategory_with_int_type() {

        String categoryPropertyId = "d9439ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectente/{enteId}/properties/{entePropertyId}",
                        ENTE_CATEGORY_ID, categoryPropertyId, CONNECT_ENTE_ID, CONNECT_ENTE_PROPERTY_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(categoryPropertyId)
                    .jsonPath("$.enteCategoryId").isEqualTo(ENTE_CATEGORY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.Integer.toString())
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath();
    }

    @Test
    public void connect_ente_to_category_property_the_types_are_not_same_should_return_conflict_error() {

        String categoryPropertyId = "e9439ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectente/{enteId}/properties/{entePropertyId}",
                        ENTE_CATEGORY_ID, categoryPropertyId, CONNECT_ENTE_ID, CONNECT_ENTE_PROPERTY_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void connect_ente_to_category_property_should_return_category_property_not_found() {

        String categoryPropertyId = "e8437ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectente/{enteId}/properties/{entePropertyId}",
                        ENTE_CATEGORY_ID, categoryPropertyId, CONNECT_ENTE_ID, CONNECT_ENTE_PROPERTY_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void connect_ente_to_category_property_should_return_ente_not_found_as_child_of_category() {

        String enteId = "b8437ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectente/{enteId}/properties/{entePropertyId}",
                        ENTE_CATEGORY_ID, CATEGORY_PROPERTY_ID, enteId, CONNECT_ENTE_PROPERTY_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void connect_ente_to_category_property_should_return_ente_property_not_found() {

        String entePropertyId = "b8637ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectente/{enteId}/properties/{entePropertyId}",
                        ENTE_CATEGORY_ID, CATEGORY_PROPERTY_ID, CONNECT_ENTE_ID, entePropertyId)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void connect_category_prop_to_category_prop_using_put_should_return_linked_entecategoryproperty() {

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectpropertycategory/{linkedEnteCategoryId}/properties/{linkedCategoryPropertyId}",
                        CONNECT_CATEGORY_CATEGORY_ID, CONNECT_CATEGORY_CATEGORY_PROP_ID,
                        CONNECT_CATEGORY_LINKED_CATEGORY_ID, CONNECT_CATEGORY_LINKED_CATEGORY_PROP_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(CONNECT_CATEGORY_CATEGORY_PROP_ID)
                    .jsonPath("$.enteCategoryId").isEqualTo(CONNECT_CATEGORY_CATEGORY_ID)
                    .jsonPath("$.name").isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$.type").isEqualTo(EnteProperty.Type.Integer.toString())
                    .jsonPath("$._links.category.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.connectente.href").hasJsonPath()
                    .jsonPath("$._links.connectpropertycategory.href").hasJsonPath()
                .consumeWith(document("entecategoryproperties-connectpropertycategory-put",
                        commonConnectPathParameters(Arrays.asList(
                                parameterWithName("linkedEnteCategoryId")
                                     .description("Connected Ente category identifier (UUID string format)"),
                                parameterWithName("linkedCategoryPropertyId")
                                     .description("Connected Ente category property identifier (UUID string format)"))
                        ),
                        commonResponseFields()));
    }

    @Test
    public void connect_category_prop_to_category_prop_should_return_linked_category_property_not_found() {

        String linkedCategoryPropertyId = "a6637ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectpropertycategory/{linkedEnteCategoryId}/properties/{linkedCategoryPropertyId}",
                        CONNECT_CATEGORY_CATEGORY_ID, CONNECT_CATEGORY_CATEGORY_PROP_ID,
                        CONNECT_CATEGORY_LINKED_CATEGORY_ID, linkedCategoryPropertyId)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void connect_category_prop_to_category_prop_should_return_linked_category_not_found_as_child_of_category() {

        String linkedEnteCategoryId = "b7637ad1-4419-4765-acca-55ce69179c0f";

        this.testClient
                .put()
                .uri("/api/entecategories/{enteCategoryId}/properties/{categoryPropertyId}" +
                                "/connectpropertycategory/{linkedEnteCategoryId}/properties/{linkedCategoryPropertyId}",
                        CONNECT_CATEGORY_CATEGORY_ID, CONNECT_CATEGORY_CATEGORY_PROP_ID,
                        linkedEnteCategoryId, CONNECT_CATEGORY_LINKED_CATEGORY_PROP_ID)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategoryProperty()), EnteCategoryProperty.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void find_enteproperty_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/entecategories/{enteCategoryId}/properties/{propertyId}",
                        ENTE_CATEGORY_ID, CATEGORY_PROPERTY_ID)
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
        fieldDescriptor.add(fieldWithPath("type").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Ente category property name"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonConnectPathParameters(List<ParameterDescriptor> parameters) {
        List<ParameterDescriptor> paramsDescriptor = new ArrayList<>(parameters);
        paramsDescriptor.add(parameterWithName("enteCategoryId")
                .description("Ente category identifier to connect (UUID string format)"));
        paramsDescriptor.add(parameterWithName("categoryPropertyId")
                .description("Ente property identifier to connect (UUID string format)"));
        return pathParameters(paramsDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("enteCategoryId").description("Ente category identifier (UUID string format)"),
                parameterWithName("propertyId").description("Ente category property identifier (UUID string format)"));
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("enteCategoryId").description("Ente category identifier (UUID) for this ente property"),
                fieldWithPath("id").description("Ente category property identifier (UUID)"),
                fieldWithPath("name").description("Ente category property name"),
                fieldWithPath("type").description("Ente property type (None, Integer, Decimal, Boolean, DateTime)"),
                subsectionWithPath("_links")
                        .description("The ente links. " + StringResource.METADATA_INFORMATION));
    }

    private static EnteCategoryProperty createEnteCategoryProperty() {
        EnteCategoryProperty enteCategoryProperty = EnteCategoryProperty.builder()
                    .name(ENTE_PROPERTY_NAME)
                    .parentId(UUID.fromString(ENTE_CATEGORY_ID))
                .build();
        enteCategoryProperty.setType(EnteProperty.Type.Integer);
        return enteCategoryProperty;
    }
}
