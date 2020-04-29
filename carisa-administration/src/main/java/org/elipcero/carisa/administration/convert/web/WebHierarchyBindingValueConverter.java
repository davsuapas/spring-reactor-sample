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
 *
 */

package org.elipcero.carisa.administration.convert.web;

import com.fasterxml.jackson.databind.JsonNode;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.exception.InvalidFormatException;

import java.util.UUID;

import static org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty.HierarchyBindingValue;

/**
 * @see WebValueConverter
 *
 * @author David Suárez
 */
public class WebHierarchyBindingValueConverter implements WebValueConverter {

    /**
     * @see WebValueConverter#create(JsonNode)
     */
    @Override
    public DynamicObjectInstanceProperty<?> create(JsonNode value) {
        UUID parentId;
        UUID childId;
        JsonNode category;

        if (!value.has(HierarchyBindingValue.PARENT_ID_COLUMN_NAME)) {
            throw new InvalidFormatException("WebHierarchyBindingValueConverter.create: The parentId must has value");
        }
        if (!value.has(HierarchyBindingValue.CHILD_ID_COLUMN_NAME)) {
            throw new InvalidFormatException("WebHierarchyBindingValueConverter.create: The childId must has value");
        }
        if (!value.has(HierarchyBindingValue.CATEGORY_COLUMN_NAME)) {
            throw new InvalidFormatException("WebHierarchyBindingValueConverter.create: The category must has value");
        }

        parentId = UUID.fromString(value.get(HierarchyBindingValue.PARENT_ID_COLUMN_NAME).asText());
        childId = UUID.fromString(value.get(HierarchyBindingValue.CHILD_ID_COLUMN_NAME).asText());

        category = value.get(HierarchyBindingValue.CATEGORY_COLUMN_NAME);
        if (!category.isBoolean()) {
            throw new InvalidFormatException("WebHierarchyBindingValueConverter.create: The category must be boolean");
        }

        return new DynamicObjectInstanceProperty<>(
                new DynamicObjectInstanceProperty.HierarchyBindingValue(parentId, childId, category.asBoolean()));
    }
}
