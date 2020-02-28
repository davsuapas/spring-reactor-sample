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

import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationEnteCategoryPropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationEnteHirarchyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationEntePropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationInstanceSpaceIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationSpaceEnteIdentifierConvert;
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.domain.SpaceEnte;
import org.elipcero.carisa.administration.repository.EnteCategoryRepository;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.administration.repository.cassandra.EnteCategoryPropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EnteHirarchyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EntePropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.InstanceSpaceRepository;
import org.elipcero.carisa.administration.repository.cassandra.SpaceEnteRepository;
import org.elipcero.carisa.core.reactive.data.DependencyRelationIdentifierConvert;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelationImpl;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.mapping.MapId;

import java.util.UUID;

/**
 * Configure the cassandra specific services
 *
 * @author David Suárez
 */
@Configuration
public class CassandraServiceConfiguration {

    // Repositories

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private InstanceSpaceRepository instanceSpaceRepository;

    @Autowired
    private SpaceEnteRepository spaceEnteRepository;

    @Autowired
    private EnteRepository enteRepository;

    @Autowired
    private EntePropertyRepository entePropertyRepository;

    @Autowired
    private EnteCategoryRepository enteCategoryRepository;

    @Autowired
    private EnteHirarchyRepository enteHirarchyRepository;

    @Autowired
    private EnteCategoryPropertyRepository enteCategoryPropertyRepository;

    // Services

    @Bean
    public DependencyRelationIdentifierConvert<EnteHierarchy, MapId, UUID> enteHirarchyIdentifierConvert() {
        return new DependencyRelationEnteHirarchyIdentifierConvert();
    }

    @Bean
    public MultiplyDependencyRelation<Instance, Space, InstanceSpace> instanceSpaceRelationService() {
        return new MultiplyDependencyRelationImpl<>(
                instanceRepository, spaceRepository, instanceSpaceRepository,
                new DependencyRelationInstanceSpaceIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<Space, Ente, SpaceEnte> spaceEnteRelationService() {
        return new MultiplyDependencyRelationImpl<>(
                spaceRepository, enteRepository, spaceEnteRepository,
                new DependencyRelationSpaceEnteIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<EnteProperty> entePropertyRelationService() {
        return new EmbeddedDependencyRelationImpl<>(
                enteRepository, entePropertyRepository, new DependencyRelationEntePropertyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<EnteCategory, EnteCategory, EnteHierarchy> enteCategoryHirarchyRelationService() {
        return new MultiplyDependencyRelationImpl<>(
                enteCategoryRepository, enteCategoryRepository, enteHirarchyRepository,
                enteHirarchyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<Space, EnteCategory, EnteHierarchy> spaceHirarchyRelationService() {
        return new MultiplyDependencyRelationImpl<>(
                spaceRepository, enteCategoryRepository, enteHirarchyRepository,
                enteHirarchyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<EnteCategory, Ente, EnteHierarchy> enteHirarchyRelationService() {
        return new MultiplyDependencyRelationImpl<>(
                enteCategoryRepository, enteRepository, enteHirarchyRepository,
                enteHirarchyIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<EnteCategoryProperty> enteCategoryPropertyRelationService() {
        return new EmbeddedDependencyRelationImpl<>(
                enteCategoryRepository, enteCategoryPropertyRepository,
                new DependencyRelationEnteCategoryPropertyIdentifierConvert());
    }
}
