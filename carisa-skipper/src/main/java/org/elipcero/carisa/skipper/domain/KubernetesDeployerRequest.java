/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elipcero.carisa.skipper.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This contain all the information to create kubernetes deployer
 *
 * @author David Suárez
 *
 */
@Getter
@AllArgsConstructor
public class KubernetesDeployerRequest {

    public static final String PLATFORM_TYPE_KUBERNETES = "kubernetes";

    private String name;
    private String namespaces;
}
