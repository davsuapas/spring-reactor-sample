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
import org.elipcero.carisa.administration.domain.EnteCategoryLinkProperty;
import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
import org.elipcero.carisa.administration.repository.EnteCategoryRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyConnectionInfo;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * @see EnteCategoryPropertyService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEnteCategoryPropertyService implements EnteCategoryPropertyService {

    @NonNull
    private final EnteCategoryRepository enteCategoryRepository;

    @NonNull
    private final EmbeddedDependencyRelation<EnteCategoryProperty> enteCategoryPropertyRelation;

    @NonNull
    private final MultiplyDependencyRelation<EnteCategoryProperty, Ente, EnteCategoryLinkProperty> linkEnteRelation;

    @NonNull
    private final EnteService enteService;

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> getById(final Map<String, Object> id) {
        return this.enteCategoryPropertyRelation.getById(id);
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> create(final EnteCategoryProperty enteCategoryProperty) {
        return this.enteCategoryPropertyRelation.create(enteCategoryProperty);
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EntityDataState<EnteCategoryProperty>> updateOrCreate(final EnteCategoryProperty enteCategoryProperty) {

        return this.enteCategoryPropertyRelation
                .updateOrCreate(enteCategoryProperty,
                        enteCategoryPropertyForUpdating -> {
                            enteCategoryPropertyForUpdating.setName(enteCategoryProperty.getName());
                        },
                        this.create(enteCategoryProperty));
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> connectEnte(UUID categoryId, UUID categoryPropertyId, UUID enteId) {

        return this.enteCategoryRepository.findById(categoryId)
                .flatMap(category -> this.linkEnteRelation.connectTo(
                    EnteCategoryLinkProperty.builder()
                                .category(false)
                                .categoryId(category.getId())
                                .linkId(enteId)
                                .parentId(categoryPropertyId)
                            .build(),
                        rel -> this.enteService.getById(enteId)))
                .map(MultiplyDependencyConnectionInfo::getParent);
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Flux<EnteCategoryProperty> getPropertiesByCategoryId(UUID enteCategoryId) {
        return this.enteCategoryPropertyRelation.getRelationsByParent(enteCategoryId);
    }
}
