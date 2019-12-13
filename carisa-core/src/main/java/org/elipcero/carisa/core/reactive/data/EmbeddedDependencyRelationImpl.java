package org.elipcero.carisa.core.reactive.data;

import lombok.NonNull;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Manage dependency relations operations. The many relations attributes is embedded into entity relation
 * @param <TParent> One relation
 * @param <TRelation> Intermediate relation
 * @param <TRelationID> Relation identifier
 */
public class EmbeddedDependencyRelationImpl<TParent, TRelation extends Relation, TRelationID, TParentID>
        extends DependencyRelationImpl<TParent, TRelation, TRelationID, TParentID>
        implements EmbeddedDependencyRelation<TRelation> {

    public EmbeddedDependencyRelationImpl(
            @NonNull ReactiveCrudRepository<TParent, TParentID> parentRepository,
            @NonNull DependencyRelationRepository<TRelation, TRelationID> relationRepository,
            @NonNull DependencyRelationIdentifierConvert<TRelation, TRelationID, TParentID> convertRelationId) {

        super(parentRepository, relationRepository, convertRelationId);
    }

   /**
    * @see EmbeddedDependencyRelation
    */
   @Override
   public Mono<TRelation> create(final TRelation relation, final String errorMessage) {

        if (relation instanceof EntityInitializer) {
            ((EntityInitializer)relation).tryInitId();
        }
        else {
            throw new IllegalArgumentException("Relation must implement EntityInitializer interface");
        }

        return this.createBasic(relation, errorMessage);
    }
}
