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
import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.skipper.domain.Platform;
import org.elipcero.carisa.skipper.factory.DeployerFactory;
import org.elipcero.carisa.skipper.factory.EnvironmentServiceFactory;
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author David Suárez
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultDeployerService implements DeployerService {

    @NonNull
    private final EnvironmentServiceFactory environmentServiceFactory;

    @NonNull
    private final SkipperSpaceService skipperSpaceService;

    @Override
    @Transactional
    public Deployer deploy(String type, final Platform platform) {

        EnvironmentService environmentService = this.environmentServiceFactory.getEnvironmentService(type);
        DeployerFactory deployerFactory = this.environmentServiceFactory.getDeployerFactory(type);

        Deployer deployerSaved = this.skipperSpaceService.save(deployerFactory.createDeployer(platform));
        platform.setId(deployerSaved.getId());

        environmentService.create(platform, deployerFactory.createProperties(platform));

        return deployerSaved;
    }
}
