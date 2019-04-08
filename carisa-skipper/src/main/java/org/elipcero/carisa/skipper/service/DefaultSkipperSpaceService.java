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
import org.elipcero.carisa.skipper.domain.KubernetesDeployer;
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.cloud.skipper.domain.Platform;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;

import java.util.List;

/**
 * @author David Suárez
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultSkipperSpaceService implements SkipperSpaceService {

    @NonNull
    private final DeployerRepository deployerRepository;

    @NonNull
    private final List<Platform> platforms;

    @Override
    public Deployer save(Deployer deployer) {
        return this.addOrReplaceToPlatform(this.saveDeployer(deployer));
    }

    private Deployer addOrReplaceToPlatform(Deployer deployer) {
        Platform platform = this.platforms.stream()
                .filter(p -> p.getName().equals(KubernetesDeployer.PLATFORM_TYPE_KUBERNETES))
                .findFirst()
                .get();

        platform.getDeployers().removeIf(d -> d.getId().equals(deployer.getId()));
        platform.getDeployers().add(deployer);
        return deployer;
    }

    private Deployer saveDeployer(Deployer deployer) {
        Deployer deployerPersisted = this.deployerRepository.findByName(deployer.getName());
        if (deployerPersisted != null) {
            deployer.setId(deployerPersisted.getId());
            this.deployerRepository.delete(deployerPersisted);
        }
        Deployer result = this.deployerRepository.save(deployer);
        this.log.info("Deployer '{}' saved for platform: '{}'", deployer.getName(), deployer.getType());
        return result;
    }
}
