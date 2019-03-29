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

package org.elipcero.carisa.skipper.factory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.skipper.domain.Deployer;
import org.elipcero.carisa.skipper.domain.KubernetesDeployer;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAppDeployer;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;

/**
 * Build kubernentes platform factory
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class KubernetesAppDeployerFactory implements DeployerFactory {

    @NonNull
    private final KubernetesClientFactoryInterface kubernetesClientFactory;

    @Override
    public org.springframework.cloud.skipper.domain.Deployer createDeployer(final Deployer platform) {

        KubernetesDeployerProperties properties = (KubernetesDeployerProperties)this.createProperties(platform);

        return new org.springframework.cloud.skipper.domain.Deployer(
                platform.getName(),
                KubernetesDeployer.PLATFORM_TYPE_KUBERNETES,
                new KubernetesAppDeployer(
                        properties, this.kubernetesClientFactory.create(properties))
        );
    }

    @Override
    public Object createProperties(final Deployer platform) {
        KubernetesDeployerProperties properties = new KubernetesDeployerProperties();
        properties.setNamespace(((KubernetesDeployer)platform).getNamespace());
        return  properties;
    }
}
