/*
 * Copyright 2019-2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.elipcero.carisa.administration.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.domain.Ente;
import org.elipcero.carisa.administration.domain.EnteCategory;
import org.elipcero.carisa.administration.domain.EnteCategoryLinkProperty;
import org.elipcero.carisa.administration.domain.EnteCategoryProperty;
import org.elipcero.carisa.administration.domain.EnteHierarchy;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.administration.domain.support.Named;
import org.elipcero.carisa.administration.domain.support.PropertyType;
import org.elipcero.carisa.administration.exception.NotMatchingTypeException;
import org.elipcero.carisa.administration.projection.EnteHierachyName;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.ParentChildName;
import org.elipcero.carisa.core.reactive.data.DependencyRelationRefNotFoundException;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyConnectionInfo;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @see EnteCategoryPropertyService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEnteCategoryPropertyService implements EnteCategoryPropertyService {

    @NonNull
    private final EmbeddedDependencyRelation<EnteCategoryProperty> enteCategoryPropertyRelation;

    @NonNull
    private final MultiplyDependencyRelation<EnteCategoryProperty, Ente, EnteCategoryLinkProperty> linkEnteRelation;

    @NonNull
    private final MultiplyDependencyRelation<EnteCategory, Ente, EnteHierarchy> enteHierarchyRelation;

    @NonNull
    private final EmbeddedDependencyRelation<EnteProperty> entePropertyRelation;

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> getById(final Map<String, Object> id) {
        return this.enteCategoryPropertyRelation.getById(id);
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> create(final EnteCategoryProperty enteCategoryProperty) {
        return this.enteCategoryPropertyRelation.create(enteCategoryProperty);
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EntityDataState<EnteCategoryProperty>> updateOrCreate(
            final EnteCategoryProperty enteCategoryProperty,
            final boolean updateType) {

        return this.enteCategoryPropertyRelation
                .updateOrCreate(enteCategoryProperty,
                        enteCategoryPropertyForUpdating -> {
                            enteCategoryPropertyForUpdating.setName(enteCategoryProperty.getName());
                            if (updateType) {
                                enteCategoryPropertyForUpdating.setType(enteCategoryProperty.getType());
                            }
                        },
                        () -> this.create(enteCategoryProperty));
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Flux<ParentChildName> getPropertiesByCategoryId(final UUID enteCategoryId) {
        return this.enteCategoryPropertyRelation.getRelationsByParent(enteCategoryId)
                .map(prop ->
                        ParentChildName.builder()
                                .parentId(prop.getParentId())
                                .childId(prop.getChildId())
                                .name(prop.getName())
                        .build());
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Flux<EnteHierachyName> getChildren(final UUID enteCategoryPropertyId) {
        return this.linkEnteRelation.getChildrenByParent(
                enteCategoryPropertyId, relation
                        -> relation.isCategory() ?
                        this.enteCategoryPropertyRelation.getById(
                                EnteCategoryProperty.GetMapId(relation.getParentLinkId(), relation.getLinkId()))
                                .cast(Named.class) :
                        this.entePropertyRelation.getById(
                                EnteProperty.GetMapId(relation.getParentLinkId(), relation.getLinkId()))
                                .cast(Named.class))
                .map(child -> EnteHierachyName
                        .builder()
                            .parentId(child.getRelation().getParentLinkId())
                            .childId(child.getRelation().getLinkId())
                            .childName(child.getChild().getName())
                            .category(child.getRelation().isCategory())
                        .build());
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> connectToEnte(final UUID enteCategoryId, final UUID categoryPropertyId,
                                                  final UUID enteId, final UUID entePropertyId) {

        return enteHierarchyRelation.existsById(EnteHierarchy.GetMapId(enteCategoryId, enteId)) // Look at children
                .flatMap(exists -> {
                    if (exists) { // Only properties of the children of the actual catalog can be referenced
                        return this.entePropertyRelation.getById(EnteProperty.GetMapId(enteId, entePropertyId))
                                .flatMap(setCategoryPropertyType(enteCategoryId, categoryPropertyId, entePropertyId))
                                .flatMap(enteProp -> this.linkEnteRelation.connectTo(
                                        EnteCategoryLinkProperty.builder()
                                                .parentCategoryId(enteCategoryId)
                                                .parentId(categoryPropertyId)
                                                .parentLinkId(enteId)
                                                .linkId(entePropertyId)
                                                .category(false)
                                             .build(),
                                        rel -> Mono.just(enteProp)))
                                .switchIfEmpty(Mono.error(new DependencyRelationRefNotFoundException(
                                        String.format("The ente property identifier: '%s' not found", enteId))));
                    }
                    else {
                        return monoErrorIncorrectReferenceToConnect(enteCategoryId);
                    }
                })
                .map(MultiplyDependencyConnectionInfo::getParent);
    }

    /**
     * @see EnteCategoryPropertyService
     */
    @Override
    public Mono<EnteCategoryProperty> connectToCategoryProperty(
            final UUID enteCategoryId, final UUID categoryPropertyId,
            final UUID linkedEnteCategoryId, final UUID linkedCategoryPropertyId) {

        return enteHierarchyRelation.existsById(EnteHierarchy.GetMapId(enteCategoryId, linkedEnteCategoryId))
                .flatMap(exists -> {
                    if (exists) { // Only properties of the children of the actual catalog can be referenced
                        return this.getById(EnteCategoryProperty.GetMapId(
                                    linkedEnteCategoryId, linkedCategoryPropertyId))
                                .flatMap(this.setCategoryPropertyType(
                                        enteCategoryId, categoryPropertyId, linkedCategoryPropertyId))
                                .flatMap(categoryProp -> this.linkEnteRelation.connectTo(
                                        EnteCategoryLinkProperty.builder()
                                                .parentCategoryId(enteCategoryId)
                                                .parentId(categoryPropertyId)
                                                .parentLinkId(linkedEnteCategoryId)
                                                .linkId(linkedCategoryPropertyId)
                                                .category(true)
                                             .build(),
                                        rel -> Mono.just(categoryProp)))
                                .switchIfEmpty(Mono.error(new DependencyRelationRefNotFoundException(
                                        String.format("The category property Id: '%s' not found",
                                                linkedCategoryPropertyId))));
                    }
                    else {
                        return monoErrorIncorrectReferenceToConnect(enteCategoryId);
                    }
                })
                .map(MultiplyDependencyConnectionInfo::getParent);
    }

    // All types of the property of the linked items must be the same.
    // If it is not exist any child the first sets the type in property category
    private Function<PropertyType, Mono<PropertyType>> setCategoryPropertyType(
            final UUID enteCategoryId, final UUID categoryPropertyId, final UUID linkedPropertyId) {

        return enteProp -> this.getById(EnteCategoryProperty.GetMapId(enteCategoryId, categoryPropertyId))
            .flatMap(categoryProp -> {
                if (categoryProp.getType() == EnteProperty.Type.None) {
                    categoryProp.setType(enteProp.getType());
                    return this.updateOrCreate(categoryProp, true).map(__ -> enteProp);
                }
                else {
                    if (enteProp.getType() != categoryProp.getType()) {
                        throw new NotMatchingTypeException(
                                String.format(
                                 "The type of linked ente property: '{0}' must be: '{1}'",
                                        linkedPropertyId, categoryProp.getType()));
                    }
                    return Mono.just(enteProp);
                }
            })
            .switchIfEmpty(Mono.error(new DependencyRelationRefNotFoundException(
                     String.format("The category property identifier: '%s' not found", categoryPropertyId))));
    }

    private static Mono<? extends MultiplyDependencyConnectionInfo<EnteCategoryProperty, Mono<PropertyType>>>
        monoErrorIncorrectReferenceToConnect(UUID enteCategoryId) {

        return Mono.error(new DependencyRelationRefNotFoundException(
                String.format(
                        "Only properties of the children of the catalog: '%s' can be referenced",
                        enteCategoryId)));
    }
}
