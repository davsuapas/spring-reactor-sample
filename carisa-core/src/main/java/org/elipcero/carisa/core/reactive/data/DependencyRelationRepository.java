package org.elipcero.carisa.core.reactive.data;

import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DependencyRelationRepository<TRelation extends Relation, ID>
        extends ReactiveCrudRepository<TRelation, ID> {

    /**
     * Find all children by parent
     *
     * @param parentId The parent id to find
     * @return dependency relations of the parent
     */
    Flux<TRelation> findAllByParentId(UUID parentId);
}
