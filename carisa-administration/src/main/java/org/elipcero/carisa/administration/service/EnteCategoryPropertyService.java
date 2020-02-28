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

import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Operations for Ente category property
 *
 * @author David Suárez
 */
public interface EnteCategoryPropertyService {

    /**
     * Get Ente category property
     * @param id
     * @return ente found
     */
    Mono<EnteCategoryProperty> getById(final Map<String, Object> id);

    /**
     * Create the Ente category property and insert it into the Ente category. It's not done in the same transaction.
     * @param enteCategoryProperty Ente category property for creating
     * @return Ente category property created
     */
    Mono<EnteCategoryProperty> create(EnteCategoryProperty enteCategoryProperty);

    /**
     * Update or create the Ente category property. If the id exits is updated
     * otherwise is created. The EnteCategoryPropertyId can not be updated. To create @see create
     * @param enteCategoryProperty Ente category property for updating or creating
     * @return Ente category property created or updated
     */
    Mono<EntityDataState<EnteCategoryProperty>> updateOrCreate(final EnteCategoryProperty enteCategoryProperty);
}
