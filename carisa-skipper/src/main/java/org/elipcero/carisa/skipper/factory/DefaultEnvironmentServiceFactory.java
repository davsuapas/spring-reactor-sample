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

import org.elipcero.carisa.skipper.service.EnvironmentService;

import java.util.HashMap;
import java.util.Map;

/**
 * Build the environment service factory
 *
 * @author David Suárez
 */
public final class DefaultEnvironmentServiceFactory implements EnvironmentServiceFactory {

    private final Map<String, EnvironmentService> environmentServices = new HashMap<String, EnvironmentService>();

    public EnvironmentServiceFactory Register(EnvironmentService environmentService) {
        this.environmentServices.put(environmentService.getType(), environmentService);
        return this;
    }

    public EnvironmentService Get(String type) {
        EnvironmentService environmentService = this.environmentServices.get(type);
        if (environmentService == null) {
            throw new IllegalArgumentException(
                    String.format("The platform: {} is not implemented", type));
        }
        else {
            return environmentService;
        }
    }
}
