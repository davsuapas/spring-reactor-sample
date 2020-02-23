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
import org.elipcero.carisa.core.data.EntityDataState;
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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author David Suárez
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiDependencyRelationTest {

    @Mock
    private ReactiveCrudRepository<Entity, UUID> parentRepository;

    @Mock
    private ReactiveCrudRepository<Entity, UUID> childRepository;

    @Mock
    DependencyRelationRepository<RelationEntity, Map<String, UUID>> relationRepository;

    private MultiplyDependencyRelation<Entity, Entity, RelationEntity> multiplyDependencyRelation;

    @Before
    public void setUp() {
        this.multiplyDependencyRelation = new MultiplyDependencyRelationImpl<>(
                this.parentRepository, this.childRepository, this.relationRepository,
                new Converter());
    }

    @Test
    public void getById_should_return_relation() {

        RelationEntity relationEntity = getRelationEntity();

        Mockito.when(this.relationRepository.findById(relationEntity.getMapId())).thenReturn(Mono.just(relationEntity));

        StepVerifier
                .create(multiplyDependencyRelation.getById(new HashMap<>(relationEntity.getMapId())))
                .expectNextMatches(result -> {
                    assertThat(result.getParentId()).isEqualTo(relationEntity.getParentId())
                            .as("Check relation parentId");
                    assertThat(result.getParentId()).isEqualTo(relationEntity.getParentId())
                            .as("Check relation childId");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void updateOrCreate_should_return_EntyDataState() {

        RelationEntity relationEntity = getRelationEntity();

        Mockito.when(this.relationRepository
                .updateCreate(relationEntity.getMapId(), null, null))
                .thenReturn(Mono.just(
                                EntityDataState.<RelationEntity>builder()
                                    .entity(relationEntity)
                                    .domainState(EntityDataState.State.created)
                                .build()));

        StepVerifier
                .create(multiplyDependencyRelation
                        .updateOrCreate(relationEntity, null, null))
                .expectNextMatches(result -> {
                    assertThat(result.getEntity().getParentId()).isEqualTo(relationEntity.getParentId())
                            .as("Check relation parentId is created");
                    assertThat(result.getEntity().getChildId()).isEqualTo(relationEntity.getChildId())
                            .as("Check relation childId is created");
                    assertThat(result.getDomainState()).isEqualTo(EntityDataState.State.created)
                            .as("Check status is created");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void create_child_and_relation_should_return_child() {

        RelationEntity relationEntity = getRelationEntity();
        Entity child = new Entity(relationEntity.getChildId());
        Entity parent = new Entity(relationEntity.getParentId());

        Mockito.when(this.parentRepository.findById(relationEntity.getParentId())).thenReturn(Mono.just(parent));
        Mockito.when(this.relationRepository.save(relationEntity)).thenReturn(Mono.just(relationEntity));
        Mockito.when(this.childRepository.save(child)).thenReturn(Mono.just(child));

        StepVerifier
                .create(multiplyDependencyRelation.create(
                        getCreateCommand(relationEntity, child), ""))
                .expectNextMatches(result -> {
                    assertThat(result.getId()).isEqualTo(child.getId()).as("Check child");
                    verify(this.childRepository, times(1)).save(child);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void create_child_and_relation_should_return_error_parent_not_found() {

        RelationEntity relationEntity = getRelationEntity();
        Entity child = new Entity(relationEntity.getChildId());

        Mockito.when(this.parentRepository.findById(relationEntity.getParentId())).thenReturn(Mono.empty());

        StepVerifier
                .create(multiplyDependencyRelation.create(
                        getCreateCommand(relationEntity, child), "Error: %s"))
                .expectErrorMessage(String.format("404 NOT_FOUND \"Error: %s\"", relationEntity.getParentId()))
                .verify();
    }

    @Test
    public void getChildrenByParent_should_return_children() {

        RelationEntity relationEntity = getRelationEntity();
        Entity child = new Entity(relationEntity.getChildId());

        Mockito.when(this.relationRepository.findAllByParentId(relationEntity.getParentId()))
                .thenReturn(Flux.just(relationEntity));
        Mockito.when(this.childRepository.findById(child.getId())).thenReturn(Mono.just(child));
        Mockito.when(this.relationRepository.deleteById(anyMap())).thenReturn(Mono.empty());

        StepVerifier
                .create(multiplyDependencyRelation.getChildrenByParent(relationEntity.getParentId()))
                .expectNextMatches(result -> {
                    assertThat(result.getRelation()).isEqualTo(relationEntity).as("Check relation");
                    assertThat(result.getChild()).isEqualTo(child).as("Check child");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void getChildrenByParent_the_child_not_exist_should_return_empty() {

        RelationEntity relationEntity = getRelationEntity();
        Entity child = new Entity(relationEntity.getChildId());

        Mockito.when(this.relationRepository.findAllByParentId(relationEntity.getParentId()))
                .thenReturn(Flux.just(relationEntity));
        Mockito.when(this.childRepository.findById(child.getId())).thenReturn(Mono.empty());
        Mockito.when(this.relationRepository.deleteById(anyMap())).thenReturn(Mono.empty());

        StepVerifier
                .create(multiplyDependencyRelation.getChildrenByParent(relationEntity.getParentId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void conectToParent_create_relation_should_return_child() {

        RelationEntity relationEntity = getRelationEntity();
        Entity child = new Entity(relationEntity.getChildId());

        Mockito.when(this.parentRepository.existsById(relationEntity.getParentId())).thenReturn(Mono.just(true));
        Mockito.when(this.childRepository.findById(relationEntity.getChildId())).thenReturn(Mono.just(child));
        Mockito.when(this.relationRepository.existsById(anyMap())).thenReturn(Mono.just(false));
        Mockito.when(this.relationRepository.save(relationEntity)).thenReturn(Mono.just(relationEntity));

        StepVerifier
                .create(multiplyDependencyRelation.connectToParent(relationEntity))
                .expectNextMatches(result -> {
                    assertThat(result.getId()).isEqualTo(child.getId()).as("Check child");
                    verify(this.relationRepository, times(1)).save(relationEntity);
                    return true;
                })
                .verifyComplete();
    }

    private DependencyRelationCreateCommand<Entity, RelationEntity> getCreateCommand(
            RelationEntity relationEntity, Entity child) {

        return new DependencyRelationCreateCommand<Entity, RelationEntity>() {
            @Override
            public Entity getChild() {
                return child;
            }

            @Override
            public RelationEntity getRelation() {
                return relationEntity;
            }
        };
    }

    private RelationEntity getRelationEntity() {
        return new RelationEntity(UUID.randomUUID(), UUID.randomUUID());
    }

    @AllArgsConstructor
    @Getter
    public class Entity implements EntityInitializer<Entity> {
        private UUID id;

        @Override
        public Entity tryInitId() {
            return this;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class RelationEntity implements Relation {
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
    }

    public class Converter
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
