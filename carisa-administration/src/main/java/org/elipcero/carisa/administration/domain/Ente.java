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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elipcero.carisa.core.data.Entity;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * The thinks of space.
 * The ente are the items of spaces to trace, count, measure, etc.
 *
 * @author David Suárez
 */
@Table("carisa_ente")
@Getter
public class Ente extends Entity implements Named {

    private UUID spaceId;

    @Setter
    private String name;

    @Builder
    public Ente(UUID id, UUID spaceId, String name) {
        super(id);
        this.name = name;
        this.spaceId = spaceId;
    }
}
