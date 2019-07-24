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
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Reactive extended crud repository
 *
 * @author David Suárez
 */
@NoRepositoryBean
public interface CustomizedReactiveCrudRepository<T, ID extends Serializable>
        extends ReactiveCrudRepository<T, ID> {

    /**
     * Update the domain entity or return the created entity. If exists update the entity through of
     * a predicate parameter, otherwise return the created entity with state
     *
     * @param id id for finding
     * @param updateChange predicate for updating entity
     * @param monoEntityCreated entity inserted
     * @return Mono<EntityDataState<T>> the entity with state
     */
    Mono<EntityDataState<T>> updateCreate(final ID id, final Consumer<T> updateChange,
                                          final Mono<T> monoEntityCreated);
}
