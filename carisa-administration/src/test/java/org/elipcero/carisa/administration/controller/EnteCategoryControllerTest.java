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

import org.elipcero.carisa.administration.domain.EnteCategory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * @author David Suárez
 */
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_ente_category_controller" })
public class EnteCategoryControllerTest extends DataAbstractControllerTest {

    // Look at entecategory-controller
    private static final String ENTECATEGORY_ID = "83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";
    private static final String ENTECATEGORY_PARENTID = "63ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";
    private static final String ENTECATEGORY_NAME = "Ente Category name";

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("ente-category-controller.cql");
            this.executeCommands("ente-hierarchy-controller.cql");
            this.executeCommands("space-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_entecategory_should_return_ok_and_entecategory_entity() {

        this.testClient
                .get()
                .uri("/api/entecategories/{id}", ENTECATEGORY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTECATEGORY_NAME)
                    .jsonPath("$._links.children.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entecategories-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_entecategory_using_post_should_return_created_and_entecategory_entity() {

        this.testClient
                .post()
                .uri("/api/entecategories").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategory(false)), EnteCategory.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTECATEGORY_NAME)
                    .jsonPath("$.root").isEqualTo(false)
                    .jsonPath("$._links.children.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entecategories-post",
                        commonRequestFields(),
                        commonResponseFields()));
    }

    @Test
    public void create_entecategory_with_root_using_post_should_return_created_and_entecategory_entity() {

        String spaceId = "52107f03-cf1b-4760-b2c2-4273482f0f7a";

        this.testClient
                .post()
                .uri("/api/entecategories").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(EnteCategory.builder()
                        .name(ENTECATEGORY_NAME)
                        .parentId(UUID.fromString(spaceId))
                        .root(true)
                        .build()), EnteCategory.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTECATEGORY_NAME)
                    .jsonPath("$.root").isEqualTo(true)
                    .jsonPath("$.parentId").isEqualTo(spaceId)
                    .jsonPath("$._links.children.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void create_entecategory_where_parent_category_no_exist_using_post_should_return_not_found_and_error() {

        this.testClient
                .post()
                .uri("/api/entecategories").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(EnteCategory.builder()
                            .name(ENTECATEGORY_NAME)
                            .parentId(UUID.randomUUID())
                        .build()), EnteCategory.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                    .jsonPath("$.message").hasJsonPath();
    }

    @Test
    public void create_entecategory_using_put_should_return_created_and_entecategory_entity() {

        String id = "51ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";

        this.testClient
                .put()
                .uri("/api/entecategories/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createEnteCategory(false)), EnteCategory.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTECATEGORY_NAME)
                    .jsonPath("$.root").isEqualTo(false)
                    .jsonPath("$._links.children.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entecategory-put",
                        commonPathParameters(),
                        commonRequestFields(),
                        commonResponseFields()));
    }

    @Test
    public void update_entecategory_using_put_should_return_ok_and_entecategory_entity() {

        String id = "73ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676"; // Look at entecategory-controller
        String newName = "Ente Category updated";

        EnteCategory enteCategoryUpdated = EnteCategory
                .builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                    .parentId(UUID.randomUUID())
                .build();

        enteCategoryUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/entecategories/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(enteCategoryUpdated), EnteCategory.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.root").isEqualTo(false)
                    .jsonPath("$._links.children.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/entecategories")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post");
    }

    @Test
    public void find_space_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/entecategories/" + ENTECATEGORY_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='name')].name").isEqualTo("name");
    }

    @Test
    public void find_entes_from_space_should_return_ok_and_entes_entity() {

        String childId = "53ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";

        this.testClient
                .get()
                .uri("/api/entecategories/{id}/children", ENTECATEGORY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._embedded.enteCategoryChildNameList[?(@.id=='%s')].name", childId)
                        .isEqualTo(ENTECATEGORY_NAME)
                    .jsonPath("$._embedded.enteCategoryChildNameList[?(@.id=='%s')]._links.category.href", childId)
                        .hasJsonPath()
                    .jsonPath("$._embedded.enteCategoryChildNameList.length()").isEqualTo(1)
                    .jsonPath("$._links.category.href").hasJsonPath()
                .consumeWith(document("entecategory-children-get",
                        categoryLink(),
                        commonPathParameters(),
                        responseFields(
                                fieldWithPath("_embedded.enteCategoryChildNameList[].id")
                                        .description("Category identifier. (UUID string format)"),
                                fieldWithPath("_embedded.enteCategoryChildNameList[].name").description("Ente name"),
                                fieldWithPath("_embedded.enteCategoryChildNameList[]._links.category.href")
                                        .description("Category information"),
                                subsectionWithPath("_links").description("View links section"))));
    }

    @Test
    public void connect_entecategory_child_with_parent_using_put_should_return_ok_and_entecategory_child() {

        String childId = "43ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676";

        this.testClient
                .put()
                .uri("/api/entecategories/{childId}/connectparent/{parentId}", childId, ENTECATEGORY_ID)
                    .contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(ENTECATEGORY_NAME)
                    .jsonPath("$.root").isEqualTo(false)
                    .jsonPath("$._links.children.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("entecategory-connectparent-put",
                        pathParameters(
                                parameterWithName("childId").description("Ente category child to connect"),
                                parameterWithName("parentId").description("Ente category parent connected")),
                        commonResponseFields()));
    }

    private LinksSnippet categoryLink() {
        return links(linkWithRel("category").description("Space"));
    }

    private static PathParametersSnippet commonPathParameters() {
        return commonPathParameters(new ArrayList<>());
    }

    private static PathParametersSnippet commonPathParameters(List<ParameterDescriptor> params) {
        List<ParameterDescriptor> paramDescriptor = new ArrayList<>(params);
        paramDescriptor.add(parameterWithName("id").description("Ente category id (UUID string format)"));
        return pathParameters(paramDescriptor);
    }

    private static RequestFieldsSnippet commonRequestFields() {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>();
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Ente category name"));
        fieldDescriptor.add(fieldWithPath("root")
                .description("If root is true then the parent belong to space otherwise belong to ente category. " +
                        "When is used to update, this property is not used"));
        fieldDescriptor.add(fieldWithPath("parentId")
                .description("Ente category parent to connect. When is used to update, this property is not used"));
        return requestFields(fieldDescriptor);
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Ente category identifier (UUID)"),
                fieldWithPath("name").description("Ente category name"),
                fieldWithPath("root")
                    .description("If root is true then the parent belong to space otherwise belong to ente category"),
                fieldWithPath("parentId").ignored(),
                generalLink());
    }

    private static FieldDescriptor generalLink() {
        return subsectionWithPath("_links")
                .description("The instance links. " + StringResource.METADATA_INFORMATION);
    }

    private static EnteCategory createEnteCategory(boolean root) {
        return EnteCategory.builder()
                    .name(ENTECATEGORY_NAME)
                    .parentId(UUID.fromString(ENTECATEGORY_PARENTID))
                    .root(root)
                .build();
    }
}
