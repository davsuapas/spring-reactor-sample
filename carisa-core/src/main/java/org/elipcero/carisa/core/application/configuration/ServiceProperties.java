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

package org.elipcero.carisa.core.application.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * Services properties
 *
 * @author David Suárez
 */
@ConfigurationProperties(prefix = "carisa.services")
@Getter
public class ServiceProperties {

    private Skipper skipper;

    @Getter
    public static class CommonService {

        private String uri;

        public URI toUri() {
            return URI.create(uri);
        }
    }

    @Getter
    public static class Skipper extends CommonService {
    }
}
