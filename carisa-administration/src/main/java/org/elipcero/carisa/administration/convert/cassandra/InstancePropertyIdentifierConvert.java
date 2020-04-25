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

package org.elipcero.carisa.administration.convert.cassandra;

import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.core.reactive.data.DependencyRelationIdentifierConvert;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.MapId;

import java.util.Map;
import java.util.UUID;

/**
 * Convert dynamic instance prototype property relation identifier to cassandra MapId
 *
 * @author David Suárez
 */
public class InstancePropertyIdentifierConvert
        implements DependencyRelationIdentifierConvert<DynamicObjectInstanceProperty<?>, MapId, UUID> {

    @Override
    public MapId convert(final DynamicObjectInstanceProperty<?> property) {
        return this.convertFromDictionary(EnteProperty.GetMapId(property.getParentId(), property.getId()));
    }

    @Override
    public MapId convertFromDictionary(final Map<String, Object> id) {
        return new BasicMapId(id);
    }

    @Override
    public UUID convertToParent(final DynamicObjectInstanceProperty<?> property) {
        return property.getParentId();
    }
}
