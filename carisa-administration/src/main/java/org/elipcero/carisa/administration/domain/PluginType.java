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
import org.elipcero.carisa.core.data.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Plugin type
 *
 * @author David Suárez
 */
@Builder
public class PluginType extends Entity {

    public static UUID QUERY_ID =  UUID.fromString("9b3d108e-89cb-4c7c-a774-a527a8b22e57");

    // All plugin type in memory
    public static Map<UUID, PluginType> INSTANCES = new HashMap<UUID, PluginType>() {{
        put(QUERY_ID, PluginType.builder().name("Query").build());
    }};

    private String name;
}
