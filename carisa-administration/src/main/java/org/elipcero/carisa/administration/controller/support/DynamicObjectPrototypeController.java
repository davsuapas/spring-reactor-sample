package org.elipcero.carisa.administration.controller.support;

import lombok.NonNull;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.support.DynamicObjectPrototypeService;
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
 * Base controller to dynamic object prototype controller.
 * It could use instead of DynamicObjectPrototype a generic but then it not work affordance
 * @see DynamicObjectPrototype
 *
 * @author David Suárez
 */
public abstract class DynamicObjectPrototypeController<TRelation extends ManyRelation>
        implements ChildControllerHypermedia<DynamicObjectPrototype> {

    protected final CrudHypermediaController<DynamicObjectPrototype> crudHypermediaController;
    protected final DynamicObjectPrototypeService<TRelation> dynamicObjectPrototypeService;

    public DynamicObjectPrototypeController(
            @NonNull final BasicReactiveRepresentationModelAssembler<DynamicObjectPrototype> modelAssembler,
            @NonNull final DynamicObjectPrototypeService<TRelation> dynamicObjectPrototypeService) {

        this.crudHypermediaController = new CrudHypermediaController<>(modelAssembler);
        this.dynamicObjectPrototypeService = dynamicObjectPrototypeService;
    }

    /**
     * Return the many to many relation. Relation between the parent and child
     * @param relation the information to build the relation
     * @return the many to many relation
     */
    protected abstract TRelation buildManyRelation(final DynamicObjectPrototype relation);

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
     * Get the DynamicObjectPrototype by id
     * @param id the DynamicObjectPrototype identifier (UUID string)
     * @return the DynamicObjectPrototype found
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<DynamicObjectPrototype>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.dynamicObjectPrototypeService.getById(UUID.fromString(id)));
    }

    /**
     * Create the DynamicObjectPrototype
     * @param entity the DynamicObjectPrototype (Id == null)
     * @return the DynamicObjectPrototype created
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototype>>> create(
            final @RequestBody DynamicObjectPrototype entity) {

        return this.crudHypermediaController.create(
                this.dynamicObjectPrototypeService.create(entity, this.buildManyRelation(entity)));
    }

    /**
     * Update or create the DynamicObjectPrototype depending of the identifier if exists.
     * @param id the DynamicObjectPrototype identifier (UUID string)
     * @param entity the DynamicObjectPrototype (Id == null)
     * @return the DynamicObjectPrototype updated or created
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototype>>> updateOrCreate(
            final @PathVariable("id") String id,
            final @RequestBody DynamicObjectPrototype entity) {

        return this.crudHypermediaController.updateOrCreate(
                this.dynamicObjectPrototypeService.updateOrCreate(
                        UUID.fromString(id), entity, this.buildManyRelation(entity)));
    }
}
