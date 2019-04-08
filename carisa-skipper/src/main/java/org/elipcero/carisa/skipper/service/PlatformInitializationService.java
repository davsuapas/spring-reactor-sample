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

package org.elipcero.carisa.skipper.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.skipper.domain.Platform;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Load the platform from repository and inject into skipper system
 *
 * @author David Suárez
 */
@Component
@RequiredArgsConstructor
public class PlatformInitializationService {

    @NonNull
    private final List<Platform> platforms;

    @NonNull
    private final DeployerService deployerService;

    @EventListener
    public void initialize(final ApplicationReadyEvent event) {
        this.deployerService.recreate(this.platforms);
    }
}
