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

package org.elipcero.carisa.core.reactive.misc;

import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionIntegrationTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.elipcero.carisa.core.config.DataConfiguration;
import org.elipcero.carisa.core.configuration.EnableCassandraDataLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * @author David Suárez
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CassandraDataLockControllerTest.LockConfiguration.class)
@CassandraDataSet(keyspace = DataConfiguration.CONST_KEY_SPACE_NAME, value = "cassandra/datalock-controller.cql")
@TestExecutionListeners(
        listeners = CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class,
        mergeMode = MERGE_WITH_DEFAULTS)
@EmbeddedCassandra
public class CassandraDataLockControllerTest {

    @Autowired
    private CassandraDataLockController dataLock;

    @Test
    public void lock_new_resource_should_return_true() {

        UUID id = UUID.randomUUID();
        StepVerifier
                .create(this.dataLock.lock(id, 60))
                    .expectNextMatches(result -> {
                        assertThat(result).isTrue().as("Resource locked");
                        return true;
                    })
                .verifyComplete();
    }

    @Test
    public void unlock_resource_should_return_true() {

        UUID id = UUID.randomUUID();
        StepVerifier
                .create(this.dataLock.lock(id, 60).flatMap(done -> this.dataLock.unLock(id)))
                .expectNextMatches(result -> {
                    assertThat(result).isTrue().as("Resource unlocked");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void lock_resource_concurrently_the_second_one_should_return_false() {

        UUID id = UUID.randomUUID();
        StepVerifier
                .create(this.dataLock.lock(id, 60).flatMap(done -> this.dataLock.lock(id, 60)))
                .expectNextMatches(result -> {
                    assertThat(result).isFalse().as("The second resource can not lock");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void lock_resource_concurrently_and_expire_the_second_one_should_return_true() {

        UUID id = UUID.randomUUID();
        StepVerifier
                .create(this.dataLock.lock(id, 1)
                        .delayElement(Duration.ofSeconds(1))
                        .flatMap(done -> this.dataLock.lock(id, 60)))
                .expectNextMatches(result -> {
                    assertThat(result).isTrue().as("The second resource lock");
                    return true;
                })
                .verifyComplete();
    }

    @Configuration
    @Import(DataConfiguration.class)
    @EnableCassandraDataLock
    public static class LockConfiguration {
    }
}
