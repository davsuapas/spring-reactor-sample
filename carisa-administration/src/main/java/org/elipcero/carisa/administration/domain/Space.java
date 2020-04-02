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
 * Each instance contains 1-N spaces. Each space split the instance in logic category.
 * Each space can have several dashboard.
 *
 * @author David Suárez
 */
@Table("carisa_space")
@Getter
public class Space extends Entity implements Relation {

    private UUID instanceId;

    @Setter
    private String name;

    @Builder
    public Space(UUID id, UUID instanceId, String name) {
        super(id);
        this.instanceId = instanceId;
        this.name = name;
    }

    @Override
    @JsonIgnore
    public Object getParentId() {
        return this.getInstanceId();
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.getId();
    }
}
