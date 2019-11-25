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
import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.projection.EnteSpaceName;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.InstanceSpaceRepository;
import org.elipcero.carisa.administration.repository.SpaceEnteRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.core.data.EntityDataState;
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
public class DefaultSpaceService implements SpaceService {

    @NonNull
    private final SpaceRepository spaceRepository;

    @NonNull
    private final InstanceSpaceRepository instanceSpaceRepository;

    @NonNull
    private final InstanceRepository instanceRepository;

    @NonNull
    private final EnteRepository enteRepository;

    @NonNull
    private final SpaceEnteRepository spaceEnteRepository;

    /**
     * @see SpaceService
     */
    @Override
    public Mono<Space> getById(final UUID id) {
        return this.spaceRepository.findById(id);
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<Space> create(final Space space) {
        space.tryInitId();

        return this.instanceRepository.findById(space.getInstanceId())
            .flatMap(__ ->
                this.instanceSpaceRepository
                    .save(InstanceSpace
                        .builder()
                                .instanceId(space.getInstanceId())
                                .spaceId(space.getId())
                        .build()))
                .flatMap(__ -> this.spaceRepository.save(space))
            .switchIfEmpty(Mono.error(
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("The instance: %s doesn't exist", space.getInstanceId()))));
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Space>> updateOrCreate(final UUID id, final Space space) {
        space.setId(id);
        return this.spaceRepository
                .updateCreate(id,
                        spaceForUpdating -> spaceForUpdating.setName(space.getName()),
                        this.create(space));
    }

    /**
     * @see SpaceService
     */
    @Override
    public Flux<EnteSpaceName> getEntesBySpace(final UUID spaceId) {

        return this.spaceEnteRepository.findAllBySpaceId(spaceId)
                .flatMap(spaceEnte -> this.enteRepository.findById(spaceEnte.getEnteId()))
                .map(ente -> EnteSpaceName
                        .builder()
                            .spaceId(spaceId)
                            .enteId(ente.getId())
                            .EnteName(ente.getName())
                        .build());
    }
}
