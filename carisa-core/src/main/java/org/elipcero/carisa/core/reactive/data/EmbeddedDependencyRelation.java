package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.Relation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Manage dependency relations operations. The many relations attributes are embedded into entity relation
 * @param <TRelation> Intermediate relation
 */
public interface EmbeddedDependencyRelation<TRelation extends Relation> extends DependencyRelation<TRelation> {

    /**
     * Create the relation as child. If the parent doesn't exist throw exception
     *
     * @param relation relation to create
     * @return the relation created
     */
    Mono<TRelation> create(TRelation relation);

    /**
     * Get children by parent.
     *
     * @param parentId parent identifier
     * @return the relations
     */
    Flux<TRelation> getRelationsByParent(UUID parentId);
}
