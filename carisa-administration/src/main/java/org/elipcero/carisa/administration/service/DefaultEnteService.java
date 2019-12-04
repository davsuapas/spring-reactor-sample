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
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.repository.EntePropertyRepository;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see SpaceService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEnteService implements EnteService {

    @NonNull
    private final EnteRepository enteRepository;

    @NonNull
    private final DependencyRelationService<Space, Ente> dependencyRelationService;

    @NonNull
    private final EntePropertyRepository entePropertyRepository;

    /**
     * @see EnteService
     */
    @Override
    public Mono<Ente> getById(final UUID id) {
        return this.enteRepository.findById(id);
    }

    /**
     * @see EnteService
     */
    @Override
    public Mono<Ente> create(final Ente ente) {
        return dependencyRelationService.create((Ente)ente.tryInitId(), "The space: %s doesn't exist");
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Ente>> updateOrCreate(final UUID id, final Ente ente) {
        ente.setId(id);
        return this.enteRepository
                .updateCreate(id,
                        enteForUpdating -> enteForUpdating.setName(ente.getName()),
                        this.create(ente));
    }

    /**
     * @see EnteService
     */
    @Override
    public Flux<EnteProperty> getEntePropertiesByEnte(final UUID enteId) {
        return this.entePropertyRepository.findAllByEnteId(enteId);
    }
}
