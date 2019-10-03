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

// Dependencies controller
package global

import "carisa/deployer/kubernetes"

type service interface {
	kubernetesDeployer() *kubernetes.Deployer
	kubernetesWeb() *kubernetes.Web
}

type ServiceConfig struct {}

func (ServiceConfig) kubernetesDeployer() *kubernetes.Deployer  {
	deployer, err := kubernetes.NewDeployer(Config.Kubernetes.ConfigPath)
	if err != nil {
		panic("error creating kubernetes deployer; error: " +  err.Error())
	}
	return deployer
}

func (s *ServiceConfig) kubernetesWeb() *kubernetes.Web {
	return &kubernetes.Web{Deployer: s.kubernetesDeployer()}
}
