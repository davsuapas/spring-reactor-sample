/*
 *  Copyright 2019-2022 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.elipcero.carisa.administration.configuration;

import org.elipcero.carisa.core.reactor.data.SimpleReactiveExtendedRepository;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Data configuration
 *
 * @author David Suárez
 */
@EnableCassandraRepositories(repositoryBaseClass = SimpleReactiveExtendedRepository.class)
public class DataConfiguration extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "carisa_administration";
    }

    public String[] getEntityBasePackages() {
        return new String[] { "org.elipcero.carisa.administration.domain" };
    }
}
