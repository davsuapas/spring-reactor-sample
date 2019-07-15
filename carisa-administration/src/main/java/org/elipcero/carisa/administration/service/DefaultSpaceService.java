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
import org.elipcero.carisa.administration.repository.InstanceSpaceRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.core.data.EntityDataState;
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
        space.tryInit();

        return this.instanceSpaceRepository
            .save(InstanceSpace
                .builder()
                        .instanceId(space.getInstanceId())
                        .spaceId(space.getId())
                .build())
            .flatMap(__ -> this.spaceRepository.save(space));
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Space>> updateOrCreate(final UUID id, final Space space) {
        return this.spaceRepository
                .updateCreate(id,
                        spaceForUpdating -> spaceForUpdating.setName(space.getName()),
                        Space
                            .builder()
                                .id(id)
                                .name(space.getName())
                                .instanceId(space.getInstanceId())
                            .build(),
                        () -> this.instanceSpaceRepository // Event before creating
                                .save(InstanceSpace
                                    .builder()
                                        .instanceId(space.getInstanceId())
                                        .spaceId(id)
                                    .build())
                );
    }
}
