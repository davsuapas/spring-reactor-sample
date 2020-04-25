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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.administration.configuration.ContextConfiguration;
import org.elipcero.carisa.administration.convert.type.ValueConverterFactory;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Serialize dynamic object instance property to json message
 *
 * @author David Suárez
 */
@Slf4j
public class DynamicObjectInstancePropertySerializer extends JsonSerializer<DynamicObjectInstanceProperty<?>> {

    private ValueConverterFactory<WebValueConverter> valueConverterFactory;

    public DynamicObjectInstancePropertySerializer() {
        this.valueConverterFactory = ContextConfiguration.getWebValueConverterFactory();
        Assert.notNull(this.valueConverterFactory, "The value converter factory is null");
    }

    @Override
    public void serialize(DynamicObjectInstanceProperty<?> dynamicObjectInstanceProperty,
              JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        if (dynamicObjectInstanceProperty.getId() != null) {
            jsonGenerator.writeStringField(
                    DynamicObjectInstanceProperty.ID_COLUMN_NAME,
                    dynamicObjectInstanceProperty.getId().toString());
        }

        if (dynamicObjectInstanceProperty.getParentId() != null) {
            jsonGenerator.writeStringField(
                    DynamicObjectInstanceProperty.INSTANCE_ID_COLUMN_NAME,
                    dynamicObjectInstanceProperty.getParentId().toString());
        }

        DynamicObjectInstanceProperty.Value value = dynamicObjectInstanceProperty.getValue();

        jsonGenerator.writeStringField(DynamicObjectPrototypeProperty.TYPE, value.getType().toString());

        this.valueConverterFactory.get(value.getType().ordinal()).writeToJson(jsonGenerator, value);

        jsonGenerator.writeEndObject();
    }
}
