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

import lombok.NonNull;
import org.elipcero.carisa.administration.controller.support.DynamicObjectPrototypeController;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.domain.Plugin;
import org.elipcero.carisa.administration.domain.PluginType;
import org.elipcero.carisa.administration.service.support.DynamicObjectPrototypeService;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Query prototype controller.
 * @see Plugin
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/queriesplugin")
public class QueryDynamicPrototypeController extends DynamicObjectPrototypeController<Plugin> {

    public QueryDynamicPrototypeController(
       @NonNull final DynamicObjectPrototypeService<Plugin> dynamicObjectService,
       @NonNull final QueryDynamicPrototypeModelAssembler queryDynamicPrototypeModelAssembler) {

        super(queryDynamicPrototypeModelAssembler, dynamicObjectService);
    }

    /**
     * Create the DynamicObjectPrototype. Configure the parent (plugin) as Query
     * @param dynamicObjectPrototype the DynamicObjectPrototype (Id == null)
     * @return the DynamicObjectPrototype created
     */
    @Override
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototype>>> create(
            final @RequestBody DynamicObjectPrototype dynamicObjectPrototype) {

        dynamicObjectPrototype.setParentId(PluginType.QUERY_ID);
        return super.create(dynamicObjectPrototype);
    }

    /**
     * @see DynamicObjectPrototypeController
     */
    @Override
    protected Plugin buildManyRelation(DynamicObjectPrototype relation) {
        return new Plugin();
    }
}
