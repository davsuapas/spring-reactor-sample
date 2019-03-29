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

package org.elipcero.carisa.skipper.repository;

import org.elipcero.carisa.skipper.domain.KubernetesDeployer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Deployer repository. we use this repository for recovering the platform information
 * when it is restarted the service and recreate skipper services
 *
 * @author David Suárez
 */
@RepositoryRestResource(exported = false)
public interface KubernetesDeployerRepository extends CrudRepository<KubernetesDeployer, String> {
}
