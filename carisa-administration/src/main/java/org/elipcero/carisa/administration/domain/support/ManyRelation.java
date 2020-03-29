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

package org.elipcero.carisa.administration.domain.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class ManyRelation implements Relation {

    public static String PARENTID_COLUMN_NAME = "parentId";
    public static String CHILDID_COLUMN_NAME = "childId";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId;

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID childId;

    @Override
    public UUID getParentId() {
        return this.parentId;
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.childId;
    }

    public static Map<String, Object> GetMapId(UUID parentId, UUID childId) {
        return new HashMap<String, Object>() {{
            put(PARENTID_COLUMN_NAME, parentId);
            put(CHILDID_COLUMN_NAME, childId);
        }};
    }
}
