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

import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.general.StringResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.PathParametersSnippet;
import reactor.core.publisher.Mono;

import java.io.IOException;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_space_controller" })
public class SpaceControllerTest extends DataAbstractControllerTest {

    public static final String SPACE_ID = "52107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
    private static final String INSTANCE_ID = "5b6962dd-3f90-4c93-8f61-eabfa4a803e2"; // Look at instance-controller
    public static final String SPACE_NAME = "Space name"; // Look at space-controller

    private static boolean beforeOnce;

    @Before
    public void prepareData() throws IOException {
        if (!beforeOnce) {
            this.executeCommands("instance-controller.cql");
            this.executeCommands("space-controller.cql");
            this.executeCommands("ente-controller.cql");
            this.executeCommands("instance-space-controller.cql");
            this.executeCommands("space-ente-controller.cql", false);
            beforeOnce = true;
        }
    }

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
                    .jsonPath("$._links.instance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("spaces-get",
                        commonPathParamters(),
                        commonResponseFields()));
    }

    @Test
    public void create_space_using_post_should_return_created_and_space_entity() {

        this.testClient
                .post()
                .uri("/api/spaces").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createSpace()), Space.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(SPACE_NAME)
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$._links.instance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("spaces-post",
                        commonRequestFields(
                                Arrays.asList(
                                    fieldWithPath("instanceId")
                                            .description("Instance identifier (UUID) for this space"))),
                        commonResponseFields()));
    }

    @Test
    public void create_space_where_instance_no_exist_using_post_should_return_not_found_and_error() {

        this.testClient
                .post()
                .uri("/api/spaces").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(Space.builder()
                        .name(SPACE_NAME)
                        .instanceId(UUID.randomUUID())
                        .build()), Space.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                    .jsonPath("$.message").hasJsonPath();
    }

    @Test
    public void create_space_using_put_should_return_created_and_space_entity() {

        String id = "361370a0-e3e5-45e5-b675-a55fe923873f";

        this.testClient
                .put()
                .uri("/api/spaces/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createSpace()), Space.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(SPACE_NAME)
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$._links.instance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("spaces-put",
                        commonPathParamters(),
                        commonRequestFields(
                                Arrays.asList(
                                    fieldWithPath("instanceId")
                                            .description("Instance identifier (UUID) for this space." +
                                                    "This property can not be updated"))),
                        commonResponseFields()));
    }

    @Test
    public void update_space_using_put_should_return_ok_and_space_entity() {

        String id = "12107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
        String newName = "Space name updated";

        Space spaceUpdated = Space
                .builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                    .instanceId(UUID.randomUUID())
                .build();

        spaceUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/spaces/" + id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(spaceUpdated), Space.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.instanceId").isEqualTo(INSTANCE_ID)
                    .jsonPath("$._links.instance.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void find_space_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/spaces/" + SPACE_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='instanceId')].name").isEqualTo("instanceId");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/spaces")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='instanceId')].name").isEqualTo("instanceId");
    }

    @Test
    public void find_entes_from_space_should_return_ok_and_entes_entity_and_entes_no_exist_purged() {

        this.testClient
                .get()
                .uri("/api/spaces/{id}/entes", SPACE_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._embedded.enteNameList[?(@.enteId=='%s')].name", EnteControllerTest.ENTE_ID)
                        .isEqualTo(EnteControllerTest.ENTE_NAME)
                    .jsonPath("$._embedded.enteNameList[?(@.enteId=='%s')]._links.ente.href", EnteControllerTest.ENTE_ID)
                        .hasJsonPath()
                    .jsonPath("$._embedded.enteNameList.length()").isEqualTo(1)
                    .jsonPath("$._links.space.href").hasJsonPath()
                .consumeWith(document("space-entes-get",
                        spaceLink(),
                        commonPathParamters(),
                        responseFields(
                                fieldWithPath("_embedded.enteNameList[].enteId")
                                        .description("Ente identifier. (UUID string format)"),
                                fieldWithPath("_embedded.enteNameList[].name").description("Ente name"),
                                fieldWithPath("_embedded.enteNameList[]._links.ente.href").description("Ente information"),
                                subsectionWithPath("_links").description("View links section"))));
    }

    private static RequestFieldsSnippet commonRequestFields(List<FieldDescriptor> fields) {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>(fields);
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Space name"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParamters() {
        return commonPathParamters(new ArrayList<ParameterDescriptor>());
    }

    private static PathParametersSnippet commonPathParamters(List<ParameterDescriptor> params) {
        List<ParameterDescriptor> paramDescriptor = new ArrayList<>(params);
        paramDescriptor.add(parameterWithName("id").description("Space id (UUID string format)"));
        return pathParameters(paramDescriptor);
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Space identifier (UUID)"),
                fieldWithPath("instanceId").description("Instance identifier (UUID) for this space"),
                fieldWithPath("name").description("Space name"),
                generalLink());
    }

    private static FieldDescriptor generalLink() {
        return subsectionWithPath("_links")
                .description("The instance links. " + StringResource.METADATA_INFORMATION);
    }

    private LinksSnippet spaceLink() {
        return links(linkWithRel("space").description("Space"));
    }

    private static Space createSpace() {
        return Space.builder()
                .name(SPACE_NAME)
                .instanceId(UUID.fromString(INSTANCE_ID))
                .build();
    }
}
