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

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.skipper.domain.KubernetesDeployerRequest;
import org.elipcero.carisa.skipper.factory.KubernetesAppDeployerFactory;
import org.elipcero.carisa.skipper.factory.KubernetesClientFactoryInterface;
import org.elipcero.carisa.skipper.service.DeployerService;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for platform operations
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("api/platforms")
@RequiredArgsConstructor
public final class PlatformController {

    @NonNull
    private RepositoryEntityLinks repoLink;

    @NonNull
    private final DeployerService deployerService;

    @NonNull
    private final KubernetesClientFactoryInterface kubernetesClientFactory;

    /**
     * Create kubernetes environment
     *
     * @param kubernetesDeployerRequest properties for kubernetes platform
     * @return deployer resource
     */
    @RequestMapping("/kubernetes/deployers")
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Deployer> deploy(@RequestBody KubernetesDeployerRequest kubernetesDeployerRequest) {

        KubernetesAppDeployerFactory factoryDeployer = new KubernetesAppDeployerFactory(kubernetesDeployerRequest);
        KubernetesDeployerProperties properties = factoryDeployer.createProperties();
        KubernetesClient kubernetesClient = kubernetesClientFactory.Create(properties);

        // Deployer has kubernetes client and properties but is protected therefore i can't access and i need it
        Deployer deployer = this.deployerService.deploy(
                kubernetesClient,
                factoryDeployer.CreateDeployer(properties, kubernetesClient),
                properties);

        return new Resource<Deployer>(deployer,
                this.repoLink.linkToSingleResource(DeployerRepository.class, deployer.getId()).withSelfRel());
    }
}
