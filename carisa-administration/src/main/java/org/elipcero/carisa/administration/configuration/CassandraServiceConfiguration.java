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

import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationEnteIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationEntePropertyIdentifierConvert;
import org.elipcero.carisa.administration.convert.cassandra.DependencyRelationInstanceSpaceIdentifierConvert;
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.administration.repository.cassandra.EntePropertyRepository;
import org.elipcero.carisa.administration.repository.cassandra.EnteRepository;
import org.elipcero.carisa.administration.repository.cassandra.InstanceSpaceRepository;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelationImpl;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure the cassandra specific services
 *
 * @author David Suárez
 */
@Configuration
public class CassandraServiceConfiguration {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private InstanceSpaceRepository instanceSpaceRepository;

    @Autowired
    private EnteRepository enteRepository;

    @Autowired
    private EntePropertyRepository entePropertyRepository;

    @Bean
    public MultiplyDependencyRelation<Space, InstanceSpace> instanceSpaceRelationService() {
        return new MultiplyDependencyRelationImpl<>(
                instanceRepository, spaceRepository, instanceSpaceRepository,
                new DependencyRelationInstanceSpaceIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<Ente> spaceEnteRelationService() {
        return new EmbeddedDependencyRelationImpl<>(
                spaceRepository, enteRepository, new DependencyRelationEnteIdentifierConvert());
    }

    @Bean
    public EmbeddedDependencyRelation<EnteProperty> entePropertyRelationService() {
        return new EmbeddedDependencyRelationImpl<>(
                enteRepository, entePropertyRepository, new DependencyRelationEntePropertyIdentifierConvert());
    }
}
