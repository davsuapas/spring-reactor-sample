/*
 *  Copyright 2019-2022 the original author or authors.
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

// Kubernetes web controller
package kubernetes

import (
	"github.com/labstack/echo/v4"
	"net/http"
)

type (
	webDeployerCreation struct {
		Namespace string `json:"namespace"`
	}

	Web struct {
		Deployer *Deployer
	}
)

// Create accepts POST request and create the kubernetes namespace
func (web *Web) Create(c echo.Context) error {
	creation := new(webDeployerCreation)

	if err := c.Bind(creation); err != nil || creation.Namespace == "" {
		return echo.NewHTTPError(http.StatusBadRequest, "Impossible recover information")
	}
	if err := web.Deployer.CreateNamespace(&deployerCreation{creation.Namespace}); err != nil {
		return echo.NewHTTPError(http.StatusInternalServerError, "Impossible create kubernetes namespace: " + creation.Namespace)
	}
	return c.JSON(http.StatusCreated, creation)
}
