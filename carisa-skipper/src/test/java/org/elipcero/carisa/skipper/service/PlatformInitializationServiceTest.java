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

package org.elipcero.carisa.skipper.service;

import org.elipcero.carisa.skipper.BaseConfiguration;
import org.elipcero.carisa.skipper.domain.KubernetesDeployer;
import org.elipcero.carisa.skipper.repository.KubernetesDeployerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAppDeployer;
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.cloud.skipper.domain.Platform;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BaseConfiguration.class },
        properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = PlatformInitializationServiceTest.TestConfig.class)
@ComponentScan(basePackages = "org.elipcero.carisa.skipper.service")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PlatformInitializationServiceTest {

    public static final String KUBERNETES_DEPLOYER_NAME = "KubernetesDeployer";
    public static final String KUBERNETES_NAMESPACE = "KubernetesNamespace";

    @Autowired
    private KubernetesDeployerRepository kubernetesDeployerRepository;

    @Autowired
    private DeployerRepository deployerRepository;

    @Autowired
    private List<Platform> platforms;

    @Test
    public void platformInitialization_load_platforms_should_return_deployers() {

        Deployer deployer = this.deployerRepository.findByName(KUBERNETES_DEPLOYER_NAME);

        assertThat(deployer).isNotNull();
        assertThat(((KubernetesAppDeployer)deployer.getAppDeployer())).isNotNull();

        deployer = platforms.stream()
                .filter(p -> p.getName().equals(KubernetesDeployer.PLATFORM_TYPE_KUBERNETES))
                .flatMap(p -> p.getDeployers().stream())
                .findFirst()
                .get();

        assertThat(deployer).isNotNull();
        assertThat(deployer.getName()).isEqualTo(KUBERNETES_DEPLOYER_NAME);
        assertThat(((KubernetesAppDeployer)deployer.getAppDeployer())).isNotNull();
    }

   @Configuration
   static class TestConfig {

        @Bean
        public PlatformInitializationPrepareData prepareData() {
            return new PlatformInitializationPrepareData();
        }
    }

    static class PlatformInitializationPrepareData {

        @Autowired
        private KubernetesDeployerRepository kubernetesDeployerRepository;

        @EventListener
        public void initialize(final ApplicationStartedEvent event) {
            this.kubernetesDeployerRepository.save(KubernetesDeployer
                    .builder()
                        .id(UUID.randomUUID().toString())
                        .name(KUBERNETES_DEPLOYER_NAME)
                        .namespace(KUBERNETES_NAMESPACE)
                    .build());
        }
    }
}
