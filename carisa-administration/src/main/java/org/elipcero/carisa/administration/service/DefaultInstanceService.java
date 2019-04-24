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
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.UpdateInstanceRequest;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see org.elipcero.carisa.administration.service.InstanceService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultInstanceService implements InstanceService {

    @NonNull
    private final InstanceRepository instanceRepository;

    @Override
    public Mono<Instance> getById(final UUID id) {
        return this.instanceRepository.findById(id);
    }

    @Override
    public Mono<Instance> create(final Instance instance) {
        return this.instanceRepository.save(instance);
    }

    @Override
    public Mono<EntityDataState<Instance>> updateOrCreate(final UUID id, final UpdateInstanceRequest updateInstance) {
        return this.instanceRepository
                .updateOrCreate(id,
                    instanceForUpdating -> instanceForUpdating.setName(updateInstance.getName()),
                    Instance
                        .builder()
                            .id(id)
                            .name(updateInstance.getName())
                        .build()
                );
    }
}
