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

package org.elipcero.carisa.core.reactor.data;

import org.elipcero.carisa.core.data.DomainDataState;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleReactiveCassandraRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Adding new features to reactive crud repository
 *
 * @author David Suárez
 */
public class SimpleReactiveExtendedRepository<T, ID extends Serializable>
        extends SimpleReactiveCassandraRepository<T, ID>
        implements ReactiveExtendedCrudRepository<T, ID> {

    public SimpleReactiveExtendedRepository(
            CassandraEntityInformation<T, ID> entityInformation,
            ReactiveCassandraOperations operations) {

        super(entityInformation, operations);
    }

    /**
     * Update or create entity of domain. If exists update the entity through of
     * a predicate parameter, otherwise insert the entity from a parameter
     *
     * @param id id for finding
     * @param updateChange predicate for updating entity
     * @param entityForCreating entity if is inserted
     * @return Mono<DomainDataState<T>>
     */
    @Override
    public Mono<DomainDataState<T>> updateOrCreate(final ID id, Consumer<T> updateChange, T entityForCreating) {
        return this
                .findById(id)
                .flatMap(entity -> {
                    updateChange.accept(entity);
                    return this.save(entity)
                            .map(instanceUpdated ->
                                    DomainDataState.<T>
                                         builder()
                                            .domainState(DomainDataState.State.updated)
                                            .entity(instanceUpdated)
                                         .build());

                })
                .switchIfEmpty(
                        this.save(entityForCreating)
                                .map(instanceCreated ->
                                        DomainDataState.<T>
                                            builder()
                                                .domainState(DomainDataState.State.created)
                                                .entity(instanceCreated)
                                            .build())
                );
    }
}
