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

import com.datastax.driver.core.DataType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Relation between Ente and Ente property
 *
 * @author David Suárez
 */
@Table("carisa_ente_property")
@Getter
@Setter
public class EnteProperty implements Relation, EntityInitializer<EnteProperty> {

    public static String ENTEID_COLUMN_NAME = "parentId";
    public static String ID_COLUMN_NAME = "id";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // ente identifier

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    private UUID spaceId; // Is necessary to find Ente

    private String name;

    @Builder
    public EnteProperty(UUID id, UUID parentId, UUID spaceId, String name, EnteProperty.Type type) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.spaceId = spaceId;
    }

    @Override
    @JsonIgnore
    public UUID getParentId() {
        return this.parentId;
    }

    // To work Affordance with a new name. Not work JsonProperty
    @JsonSetter("enteId")
    public void setParentId(UUID value) {
        this.parentId = value;
    }

    // To work Affordance with a new name. Not work JsonProperty
    public UUID getEnteId() {
        return this.getParentId();
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.id;
    }

    public enum Type {
        Integer,
        Decimal,
        Boolean,
        DateTime
    }

    public static Map<String, Object> GetMapId(UUID enteId, UUID id) {
        return new HashMap<String, Object>() {{
            put(ENTEID_COLUMN_NAME, enteId);
            put(ID_COLUMN_NAME, id);
        }};
    }

    @CassandraType(type = DataType.Name.INT)
    @Setter
    private EnteProperty.Type type;

    public EnteProperty tryInitId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        return this;
    }
}

