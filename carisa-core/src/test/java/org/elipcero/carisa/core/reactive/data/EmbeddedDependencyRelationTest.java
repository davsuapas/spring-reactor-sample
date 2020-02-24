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

package org.elipcero.carisa.core.reactive.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.Relation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Suárez
 */
@RunWith(MockitoJUnitRunner.class)
public class EmbeddedDependencyRelationTest {

    @Mock
    private ReactiveCrudRepository<Entity, UUID> parentRepository;

    @Mock
    DependencyRelationRepository<RelationEntity, Map<String, UUID>> relationRepository;

    private EmbeddedDependencyRelation<RelationEntity> embeddedDependencyRelation;

    @Before
    public void setUp() {
        this.embeddedDependencyRelation = new EmbeddedDependencyRelationImpl<>(
                this.parentRepository, this.relationRepository, new Converter());
    }

    @Test
    public void create_relation_should_return_relation() {

        RelationEntity relationEntity = getRelationEntity();
        Entity parent = new Entity(relationEntity.getParentId());

        Mockito.when(this.parentRepository.findById(relationEntity.getParentId())).thenReturn(Mono.just(parent));
        Mockito.when(this.relationRepository.save(relationEntity)).thenReturn(Mono.just(relationEntity));

        StepVerifier
                .create(embeddedDependencyRelation.create(relationEntity, ""))
                .expectNextMatches(result -> {
                    assertThat(result.getChildId()).isEqualTo(relationEntity.getChildId()).as("Check child");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void create_relation_should_return_error_parent_not_found() {

        RelationEntity relationEntity = getRelationEntity();

        Mockito.when(this.parentRepository.findById(relationEntity.getParentId())).thenReturn(Mono.empty());

        StepVerifier
                .create(embeddedDependencyRelation.create(relationEntity, "Error: %s"))
                .expectErrorMessage(String.format("404 NOT_FOUND \"Error: %s\"", relationEntity.getParentId()))
                .verify();
    }

    @Test
    public void getChildrenByParent_should_return_children() {

        RelationEntity relationEntity = getRelationEntity();

        Mockito.when(this.relationRepository.findAllByParentId(relationEntity.getParentId()))
                .thenReturn(Flux.just(relationEntity));

        StepVerifier
                .create(embeddedDependencyRelation.getRelationsByParent(relationEntity.getParentId()))
                .expectNextMatches(result -> {
                    assertThat(result.getParentId()).isEqualTo(relationEntity.getParentId())
                            .as("Check parent");
                    assertThat(result.getChildId()).isEqualTo(relationEntity.getChildId()).as("Check child");
                    return true;
                })
                .verifyComplete();
    }

    private RelationEntity getRelationEntity() {
        return new RelationEntity(UUID.randomUUID(), UUID.randomUUID());
    }

    @AllArgsConstructor
    @Getter
    private class Entity {
        private UUID id;
    }

    @AllArgsConstructor
    @Getter
    private static class RelationEntity implements Relation, EntityInitializer<Relation> {
        private UUID parentId;
        private UUID childId;

        public Map<String, UUID> getMapId() {
            return getMapId(this.getParentId(), this.getChildId());
        }

        public static Map<String, UUID> getMapId(UUID parentId, UUID childId) {
            Map<String, UUID> idToMap = new HashMap<>();
            idToMap.put("parentId", parentId);
            idToMap.put("childId", childId);
            return idToMap;
        }

        @Override
        public Relation tryInitId() {
            return this;
        }
    }

    private class Converter
            implements DependencyRelationIdentifierConvert<RelationEntity, Map<String, UUID>, UUID> {

        @Override
        public Map<String, UUID> convert(RelationEntity relationEntity) {
            return RelationEntity.getMapId(relationEntity.getParentId(), relationEntity.getChildId());
        }

        @Override
        public Map<String, UUID> convertFromDictionary(Map<String, Object> id) {
            return RelationEntity.getMapId((UUID) id.get("parentId"), (UUID) id.get("childId"));
        }

        @Override
        public UUID convertForParent(RelationEntity relationEntity) {
            return relationEntity.getParentId();
        }
    }
}
