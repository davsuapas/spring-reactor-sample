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
import org.elipcero.carisa.administration.controller.support.DynamicObjectPrototypePropertyController;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.administration.service.DynamicObjectPrototypePropertyService;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Query prototype properties controller.
 * @see DynamicObjectPrototypeProperty
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api")
public class QueryPluginPrototypePropertyController extends DynamicObjectPrototypePropertyController {

    public QueryPluginPrototypePropertyController(
            @NonNull final DynamicObjectPrototypePropertyService service,
            @NonNull final QueryPluginPrototypePropertyModelAssembler modelAssembler) {

        super(service, modelAssembler);
    }

    /**
     * @see DynamicObjectPrototypePropertyController
     */
    @Override
    @GetMapping("/querypluginproperties")
    public Publisher<EntityModel<String>> getMetadata() {
        return super.getMetadata();
    }

    /**
     * @see DynamicObjectPrototypePropertyController
     */
    @Override
    @GetMapping("/queriesplugin/{prototypeId}/properties/{propertyId}")
    public Publisher<EntityModel<DynamicObjectPrototypeProperty>> getById(
            final @PathVariable("prototypeId") String prototypeId,
            final @PathVariable("propertyId") String propertyId) {

        return super.getById(prototypeId, propertyId);
    }

    /**
     * @see DynamicObjectPrototypePropertyController
     */
    @Override
    @PostMapping("/querypluginproperties")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototypeProperty>>> create(
            final @RequestBody DynamicObjectPrototypeProperty property) {

        return super.create(property);
    }

    /**
     * @see DynamicObjectPrototypePropertyController
     */
    @Override
    @PutMapping("/queriesplugin/{prototypeId}/properties/{propertyId}")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototypeProperty>>> updateOrCreate(
            final @PathVariable("prototypeId") String prototypeId,
            final @PathVariable("propertyId") String propertyId,
            final @RequestBody DynamicObjectPrototypeProperty property) {

        return super.updateOrCreate(prototypeId, propertyId, property);
    }
}
