/*
 *  Copyright 2019-2022 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.elipcero.carisa.administration.configuration;

import lombok.extern.slf4j.Slf4j;
import org.elipcero.carisa.administration.convert.cassandra.type.DataEngineBooleanValueConverter;
import org.elipcero.carisa.administration.convert.cassandra.type.DataEngineHierarchyBindingValueConverter;
import org.elipcero.carisa.administration.convert.cassandra.type.DataEngineIntegerValueConverter;
import org.elipcero.carisa.administration.convert.cassandra.type.DataEngineStringValueConverter;
import org.elipcero.carisa.administration.convert.cassandra.type.ObjectInstancePropertyValueReadConverter;
import org.elipcero.carisa.administration.convert.cassandra.type.ObjectInstancePropertyValueWriteConverter;
import org.elipcero.carisa.administration.convert.type.DataEngineValueConverter;
import org.elipcero.carisa.administration.convert.type.ValueConverterFactory;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.convert.CustomConversions;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Cassandra configuration
 *
 * @author David Suárez
 */
@Slf4j
@EnableConfigurationProperties(CassandraProperties.class)
@Configuration
public class CassandraConfiguration extends AbstractReactiveCassandraConfiguration {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Override
    protected String getKeyspaceName() {
        String keyNamespace = this.cassandraProperties.getKeyspaceName();
        if (keyNamespace == null || keyNamespace.equals("")) {
            keyNamespace = "carisa_administration";
        }
        log.info("Configured cassandra namespace: " + keyNamespace);
        return keyNamespace;
    }

    @Override
    public CustomConversions customConversions() {
        ValueConverterFactory<DataEngineValueConverter> factory =
                new ValueConverterFactory<>(new HashMap<Integer, DataEngineValueConverter>() {{
            put(DynamicObjectPrototypeProperty.Type.Integer.ordinal(), new DataEngineIntegerValueConverter());
            put(DynamicObjectPrototypeProperty.Type.String.ordinal(), new DataEngineStringValueConverter());
            put(DynamicObjectPrototypeProperty.Type.Boolean.ordinal(), new DataEngineBooleanValueConverter());
            put(DynamicObjectPrototypeProperty.Type.HierarchyBinding.ordinal(), new DataEngineHierarchyBindingValueConverter());
        }});

        return new CassandraCustomConversions(
                Arrays.asList(
                        new ObjectInstancePropertyValueReadConverter(factory),
                        new ObjectInstancePropertyValueWriteConverter(factory)));
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[] { "org.elipcero.carisa.administration.domain" };
    }

    @Override
    protected boolean getMetricsEnabled() {
        return false;
    }

    @Override
    protected int getPort() {
        return this.cassandraProperties.getPort();
    }
}
