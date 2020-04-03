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
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.domain.SpaceQueryInstance;
import org.elipcero.carisa.administration.repository.DynamicObjectInstanceRepository;
import org.elipcero.carisa.administration.repository.DynamicObjectPrototypeRepository;
import org.elipcero.carisa.administration.service.support.DynamicObjectInstanceService;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;

/**
 * Operations for query instance
 * @see DynamicObjectInstanceService
 *
 * @author David Suárez
 */

public class QueryDynamicInstanceService extends DynamicObjectInstanceService<SpaceQueryInstance> {

    public QueryDynamicInstanceService(
         @NonNull DynamicObjectInstanceRepository dynamicObjectRepository,
         @NonNull MultiplyDependencyRelation<Space, DynamicObjectInstance, SpaceQueryInstance> relation,
         @NonNull DynamicObjectPrototypeRepository dynamicObjectPrototypeRepository) {

        super(dynamicObjectRepository, relation, dynamicObjectPrototypeRepository);
    }
}
