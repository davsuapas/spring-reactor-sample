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

import org.elipcero.carisa.administration.convert.cassandra.EnteCategoryPropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.EnteCategoryPropertyLinkIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.EnteHirarchyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.EntePropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.InstancePropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.InstanceSpaceIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.PluginTypePluginPrototypeIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.PrototypePropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.SpaceEnteIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.SpaceQueryInstanceIdentifierConvert;
import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteCategoryLinkProperty;
import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.elipcero.carisa.administration.domain.Plugin;
import org.elipcero.carisa.administration.domain.PluginType;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.domain.SpaceEnte;
import org.elipcero.carisa.administration.domain.SpaceQueryInstance;
import org.elipcero.carisa.administration.repository.DynamicObjectInstanceRepository;
import org.elipcero.carisa.administration.repository.DynamicObjectPrototypeRepository;
import org.elipcero.carisa.administration.repository.EnteCategoryRepository;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.PluginTypeRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.administration.repository.cassandra.DynamicObjectInstancePropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.DynamicObjectPrototypePropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EnteCategoryLinkPropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EnteCategoryPropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EnteHirarchyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EntePropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.InstanceSpaceRepository;
import org.elipcero.carisa.administration.repository.cassandra.PluginRepository;
import org.elipcero.carisa.administration.repository.cassandra.SpaceEnteRepository;
import org.elipcero.carisa.administration.repository.cassandra.SpaceQueryInstanceRepository;
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

    @Autowired
    private EnteCategoryLinkPropertyRepository enteCategoryLinkPropertyRepository;

    @Autowired
    private DynamicObjectPrototypeRepository dynamicObjectPrototypeRepository;

    @Autowired
    private DynamicObjectInstanceRepository dynamicObjectInstanceRepository;

    @Autowired
    private SpaceQueryInstanceRepository spaceQueryInstanceRepository;

    @Autowired
    private PluginRepository pluginRepository;

    @Bean
    public PluginTypeRepository pluginTypeRepository() {
        return new PluginTypeRepository();
    }

    @Autowired
    private DynamicObjectPrototypePropertyRepository dynamicObjectPrototypePropertyRepository;

    @Autowired
    private DynamicObjectInstancePropertyRepository dynamicObjectInstancePropertyRepository;

    // Converter

    @Bean
    public DependencyRelationIdentifierConvert<EnteHierarchy, MapId, UUID> enteHirarchyIdentifierConvert() {
        return new EnteHirarchyIdentifierConvert();
    }

    // Relations

    @Bean
    public MultiplyDependencyRelation<Instance, Space, InstanceSpace> instanceSpaceRelationRelation() {
        return new MultiplyDependencyRelationImpl<>(
                instanceRepository, spaceRepository, instanceSpaceRepository,
                new InstanceSpaceIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<Space, Ente, SpaceEnte> spaceEnteRelation() {
        return new MultiplyDependencyRelationImpl<>(
                spaceRepository, enteRepository, spaceEnteRepository,
                new SpaceEnteIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<EnteProperty> entePropertyRelation() {
        return new EmbeddedDependencyRelationImpl<>(
                enteRepository, entePropertyRepository, new EntePropertyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<EnteCategory, EnteCategory, EnteHierarchy> enteCategoryHirarchyRelation() {
        return new MultiplyDependencyRelationImpl<>(
                enteCategoryRepository, enteCategoryRepository, enteHirarchyRepository,
                enteHirarchyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<Space, EnteCategory, EnteHierarchy> spaceHirarchyRelation() {
        return new MultiplyDependencyRelationImpl<>(
                spaceRepository, enteCategoryRepository, enteHirarchyRepository,
                enteHirarchyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<EnteCategory, Ente, EnteHierarchy> enteHirarchyRelation() {
        return new MultiplyDependencyRelationImpl<>(
                enteCategoryRepository, enteRepository, enteHirarchyRepository,
                enteHirarchyIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<EnteCategoryProperty> enteCategoryPropertyRelation() {
        return new EmbeddedDependencyRelationImpl<>(
                enteCategoryRepository, enteCategoryPropertyRepository,
                new EnteCategoryPropertyIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<EnteCategoryProperty, Ente, EnteCategoryLinkProperty> linkEnteRelation() {
        return new MultiplyDependencyRelationImpl<>(
                enteCategoryPropertyRepository, enteRepository, enteCategoryLinkPropertyRepository,
                new EnteCategoryPropertyLinkIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<Space, DynamicObjectInstance, SpaceQueryInstance> spaceQueryRelation() {
        return new MultiplyDependencyRelationImpl<>(
                spaceRepository, dynamicObjectInstanceRepository, spaceQueryInstanceRepository,
                new SpaceQueryInstanceIdentifierConvert());
    }

    @Bean
    public MultiplyDependencyRelation<PluginType, DynamicObjectPrototype, Plugin> pluginTypePluginRelation() {
        return new MultiplyDependencyRelationImpl<>(
                pluginTypeRepository(), dynamicObjectPrototypeRepository, pluginRepository,
                new PluginTypePluginPrototypeIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<DynamicObjectPrototypeProperty> prototypeProperty() {
        return new EmbeddedDependencyRelationImpl<>(
                dynamicObjectPrototypeRepository, dynamicObjectPrototypePropertyRepository,
                new PrototypePropertyIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<DynamicObjectInstanceProperty<?>> instanceProperty() {
        return new EmbeddedDependencyRelationImpl<>(
                dynamicObjectInstanceRepository, dynamicObjectInstancePropertyRepository,
                new InstancePropertyIdentifierConvert());
    }
}
