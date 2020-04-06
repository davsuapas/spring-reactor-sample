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
import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.InstanceSpace;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.domain.SpaceEnte;
import org.elipcero.carisa.administration.domain.SpaceQueryInstance;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.ParentChildName;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see SpaceService
 *
 * @author David Suárez
 */
public class DefaultSpaceService
        extends MultiplyDependencyRelationService<Space, InstanceSpace>
        implements SpaceService {

    private final MultiplyDependencyRelation<Space, Ente, SpaceEnte> spaceEnteRelation;

    private final MultiplyDependencyRelation<EnteCategory, EnteCategory, EnteHierarchy> enteCategoryHierarchyRelation;

    private final MultiplyDependencyRelation<Space, DynamicObjectInstance, SpaceQueryInstance> spaceQueryInstanRelation;

    public DefaultSpaceService(
           @NonNull final SpaceRepository spaceRepository,
           @NonNull final MultiplyDependencyRelation<Space, Ente, SpaceEnte> spaceEnteRelation,
           @NonNull final MultiplyDependencyRelation<EnteCategory, EnteCategory, EnteHierarchy> enteCategoryHierarchyRelation,
           @NonNull final MultiplyDependencyRelation<Instance, Space, InstanceSpace> instanceSpaceRelation,
           @NonNull final MultiplyDependencyRelation<Space, DynamicObjectInstance, SpaceQueryInstance> spaceQueryInstanRelation) {

        super(spaceRepository, instanceSpaceRelation);

        this.spaceEnteRelation = spaceEnteRelation;
        this.enteCategoryHierarchyRelation = enteCategoryHierarchyRelation;
        this.spaceQueryInstanRelation = spaceQueryInstanRelation;
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<Space> create(final Space space) {
        return super.create(space, new InstanceSpace());
    }

    /**
     * @see SpaceService
     */
    @Override
    public Mono<EntityDataState<Space>> updateOrCreate(final UUID id, final Space space) {
        return super.updateOrCreate(id, space, new InstanceSpace());
    }

    /**
     * @see SpaceService
     */
    @Override
    public Flux<ParentChildName> getEntesBySpace(final UUID spaceId) {
        return this.spaceEnteRelation.getChildrenByParent(spaceId)
                .map(ente -> ParentChildName
                        .builder()
                            .parentId(spaceId)
                            .childId(ente.getChild().getId())
                            .name(ente.getChild().getName())
                        .build());
    }

    /**
     * @see SpaceService
     */
    @Override
    public Flux<ParentChildName> getEnteCategoriesBySpace(final UUID spaceId) {
        return this.enteCategoryHierarchyRelation.getChildrenByParent(spaceId)
                .map(enteCategory -> ParentChildName
                        .builder()
                            .parentId(spaceId)
                            .childId(enteCategory.getChild().getId())
                            .name(enteCategory.getChild().getName())
                        .build());
    }

    /**
     * @see SpaceService
     */
    @Override
    public Flux<ParentChildName> getQueryPrototypesBySpace(final UUID spaceId) {
        return this.spaceQueryInstanRelation.getChildrenByParent(spaceId)
                .map(query -> ParentChildName
                        .builder()
                            .parentId(spaceId)
                            .childId(query.getChild().getId())
                            .name(query.getChild().getName())
                        .build());
    }

    /**
     * @see MultiplyDependencyRelationService
     */
    @Override
    protected void updateEntity(Space entityForUpdating, Space entity) {
        entityForUpdating.setName(entity.getName());
    }
}
