package org.elipcero.carisa.core.data;

import java.util.UUID;

/**
 * Relation information
 */
public interface Relation {

    /**
     * Getting parent identifier
     *
     * @return identifier
     */
    UUID getParentId();

    /**
     * Getting child identifier
     *
     * @return identifier
     */
    UUID getChildId();
}
