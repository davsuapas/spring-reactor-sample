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

package org.elipcero.carisa.administration.convert.type;

import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;

/**
 * Converter for dynamic object instance property from/to data engine
 *
 * @author David Suárez
 */
public interface DataEngineValueConverter {

    /**
     * Create the Wrapper value from string
     * @see org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty
     * @param value the value of the instance property
     * @return the wrapper value
     */
    DynamicObjectInstanceProperty.Value create(String value);

    /**
     * Convert the object value to string
     * @param value the value to convert
     * @returnthe  value string converted
     */
    String writeToString(DynamicObjectInstanceProperty.Value value);
}
