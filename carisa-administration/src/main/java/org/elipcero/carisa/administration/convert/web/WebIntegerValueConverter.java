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

/**
 * @see WebValueConverter
 *
 * @author David Suárez
 */
public class WebIntegerValueConverter implements WebValueConverter {

    /**
     * @see WebValueConverter#create(JsonNode)
     */
    @Override
    public DynamicObjectInstanceProperty<?> create(JsonNode value) {
        if (value.isInt()) {
            return new DynamicObjectInstanceProperty<>(new DynamicObjectInstanceProperty.IntegerValue(value.asInt()));
        }
        throw new InvalidFormatException("The value must be integer. Value: " + value.asText());
    }
}
