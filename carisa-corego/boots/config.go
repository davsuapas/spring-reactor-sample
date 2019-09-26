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

// Manage configurations through yaml files
package boots

import (
	"fmt"
	"gopkg.in/yaml.v3"
	"io/ioutil"
	"os"
	"strconv"
	"strings"
)

// LoadConfig loads configuration from yaml files or environment variables files
// depending of the local parameter.
// params is a array of serviceName, local and environment
// If local = true is loaded from yaml file otherwise it's loaded from environment variables.
// The files are located in resources path.
// 		Format of the file: /resources/config-<environment>.yaml
//		Format of environment variable: CARISA_<SERVICENAME>_CONFIG_<ENVIRONMENT>
// The result is included in data interface
func LoadConfig(params []string, out interface{}) {

	var config []byte
	var err error
	var resourceName string

	serviceName, local, environment := mainParams(params)

	if serviceName == "" {
		return // default values
	}

	if local {
		resourceName = fmt.Sprintf("./resources/config-%s.yaml", environment)
		if config, err = ioutil.ReadFile(resourceName); err != nil {
			panic("error reading config file: " + resourceName)
		}
	} else {
		resourceName = fmt.Sprintf(
			"CARISA_%s_CONFIG_%s",
			strings.ToUpper(serviceName),
			strings.ToUpper(environment))

		config = []byte(os.Getenv(resourceName))
	}

	if err = yaml.Unmarshal(config, out); err != nil {
		panic("error unmarshal config file: " + resourceName)
	}
}

// Getting main parameters of the service
func mainParams(params []string) (string, bool, string) {

	if len(params) == 1 {
		return "", false, ""
	}

	if len(params) < 3 {
		panic(
			`The args number is wrong.
		Use ./servicenname local:<true|false> environment:<string>
		Example: ./serviceName true development`)
	}

	serviceName := params[0]
	local, err := strconv.ParseBool(params[1])
	if err != nil {
		panic("the local parameter must be bool")
	}
	environment := params[2]

	return serviceName, local, environment
}
