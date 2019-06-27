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

package org.elipcero.carisa.core.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.core.reactive.misc.CassandraDataLockController;
import org.elipcero.carisa.core.reactive.misc.DataLockController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;

import javax.annotation.PostConstruct;

/**
 * Configuration to critical section lock between process using cassandra database
 *
 * @author David Suárez
 */
@Slf4j
@Configuration
public class CassandraDataLockConfiguration {

    @Autowired
    private ReactiveCassandraOperations cqlTemplate;


    @PostConstruct
    public void build() {
        new DatalockSchema(this).BuildIfNecessary();
    }

    @Bean
    public DataLockController dataLockController() {
        return new CassandraDataLockController(this.cqlTemplate);
    }

    @AllArgsConstructor
    private static class DatalockSchema {

        private CassandraDataLockConfiguration cassandraDataLockConfiguration;

        public void BuildIfNecessary() {
            this.cassandraDataLockConfiguration.cqlTemplate.getReactiveCqlOperations()
                .execute("CREATE TABLE IF NOT EXISTS data_lock (Id uuid PRIMARY KEY, DateExpired timestamp)")
                .subscribe(
                        done -> this.cassandraDataLockConfiguration.log.info("Built datalock schema. Done: {}", done));
        }
    }
}
