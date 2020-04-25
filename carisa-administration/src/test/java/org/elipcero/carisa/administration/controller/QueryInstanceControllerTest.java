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

import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
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
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_query_instance_controller" })
public class QueryInstanceControllerTest extends DataAbstractControllerTest {

    private static final String QUERY_ID = "a985074c-796b-4ecb-9a8f-21f4b26aa11b"; // Look at query-instance-controller
    private static final String SPACE_ID = "52107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
    // Look at query-prototype-controller
    private static final String QUERY_PROTOTYPE_ID = "5d191729-1f4c-4b7e-b573-b90cf3457df8";
    private static final String QUERY_NAME = "Query type name"; // Look at query-instance-controller
    private static final String QUERY_DESCRIPTION = "Query type description"; // Look at query-instance-controller

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("space-controller.cql");
            this.executeCommands("space-query-instance-controller.cql");
            this.executeCommands("query-instance-controller.cql");
            this.executeCommands("query-prototype-controller.cql");
            this.executeCommands("query-instance-property-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_query_should_return_ok_and_query_entity() {

        this.testClient
                .get()
                .uri("/api/queryinstances/{id}", QUERY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.description").isEqualTo(QUERY_DESCRIPTION)
                    .jsonPath("$.prototypeId").isEqualTo(QUERY_PROTOTYPE_ID)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.queryinstanceproperties.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryinstances-get",
                        commonPathParameters(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_using_post_should_return_created_and_query_entity() {

        this.testClient
                .post()
                .uri("/api/queryinstances").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQuery()), DynamicObjectInstance.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.description").isEqualTo(QUERY_DESCRIPTION)
                    .jsonPath("$.prototypeId").isEqualTo(QUERY_PROTOTYPE_ID)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.queryinstanceproperties.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryinstances-post",
                        commonRequestFields(),
                        commonResponseFields()));
    }

    @Test
    public void create_query_using_post_protoype_not_exist_should_return_error_not_found() {

        DynamicObjectInstance objectPrototype = DynamicObjectInstance.builder()
                    .prototypeId(UUID.randomUUID())
                .build();

        this.testClient
                .post()
                .uri("/api/queryinstances").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(objectPrototype), DynamicObjectInstance.class)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void create_query_using_put_should_return_created_and_query_entity() {

        String id = "4837648b-896b-4a91-b579-8a02cc6ce031";

        this.testClient
                .put()
                .uri("/api/queryinstances/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createQuery()), DynamicObjectInstance.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(QUERY_NAME)
                    .jsonPath("$.description").isEqualTo(QUERY_DESCRIPTION)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$.prototypeId").isEqualTo(QUERY_PROTOTYPE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.queryinstanceproperties.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath()
                .consumeWith(document("queryinstances-put",
                        commonPathParameters(),
                        commonRequestFields(),
                        commonResponseFields()));
    }

    @Test
    public void update_query_using_put_should_return_ok_and_query_entity() {

        String id = "b985074c-796b-4ecb-9a8f-21f4b26aa11b"; // Look at query-instance-controller
        String newName = "Updated Query name";
        String newDescription = "Updated Query description";

        DynamicObjectInstance queryUpdated = DynamicObjectInstance
                .builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                    .description(newDescription)
                    .parentId(UUID.fromString(SPACE_ID))
                .build();

        this.testClient
                .put()
                .uri("/api/queryinstances/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(queryUpdated), DynamicObjectInstance.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$.description").isEqualTo(newDescription)
                    .jsonPath("$.parentId").isEqualTo(SPACE_ID)
                    .jsonPath("$.prototypeId").isEqualTo(QUERY_PROTOTYPE_ID)
                    .jsonPath("$._links.space.href").hasJsonPath()
                    .jsonPath("$._links.queryplugin.href").hasJsonPath()
                    .jsonPath("$._links.queryinstanceproperties.href").hasJsonPath()
                    .jsonPath("$._links.self.href").hasJsonPath();
    }

    @Test
    public void list_properties_from_query_should_return_ok_and_properties_entity() {

        String propertyId = "c6b34eb0-e15e-4e5a-a20d-7548a6967085";
        String propertyName = "Query property name";

        this.testClient
                .get()
                .uri("/api/queryinstances/{id}/properties", QUERY_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$._embedded.childNameList[?(@.id=='%s')].name", propertyId).isEqualTo(propertyName)
                .jsonPath("$._embedded.childNameList[?(@.id=='%s')]._links.property.href", propertyId).hasJsonPath()
                .jsonPath("$._links.queryinstance.href").hasJsonPath()
                .consumeWith(document("queryinstances-properties-get",
                        links(linkWithRel("queryinstance").description("Query instance")),
                        commonPathParameters(),
                        responseFields(
                                fieldWithPath("_embedded.childNameList[].id")
                                        .description("Query instance identifier. (UUID string format)"),
                                fieldWithPath("_embedded.childNameList[].name")
                                        .description("Query instance property name"),
                                fieldWithPath("_embedded.childNameList[]._links.property.href")
                                        .description("Query instance property information"),
                                subsectionWithPath("_links").description("View links section"))));
    }

    @Test
    public void find_query_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/queryinstances/{id}", QUERY_ID)
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
                .uri("/api/queryinstances")
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
        fieldDescriptor.add(fieldWithPath("name").description("Query instance name defined by user."));
        fieldDescriptor.add(fieldWithPath("description").description("Query instance description defined by user."));
        fieldDescriptor.add(fieldWithPath("parentId")
                .description("Space identifier (UUID) for this query instance. This property can not be updated"));
        fieldDescriptor.add(fieldWithPath("prototypeId")
                .description("Query prototype identifier (UUID). This property can not be updated"));
        return requestFields(fieldDescriptor);
    }

    private static PathParametersSnippet commonPathParameters() {
        return pathParameters(
                parameterWithName("id").description("Query instance id (UUID string format)")
        );
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Query instance identifier (UUID)"),
                fieldWithPath("parentId").description("Space identifier (UUID) for this query instance"),
                fieldWithPath("prototypeId").description("Query prototype identifier (UUID)"),
                fieldWithPath("name").description("Query instance name defined by user."),
                fieldWithPath("description").description("Query instance description defined by user."),
                subsectionWithPath("_links")
                        .description("The query instance links. " + StringResource.METADATA_INFORMATION));
    }

    private static DynamicObjectInstance createQuery() {
        return DynamicObjectInstance.builder()
                    .name(QUERY_NAME)
                    .description(QUERY_DESCRIPTION)
                    .parentId(UUID.fromString(SPACE_ID))
                    .prototypeId(UUID.fromString(QUERY_PROTOTYPE_ID))
                .build();
    }
}
