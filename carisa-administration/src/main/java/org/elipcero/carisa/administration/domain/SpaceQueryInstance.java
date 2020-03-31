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
 * The query represents the aggregation information of each Ente properties or categories property
 * Can exist many query types. Each query will have associate a micro services to operate
 * The dynamic object prototype define the metadata of each query type and can have many dynamic properties
 *
 * @author David Suárez
 */

@Table("carisa_space_query_instance")
public class SpaceQueryInstance extends ManyRelation {
}
