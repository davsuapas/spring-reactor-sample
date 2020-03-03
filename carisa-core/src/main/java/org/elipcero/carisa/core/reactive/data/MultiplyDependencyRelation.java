package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.Relation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

/**
 * Manage dependency relations operations. It control the relation intermediate
 * between the one relation and many relations
 * @param <TParent> One relation. Used to injection in the interface
 * @param <TChild> Many relation
 * @param <TRelation> Intermediate relation
 */
public interface MultiplyDependencyRelation<TParent, TChild, TRelation extends Relation>
        extends DependencyRelation<TRelation> {

    /**
     * Create the child and the relation referencing to the parent.
     * If the parent doesn't exist throw exception.
     *
     * @param createCommand createCommand are the parameters to create
     * @return many relation
     */
    Mono<TChild> create(DependencyRelationCreateCommand<TChild, TRelation> createCommand);

    /**
     * Get children by parent. If the child doesn't exist is removed
     * @param parentId parent identifier
     * @param overwriteFindChild customized function to find child
     * @param <TOChild> the child type
     * @return the relations and child
     */
    <TOChild> Flux<MultiplyDependencyChildInfo<TRelation, TOChild>> getChildrenByParent(
            UUID parentId, Function<TRelation, Mono<TOChild>> overwriteFindChild);

    /**
     * Get children by parent. If the child doesn't exist is removed
     *
     * @param parentId parent identifier
     * @return the relations and child
     */
    Flux<MultiplyDependencyChildInfo<TRelation, TChild>> getChildrenByParent(UUID parentId);

    /**
     * Connect child to parent
     * @param relation the relation
     * @return the child
     */
    Mono<TChild> connectToParent(TRelation relation);
}
