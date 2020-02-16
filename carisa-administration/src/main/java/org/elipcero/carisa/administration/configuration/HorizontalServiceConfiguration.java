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

import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.repository.EnteCategoryRepository;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.administration.service.DefaultEnteCategoryService;
import org.elipcero.carisa.administration.service.DefaultEntePropertyService;
import org.elipcero.carisa.administration.service.DefaultEnteService;
import org.elipcero.carisa.administration.service.DefaultInstanceService;
import org.elipcero.carisa.administration.service.DefaultSpaceService;
import org.elipcero.carisa.administration.service.EnteCategoryService;
import org.elipcero.carisa.administration.service.EntePropertyService;
import org.elipcero.carisa.administration.service.EnteService;
import org.elipcero.carisa.administration.service.InstanceService;
import org.elipcero.carisa.administration.service.SpaceService;
import org.elipcero.carisa.core.application.configuration.ServiceProperties;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
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
public class HorizontalServiceConfiguration {

    @Autowired
    private ServiceProperties serviceProperties;

    @Autowired
    private DataLockController dataLockController;

    // Relations

    @Autowired
    private MultiplyDependencyRelation<Instance, Space, InstanceSpace> instanceSpaceService;

    @Autowired
    private EmbeddedDependencyRelation<Ente> spaceEnteService;

    @Autowired
    private EmbeddedDependencyRelation<EnteProperty> entePropertyService;

    @Autowired
    private MultiplyDependencyRelation<EnteCategory, EnteCategory, EnteHierarchy> enteCategoryHirarchyService;

    @Autowired
    private MultiplyDependencyRelation<Space, EnteCategory, EnteHierarchy> spaceHirarchyService;

    // Instance configuration

    @Autowired
    private InstanceRepository instanceRepository;

    @Bean
    public InstanceService instanceService() {
        return new DefaultInstanceService(
                instanceRepository, serviceProperties, dataLockController, instanceSpaceService);
    }

    // Space configuration

    @Autowired
    private SpaceRepository spaceRepository;

    @Bean
    public SpaceService spaceService() {
        return new DefaultSpaceService(spaceRepository, instanceSpaceService, spaceEnteService, spaceHirarchyService);
    }

    // Ente configuration

    @Bean
    public EnteService enteService() {
        return new DefaultEnteService(spaceEnteService, entePropertyService);
    }

    // Ente property configuration

    @Bean
    public EntePropertyService entePropertyService() {
        return new DefaultEntePropertyService(entePropertyService);
    }

    // Ente category configuration

    @Autowired
    private EnteCategoryRepository enteCategoryRepository;

    @Bean
    public EnteCategoryService enteCategoryService() {
        return new DefaultEnteCategoryService(
                enteCategoryRepository, enteCategoryHirarchyService, spaceHirarchyService);
    }
}
