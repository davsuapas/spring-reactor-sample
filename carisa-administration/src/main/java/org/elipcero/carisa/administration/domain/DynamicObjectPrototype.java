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
 * Allow make prototype of dynamic object. Each object have n dynamic properties
 * Each object define the representative name
 * The plugin developers could define the necessary information what the plugin needs
 * with DynamicObjectPrototype and DynamicObjectPrototypeProperty. The user interface will
 * be built dynamically with this information.
 * External users could make dynamic object instance using dynamic object prototype
 * @see DynamicObjectPrototypeProperty
 * @see DynamicObjectInstance
 *
 * @author David Suárez
 */
@Table("carisa_dynamic_object_prototype")
@Getter
public class DynamicObjectPrototype extends Entity implements Relation {

    @Setter
    private UUID parentId;

    @Setter
    private String name;

    @Setter
    private String description;

    @Builder
    public DynamicObjectPrototype(UUID id, UUID parentId, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
        this.parentId = parentId;
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.getId();
    }
}
