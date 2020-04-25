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
import org.elipcero.carisa.administration.controller.support.DynamicObjectInstancePropertyController;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.service.DynamicObjectInstancePropertyService;
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
 * Query instance properties controller.
 * @see DynamicObjectInstanceProperty
 *
 * @author David Suárez
 */
@RestController
@RequestMapping("/api")
public class QueryInstancePropertyController extends DynamicObjectInstancePropertyController {

    public QueryInstancePropertyController(
            @NonNull final DynamicObjectInstancePropertyService service,
            @NonNull final QueryInstancePropertyModelAssembler modelAssembler) {

        super(service, modelAssembler);
    }

    /**
     * @see DynamicObjectInstancePropertyController
     */
    @Override
    @GetMapping("/queryinstanceproperties")
    public Publisher<EntityModel<String>> getMetadata() {
        return super.getMetadata();
    }

    /**
     * @see DynamicObjectInstancePropertyController
     */
    @Override
    @GetMapping("/queryinstances/{instanceId}/properties/{propertyId}")
    public Publisher<EntityModel<DynamicObjectInstanceProperty<?>>> getById(
            final @PathVariable("instanceId") String instanceId,
            final @PathVariable("propertyId") String propertyId) {

        return super.getById(instanceId, propertyId);
    }

    /**
     * @see QueryInstancePropertyController
     */
    @Override
    @PostMapping("/queryinstanceproperties")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectInstanceProperty<?>>>> create(
            final @RequestBody DynamicObjectInstanceProperty<?> property) {

        return super.create(property);
    }

    /**
     * @see QueryInstancePropertyController
     */
    @Override
    @PutMapping("/queryinstances/{instanceId}/properties/{propertyId}")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectInstanceProperty<?>>>> updateOrCreate(
            final @PathVariable("instanceId") String instanceId,
            final @PathVariable("propertyId") String propertyId,
            final @RequestBody DynamicObjectInstanceProperty<?> property) {

        return super.updateOrCreate(instanceId, propertyId, property);
    }
}
