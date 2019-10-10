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
import lombok.Setter;
import org.elipcero.carisa.core.data.Entity;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * The Ente property.
 *
 * @author David Suárez
 */
@Table("carisa_ente_property")
@Getter
public class EnteProperty extends Entity {

    private UUID enteId;

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
    private Type type;

    @Builder
    public EnteProperty(UUID id, UUID enteId, String name, Type type) {
        super(id);
        this.enteId = enteId;
        this.name = name;
        this.type = type;
    }
}
