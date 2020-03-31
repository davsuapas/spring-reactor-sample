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
import lombok.Setter;
import org.elipcero.carisa.core.data.Entity;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Instance of a prototype of dynamic object. Each instance have n dynamic properties
 * External users could instance dynamic object using dynamic object instance
 * Example: Queries. The queries has a object prototype where define the metadata o a query type
 * and the instance is the particular information of those metadata configure by users
 *
 * @author David Suárez
 */
@Table("carisa_dynamic_object_instance")
@Getter
public class DynamicObjectInstance extends Entity implements Relation {

    private UUID parentId;
    private UUID prototypeId; // referenced dynamic object prototype identifier

    @Setter
    private String name;

    @Setter
    private String description;

    @Builder
    public DynamicObjectInstance(UUID id, UUID parentId, UUID prototypeId, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.prototypeId = prototypeId;
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.getId();
    }
}
