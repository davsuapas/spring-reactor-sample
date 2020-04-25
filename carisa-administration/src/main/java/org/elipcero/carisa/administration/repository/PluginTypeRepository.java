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

package org.elipcero.carisa.administration.repository;

import org.elipcero.carisa.administration.domain.PluginType;
import org.elipcero.carisa.administration.exception.NotSupportedException;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.CustomizedReactiveCrudRepository;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Repository for plugin type
 * This repository gets information from memory
 *
 * @author David Suárez
 */
public class PluginTypeRepository implements CustomizedReactiveCrudRepository<PluginType, UUID> {

    @Override
    public Mono<Long> count() {
        return Mono.just((long)PluginType.INSTANCES.size());
    }

    @Override
    public Flux<PluginType> findAll() {
        return Flux.fromIterable(PluginType.INSTANCES.values());
    }

    @Override
    public Mono<Boolean> existsById(UUID uuid) {
        return Mono.just(PluginType.INSTANCES.containsKey(uuid));
    }

    @Override
    public Mono<PluginType> findById(UUID uuid) {
        PluginType pluginType = PluginType.INSTANCES.get(uuid);
        if (pluginType == null) {
            return Mono.empty();
        }
        return Mono.just(pluginType);
    }

    @Override
    public <S extends PluginType> Mono<S> save(S s) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends PluginType> Flux<S> saveAll(Iterable<S> iterable) {
        throw new NotSupportedException();
    }

    @Override
    public <S extends PluginType> Flux<S> saveAll(Publisher<S> publisher) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<PluginType> findById(Publisher<UUID> publisher) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Boolean> existsById(Publisher<UUID> publisher) {
        throw new NotSupportedException();
    }

    @Override
    public Flux<PluginType> findAllById(Iterable<UUID> iterable) {
        throw new NotSupportedException();
    }

    @Override
    public Flux<PluginType> findAllById(Publisher<UUID> publisher) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Void> deleteById(UUID uuid) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Void> deleteById(Publisher<UUID> publisher) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Void> delete(PluginType space) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends PluginType> iterable) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends PluginType> publisher) {
        throw new NotSupportedException();
    }

    @Override
    public Mono<Void> deleteAll() {
        throw new NotSupportedException();
    }

    @Override
    public Mono<EntityDataState<PluginType>> updateCreate(
            UUID uuid, Consumer<PluginType> onUpdateChange, Supplier<Mono<PluginType>> onCreatedEntity) {

        throw new NotSupportedException();
    }
}
