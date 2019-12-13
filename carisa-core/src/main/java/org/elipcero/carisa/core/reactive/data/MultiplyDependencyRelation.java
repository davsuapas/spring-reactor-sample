package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.Relation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Manage dependency relations operations. It control the relation intermediate
 * between the one relation and many relations
 * @param <TChild> Many relation
 * @param <TRelation> Intermediate relation
 */
public interface MultiplyDependencyRelation<TChild, TRelation extends Relation> extends DependencyRelation<TRelation> {

    /**
     * Create the child and the relation referencing to the parent.
     * If the parent doesn't exist throw exception.
     *
     * @param createCommand createCommand are the parameters to create
     * @param errorMessage  error message for user If the parent doesn't exist
     * @return many relation
     */
    Mono<TChild> create(
            DependencyRelationCreateCommand<TChild, TRelation> createCommand,
            String errorMessage);

    /**
     * Get children by parent. If the child doesn't exist is removed
     *
     * @param parentId parent identifier
     * @return may relations
     */
    Flux<TChild> getChildrenByParent(UUID parentId);
}
