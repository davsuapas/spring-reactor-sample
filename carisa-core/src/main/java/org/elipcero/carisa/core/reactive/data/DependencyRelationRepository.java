package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.Relation;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DependencyRelationRepository<TRelation extends Relation, ID>
        extends CustomizedReactiveCrudRepository<TRelation, ID> {

    /**
     * Find all children by parent
     *
     * @param parentId The parent id to find
     * @return dependency relations of the parent
     */
    Flux<TRelation> findAllByParentId(UUID parentId);
}
