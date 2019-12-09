package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.Relation;

public interface DependencyRelationCreateCommand<TChild, TRelation extends Relation> {
    TChild getChild();
    TRelation getRelation();
}
