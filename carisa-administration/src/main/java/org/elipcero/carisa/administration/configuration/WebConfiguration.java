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

package org.elipcero.carisa.administration.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.stream.Collectors;

/**
 * Web configuration
 *
 * @author David Suárez
 */
@EnableHypermediaSupport(type = {
        EnableHypermediaSupport.HypermediaType.HAL, EnableHypermediaSupport.HypermediaType.HAL_FORMS })
@Configuration
public class WebConfiguration {

    @Autowired
    private ErrorAttributes errorAttributes;

    @Autowired
    private ResourceProperties resourceProperties;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectProvider<ViewResolver> viewResolversProvider;

    @Autowired
    private ServerCodecConfigurer serverCodecConfigurer;

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler errorWebExceptionHandler() {
        GlobalErrorWebExceptionHandler exceptionHandler =
                new GlobalErrorWebExceptionHandler(this.errorAttributes, this.resourceProperties,
                        this.serverProperties.getError(), this.applicationContext);

        exceptionHandler.setViewResolvers(this.viewResolversProvider.orderedStream().collect(Collectors.toList()));
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());

        return exceptionHandler;
    }
}
