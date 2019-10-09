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
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestLoadConfig(t *testing.T) {
	cnf := LoadConfig([]string{"sn", "true", "test"})
	assert.Equal(t, "$HOME/.kube/config", cnf.Kubernetes.ConfigPath, "should return config path for kubernetes")
	assert.Equal(t, 1881, cnf.Server.Port, "should return server port")
	assert.Equal(t, "info", cnf.Log.Level, "should return log level")
}

func TestGetLog(t *testing.T) {
	log := Log(LoadConfig([]string{"sn", "true", "test"}))
	assert.Equal(t, "sn", log.Data["service"], "should return log configured")
}

func TestGetLogForTesting(t *testing.T) {
	assert.Equal(t, "service", LogTest().Data["service"], "should return log configured")
}
