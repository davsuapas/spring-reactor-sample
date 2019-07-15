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

package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.EntityDataState;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleReactiveCassandraRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Adding new features to reactive crud repository
 *
 * @author David Suárez
 */
public class CustomizedReactiveCrudRepositoryImpl<T, ID extends Serializable>
        extends SimpleReactiveCassandraRepository<T, ID>
        implements CustomizedReactiveCrudRepository<T, ID> {

    public CustomizedReactiveCrudRepositoryImpl(
            CassandraEntityInformation<T, ID> entityInformation,
            ReactiveCassandraOperations operations) {

        super(entityInformation, operations);
    }

    /**
     * @see CustomizedReactiveCrudRepository
     */
    @Override
    public Mono<EntityDataState<T>> updateCreate(
            final ID id, final Consumer<T> updateChange, final T entityForCreating) {

        return this.updateCreate(id, updateChange, entityForCreating, () -> Mono.empty());
    }

    /**
     * @see CustomizedReactiveCrudRepository
     */
    @Override
    public Mono<EntityDataState<T>> updateCreate(
            final ID id, final Consumer<T> updateChange,
            final T entityForCreating, final Supplier<Mono<?>> eventBeforeCreating) {

        return this
                .findById(id)
                .flatMap(entity -> {
                    updateChange.accept(entity);
                    return this.save(entity)
                            .map(instanceUpdated ->
                                    EntityDataState.<T>
                                         builder()
                                            .domainState(EntityDataState.State.updated)
                                            .entity(instanceUpdated)
                                         .build());

                })
                .switchIfEmpty(
                        eventBeforeCreating.get().then(
                            this.save(entityForCreating)
                                    .map(instanceCreated ->
                                            EntityDataState.<T>
                                                builder()
                                                    .domainState(EntityDataState.State.created)
                                                    .entity(instanceCreated)
                                                .build()))
                );
    }
}
