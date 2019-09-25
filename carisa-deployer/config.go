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

// Manage several configuration environment
package carisa_deployer

import "carisa/core/boots"

type ConfigKubernetes struct {
	ConfigPath string `yaml:"configPath"`
}

type Config struct {
	Kubernetes ConfigKubernetes
}

// Load configuration of the service depending of environment
func LoadConfig(local bool, serviceName string, environment string) Config {
	cnf := Config{}
	boots.LoadConfig(local, serviceName, environment, &cnf)
	return cnf
}
