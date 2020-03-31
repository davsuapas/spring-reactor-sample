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

import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * The plugin represents all queries types of the system.
 * The plugin parentId contain the type identifier
 * There are several plugin types: Query
 * @see SpaceQueryInstance
 *
 * @author David Suárez
 */
@Table("carisa_plugin")
public class Plugin extends ManyRelation {
}
