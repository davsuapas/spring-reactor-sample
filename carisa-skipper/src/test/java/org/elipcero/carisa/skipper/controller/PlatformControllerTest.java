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
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.elipcero.carisa.skipper.controller.mock.MockKubernetesClientFactory;
import org.elipcero.carisa.skipper.domain.KubernetesDeployerRequest;
import org.elipcero.carisa.skipper.factory.KubernetesClientFactoryInterface;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeployerAutoConfiguration;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAutoConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerAutoConfiguration;
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.cloud.skipper.server.EnableSkipperServer;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlatformControllerTest.TestConfig.class,
        properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
public class PlatformControllerTest {

    public PlatformControllerTest() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new Jackson2HalModule());
    }

    private final ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeployerRepository deployerRepository;

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
                .andExpect(jsonPath("$.name", is(deployName)));
    }

    @TestConfiguration
    @EnableAutoConfiguration(exclude = {
            CloudFoundryDeployerAutoConfiguration.class,
            KubernetesAutoConfiguration.class,
            LocalDeployerAutoConfiguration.class,
            ManagementWebSecurityAutoConfiguration.class,
            SecurityAutoConfiguration.class,
            UserDetailsServiceAutoConfiguration.class,
            SessionAutoConfiguration.class})
    @EnableSkipperServer
    static class TestConfig {

        private KubernetesServer server;

        @PostConstruct
        void createKubernetesServer() {
            this.server = new KubernetesServer(true, true);
            this.server.before();
        }

        @Bean
        public KubernetesClientFactoryInterface kubernetesClientFactory() {
            return new MockKubernetesClientFactory(this.server.getClient());
        }

        @PreDestroy
        void destroyKubernetesServer() {
            if (this.server != null) {
                this.server.after();
            }
        }
    }
}
