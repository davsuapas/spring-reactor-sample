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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.elipcero.carisa.administration.convert.web.DynamicObjectInstancePropertyDeserializer;
import org.elipcero.carisa.administration.convert.web.DynamicObjectInstancePropertySerializer;
import org.elipcero.carisa.administration.domain.support.Named;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Object instance property. it contains the property information at the time moment referenced by
 * the prototype property. Setting values to the prototype. This values are defined in the prototype
 * @see DynamicObjectPrototypeProperty
 *
 *
 * @author David Suárez
 */
@Table("carisa_dynamic_object_instance_property")
@Getter
@Setter
@JsonDeserialize(using = DynamicObjectInstancePropertyDeserializer.class)
@JsonSerialize(using = DynamicObjectInstancePropertySerializer.class)
public class DynamicObjectInstanceProperty<TValue extends DynamicObjectInstanceProperty.Value>
        implements Relation, EntityInitializer<DynamicObjectInstanceProperty<TValue>>, Named {

    public static final String ID_COLUMN_NAME = "id";
    public static final String PARENT_ID_COLUMN_NAME = "parentId";
    public static final String INSTANCE_ID_COLUMN_NAME = "instanceId";
    public static final String VALUE_COLUMN_NAME = "value";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // Object instance identifier

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    // Look at custom values
    @Column("value")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private WrapperValue<TValue> wrapperValue;

    public DynamicObjectInstanceProperty(TValue value) {
        this(null, null, value);
    }

    @Builder
    public DynamicObjectInstanceProperty(UUID id, UUID parentId, TValue value) {
        this.id = id;
        this.parentId = parentId;
        this.setValue(value);
    }

    public TValue getValue() {
        return this.wrapperValue.getValue();
    }
    public void setValue(TValue value) {
        this.wrapperValue = new WrapperValue<>(value);
    }

    @Override
    public UUID getParentId() {
        return this.parentId;
    }

    @Override
    public UUID getChildId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.id.toString();
    }

    public DynamicObjectInstanceProperty<TValue> tryInitId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        return this;
    }

    public static Map<String, Object> GetMapId(UUID instanceId, UUID id) {
        return new HashMap<String, Object>() {{
            put(PARENT_ID_COLUMN_NAME, instanceId);
            put(ID_COLUMN_NAME, id);
        }};
    }

    // Custom values

    public interface Value {
        DynamicObjectPrototypeProperty.Type getType();
    }

    @RequiredArgsConstructor
    @Getter
    public static class WrapperValue<TValue extends DynamicObjectInstanceProperty.Value> {

        @NonNull
        TValue value;
    }

    @RequiredArgsConstructor
    @Getter
    public static class IntegerValue implements Value {

        @NonNull
        private Integer value;

        @Override
        public DynamicObjectPrototypeProperty.Type getType() {
            return DynamicObjectPrototypeProperty.Type.Integer;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class HierarchyBindingValue implements Value {

        @NonNull
        private UUID parentId;

        @NonNull
        private UUID childId;

        @NonNull
        private boolean category;

        @Override
        public DynamicObjectPrototypeProperty.Type getType() {
            return DynamicObjectPrototypeProperty.Type.HierarchyBinding;
        }
    }
}

