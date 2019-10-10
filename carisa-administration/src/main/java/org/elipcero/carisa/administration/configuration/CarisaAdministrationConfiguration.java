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

import org.elipcero.carisa.administration.repository.*;
import org.elipcero.carisa.administration.service.*;
import org.elipcero.carisa.core.application.configuration.ServiceProperties;
import org.elipcero.carisa.core.reactive.misc.DataLockController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * General configuration
 *
 * @author David Suárez
 */
@EnableConfigurationProperties(ServiceProperties.class)
@Configuration
public class CarisaAdministrationConfiguration {

    @Autowired
    private ServiceProperties serviceProperties;

    @Autowired
    private DataLockController dataLockController;

    // Instance configuration

    @Autowired
    private InstanceRepository instanceRepository;

    @Bean
    public InstanceService instanceService() {
        return new DefaultInstanceService(
                instanceRepository, serviceProperties, dataLockController,
                instanceSpaceRepository, spaceRepository);
    }

    // Space configuration

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private InstanceSpaceRepository instanceSpaceRepository;

    @Bean
    public SpaceService spaceService() {
        return new DefaultSpaceService(
                spaceRepository, instanceSpaceRepository, instanceRepository,
                enteRepository, spaceEnteRepository);
    }

    // Ente configuration

    @Autowired
    private EnteRepository enteRepository;

    @Autowired
    private SpaceEnteRepository spaceEnteRepository;

    @Bean
    public EnteService enteService() {
        return new DefaultEnteService(enteRepository, spaceEnteRepository,
                spaceRepository, entePropertyRepository, enteEntePropertyRepository);
    }

    // Ente property configuration

    @Autowired
    private EntePropertyRepository entePropertyRepository;

    @Autowired
    private EnteEntePropertyRepository enteEntePropertyRepository;

    @Bean
    public EntePropertyService entePropertyService() {
        return new DefaultEntePropertyService(entePropertyRepository, enteEntePropertyRepository, enteRepository);
    }
}
