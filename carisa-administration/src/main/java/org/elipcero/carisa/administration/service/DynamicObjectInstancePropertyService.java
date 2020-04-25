package org.elipcero.carisa.administration.service;

import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Operations for dynamic object instance property
 * @see DynamicObjectInstanceProperty
 *
 * @author David Suárez
 */
public interface DynamicObjectInstancePropertyService {

    /**
     * Get dynamic object instance property
     */
    Mono<DynamicObjectInstanceProperty<?>> getById(Map<String, Object> id);

    /**
     * Create the instance property
     * @param property instance property for creating
     * @return created instance property
     */
    Mono<DynamicObjectInstanceProperty<?>> create(DynamicObjectInstanceProperty<?> property);

    /**
     * Update or create the instance property. If the id exits is updated
     * otherwise is created. The references property can not be updated.
     * @see DynamicObjectInstancePropertyService#create(DynamicObjectInstanceProperty)
     * @param property the instance property for updating or creating
     * @return the instance property created or updated
     */
    Mono<EntityDataState<DynamicObjectInstanceProperty<?>>> updateOrCreate(DynamicObjectInstanceProperty<?> property);
}
