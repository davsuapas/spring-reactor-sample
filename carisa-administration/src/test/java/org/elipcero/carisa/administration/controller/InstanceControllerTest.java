/*
 *  Copyright 2019-2022 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.elipcero.carisa.administration.controller;

import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.core.reactive.misc.DataLockController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.containsString;
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
@AutoConfigureWireMock
@SpringBootTest(properties = { "spring.data.cassandra.keyspaceName=test_admin_instance_controller" })
public class InstanceControllerTest extends DataAbstractControllerTest {

    private static final String INSTANCE_NAME = "Instance name";
    private static final String INSTANCE_ID = "5b6962dd-3f90-4c93-8f61-eabfa4a803e2"; // Look at instance-controller
    private static final String SPACE_ID = "52107f03-cf1b-4760-b2c2-4273482f0f7a"; // Look at space-controller
    private static final String SPACE_NAME = "Space name"; // Look at space-controller

    @Autowired
    private DataLockController dataLockController;

    private static boolean beforeOnce;

    @Before
    public void prepareData() {
        if (!beforeOnce) {
            this.executeCommands("instance-controller.cql");
            this.executeCommands("instance-space-controller.cql");
            this.executeCommands("space-controller.cql");
            beforeOnce = true;
        }
    }

    @Test
    public void find_instance_should_return_ok_and_instance_entity() {

        this.testClient
                .get()
                .uri("/api/instances/{id}", INSTANCE_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                    .jsonPath("$.state").isEqualTo(Instance.State.None.toString())
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.deploy.href").hasJsonPath()
                    .jsonPath("$._links.spaces.href").hasJsonPath()
                .consumeWith(document("instances-get",
                        commonLinks(),
                        commonPathParamters(),
                        commonResponseFields()));
    }

    @Test
    public void list_spaces_from_instance_should_return_ok_and_spaces_entity() {

        this.testClient
                .get()
                .uri("/api/instances/{id}/spaces", INSTANCE_ID)
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._embedded.childNameList[?(@.id=='%s')].name", SPACE_ID)
                        .isEqualTo(SPACE_NAME)
                    .jsonPath("$._embedded.childNameList[?(@.id=='%s')]._links.space.href", SPACE_ID)
                        .hasJsonPath()
                    .jsonPath("$._embedded.childNameList.length()").isEqualTo(1)
                    .jsonPath("$._links.instance.href").hasJsonPath()
                .consumeWith(document("instances-spaces-get",
                        instanceLink(),
                        commonPathParamters(),
                        responseFields(
                                fieldWithPath("_embedded.childNameList[].id")
                                        .description("Space identifier. (UUID string format)"),
                                fieldWithPath("_embedded.childNameList[].name").description("Space name"),
                                fieldWithPath("_embedded.childNameList[]._links.space.href").description("Space information"),
                                subsectionWithPath("_links").description("View links section"))));
    }

    @Test
    public void create_instance_using_post_should_return_created_and_instance_entity() {

        this.testClient
                .post()
                .uri("/api/instances").contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createInstance()), Instance.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                    .jsonPath("$.state").isEqualTo(Instance.State.None.toString())
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._links.deploy.href").hasJsonPath()
                    .jsonPath("$._links.spaces.href").hasJsonPath()
                .consumeWith(document("instances-post",
                    commonLinks(),
                    commonRequestFields(),
                    commonResponseFields()));
    }

    @Test
    public void create_instance_using_put_should_return_created_and_instance_entity() {

        String id = "8b6962dd-3f90-4c93-8f61-eabfa4a803e2";

        this.testClient
                .put()
                .uri("/api/instances/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(createInstance()), Instance.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(INSTANCE_NAME)
                    .jsonPath("$.state").isEqualTo(Instance.State.None.toString())
                    .jsonPath("$._links.self.href").value(containsString(id))
                    .jsonPath("$._links.deploy.href").hasJsonPath()
                    .jsonPath("$._links.spaces.href").hasJsonPath()
                .consumeWith(document("instances-put",
                    commonLinks(),
                    commonPathParamters(),
                    commonRequestFields(),
                    commonResponseFields()));
    }

    @Test
    public void update_instance_using_put_should_return_ok_and_instance_entity() {

        String id = "9b6962dd-3f90-4c93-8f61-eabfa4a803e2"; // Look at instance-controller
        String newName = "Instance name updated";

        Instance instanceUpdated = Instance.builder()
                    .id(UUID.fromString(id))
                    .name(newName)
                .build();

        instanceUpdated.setName(newName);

        this.testClient
                .put()
                .uri("/api/instances/{id}", id).contentType(MediaTypes.HAL_JSON)
                .accept(MediaTypes.HAL_JSON)
                .body(Mono.just(instanceUpdated), Instance.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.name").isEqualTo(newName)
                    .jsonPath("$._links.self.href").value(containsString(id))
                    .jsonPath("$._links.deploy.href").hasJsonPath()
                    .jsonPath("$._links.spaces.href").hasJsonPath();
    }

    @Test
    public void find_instance_should_return_affordance() {

        this.testClient
                .get()
                .uri("/api/instances/" + INSTANCE_ID)
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$._links.self.href").hasJsonPath()
                    .jsonPath("$._templates.default.method").isEqualTo("put")
                    .jsonPath("$._templates.default.properties[?(@.name=='name')].name").isEqualTo("name");
    }

    @Test
    public void get_metadata_should_return_ok_and_affordance() {

        this.testClient
                .get()
                .uri("/api/instances")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.resource").isEqualTo(StringResource.METADATA_INFORMATION)
                    .jsonPath("$._templates.default.method").isEqualTo("post")
                    .jsonPath("$._templates.default.properties[?(@.name=='name')].name").isEqualTo("name");
    }

    @Test
    public void build_instance_should_return_instance_with_state_deployed() {

        stubFor(put(urlEqualTo("/api/platforms/kubernetes/deployers"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.CREATED.value())));

        this.testClient
                .put()
                .uri("/api/instances/{id}/deploy", "1b6962dd-3f90-4c93-8f61-eabfa4a803e2")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.state").isEqualTo(Instance.State.Deployed.toString())
                .consumeWith(document("instances-deploy",
                        commonPathParamters(),
                        commonResponseFields()));
    }

    @Test
    public void build_instance_should_return_instance_with_state_error() {

        stubFor(put(urlEqualTo("/api/platforms/kubernetes/deployers"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.BAD_REQUEST.value())));

        this.testClient
                .put()
                .uri("/api/instances/{id}/deploy", "2b6962dd-3f90-4c93-8f61-eabfa4a803e2")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.state").isEqualTo(Instance.State.Error.toString());
    }

    @Test
    public void try_build_instance_locked_should_return_instance_with_state_none() {

        UUID instanceId = UUID.fromString("2b6962dd-3f90-4c93-8f61-eabfa4a803e2");

        this.dataLockController.lock(instanceId, 15)
                .flatMap(__ -> {
                    this.testClient
                            .put()
                            .uri("/api/instances/{id}/deploy", instanceId.toString())
                            .accept(MediaTypes.HAL_JSON)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody()
                                .jsonPath("$.state").isEqualTo(Instance.State.InProgress.toString());
                    return Mono.empty();
                })
                .doFinally(__ -> this.dataLockController.unLock(instanceId));
    }

    private static PathParametersSnippet commonPathParamters() {
        return commonPathParamters(new ArrayList<ParameterDescriptor>());
    }

    private static PathParametersSnippet commonPathParamters(List<ParameterDescriptor> params) {
        List<ParameterDescriptor> paramDescriptor = new ArrayList<>(params);
        paramDescriptor.add(parameterWithName("id").description("Instance identifier (UUID string format)"));
        return pathParameters(paramDescriptor);
    }

    private static RequestFieldsSnippet commonRequestFields() {
        return requestFields(
                fieldWithPath("id").ignored(),
                fieldWithPath("name").description("Instance name"),
                fieldWithPath("state").ignored());
    }

    private static ResponseFieldsSnippet commonResponseFields() {
        return responseFields(
                fieldWithPath("id").description("Instance identifier (UUID)"),
                fieldWithPath("name").description("Instance name"),
                fieldWithPath("state")
                        .description("Instance state; NONE: No built, INPROGRESS: Deploying," +
                                " DEPLOYED: Deployed, ERROR: Deploying error"),
                generalLink());
    }

    private static FieldDescriptor generalLink() {
        return subsectionWithPath("_links")
                .description("The instance links. " + StringResource.METADATA_INFORMATION);
    }

    private LinksSnippet commonLinks() {
        return links(
                linkWithRel("self").description("Resource instance"),
                linkWithRel("deploy").description("Deploy instance into platform"),
                linkWithRel(SpaceModelAssembler.SPACES_REL_NAME).description("Spaces list by instance"));
    }

    private LinksSnippet instanceLink() {
        return links(linkWithRel("instance").description("Instance"));
    }

    private static Instance createInstance() {
        return Instance.builder()
                .name(INSTANCE_NAME)
                .build();
    }
}
