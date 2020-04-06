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
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.domain.SpaceEnte;
import org.elipcero.carisa.administration.repository.EnteRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.ParentChildName;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyConnectionInfo;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see EnteService
 *
 * @author David Suárez
 */
public class DefaultEnteService
        extends MultiplyDependencyRelationService<Ente, SpaceEnte>
        implements EnteService {

    private final MultiplyDependencyRelation<EnteCategory, Ente, EnteHierarchy> enteHierarchyRelation;

    private final EmbeddedDependencyRelation<EnteProperty> entePropertyRelation;

    public DefaultEnteService(
            @NonNull final EnteRepository enteRepository,
            @NonNull final MultiplyDependencyRelation<EnteCategory, Ente, EnteHierarchy> enteHierarchyRelation,
            @NonNull final EmbeddedDependencyRelation<EnteProperty> entePropertyRelation,
            @NonNull final MultiplyDependencyRelation<Space, Ente, SpaceEnte> spaceEnteRelation) {

        super(enteRepository, spaceEnteRelation);

        this.enteHierarchyRelation = enteHierarchyRelation;
        this.entePropertyRelation = entePropertyRelation;
    }

    /**
     * @see EnteService
     */
    @Override
    public Mono<Ente> create(final Ente ente) {
        return super.create(ente, new SpaceEnte());
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Ente>> updateOrCreate(final UUID id, final Ente ente) {
        return super.updateOrCreate(id, ente, new SpaceEnte());
    }

    /**
     * @see EnteService
     */
    @Override
    public Flux<ParentChildName> getEntePropertiesByEnteId(final UUID enteId) {
        return this.entePropertyRelation.getRelationsByParent(enteId)
                .map(prop ->
                        ParentChildName.builder()
                                .parentId(prop.getParentId())
                                .childId(prop.getChildId())
                                .name(prop.getName())
                                .build());
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

    /**
     * @see MultiplyDependencyRelationService
     */
    @Override
    protected void updateEntity(Ente entityForUpdating, Ente entity) {
        entityForUpdating.setName(entity.getName());
    }
}
