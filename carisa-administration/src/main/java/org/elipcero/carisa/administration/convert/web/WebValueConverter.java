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

/**
 * Convert json message to dynamic object instance property
 *
 * @author David Suárez
 */
public interface WebValueConverter {

    /**
     * Create dynamic instance object property from value
     * @param value the value of the property
     * @return the dynamic instance object property
     */
    DynamicObjectInstanceProperty<?> create(JsonNode value);
}
