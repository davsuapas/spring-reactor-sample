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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * @see SpaceService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEnteService implements EnteService {

    @NonNull
    private final EmbeddedDependencyRelation<Ente> spaceEnteService;

    @NonNull
    private final EmbeddedDependencyRelation<EnteProperty> entePropertyService;

    /**
     * @see EnteService
     */
    @Override
    public Mono<Ente> getById(final Map<String, Object> id) {
        return this.spaceEnteService.getById(id);
    }

    /**
     * @see EnteService
     */
    @Override
    public Mono<Ente> create(final Ente ente) {
        return spaceEnteService.create(ente, "The space: %s doesn't exist");
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Ente>> updateOrCreate(final Ente ente) {
        return this.spaceEnteService
                .updateOrCreate(ente,
                        enteForUpdating -> enteForUpdating.setName(ente.getName()),
                        this.create(ente));
    }

    /**
     * @see EnteService
     */
    @Override
    public Flux<EnteProperty> getEntePropertiesByEnte(final UUID enteId) {
        return this.entePropertyService.getRelationsByParent(enteId);
    }
}
