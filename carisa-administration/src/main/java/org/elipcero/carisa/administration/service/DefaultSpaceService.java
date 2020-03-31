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
import org.elipcero.carisa.core.reactive.data.DependencyRelationCreateCommand;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
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
    private final MultiplyDependencyRelation<Space, Ente, SpaceEnte> spaceEnteRelation;

    @NonNull
    private final MultiplyDependencyRelation<EnteCategory, EnteCategory, EnteHierarchy> enteCategoryHierarchyRelation;

    @NonNull
    private final MultiplyDependencyRelation<Instance, Space, InstanceSpace> instanceSpaceRelation;

    @NonNull
    private final MultiplyDependencyRelation<Space, DynamicObjectInstance, SpaceQueryInstance> spaceQueryInstanRelation;

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
        return this.instanceSpaceRelation.create(
                new DependencyRelationCreateCommand<Space, InstanceSpace>() {
                    @Override
                    public Space getChild() {
                        return space;
                    }

                    @Override
                    public InstanceSpace getRelation() {
                        return InstanceSpace.builder()
                                .parentId(space.getInstanceId())
                                .childId(space.getId()).build();
                    }
                });
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
}
