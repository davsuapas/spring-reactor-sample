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

package org.elipcero.carisa.skipper.configuration;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.elipcero.carisa.skipper.configuration.factory.DefaultEnvironmentServiceFactory;
import org.elipcero.carisa.skipper.configuration.factory.EnvironmentServiceFactory;
import org.elipcero.carisa.skipper.service.DefaultDeployerService;
import org.elipcero.carisa.skipper.service.DeployerService;
import org.elipcero.carisa.skipper.service.KubernetesEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom Skipper configuration
 *
 * @author David Suárez
 */
@Configuration
public class CarisaSkipperConfiguration {

    @Autowired
    private DeployerRepository deployerRepository;

    @Autowired
    private KubernetesClient kubernetesClient;

    @Bean
    public EnvironmentServiceFactory environmentServiceFactory() {
        return new DefaultEnvironmentServiceFactory()
                .Register(new KubernetesEnvironmentService(kubernetesClient));
    }

    @Bean
    public DeployerService deployerService() {
        return new DefaultDeployerService(this.deployerRepository, this.environmentServiceFactory());
    }
}
