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

import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.core.reactive.data.DependencyRelationIdentifierConvert;
import org.springframework.data.cassandra.core.mapping.MapId;

import java.util.Map;
import java.util.UUID;

/**
 * Convert Ente relation identifier to cassandra MapId
 *
 * @author David Suárez
 */
public class DependencyRelationEnteIdentifierConvert implements DependencyRelationIdentifierConvert<Ente, MapId, UUID> {

    @Override
    public MapId convert(Ente ente) {
        return this.convertFromDictionary(Ente.GetMapId(ente.getSpaceId(), ente.getId()));
    }

    @Override
    public MapId convertFromDictionary(Map<String, Object> id) {
        return (MapId)id;
    }

    @Override
    public UUID convertForParent(Ente ente) {
        return ente.getSpaceId();
    }
}
