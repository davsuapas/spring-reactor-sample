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

package org.elipcero.carisa.skipper;

import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.elipcero.carisa.skipper.configuration.CarisaSkipperConfiguration;
import org.elipcero.carisa.skipper.factory.KubernetesClientFactoryInterface;
import org.elipcero.carisa.skipper.mock.MockKubernetesClientFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeployerAutoConfiguration;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAutoConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerAutoConfiguration;
import org.springframework.cloud.skipper.server.EnableSkipperServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author David Suárez
 */
@Configuration
@EnableAutoConfiguration(exclude = {
        CloudFoundryDeployerAutoConfiguration.class,
        KubernetesAutoConfiguration.class,
        LocalDeployerAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        SessionAutoConfiguration.class})
@Import(CarisaSkipperConfiguration.class)
@EnableSkipperServer
public class BaseConfiguration {

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
