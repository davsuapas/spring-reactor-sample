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
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.repository.EntePropertyRepository;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * @see EntePropertyService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEntePropertyService implements EntePropertyService {

    @NonNull
    private final EntePropertyRepository entePropertyRepository;

    @NonNull
    private final EnteRepository enteRepository;

    /**
     * @see EntePropertyService
     */
    @Override
    public Mono<EnteProperty> getById(final MapId id) {
        return this.entePropertyRepository.findById(id);
    }

    /**
     * @see EntePropertyService
     */
    @Override
    public Mono<EnteProperty> create(final EnteProperty enteProperty) {
        enteProperty.tryInitId();
        return this.enteRepository.findById(enteProperty.getEnteId())
            .flatMap(__ ->
                this.entePropertyRepository
                    .save(EnteProperty
                        .builder()
                                .enteId(enteProperty.getEnteId())
                                .propertyId(enteProperty.getPropertyId())
                                .name(enteProperty.getName())
                                .type(enteProperty.getType())
                        .build()))
            .switchIfEmpty(Mono.error(
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("The Ente: %s doesn't exist", enteProperty.getEnteId()))));
    }

    /**
     * @see EntePropertyService
     */
    @Override
    public Mono<EntityDataState<EnteProperty>> updateOrCreate(final MapId id, final EnteProperty enteProperty) {
        enteProperty.setId(id);
        return this.entePropertyRepository
                .updateCreate(id,
                        entePropertyForUpdating -> {
                            entePropertyForUpdating.setName(enteProperty.getName());
                            entePropertyForUpdating.setType(enteProperty.getType());
                        },
                        this.create(enteProperty));
    }
}
