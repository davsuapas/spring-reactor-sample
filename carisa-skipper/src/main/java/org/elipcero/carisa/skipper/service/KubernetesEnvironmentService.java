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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.skipper.domain.Deployer;
import org.elipcero.carisa.skipper.domain.KubernetesDeployer;
import org.elipcero.carisa.skipper.factory.KubernetesClientFactoryInterface;
import org.elipcero.carisa.skipper.repository.KubernetesDeployerRepository;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;

/**
 * create kubernetes namespace
 *
 * @author David Suárez
 */
@Slf4j
@RequiredArgsConstructor
public class KubernetesEnvironmentService implements EnvironmentService {

    @NonNull
    private final KubernetesDeployerRepository kubernetesDeployerRepository;

    @NonNull
    private final KubernetesClientFactoryInterface kubernetesClientFactory;

    /**
     * Create namespace in kubernetes. If it exists replace
     * Save the deployer into db
     *
     * @param deployer The platform information
     * @param properties platform properties
     */
    @Override
    public void create(Deployer deployer, Object properties) {

        KubernetesDeployerProperties kubernetesProperties = (KubernetesDeployerProperties)properties;
        KubernetesClient client = this.kubernetesClientFactory.create(kubernetesProperties);

        this.kubernetesDeployerRepository.save((KubernetesDeployer)deployer);
        this.log.debug("Kubernetes platform saved {}", deployer.getName());

        this.log.debug("Kubernetes client configuration {}", client.getConfiguration().toString());
        client.namespaces().createOrReplace(
            new NamespaceBuilder().withNewMetadata()
                .withName(kubernetesProperties.getNamespace())
                .endMetadata().build());
        this.log.info("Kubernetes namespace '{}' created or replaced ", kubernetesProperties.getNamespace());
    }

    @Override
    public Iterable<? extends Deployer> readDeployers() {
        return this.kubernetesDeployerRepository.findAll();
    }
}
