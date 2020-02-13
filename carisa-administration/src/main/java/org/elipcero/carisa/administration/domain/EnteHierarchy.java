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

package org.elipcero.carisa.administration.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Table("carisa_ente_hierarchy")
@Builder
@Getter
public class EnteHierarchy implements Relation {

    public static String COLUMN_NAME_PARENTID = "parentId";
    public static String COLUMN_NAME_CHILDID = "id";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // Ente category

    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID id; // Ente category or Ente adapter

    private boolean category;

    @Override
    public UUID getParentId() {
        return this.parentId;
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.id;
    }

    public static Map<String, Object> GetMapId(UUID parentId, UUID childId) {
        return new HashMap<String, Object>() {{
            put(COLUMN_NAME_PARENTID, parentId);
            put(COLUMN_NAME_CHILDID, childId);
        }};
    }
}
