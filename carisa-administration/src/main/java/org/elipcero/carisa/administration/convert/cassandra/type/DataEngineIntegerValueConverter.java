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

package org.elipcero.carisa.administration.convert.cassandra.type;

import org.elipcero.carisa.administration.convert.type.DataEngineValueConverter;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;

/**
 * Convert from/to cassandra dynamic object instance property values
 *
 * @author David Suárez
 */
public class DataEngineIntegerValueConverter implements DataEngineValueConverter {

    @Override
    public DynamicObjectInstanceProperty.WrapperValue<?> create(String value) {
        return new DynamicObjectInstanceProperty.WrapperValue<>(
                new DynamicObjectInstanceProperty.IntegerValue(Integer.valueOf(value))
        );
    }

    @Override
    public String writeToString(DynamicObjectInstanceProperty.Value value) {
        return ((DynamicObjectInstanceProperty.IntegerValue)value).getValue().toString();
    }
}
