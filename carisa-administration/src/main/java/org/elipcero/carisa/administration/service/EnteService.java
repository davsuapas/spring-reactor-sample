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

package org.elipcero.carisa.administration.service;

import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Operations for Ente
 *
 * @author David Suárez
 */
public interface EnteService {

    /**
     * Get ente by id
     * @param id
     * @return ente found
     */
    Mono<Ente> getById(UUID id);

    /**
     * Create the Ente and insert it into the Space. Insert the Ente into Space
     * and create it are not done in the same transaction. In this case, the user has the
     * responsibility of removing the relation when fault. Checking if the space exists
     * @param ente Ente for creating
     * @return Ente created
     */
    Mono<Ente> create(Ente ente);

    /**
     * Update or create the ente. If the id exits is updated
     * otherwise is created. The EnteId can not be updated. To create @see create
     * @param id
     * @param ente Ente for updating or creating
     * @return Ente created or updated
     */
    Mono<EntityDataState<Ente>> updateOrCreate(final UUID id, final Ente ente);
}