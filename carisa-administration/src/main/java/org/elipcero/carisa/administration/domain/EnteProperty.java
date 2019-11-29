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
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Relation between Ente and Ente property
 *
 * @author David Suárez
 */
@Table("carisa_ente_property")
@Getter
public class EnteProperty {

    public static final String ENTE_ID = "enteId";
    public static final String PROPERTY_ID = "propertyId";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID enteId;

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID propertyId;

    @Setter
    private String name;

    public enum Type {
        Integer,
        Decimal,
        Boolean,
        DateTime
    }

    @CassandraType(type = DataType.Name.INT)
    @Setter
    private EnteProperty.Type type;

    @JsonIgnore
    public void setId(MapId id) {
        this.enteId = (UUID)id.get(ENTE_ID);
        this.propertyId = (UUID)id.get(PROPERTY_ID);
    }

    public EnteProperty tryInitId() {
        if (this.propertyId == null) {
            this.propertyId = UUID.randomUUID();
        }
        return this;
    }

    @Builder
    public EnteProperty(UUID propertyId, UUID enteId, String name, EnteProperty.Type type) {
        this.propertyId = propertyId;
        this.enteId = enteId;
        this.name = name;
        this.type = type;
    }

    public static MapId getId(String enteId, String propertyId) {
        return getId(UUID.fromString(enteId), UUID.fromString(propertyId));
    }

    public static MapId getId(UUID enteId, UUID propertyId) {
        return BasicMapId.id(ENTE_ID, enteId).with(PROPERTY_ID, propertyId);
    }
}

