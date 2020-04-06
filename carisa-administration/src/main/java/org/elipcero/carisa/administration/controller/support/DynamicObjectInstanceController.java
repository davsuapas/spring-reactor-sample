package org.elipcero.carisa.administration.controller.support;

import lombok.NonNull;
import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.support.DynamicObjectInstanceService;
import org.elipcero.carisa.core.data.ManyRelation;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.elipcero.carisa.core.reactive.web.ChildControllerHypermedia;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Base controller to dynamic object instance controller.
 * It could use instead of DynamicObjectInstance a generic but then it not work affordance
 * @see DynamicObjectInstance
 *
 * @author David Suárez
 */
public abstract class DynamicObjectInstanceController<TRelation extends ManyRelation>
        implements ChildControllerHypermedia<DynamicObjectInstance> {

    private final CrudHypermediaController<DynamicObjectInstance> crudHypermediaController;
    private final DynamicObjectInstanceService<TRelation> multiDependencyService;

    public DynamicObjectInstanceController(
           @NonNull final BasicReactiveRepresentationModelAssembler<DynamicObjectInstance> modelAssembler,
           @NonNull final DynamicObjectInstanceService<TRelation> multiDependencyService) {

        this.crudHypermediaController = new CrudHypermediaController<>(modelAssembler);
        this.multiDependencyService = multiDependencyService;
    }

    /**
     * Return the many to many relation. Relation between the parent and child
     * @param relation the information to build the relation
     * @return the many to many relation
     */
    protected abstract TRelation buildManyRelation(DynamicObjectInstance relation);

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(this.getClass()).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(this.getClass()).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get the entity by id
     * @param id the entity identifier (UUID string)
     * @return the entity found
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<DynamicObjectInstance>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.multiDependencyService.getById(UUID.fromString(id)));
    }

    /**
     * Create the entity
     * @param entity the entity (Id == null)
     * @return the entity created
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<DynamicObjectInstance>>> create(
            final @RequestBody DynamicObjectInstance entity) {

        return this.crudHypermediaController.create(
                this.multiDependencyService.create(entity, this.buildManyRelation(entity)));
    }

    /**
     * Update or create the entity depending of the identifier if exists.
     * @param id the entity identifier (UUID string)
     * @param entity the entity (Id == null)
     * @return the entity updated or created
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectInstance>>> updateOrCreate(
            final @PathVariable("id") String id,
            final @RequestBody DynamicObjectInstance entity) {

        return this.crudHypermediaController.updateOrCreate(
                this.multiDependencyService.updateOrCreate(
                        UUID.fromString(id), entity, this.buildManyRelation(entity)));
    }
}
