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

package boots

import (
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

type config struct {
	Key  string
	Key1 string
}

func TestLoadConfigFromEnvironmentVariables(t *testing.T) {

	os.Setenv("CARISA_SN_CONFIG_TEST", "key: \"value\"\nkey1: \"value1\"")

	cnf := config{}

	LoadConfig("sn", false,"test", &cnf)

	assert.Equal(t, "value", cnf.Key, "should return the value from environment configuration")
	assert.Equal(t, "value1", cnf.Key1, "should return the value1 from environment configuration")
}

func TestLoadConfigFromLocal(t *testing.T) {

	cnf := config{}

	LoadConfig("sn", true, "test", &cnf)

	assert.Equal(t, "value", cnf.Key, "should return the value from local configuration")
	assert.Equal(t, "value1", cnf.Key1, "should return the value1 from local configuration")
}

func TestMainParams(t *testing.T) {

	sn, local, env := MainParams([]string{"sn", "true", "test"})

	assert.Equal(t, "sn", sn, "should return the service name")
	assert.Equal(t, true, local, "should return local equal true")
	assert.Equal(t, "test", env, "should return the environment name")
}

func TestMainParamsWithArgsWrong(t *testing.T) {
	assert.Panics(t, func() {MainParams([]string{"true", "test"})}, "should do panic")
}

func TestMainParamsWithVariableLocalWrong(t *testing.T) {
	assert.Panics(t, func() {MainParams([]string{"sn", "other", "test"})}, "should do panic")
}