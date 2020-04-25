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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.administration.configuration.ContextConfiguration;
import org.elipcero.carisa.administration.convert.type.ValueConverterFactory;
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.administration.exception.InvalidFormatException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.UUID;

/**
 * Deserialize json message to dynamic object instance property
 *
 * @author David Suárez
 */
@Slf4j
public class DynamicObjectInstancePropertyDeserializer extends JsonDeserializer<DynamicObjectInstanceProperty<?>> {

    private ValueConverterFactory<WebValueConverter> valueConverterFactory;

    public DynamicObjectInstancePropertyDeserializer() {
        this.valueConverterFactory = ContextConfiguration.getWebValueConverterFactory();
        Assert.notNull(this.valueConverterFactory, "The value converter factory is null");
    }

    @Override
    public DynamicObjectInstanceProperty<?> deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            if (!node.hasNonNull(DynamicObjectPrototypeProperty.TYPE)) {
                throw new InvalidFormatException("The type doesn't exist in dynamic object instance property");
            }

            DynamicObjectInstanceProperty<?> property =
                    this.valueConverterFactory
                            .get(DynamicObjectPrototypeProperty.typeToInteger(node.get(DynamicObjectPrototypeProperty.TYPE)
                                    .asText()))
                            .create(node.get(DynamicObjectInstanceProperty.VALUE_COLUMN_NAME));

            if (node.hasNonNull(DynamicObjectInstanceProperty.ID_COLUMN_NAME)) {
                property.setId(UUID.fromString(node.get(DynamicObjectInstanceProperty.ID_COLUMN_NAME).asText()));
            }
            if (node.hasNonNull(DynamicObjectInstanceProperty.INSTANCE_ID_COLUMN_NAME)) {
                property.setParentId(UUID.fromString(node.get(DynamicObjectInstanceProperty.INSTANCE_ID_COLUMN_NAME).asText()));
            }

            return property;
        }
        catch (Exception ex) {
            log.error("Invalid format: " + ex.getMessage());
            throw new InvalidFormatException(ex.getMessage());
        }
    }
}
