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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.skipper.domain.Deployer;
import org.elipcero.carisa.skipper.factory.DeployerFactory;
import org.elipcero.carisa.skipper.factory.EnvironmentServiceFactory;
import org.springframework.data.util.Pair;

import java.util.List;

/**
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultDeployerService implements DeployerService {

    @NonNull
    private final EnvironmentServiceFactory environmentServiceFactory;

    @NonNull
    private final SkipperSpaceService skipperSpaceService;

    @Override
    public org.springframework.cloud.skipper.domain.Deployer deploy(String type, final Deployer deployer) {

        EnvironmentService environmentService = this.environmentServiceFactory.getEnvironmentService(type);
        DeployerFactory deployerFactory = this.environmentServiceFactory.getDeployerFactory(type);

        org.springframework.cloud.skipper.domain.Deployer deployerSaved =
                this.skipperSpaceService.save(deployerFactory.createDeployer(deployer));
        deployer.setId(deployerSaved.getId());

        environmentService.create(deployer, deployerFactory.createProperties(deployer));

        return deployerSaved;
    }

    public void recreate(final List<org.springframework.cloud.skipper.domain.Platform> platforms) {
        platforms.stream()
                .filter(platform -> environmentServiceFactory.isImplementedEnvironment(platform.getName()))
                .map(platform -> Pair.of(
                        platform.getName(),
                        environmentServiceFactory.getEnvironmentService(platform.getName()).readDeployers()))
                .forEach(tuple -> {
                    DeployerFactory deployerFactory = environmentServiceFactory.getDeployerFactory(tuple.getFirst());
                    tuple.getSecond()
                            .forEach(deployer -> skipperSpaceService.save(deployerFactory.createDeployer(deployer)));
                });
    }
}
