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
import org.elipcero.carisa.core.data.ChildName;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Query prototype controller.
 * @see Plugin
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api/queriesplugin")
public class QueryPrototypeController extends DynamicObjectPrototypeController<Plugin> {

    public QueryPrototypeController(
       @NonNull final DynamicObjectPrototypeService<Plugin> dynamicObjectService,
       @NonNull final QueryPrototypeModelAssembler queryPrototypeModelAssembler) {

        super(queryPrototypeModelAssembler, dynamicObjectService);
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
    protected Plugin buildManyRelation(final DynamicObjectPrototype relation) {
        return new Plugin();
    }

    /**
     * Get properties by plugin (prototypeId)
     * @param id the prototype identifier (UUID string)
     * @return the property collections with links
     */
    @GetMapping("/{id}/properties")
    public Publisher<CollectionModel<EntityModel<ChildName>>> getProperties(final @PathVariable("id") String id) {

        return this.crudHypermediaController.childrenByParentWithBiKey(
                id,
                this.dynamicObjectPrototypeService.getPropertiesByPrototypeId(UUID.fromString(id)),
                QueryPrototypeController.class, QueryPrototypeModelAssembler.QUERY_PROTOTYPE_REL_NAME,
                QueryPrototypePropertyController.class, QueryPrototypePropertyModelAssembler.QUERY_PROTOTYPE_PROP_REL_NAME);
    }
}
