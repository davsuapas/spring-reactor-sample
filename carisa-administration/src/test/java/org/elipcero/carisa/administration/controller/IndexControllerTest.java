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

package org.elipcero.carisa.administration.controller;

import org.elipcero.carisa.administration.domain.Index;
import org.elipcero.carisa.administration.general.StringResource;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.hateoas.MediaTypes;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * @author David Suárez
 */
@WebFluxTest(IndexController.class)
public class IndexControllerTest extends AbstractControllerTest {

    @Test
    public void get_index_should_return_ok_and_links() {

        this.testClient
                .get()
                .uri("/")
                .accept(MediaTypes.HAL_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.version").isEqualTo(new Index().getVersion())
                    .jsonPath("$.links.[?(@.rel=='instances')].href").isEqualTo("/api/instances")
                    .jsonPath("$.links.[?(@.rel=='spaces')].href").isEqualTo("/api/spaces")
                    .jsonPath("$.links.[?(@.rel=='entes')].href").isEqualTo("/api/entes")
                    .jsonPath("$.links.[?(@.rel=='enteProperties')].href").isEqualTo("/api/enteproperties")
                    .jsonPath("$.links.[?(@.rel=='enteCategories')].href").isEqualTo("/api/entecategories")
                .consumeWith(document("index",
                    responseFields(
                        fieldWithPath("version").description("Carisa API version (x.x.x)"),
                        subsectionWithPath("links")
                                .description("The Carisa resources. " + StringResource.METADATA_INFORMATION)
                    )));
    }
}
