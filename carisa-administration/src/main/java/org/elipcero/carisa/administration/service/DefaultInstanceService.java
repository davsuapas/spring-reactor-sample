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

import org.elipcero.carisa.administration.domain.Instance;
import org.elipcero.carisa.administration.domain.KubernetesDeployer;
import org.elipcero.carisa.administration.domain.Space;
import org.elipcero.carisa.administration.projection.SpaceInstanceName;
import org.elipcero.carisa.administration.repository.InstanceRepository;
import org.elipcero.carisa.administration.repository.InstanceSpaceRepository;
import org.elipcero.carisa.administration.repository.SpaceRepository;
import org.elipcero.carisa.core.application.configuration.ServiceProperties;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.misc.DataLockController;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see org.elipcero.carisa.administration.service.InstanceService
 *
 * @author David Suárez
 */
public class DefaultInstanceService implements InstanceService {

    private final InstanceRepository instanceRepository;
    private final DataLockController dataLockController;
    private final InstanceSpaceRepository instanceSpaceRepository;
    private final SpaceRepository spaceRepository;

    private final WebClient webClient;

    public DefaultInstanceService(
            final InstanceRepository instanceRepository,
            final ServiceProperties serviceProperties,
            final DataLockController dataLockController,
            final InstanceSpaceRepository instanceSpaceRepository,
            final SpaceRepository spaceRepository) {

        Assert.notNull(instanceRepository, "The instanceRepository can not be null");
        Assert.notNull(serviceProperties, "The serviceProperties can not be null");
        Assert.notNull(dataLockController, "The serviceProperties can not be null");
        Assert.notNull(instanceSpaceRepository, "The instanceSpaceRepository can not be null");
        Assert.notNull(spaceRepository, "The spaceRepository can not be null");

        this.instanceRepository = instanceRepository;
        this.dataLockController = dataLockController;
        this.instanceSpaceRepository = instanceSpaceRepository;
        this.spaceRepository = spaceRepository;

        ServiceProperties.Skipper skipper = serviceProperties.getSkipper();
        Assert.notNull(skipper, "The skipper configuration can not be null");

        this.webClient = WebClient
                .builder()
                    .baseUrl(skipper.getUri())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * @see InstanceService
     */
    @Override
    public Mono<Instance> getById(final UUID id) {
        return this.instanceRepository.findById(id);
    }

    /**
     * @see InstanceService
     */
    @Override
    public Flux<SpaceInstanceName> getSpacesByInstance(final UUID instanceId) {

        return this.instanceSpaceRepository.findAllByInstanceId(instanceId)
                .flatMap(instanceSpace ->
                        this.spaceRepository
                                .findById(instanceSpace.getSpaceId())
                                .switchIfEmpty(this.purgeInstanceSpace(instanceSpace.getId())))
                .map(space -> SpaceInstanceName
                        .builder()
                            .instanceId(instanceId)
                            .spaceId(space.getId())
                            .SpaceName(space.getName())
                        .build());
    }

    private Mono<Space> purgeInstanceSpace(MapId id) {
        return this.instanceSpaceRepository.deleteById(id).then(Mono.empty());
    }

    /**
     * @see InstanceService
     */
    @Override
    public Mono<Instance> create(final Instance instance) {
        return this.instanceRepository.save(instance);
    }

    /**
     * @see InstanceService
     */
    @Override
    public Mono<EntityDataState<Instance>> updateOrCreate(final UUID id, final Instance instance) {
        instance.setId(id);
        instance.tryInitState();
        return this.instanceRepository
                .updateCreate(id,
                    instanceForUpdating -> instanceForUpdating.setName(instance.getName()),
                    this.create(instance));
    }

    /**
     * @see InstanceService
     */
    public Mono<Instance> deploy(final UUID id) {
        return this.dataLockController
            .lock(id, 60)
            .flatMap(couldLock -> {
                if (couldLock) {
                    return this.instanceRepository.changeState(id, Instance.State.InProgress)
                        .flatMap(__ -> this.webClient.put()
                            .uri("/api/platforms/kubernetes/deployers")
                            .accept(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromObject(KubernetesDeployer
                                    .builder()
                                        .name(id.toString())
                                        .namespace(id.toString())
                                    .build()))
                            .exchange()
                            .flatMap(response -> {
                                if (response.statusCode().isError()) {
                                    return this.instanceRepository.changeState(id, Instance.State.Error);
                                }
                                else {
                                    return this.instanceRepository.changeState(id, Instance.State.Deployed);
                                }
                            }));
                }
                return Mono.just(false);
            })
            .doFinally(__ -> this.dataLockController.unLock(id))
            .flatMap(__ -> this.instanceRepository.findById(id));
    }
}
