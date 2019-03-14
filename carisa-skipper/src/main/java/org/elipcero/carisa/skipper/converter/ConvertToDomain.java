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

package org.elipcero.carisa.skipper.converter;

import org.elipcero.carisa.skipper.domain.KubernetesDeployerRequest;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAppDeployer;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;
import org.springframework.cloud.skipper.domain.Deployer;

/**
 * Convert request to class domain
 *
 * @author David Suárez
 */
public final class ConvertToDomain {

    public static final String PLATFORM_TYPE_KUBERNETES = "kubernetes";

    public static Deployer from(final KubernetesDeployerRequest kubernetesDeployerRequest) {
        KubernetesDeployerProperties properties = new KubernetesDeployerProperties();
        properties.setNamespace(kubernetesDeployerRequest.getNamespaces());

        return new Deployer(
                kubernetesDeployerRequest.getName(),
                PLATFORM_TYPE_KUBERNETES,
                new KubernetesAppDeployer(
                        properties,
                        /*Client is not necessary. Just need KubernetesAppDeployer to save into repository*/
                        null)
        );
    }
}
