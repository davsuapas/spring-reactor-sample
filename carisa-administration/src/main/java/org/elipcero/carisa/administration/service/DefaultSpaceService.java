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
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.projection.EnteSpaceName;
import org.elipcero.carisa.administration.repository.SpaceRepository;
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
public class DefaultSpaceService implements SpaceService {

    @NonNull
    private final SpaceRepository spaceRepository;

    @NonNull
    private final DependencyRelationService<Instance, Space> instanceSpaceService;

    @NonNull
    private final DependencyRelationService<Space, Ente> spaceEnteService;

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
        return this.instanceSpaceService.create(
                (Space)space.tryInitId(), "The instance: %s doesn't exist");
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

        return this.spaceEnteService.getChildrenByParent(spaceId)
                .map(ente -> EnteSpaceName
                        .builder()
                            .spaceId(spaceId)
                            .enteId(ente.getId())
                            .EnteName(ente.getName())
                        .build());
    }
}
