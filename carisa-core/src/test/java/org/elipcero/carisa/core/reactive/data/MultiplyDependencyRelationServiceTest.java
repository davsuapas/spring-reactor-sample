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

import lombok.Getter;
import org.elipcero.carisa.core.data.Entity;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.EntityInitializer;
import org.elipcero.carisa.core.data.ManyRelation;
import org.elipcero.carisa.core.data.Relation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author David Suárez
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiplyDependencyRelationServiceTest {

    @Mock
    private CustomizedReactiveCrudRepository<EntityTest, UUID> entityRepository;

    @Mock
    private MultiplyDependencyRelation<? extends Entity, EntityTest, RelationEntity> relation;

    private MultiplyDependencyRelationService<EntityTest, RelationEntity> relationService;

    @Before
    public void setUp() {
        this.relationService = new RelationService();
    }

    @Test
    public void getById_should_return_entity() {

        EntityTest entity = new EntityTest();

        Mockito.when(this.entityRepository.findById(entity.getId())).thenReturn(Mono.just(entity));

        StepVerifier
                .create(this.relationService.getById(entity.getId()))
                .expectNextMatches(result -> {
                    assertThat(result.getParentId()).isEqualTo(entity.getParentId())
                            .as("Check entity parentId");
                    assertThat(result.getChildId()).isEqualTo(entity.getChildId())
                            .as("Check entity childId");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void create_should_return_created_entity() {

        EntityTest entity = new EntityTest();
        RelationEntity relation = new RelationEntity();

        Mockito.when(this.relation.create(any())).thenReturn(Mono.just(entity));

        StepVerifier
                .create(this.relationService.create(entity, relation))
                .expectNextMatches(result -> {
                    assertThat(result.getParentId()).isEqualTo(entity.getParentId())
                            .as("Check entity parentId");
                    assertThat(result.getChildId()).isEqualTo(entity.getChildId())
                            .as("Check entity childId");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void updateOrCreate_should_return_created_entity() {

        EntityTest entity = new EntityTest();
        RelationEntity relation = new RelationEntity();

        Mockito.when(this.entityRepository.updateCreate(eq(entity.getId()), any(), any()))
                .thenReturn(Mono.just(EntityDataState.<EntityTest>builder()
                        .domainState(EntityDataState.State.updated)
                        .entity(entity)
                    .build()));

        StepVerifier
                .create(this.relationService.updateOrCreate(entity.getId(), entity, relation))
                .expectNextMatches(result -> {
                    assertThat(result.getEntity().getParentId()).isEqualTo(entity.getParentId())
                            .as("Check entity parentId");
                    assertThat(result.getEntity().getChildId()).isEqualTo(entity.getChildId())
                            .as("Check entity childId");
                    assertThat(result.getDomainState()).isEqualTo(EntityDataState.State.updated)
                            .as("Check state");
                    return true;
                })
                .verifyComplete();
    }

    private class RelationService extends MultiplyDependencyRelationService<EntityTest, RelationEntity> {

        public RelationService() {
            super(entityRepository, relation);
        }

        @Override
        protected void updateEntity(EntityTest entityForUpdating, EntityTest entity) {
        }
    }

    @Getter
    private static class EntityTest implements EntityInitializer<EntityTest>, Relation {
        private UUID id;
        private UUID parentId;

        public EntityTest() {
            this.id = UUID.randomUUID();
            this.parentId = UUID.randomUUID();
        }

        @Override
        public EntityTest tryInitId() {
            return this;
        }

        @Override
        public UUID getChildId() {
            return this.id;
        }
    }

    private static class RelationEntity extends ManyRelation {
    }
}
