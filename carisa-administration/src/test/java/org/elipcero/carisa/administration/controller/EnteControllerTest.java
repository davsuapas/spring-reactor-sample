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

import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.general.StringResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_ente_controller" })
public class EnteControllerTest extends DataAbstractControllerTest {

    private static final String ENTE_ID = "7acdac69-fdf8-45e5-a189-2b2b4beb1c26"; // Look at ente-controller
    private static final String SPACE_ID = "52107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
    private static final String ENTE_NAME = "Ente name";
    private static final String ENTE_PROPERTY_NAME = "Ente property name"; // Look at ente-property-controller.cql
    private static final String ENTE_PROPERTY_ID = "c0838415-6ae2-4914-b202-f1b3adbf0353";

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("space-controller.cql");
            this.executeCommands("space-ente-controller.cql");
            this.executeCommands("ente-controller.cql");
            this.executeCommands("ente-property-controller.cql");
            this.executeCommands("ente-category-controller.cql");
            this.executeCommands("ente-hierarchy-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_ente_should_return_ok_and_ente_entity() {

        this.testClient
                .get()
                .uri("/api/entes/{enteId}", ENTE_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTE_NAME)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entes-get",
                        commonPathParamters(),
                        commonResponseFields()));
    }

    @Test
    public void create_ente_using_post_should_return_created_and_ente_entity() {

        this.testClient
                .post()
                .uri("/api/entes").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnte()), Ente.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTE_NAME)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entes-post",
                        commonRequestFields(
                                Arrays.asList(
                                    fieldWithPath("spaceId")
                                            .description("Space identifier (UUID) for this ente"))),
                        commonResponseFields()));
    }

    @Test
    public void create_ente_using_put_should_return_created_and_ente_entity() {

        String id = "361370a0-e3e5-45e5-b675-a55fe923873f";

        this.testClient
                .put()
                .uri("/api/entes/{enteId}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnte()), Ente.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTE_NAME)
                    .jsonPath("$.spaceId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entes-put",
                        commonPathParamters(),
                        commonRequestFields(
                                Arrays.asList(
                                    fieldWithPath("spaceId")
                                            .description("Space identifier (UUID) for this ente." +
                                                    "This property can not be updated"))),
                        commonResponseFields()));
    }

    @Test
    public void update_ente_using_put_should_return_ok_and_ente_entity() {

        String id = "8acdac69-fdf8-45e5-a189-2b2b4beb1c26"; // Look at ente-controller
        String newName = "Ente name updated";

        Ente enteUpdated = Ente
                .builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                    .spaceId(UUID.fromString(SPACE_ID))
                .build();

        enteUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/entes/{enteId}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(enteUpdated), Ente.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.spaceId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_ente_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/entes/{enteId}", ENTE_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='spaceId')].name")
                        .isEqualTo("spaceId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/entes")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='spaceId')].name")
                        .isEqualTo("spaceId");
    }

    @Test
    public void find_enteproperties_from_ente_should_return_ok_and_enteproperties_entity() {

        this.testClient
                .get()
                .uri("/api/entes/{enteId}/properties", ENTE_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._embedded.entePropertyNameList[?(@.entePropertyId=='%s')].name",
                            ENTE_PROPERTY_ID).isEqualTo(ENTE_PROPERTY_NAME)
                    .jsonPath("$._embedded.entePropertyNameList[?(@.entePropertyId=='%s')]._links.property.href",
                            ENTE_PROPERTY_ID)
                        .hasJsonPath()
                    .jsonPath("$._links.ente.href").hasJsonPath()
                .consumeWith(document("ente-enteproperties-get",
                        enteLink(),
                        commonPathParamters(),
                        responseFields(
                                fieldWithPath("_embedded.entePropertyNameList[].entePropertyId")
                                        .description("Ente property identifier. (UUID string format)"),
                                fieldWithPath("_embedded.entePropertyNameList[].name")
                                        .description("Ente property name"),
                                fieldWithPath("_embedded.entePropertyNameList[]._links.property.href")
                                        .description("Ente property information"),
                                subsectionWithPath("_links").description("View links section"))));
    }

    @Test
    public void connect_ente_with_category_using_put_should_return_ok_and_ente() {

        String categoryId = "83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";

        this.testClient
                .put()
                .uri("/api/entes/{enteId}/connectcategory/{categoryId}", ENTE_ID, categoryId)
                .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTE_NAME)
                    .jsonPath("$.spaceId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("ente-connectcategory-put",
                        pathParameters(
                                parameterWithName("enteId").description("Ente identifier to connect"),
                                parameterWithName("categoryId").description("Category identifier where is connected")),
                        commonResponseFields()));
    }

    private LinksSnippet enteLink() {
        return links(linkWithRel("ente").description("Ente"));
    }

    private static FieldDescriptor generalLink() {
        return subsectionWithPath("_links")
                .description("The instance links. " + StringResource.METADATA_INFORMATION);
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Ente name"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParamters() {
        return pathParameters(
                parameterWithName("enteId").description("Ente id (UUID string format)")
        );
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Ente identifier (UUID)"),
                fieldWithPath("spaceId").description("Space identifier (UUID) for this ente"),
                fieldWithPath("name").description("Ente name"),
                subsectionWithPath("_links")
                        .description("The ente links. " + StringResource.METADATA_INFORMATION));
    }

    private static Ente createEnte() {
        return Ente.builder()
                    .name(ENTE_NAME)
                    .spaceId(UUID.fromString(SPACE_ID))
                .build();
    }
}
