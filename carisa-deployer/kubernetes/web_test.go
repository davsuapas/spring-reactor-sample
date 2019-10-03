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

package kubernetes

import (
	"github.com/labstack/echo/v4"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes/fake"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestCreateWithStateOk(t *testing.T) {
	const body = `{"namespace":"namespace"}` + "\n"

	rec, c, w := mockHttp(body)

	if assert.NoError(t, w.Create(c)) {
		assert.Equal(t, http.StatusCreated, rec.Code, "should return status created")
		assert.Equal(t, body, rec.Body.String(), "should return namespace created")
		n, _ := w.Deployer.apiCore.Namespaces().List(metav1.ListOptions{})
		assert.Equal(t,1, len(n.Items), "should return one namespace")
	}
}

func TestCreateWithStateBadRequest(t *testing.T) {
	const body = `{"other":"namespace"}`

	_, c, w := mockHttp(body)

	assert.Equal(t, http.StatusBadRequest, w.Create(c).(*echo.HTTPError).Code, "should return status bad request")
}

func mockHttp(body string) (*httptest.ResponseRecorder, echo.Context, *Web) {
	e := echo.New()
	req := httptest.NewRequest(
		http.MethodPost,
		"/api/platforms",
		strings.NewReader(body))
	req.Header.Set(echo.HeaderContentType, echo.MIMEApplicationJSON)
	rec := httptest.NewRecorder()
	c := e.NewContext(req, rec)
	w := &Web{mockNewDeployer()}
	return rec, c, w
}

func mockNewDeployer() *Deployer {
	clientSet := fake.NewSimpleClientset()
	return &Deployer{clientSet.CoreV1()}
}