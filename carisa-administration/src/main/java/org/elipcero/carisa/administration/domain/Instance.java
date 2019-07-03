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
 * Instance information. Instance represents a set of spaces. Each space can have
 * several dashboard.
 * Each instance is independently of another instance in all system
 *
 * @author David Suárez
 */
@Table("carisa_instance")
@Getter
public class Instance extends Entity {

    @Setter
    private String name;

    public enum State {
        None,
        InProgress,
        Deployed,
        Error
    }

    @CassandraType(type = DataType.Name.INT)
    private State state = State.None;

    @Builder
    public Instance(UUID id, String name, State state) {
        super(id);
        this.name = name;
        this.state = state;
    }

    @Override
    public void tryInit() {
        super.tryInit();
        this.initState();
    }

    public void initState() {
        this.state = State.None;
    }
}
