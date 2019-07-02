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

package org.elipcero.carisa.administration.configuration;

import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.administration.service.DefaultInstanceService;
import org.elipcero.carisa.administration.service.DefaultSpaceService;
import org.elipcero.carisa.administration.service.InstanceService;
import org.elipcero.carisa.administration.service.SpaceService;
import org.elipcero.carisa.core.application.configuration.ServiceProperties;
import org.elipcero.carisa.core.reactive.misc.DataLockController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

/**
 * General configuration
 *
 * @author David Suárez
 */
@EnableHypermediaSupport(type = {
        EnableHypermediaSupport.HypermediaType.HAL, EnableHypermediaSupport.HypermediaType.HAL_FORMS })
@EnableConfigurationProperties(ServiceProperties.class)
@Configuration
public class CarisaAdministrationConfiguration {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private ServiceProperties serviceProperties;

    @Autowired
    private DataLockController dataLockController;

    @Bean
    public InstanceService instanceService() {
        return new DefaultInstanceService(instanceRepository, serviceProperties, dataLockController);
    }

    @Autowired
    private SpaceRepository spaceRepository;

    @Bean
    public SpaceService spaceService() {
        return new DefaultSpaceService(spaceRepository);
    }
}
