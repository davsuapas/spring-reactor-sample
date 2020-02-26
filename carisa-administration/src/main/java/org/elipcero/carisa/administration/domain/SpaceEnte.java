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

@Table("carisa_space_ente")
@Builder
@Getter
public class SpaceEnte implements Relation {

    public static String SPACEID_COLUMN_NAME = "parentId";
    public static String ENTEID_COLUMN_NAME = "enteId";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // space identifier

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID enteId;

    @Override
    public UUID getParentId() {
        return this.parentId;
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.enteId;
    }

    public static Map<String, Object> GetMapId(UUID spaceId, UUID enteId) {
        return new HashMap<String, Object>() {{
            put(SPACEID_COLUMN_NAME, spaceId);
            put(ENTEID_COLUMN_NAME, enteId);
        }};
    }
}
