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
import org.elipcero.carisa.administration.controller.support.DynamicObjectInstanceController;
import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
import org.elipcero.carisa.administration.domain.SpaceQueryInstance;
import org.elipcero.carisa.administration.service.support.DynamicObjectInstanceService;
import org.elipcero.carisa.core.data.ChildName;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Query instance controller.
 * @see SpaceQueryInstance
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/queryinstances")
public class QueryInstanceController extends DynamicObjectInstanceController<SpaceQueryInstance> {

    public QueryInstanceController(
       @NonNull final DynamicObjectInstanceService<SpaceQueryInstance> dynamicObjectService,
       @NonNull final QueryInstanceModelAssembler queryInstanceModelAssembler) {

        super(queryInstanceModelAssembler, dynamicObjectService);
    }

    /**
     * @see DynamicObjectInstanceController
     */
    @Override
    protected SpaceQueryInstance buildManyRelation(final DynamicObjectInstance relation) {
        return new SpaceQueryInstance();
    }

    /**
     * Get properties by instance
     * @param id the instance identifier (UUID string)
     * @return the property collections with links
     */
    @GetMapping("/{id}/properties")
    public Publisher<CollectionModel<EntityModel<ChildName>>> getProperties(final @PathVariable("id") String id) {

        return this.crudHypermediaController.childrenByParentWithBiKey(
                id,
                this.dynamicObjectInstanceService.getPropertiesByInstanceId(UUID.fromString(id)),
                QueryInstanceController.class, QueryInstanceModelAssembler.QUERY_INSTANCE_REL_NAME,
                QueryInstancePropertyController.class, QueryInstancePropertyModelAssembler.QUERY_INSTANCE_PROP_REL_NAME);
    }
}
