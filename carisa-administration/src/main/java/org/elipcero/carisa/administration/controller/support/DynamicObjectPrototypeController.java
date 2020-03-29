package org.elipcero.carisa.administration.controller.support;

import org.elipcero.carisa.administration.controller.QueryPrototypeController;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.domain.SpaceQueryPrototype;
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.elipcero.carisa.administration.general.StringResource;
import org.elipcero.carisa.administration.service.support.DynamicObjectPrototypeService;
import org.elipcero.carisa.core.hateoas.BasicReactiveRepresentationModelAssembler;
import org.elipcero.carisa.core.reactive.web.ChildControllerHypermedia;
import org.elipcero.carisa.core.reactive.web.CrudHypermediaController;
import org.reactivestreams.Publisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Dynamic object base controller.
 * @see SpaceQueryPrototype
 *
 * @author David Suárez
 */
public abstract class DynamicObjectPrototypeController implements ChildControllerHypermedia<DynamicObjectPrototype> {

    private final CrudHypermediaController<DynamicObjectPrototype> crudHypermediaController;
    private final DynamicObjectPrototypeService dynamicObjectService;

    public DynamicObjectPrototypeController(
            final BasicReactiveRepresentationModelAssembler<DynamicObjectPrototype> objectPrototypeModelAssembler,
            final DynamicObjectPrototypeService dynamicObjectService) {

        Assert.notNull(objectPrototypeModelAssembler, "The queryPrototypeModelAssembler can not be null");
        Assert.notNull(dynamicObjectService, "The dynamicObjectService can not be null");

        this.crudHypermediaController = new CrudHypermediaController<>(objectPrototypeModelAssembler);
        this.dynamicObjectService = dynamicObjectService;
    }

    /**
     * Return the many to many relation. Relation between the parent and child
     * @param queryPrototype the information to build the relation
     * @return the many to many relation
     */
    protected abstract ManyRelation buildManyRelation(DynamicObjectPrototype queryPrototype);

    /**
     * Return schema
     * @return
     */
    @GetMapping
    public Publisher<EntityModel<String>> getMetadata() {
        return linkTo(
                methodOn(QueryPrototypeController.class).getMetadata())
                .withSelfRel()
                .andAffordance(methodOn(QueryPrototypeController.class).create(null))
                .toMono().map(link -> new EntityModel<>(StringResource.METADATA_INFORMATION, link));
    }

    /**
     * Get object prototype by id
     * @param id the object prototype identifier (UUID string)
     * @return the object prototype entity
     */
    @GetMapping("/{id}")
    public Publisher<EntityModel<DynamicObjectPrototype>> getById(final @PathVariable("id") String id) {
        return this.crudHypermediaController.get(this.dynamicObjectService.getById(UUID.fromString(id)));
    }

    /**
     * Create the qobjectuery prototype
     * @param objectPrototype the object prototype (Id == null)
     * @return the object prototype
     */
    @PostMapping
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototype>>> create(
            final @RequestBody DynamicObjectPrototype objectPrototype) {

        return this.crudHypermediaController.create(
                this.dynamicObjectService.create(objectPrototype, this.buildManyRelation(objectPrototype)));
    }

    /**
     * Update or create the query prototype depending of the identifier if exists.
     * @param id the object prototype identifier (UUID string)
     * @param objectPrototype the object prototype (Id == null)
     * @return the object prototype
     */
    @PutMapping("/{id}")
    public Publisher<ResponseEntity<EntityModel<DynamicObjectPrototype>>> updateOrCreate(
            final @PathVariable("id") String id,
            final @RequestBody DynamicObjectPrototype objectPrototype) {

        return this.crudHypermediaController.updateOrCreate(
                this.dynamicObjectService.updateOrCreate(
                        UUID.fromString(id), objectPrototype, this.buildManyRelation(objectPrototype)));
    }
}
