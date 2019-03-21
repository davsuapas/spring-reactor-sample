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

import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.skipper.domain.KubernetesDeployerRequest;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;

/**
 * Create kubernetes namespace
 *
 * @author David Suárez
 */
@Slf4j
public class KubernetesEnvironmentService implements EnvironmentService {

    public String getType() {
        return KubernetesDeployerRequest.PLATFORM_TYPE_KUBERNETES;
    }

    /**
     * Create namespace in kubernetes. If it exists replace
     *
     * @param propertiesDeployer properties
     * @param client kubernetes client
     */
    public void create(Object propertiesDeployer, KubernetesClient client) {
        this.log.debug("Kubernetes client configuration {}", client.getConfiguration().toString());

        KubernetesDeployerProperties properties = (KubernetesDeployerProperties)propertiesDeployer;

        client.namespaces().createOrReplace(
            new NamespaceBuilder().withNewMetadata()
                .withName(properties.getNamespace())
                .endMetadata().build());

        this.log.info("Kubernetes namespace '{}' created or replaced ", properties.getNamespace());
    }
}
