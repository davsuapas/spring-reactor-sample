package org.elipcero.carisa.core.reactive.data;

import lombok.NonNull;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

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
            final DependencyRelationCreateCommand<TChild, TRelation> createCommand) {

        if (createCommand.getChild() instanceof EntityInitializer) {
            ((EntityInitializer)createCommand.getChild()).tryInitId();
        }
        else {
            throw new IllegalArgumentException("Child must implement EntityInitializer interface");
        }

        return this
                .createBasic(createCommand.getRelation())
                .flatMap(__ -> this.childRepository.save(createCommand.getChild()));
    }

    /**
     * @see MultiplyDependencyRelation
     */
    @Override
    public Flux<MultiplyDependencyChildInfo<TRelation, TChild>> getChildrenByParent(UUID parentId) {
        return this.getChildrenByParent(parentId, (relation) -> this.childRepository.findById(relation.getChildId()));
    }

    /**
     * @see MultiplyDependencyRelation
     */
    @Override
    public <TOChild> Flux<MultiplyDependencyChildInfo<TRelation, TOChild>> getChildrenByParent(
            UUID parentId, Function<TRelation, Mono<TOChild>> overwriteFindChild) {
        return this.getRelationsByParent(parentId)
                .flatMap(relation -> overwriteFindChild.apply(relation)
                            .map(child -> new MultiplyDependencyChildInfo<>(relation, child))
                            .switchIfEmpty(this.purge(relation)));
    }

    /**
     * @see MultiplyDependencyRelation
     */
    @Override
    public Mono<MultiplyDependencyConnectionInfo<TParent, TChild>> connectTo(TRelation relation) {
        return this.connectTo(relation, rel -> this.childRepository.findById(rel.getChildId()));
    }

    /**
     * @see MultiplyDependencyRelation
     */
    @Override
    public <TOChild> Mono<MultiplyDependencyConnectionInfo<TParent, TOChild>> connectTo(
            TRelation relation, Function<TRelation, Mono<TOChild>> overwriteFindChild) {

        return this.parentRepository
                .findById(this.convertRelationId.convertToParentFromObject(relation.getParentId()))
                .flatMap(parent ->
                    overwriteFindChild.apply(relation)
                            .flatMap(child ->
                                this.relationRepository.existsById(this.convertRelationId.convert(relation))
                                        .flatMap(existsRelation -> {
                                            if (!existsRelation) {
                                                return this.relationRepository.save(relation)
                                                     .map(__ -> new MultiplyDependencyConnectionInfo<>(parent, child));
                                            }
                                            return Mono.just(new MultiplyDependencyConnectionInfo<>(parent, child));
                                        }))
                            .switchIfEmpty(Mono.error(new DependencyRelationRefNotFoundException(
                                    String.format("The ChildId: '%s' not found", relation.getChildId()))))
                )
                .switchIfEmpty(Mono.error(new DependencyRelationRefNotFoundException(
                        String.format("The ParentId: '%s' not found", relation.getParentId()))));
    }

    private <TOChild> Mono<MultiplyDependencyChildInfo<TRelation, TOChild>> purge(TRelation relation) {
        return this.relationRepository.deleteById(this.convertRelationId.convert(relation)).then(Mono.empty());
    }

}
