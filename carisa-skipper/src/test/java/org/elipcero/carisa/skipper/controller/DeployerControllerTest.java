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

package org.elipcero.carisa.skipper.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elipcero.carisa.skipper.BaseConfiguration;
import org.elipcero.carisa.skipper.domain.KubernetesDeployer;
import org.elipcero.carisa.skipper.domain.KubernetesDeployerRequest;
import org.elipcero.carisa.skipper.factory.KubernetesClientFactoryInterface;
import org.elipcero.carisa.skipper.repository.KubernetesDeployerRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.cloud.skipper.domain.Platform;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaseConfiguration.class,
        properties = "spring.main.allow-bean-definition-overriding=true")
@ComponentScan(basePackages = "org.elipcero.carisa.skipper.controller")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DeployerControllerTest {

    public DeployerControllerTest() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new Jackson2HalModule());
    }

    protected final ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeployerRepository deployerRepository;

    @Autowired
    private KubernetesDeployerRepository kubernetesDeployerRepository;

    @Autowired
    private List<Platform> platforms;

    @Autowired
    private KubernetesClientFactoryInterface kubernetesClientFactory;

    @After
    public void cleanup() {
        this.deployerRepository.deleteAll();
    }

    @Test
    public void deploy_kubernetes_should_return_ok() throws Exception {

        String deployName = "deployName";
        String namespace = "namespace";

        KubernetesDeployerRequest request = new KubernetesDeployerRequest(deployName, namespace);

        MvcResult resultDeployer = this.mockMvc.perform(
                post("/api/platforms/kubernetes/deployers")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        Resource<Deployer> deployerDeploy = this.objectMapper
                .readValue(resultDeployer.getResponse().getContentAsString(),
                        new TypeReference<Resource<Deployer>>() { });

        this.mockMvc.perform(get(deployerDeploy.getLink(Link.REL_SELF).getHref()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(deployName)))
                .andExpect(jsonPath("$.description", notNullValue()));

        assertThat(this.platforms.stream()
                .filter(p -> p.getName().equals(KubernetesDeployer.PLATFORM_TYPE_KUBERNETES))
                .findFirst()
                .get().getDeployers().isEmpty()).isFalse();

        assertThat(this.kubernetesDeployerRepository
                .findById(getId(deployerDeploy.getLink(Link.REL_SELF).getHref()))
                    .isPresent()).isTrue();

        assertThat(kubernetesClientFactory
                .create(null)
                .namespaces()
                .withName(namespace).get()).isNotNull();
    }

    private static String getId(String hRef) {
        return Arrays.stream(hRef.split("/")).reduce((first, second) -> second).get();
    }
}
