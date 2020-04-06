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
import org.elipcero.carisa.administration.domain.PluginType;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_query_prototype_controller" })
public class QueryPrototypeControllerTest extends DataAbstractControllerTest {

    private static final String QUERY_ID = "5d191729-1f4c-4b7e-b573-b90cf3457df8"; // Look at query-prototype-controller
    private static final String QUERY_NAME = "Query prototype name"; // Look at query-prototype-controller
    private static final String QUERY_DESCRIPTION = "Query prototype description"; // Look at query-prototype-controller

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("query-prototype-controller.cql");
            this.executeCommands("plugin-controller.cql");
            this.executeCommands("query-prototype-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_query_should_return_ok_and_query_entity() {

        this.testClient
                .get()
                .uri("/api/queriesplugin/{id}", QUERY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.description").isEqualTo(QUERY_DESCRIPTION)
                    .jsonPath("$.parentId").isEqualTo(PluginType.QUERY_ID.toString())
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queriesplugin-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_using_post_should_return_created_and_query_entity() {

        this.testClient
                .post()
                .uri("/api/queriesplugin").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQuery()), DynamicObjectPrototype.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.description").isEqualTo(QUERY_DESCRIPTION)
                    .jsonPath("$.parentId").isEqualTo(PluginType.QUERY_ID.toString())
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queriesplugin-post",
                        commonRequestFields(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_using_put_should_return_created_and_query_entity() {

        String id = "4837648b-896b-4a91-b579-8a02cc6ce031";

        this.testClient
                .put()
                .uri("/api/queriesplugin/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQuery()), DynamicObjectPrototype.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.description").isEqualTo(QUERY_DESCRIPTION)
                    .jsonPath("$.parentId").isEqualTo(PluginType.QUERY_ID.toString())
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queriesplugin-put",
                        commonPathParameters(),
                        commonRequestFields(),
                        commonResponseFields()));
    }

    @Test
    public void update_query_using_put_should_return_ok_and_query_entity() {

        String id = "4d191729-1f4c-4b7e-b573-b90cf3457df8"; // Look at query-prototype-controller
        String newName = "Updated query name";
        String newDescription = "Updated query description";

        DynamicObjectPrototype queryUpdated = DynamicObjectPrototype
                .builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                    .description(newDescription)
                    .parentId(UUID.randomUUID())
                .build();

        this.testClient
                .put()
                .uri("/api/queriesplugin/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(queryUpdated), DynamicObjectPrototype.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.description").isEqualTo(newDescription)
                    .jsonPath("$.parentId").isEqualTo(PluginType.QUERY_ID.toString())
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void list_properties_from_query_should_return_ok_and_properties_entity() {

        String propertyId = "249f1073-3164-4ed0-9ad5-4415945b273f";
        String propertyName = "Query property name";

        this.testClient
                .get()
                .uri("/api/queriesplugin/{id}/properties", QUERY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._embedded.childNameList[?(@.id=='%s')].name", propertyId).isEqualTo(propertyName)
                    .jsonPath("$._embedded.childNameList[?(@.id=='%s')]._links.property.href", propertyId).hasJsonPath()
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                .consumeWith(document("queriesplugin-properties-get",
                        links(linkWithRel("queryplugin").description("Query plugin")),
                        commonPathParameters(),
                        responseFields(
                                fieldWithPath("_embedded.childNameList[].id")
                                        .description("Query plugin identifier. (UUID string format)"),
                                fieldWithPath("_embedded.childNameList[].name")
                                        .description("Query plugin property name"),
                                fieldWithPath("_embedded.childNameList[]._links.property.href")
                                        .description("Query Plugin property information"),
                                subsectionWithPath("_links").description("View links section"))));
    }

    @Test
    public void find_query_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/queriesplugin/{id}", QUERY_ID)
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
                .uri("/api/queriesplugin")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='parentId')].name")
                        .isEqualTo("parentId");
    }

    private static RequestFieldsSnippet commonRequestFields() {
        List<FieldDescriptor> fieldDescriptor = new ArrayList<>();
        fieldDescriptor.add(fieldWithPath("id").ignored());
        fieldDescriptor.add(fieldWithPath("name").description("Query prototype name defined by plugin developer."));
        fieldDescriptor.add(fieldWithPath("description")
                .description("Query prototype description defined by plugin developer."));
        fieldDescriptor.add(fieldWithPath("parentId")
                .description("Plugin type identifier (UUID). In this case Query. This property can not be updated"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("id").description("Query prototype id (UUID string format)")
        );
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Query prototype identifier (UUID)"),
                fieldWithPath("parentId")
                    .description("Plugin type identifier (UUID). In this case Query. This property can not be updated"),
                fieldWithPath("name").description("Query prototype name defined by plugin developer."),
                fieldWithPath("description").description("Query prototype description defined by plugin developer."),
                subsectionWithPath("_links")
                        .description("The query prototype links. " + StringResource.METADATA_INFORMATION));
    }

    private static DynamicObjectPrototype createQuery() {
        return DynamicObjectPrototype.builder()
                    .name(QUERY_NAME)
                    .description(QUERY_DESCRIPTION)
                    .parentId(PluginType.QUERY_ID)
                .build();
    }
}
