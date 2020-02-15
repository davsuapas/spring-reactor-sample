package org.elipcero.carisa.core.reactive.data;

import lombok.NonNull;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Manage dependency relations operations. It control the relation intermediate
 * between the one relation and many relations
 * @param <TParent> One relation
 * @param <TChild> Many relation
 * @param <TRelation> Intermediate relation
 * @param <TRelationID> Relation identifier
 */
public class MultiplyDependencyRelationImpl<TParent, TChild,
            TRelation extends Relation, TRelationID, TParentID>
        extends DependencyRelationImpl<TParent, TRelation, TRelationID, TParentID>
        implements MultiplyDependencyRelation<TParent, TChild, TRelation> {

    private final ReactiveCrudRepository<TChild, UUID> childRepository;

    public MultiplyDependencyRelationImpl(
            @NonNull ReactiveCrudRepository<TParent, TParentID> parentRepository,
            @NonNull ReactiveCrudRepository<TChild, UUID> childRepository,
            @NonNull DependencyRelationRepository<TRelation, TRelationID> relationRepository,
            @NonNull DependencyRelationIdentifierConvert<TRelation, TRelationID, TParentID> convertRelationId) {

        super(parentRepository, relationRepository, convertRelationId);
        this.childRepository = childRepository;
    }

    /**
     * @see MultiplyDependencyRelation
     */
    @Override
    public Mono<TChild> create(
            final DependencyRelationCreateCommand<TChild, TRelation> createCommand,
            final String errorMessage) {

        if (createCommand.getChild() instanceof EntityInitializer) {
            ((EntityInitializer)createCommand.getChild()).tryInitId();
        }
        else {
            throw new IllegalArgumentException("Child must implement EntityInitializer interface");
        }

        return this
                .createBasic(createCommand.getRelation(), errorMessage)
                .flatMap(__ -> this.childRepository.save(createCommand.getChild()));
    }

    /**
     * @see MultiplyDependencyRelation
     */
    @Override
    public Flux<MultiplyDependencyChildInfo<TRelation, TChild>> getChildrenByParent(UUID parentId) {
        return this.getRelationsByParent(parentId)
                .flatMap(relation -> this.childRepository
                        .findById(relation.getChildId())
                            .map(child -> MultiplyDependencyChildInfo.<TRelation, TChild>builder()
                                    .relation(relation).child(child).build())
                            .switchIfEmpty(this.purge(relation)));
    }

    @Override
    public Mono<TChild> connectToParent(TRelation relation) {
        return this.parentRepository
                .existsById((TParentID)relation.getParentId())
                .flatMap(existsParent -> {
                    if (existsParent) {
                        return this.childRepository.findById(relation.getChildId())
                                .flatMap(child ->
                                    this.relationRepository.existsById(this.convertRelationId.convert(relation))
                                            .map(existsRelation -> {
                                                if (!existsRelation) {
                                                    this.relationRepository.save(relation);
                                                }
                                                return child;
                                            }))
                                .switchIfEmpty(Mono.error(new MultiplyDependencyChildNotFoundException(
                                        String.format("The ChildId: '%s' not found", relation.getChildId()))));
                    }
                    return Mono.error(new MultiplyDependencyParentNotFoundException(
                            String.format("The ParentId: '%s' not found", relation.getParentId())));
                });
    }

    private Mono<MultiplyDependencyChildInfo<TRelation, TChild>> purge(TRelation relation) {
        return this.relationRepository
                .deleteById(this.convertRelationId.convert(relation)).then(Mono.empty());
    }
}
