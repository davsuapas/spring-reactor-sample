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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.convert.type.DataEngineValueConverter;
import org.elipcero.carisa.administration.convert.type.ValueConverterFactory;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert from cassandra type to object instance property value
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class ObjectInstancePropertyValueReadConverter
        implements Converter<String, DynamicObjectInstanceProperty.Value> {

    @NonNull
    private ValueConverterFactory<DataEngineValueConverter> dataEngineValueConverter;

    @Override
    public DynamicObjectInstanceProperty.Value convert(final String value) {
        String[] properties = value.split(";");
        return dataEngineValueConverter.get(Integer.parseInt(properties[0])).create(properties[1]);
    }
}
