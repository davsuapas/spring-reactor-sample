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
import org.elipcero.carisa.administration.domain.support.Named;
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
 * Object prototype property
 *
 * @author David Suárez
 */
@Table("carisa_dynamic_object_prototype_property")
@Getter
@Setter
public class DynamicObjectPrototypeProperty
        implements Relation, EntityInitializer<DynamicObjectPrototypeProperty>, Named {

    public static String PROTOTYPE_ID_COLUMN_NAME = "parentId";
    public static String ID_COLUMN_NAME = "id";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // Object prototype identifier

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    private String name;
    private String description;

    public enum Type {
        Integer,
        Decimal,
        Boolean,
        DateTime,
        HierarchyBinding
    }

    @CassandraType(type = DataType.Name.INT)
    @Setter
    private DynamicObjectPrototypeProperty.Type type;

    @Builder
    public DynamicObjectPrototypeProperty(
            UUID id, UUID parentId, String name, String description, DynamicObjectPrototypeProperty.Type type) {

        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    @Override
    @JsonIgnore
    public UUID getParentId() {
        return this.parentId;
    }

    // To work Affordance with a new name. Not work JsonProperty
    @JsonSetter("prototypeId")
    public void setParentId(UUID value) {
        this.parentId = value;
    }

    // To work Affordance with a new name. Not work JsonProperty
    public UUID getPrototypeId() {
        return this.getParentId();
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.id;
    }

    public static Map<String, Object> GetMapId(UUID prototypeId, UUID id) {
        return new HashMap<String, Object>() {{
            put(PROTOTYPE_ID_COLUMN_NAME, prototypeId);
            put(ID_COLUMN_NAME, id);
        }};
    }

    public DynamicObjectPrototypeProperty tryInitId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        return this;
    }
}

