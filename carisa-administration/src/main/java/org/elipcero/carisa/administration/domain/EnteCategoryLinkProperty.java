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
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The category properties links with Ente properties or category properties. Each category properties can have
 * more than one link. Each one can link with properties of the down level in her hierarchy. All links have to be
 * of the same property type
 *
 * @author David Suárez
 */
@Table("carisa_ente_category_property_link")
@Getter
public class EnteCategoryLinkProperty implements Relation {

    public static String ENTE_CATEGORY_PROPERTY_ID_COLUMN_NAME = "parentId";
    public static String LINKID_COLUMN_NAME = "linkId";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // Ente category property identifier

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID linkId; // Ente category or Ente identifier

    private UUID categoryId;

    private boolean category;

    @Builder
    public EnteCategoryLinkProperty(UUID linkId, UUID parentId, UUID categoryId, boolean category) {
        this.linkId = linkId;
        this.parentId = parentId;
        this.categoryId = categoryId;
        this.category = category;
    }

    @Override
    public UUID getParentId() {
        return this.parentId;
    }

    @Override
    public UUID getChildId() {
        return this.linkId;
    }

    public static Map<String, Object> GetMapId(UUID enteCategoryPropertyId, UUID linkId) {
        return new HashMap<String, Object>() {{
            put(ENTE_CATEGORY_PROPERTY_ID_COLUMN_NAME, enteCategoryPropertyId);
            put(LINKID_COLUMN_NAME, linkId);
        }};
    }
}

