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
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Relations N to N. In this entity is stored all N to N relations of the system
 *
 * @author David Suárez
 */
@Table("carisa_dependency_relation")
@Builder
@Getter
public class DependencyRelation {

    public enum Relation {
        InstanceSpace,
        SpaceEnte,
        EnteCategoryEnteCategory
    }

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId;

    @CassandraType(type = DataType.Name.INT)
    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private DependencyRelation.Relation relation;

    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID childId;

    public MapId getId() {
        return BasicMapId
                .id("parentId", parentId)
                .with("relation", relation.ordinal())
                .with("childId", childId);
    }
}

