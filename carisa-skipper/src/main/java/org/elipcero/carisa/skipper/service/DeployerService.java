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
import org.springframework.cloud.skipper.domain.Deployer;
import org.springframework.cloud.skipper.server.repository.map.DeployerRepository;
import org.springframework.stereotype.Service;

/**
 * Populates deployer in hot (through rest service)
 *
 * @author David Suárez
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class DeployerService {

    @NonNull
    private final DeployerRepository deployerRepository;

    /**
     * Save deployer into repository and create the platform environment
     * @param deployer the deployer
     */
    public Deployer deploy(final Deployer deployer) {
        Deployer result = this.deployerRepository.save(deployer);
        this.log.info("Deployer '{}' saved for platform: '{}'", deployer.getName(), deployer.getType());
        return result;
    }
}
