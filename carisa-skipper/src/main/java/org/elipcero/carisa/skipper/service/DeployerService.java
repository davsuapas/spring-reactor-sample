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

import org.springframework.cloud.skipper.domain.Deployer;

/**
 * Populates deployer in hot (through rest service)
 *
 * @author David Suárez
 */
public interface DeployerService {

    /**
     * Save deployer into repository and create the platform environment
     * Deployer has the properties but is protected therefore i can't access
     * @param deployer the deployer
     * @param properties properties
     */
    Deployer deploy(Deployer deployer, final Object properties);
}
