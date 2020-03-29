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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elipcero.carisa.administration.domain.support.Named;
import org.elipcero.carisa.core.data.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Ente categories. Each category can has categories or entes wrapped.
 * Each category references to ente properties or properties of other categories.
 * If it is root, the parent is a space,
 *
 * @author David Suárez
 */
@Table("carisa_ente_category")
@Getter
@NoArgsConstructor
public class EnteCategory extends Entity implements Named {

    @Transient
    private UUID parentId;

    @Setter
    private String name;

    private boolean root;

    @Builder
    public EnteCategory(UUID id, UUID parentId, String name, boolean root) {
        super(id);
        this.parentId = parentId;
        this.name = name;
        this.root = root;
    }
}

