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

package org.elipcero.carisa.administration.convert.cassandra.support;

import org.elipcero.carisa.core.data.ManyRelation;
import org.elipcero.carisa.core.reactive.data.DependencyRelationIdentifierConvert;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.MapId;

import java.util.Map;
import java.util.UUID;

/**
 * Convert generic many relation identifier to cassandra MapId
 *
 * @author David Suárez
 */
public abstract class ManyRelationIdentifierConvert<TRelation extends ManyRelation>
        implements DependencyRelationIdentifierConvert<TRelation, MapId, UUID> {

    @Override
    public MapId convert(final TRelation manyRelation) {
        return this.convertFromDictionary(
                ManyRelation.GetMapId(manyRelation.getParentId(), manyRelation.getChildId()));
    }

    @Override
    public MapId convertFromDictionary(final Map<String, Object> id) {
        return new BasicMapId(id);
    }

    @Override
    public UUID convertToParent(final TRelation manyRelation) {
        return manyRelation.getParentId();
    }
}
