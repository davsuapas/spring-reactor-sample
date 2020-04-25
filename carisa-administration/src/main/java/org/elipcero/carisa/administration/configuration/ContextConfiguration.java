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
 *
 */

package org.elipcero.carisa.administration.configuration;

import org.elipcero.carisa.administration.convert.type.ValueConverterFactory;
import org.elipcero.carisa.administration.convert.web.WebValueConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Configure general contexts
 *
 * @author David Suárez
 */
@Configuration
public class ContextConfiguration {

    private static ValueConverterFactory<WebValueConverter> webValueConverterFactory;

    @Autowired
    private void setWebValueConverterFactory(ValueConverterFactory<WebValueConverter> value) {
        webValueConverterFactory = value;
    }

    public static ValueConverterFactory<WebValueConverter> getWebValueConverterFactory() {
        return webValueConverterFactory;
    }
}
