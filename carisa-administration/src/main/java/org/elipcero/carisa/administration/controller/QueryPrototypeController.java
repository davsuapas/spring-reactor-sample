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

package org.elipcero.carisa.administration.controller;

import org.elipcero.carisa.administration.controller.support.DynamicObjectPrototypeController;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.domain.SpaceQueryPrototype;
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.elipcero.carisa.administration.service.support.DynamicObjectPrototypeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Query controller.
 * @see SpaceQueryPrototype
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/queryprototypes")
public class QueryPrototypeController extends DynamicObjectPrototypeController {

    public QueryPrototypeController(
            @Qualifier("QueryPrototypeService") final DynamicObjectPrototypeService dynamicObjectService,
            final QueryPrototypeModelAssembler queryPrototypeModelAssembler) {

        super(queryPrototypeModelAssembler, dynamicObjectService);
    }

    /**
     * @see DynamicObjectPrototypeController
     */
    @Override
    public ManyRelation buildManyRelation(DynamicObjectPrototype queryPrototype) {
        return new SpaceQueryPrototype();
    }
}
