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

import org.elipcero.carisa.skipper.factory.DefaultKubernetesClientFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
public class DefaultKubernetesClientFactoryTest {

    @Test
    public void create_kubernetes_factory_should_return_kubernetes_client() {
        assertThat(new DefaultKubernetesClientFactory()
                    .create(new KubernetesDeployerProperties())).isNotNull();
    }
}
