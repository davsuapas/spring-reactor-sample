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
import org.elipcero.carisa.administration.domain.SpaceEnte;
import org.elipcero.carisa.administration.projection.EnteEntePropertyName;
import org.elipcero.carisa.administration.repository.*;
import org.elipcero.carisa.core.data.EntityDataState;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
    private final SpaceEnteRepository spaceEnteRepository;

    @NonNull
    private final SpaceRepository spaceRepository;

    @NonNull
    private final EntePropertyRepository entePropertyRepository;

    @NonNull
    private final EnteEntePropertyRepository enteEntePropertyRepository;

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
        ente.tryInitId();

        return this.spaceRepository.findById(ente.getSpaceId())
            .flatMap(__ ->
                this.spaceEnteRepository
                    .save(SpaceEnte
                        .builder()
                                .spaceId(ente.getSpaceId())
                                .enteId(ente.getId())
                        .build()))
                .flatMap(__ -> this.enteRepository.save(ente))
            .switchIfEmpty(Mono.error(
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("The space: %s doesn't exist", ente.getSpaceId()))));
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
    public Flux<EnteEntePropertyName> getEntePropertiesByEnte(final UUID enteId) {
        return this.enteEntePropertyRepository.findAllByEnteId(enteId)
                .flatMap(enteEnteProperty -> this.entePropertyRepository.findById(enteEnteProperty.getEntePropertyId()))
                .map(enteProperty -> EnteEntePropertyName
                        .builder()
                            .enteId(enteId)
                            .entePropertyId(enteProperty.getId())
                            .entePropertyName(enteProperty.getName())
                        .build());
    }

    /**
     * @see EnteService
     */
    public Mono<Boolean> removeEnteEnteProperty(final UUID enteId, final UUID entePropertyId) {
        return this.entePropertyRepository.findById(entePropertyId)
                .map(__ -> false)
                .switchIfEmpty(
                        this.enteEntePropertyRepository.deleteById(
                                BasicMapId.id("enteId", enteId).with("entePropertyId", entePropertyId))
                                .then(Mono.just(true)));
    }
}
