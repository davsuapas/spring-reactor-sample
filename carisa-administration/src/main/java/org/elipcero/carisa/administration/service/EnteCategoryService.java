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

import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.projection.EnteHierachyName;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Operations for ente category
 *
 * @author David Suárez
 */
public interface EnteCategoryService {

    /**
     * Get ente category by id
     * @param id
     * @return ente category found
     */
    Mono<EnteCategory> getById(UUID id);

    /**
     * Create the ente category and insert the ente category in his parent. it's not done in the same transaction.
     * @param enteCategory ente category for creating
     * @return space created
     */
    Mono<EnteCategory> create(EnteCategory enteCategory);

    /**
     * Update or create the ente category. If the id exits is updated
     * otherwise is created. The parentId can not be updated. To create @see create
     * @param id
     * @param enteCategory ente category for updating or creating
     * @return Ente category created or updated
     */
    Mono<EntityDataState<EnteCategory>> updateOrCreate(final UUID id, final EnteCategory enteCategory);

    /**
     * Get children (Ente category or ente)
     * @param enteCategoryId ente category identifier
     * @return ente category projection
     */
    Flux<EnteHierachyName> getChildren(final UUID enteCategoryId);

    /**
     * Connect the child category to the parent category
     * @param childId the child category
     * @param parentId the parent category
     * @return child
     */
    Mono<EnteCategory> connectToParent(UUID childId, UUID parentId);
}
