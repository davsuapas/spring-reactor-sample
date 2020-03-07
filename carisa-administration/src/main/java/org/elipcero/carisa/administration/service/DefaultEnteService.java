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
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyConnectionInfo;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see EnteService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEnteService implements EnteService {

    @NonNull
    private final EnteRepository enteRepository;

    @NonNull
    private final MultiplyDependencyRelation<EnteCategory, Ente, EnteHierarchy> enteHierarchyRelation;

    @NonNull
    private final EntePropertyService entePropertyService;

    @NonNull
    private final SpaceService spaceService;

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
        return this.spaceService.AddEnteIntoSpace(ente);
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Ente>> updateOrCreate(final UUID id, final Ente ente) {
        return this.enteRepository
                .updateCreate(id,
                        enteForUpdating -> enteForUpdating.setName(ente.getName()),
                        this.create(ente));
    }

    /**
     * @see EnteService
     */
    @Override
    public Flux<EnteProperty> getEntePropertiesByEnteId(final UUID enteId) {
        return this.entePropertyService.getEntePropertiesByEnteId(enteId);
    }

    /**
     * @see EnteCategoryService
     */
    @Override
    public Mono<Ente> connectToCategory(UUID enteId, UUID categoryId) {
        return this.enteHierarchyRelation.connectTo(
                EnteHierarchy.builder()
                        .parentId(categoryId)
                        .id(enteId)
                        .category(false)
                    .build()).map(MultiplyDependencyConnectionInfo::getChild);
    }
}
