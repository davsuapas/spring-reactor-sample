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

package global

import (
	"carisa/deployer/kubernetes"
	"github.com/labstack/echo/v4"
	"github.com/stretchr/testify/assert"
	"testing"
)

type mockWeb struct {
}

func (w *mockWeb) kubernetesWeb() *kubernetes.Web {
	return &kubernetes.Web{}
}

func (w *mockWeb) kubernetesDeployer() *kubernetes.Deployer {
	return nil
}

func TestWebRoutes(t *testing.T) {
	e := echo.New()
	w := &WebServer{&mockWeb{}}
	w.Routes(e)
	assert.Equal(t, 1, len(e.Routes()),"should return routes")
}
