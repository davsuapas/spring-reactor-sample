package org.elipcero.carisa.core.data;

import java.util.UUID;

public interface Relation {
    UUID getParentId();
    UUID getChildId();
}
