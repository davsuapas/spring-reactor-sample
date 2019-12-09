package org.elipcero.carisa.core.reactive.data;

import lombok.NonNull;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Manage dependency relations operations. The many relations attributes is embedded into entity relation
 * @param <TParent> One relation
 * @param <TChild> Many relation
 * @param <TRelation> Intermediate relation
 * @param <TRelationID> Relation identifier
 */
public class EmbeddedDependencyRelationImpl<TParent, TChild, TRelation extends Relation, TRelationID>
        extends DependencyRelation<TParent, TRelation, TRelationID>
        implements EmbeddedDependencyRelation<TRelation> {

    public EmbeddedDependencyRelationImpl(
            @NonNull ReactiveCrudRepository<TParent, UUID> parentRepository,
            @NonNull DependencyRelationRepository<TRelation, TRelationID> relationRepository) {

        super(parentRepository, relationRepository);
    }

    /**
     * @see EmbeddedDependencyRelation
     */
    @Override
    public Mono<TRelation> create(final TRelation relation, final String errorMessage) {
        return this.create(relation, errorMessage);
    }
}
