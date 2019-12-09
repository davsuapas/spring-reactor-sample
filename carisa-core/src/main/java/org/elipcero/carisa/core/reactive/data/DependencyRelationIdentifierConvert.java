package org.elipcero.carisa.core.reactive.data;

public interface DependencyRelationIdentifierConvert<TRelation, TRelationID> {
    TRelationID convert(TRelation relation);
}
