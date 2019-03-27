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

package org.elipcero.carisa.skipper.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This contain kubernenetes information
 *
 * @author David Suárez
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "carisa_skipper_platform")
public class KubernetesPlatform extends Platform {

    public static final String PLATFORM_TYPE_KUBERNETES = "kubernetes";

    private String namespace;

    @Builder
    public KubernetesPlatform(String name, String namespace) {
        super(name);
        this.namespace = namespace;
    }
}
