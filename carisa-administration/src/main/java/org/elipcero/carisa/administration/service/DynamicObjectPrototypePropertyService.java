package org.elipcero.carisa.administration.service;

import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Operations for dynamic object prototype property
 * @see DynamicObjectPrototypeProperty
 *
 * @author David Suárez
 */
public interface DynamicObjectPrototypePropertyService {

    /**
     * Get dynamic object prototype property
     */
    Mono<DynamicObjectPrototypeProperty> getById(Map<String, Object> id);

    /**
     * Create the prototype property
     * @param prototypeProperty prototype property for creating
     * @return created prototype property
     */
    Mono<DynamicObjectPrototypeProperty> create(DynamicObjectPrototypeProperty prototypeProperty);

    /**
     * Update or create the prototype property. If the id exits is updated
     * otherwise is created. The reference property can not be updated.
     * @see DefaultDynamicObjectPrototypePropertyService#create(DynamicObjectPrototypeProperty)
     * @param prototypeProperty prototype property for updating or creating
     * @return prototype property created or updated
     */
    Mono<EntityDataState<DynamicObjectPrototypeProperty>> updateOrCreate(
            DynamicObjectPrototypeProperty prototypeProperty);
}
