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
package configuration

import (
	"carisa/core/boots"
	"carisa/deployer/global"
	"carisa/deployer/kubernetes"
)

type WebService interface {
	kubernetesWeb() *kubernetes.Web
}

type Service struct {
	config *global.ConfigContext
	log    *boots.LogWrap
}

func NewService(c *global.ConfigContext) *Service {
	return &Service{c, global.Log(c)}
}

func (s *Service) kubernetesWeb() *kubernetes.Web {
	return kubernetes.NewWeb(s.kubernetesDeployer(), s.log)
}

func (s *Service) kubernetesDeployer() *kubernetes.Deployer {
	deployer, err := kubernetes.NewDeployer(s.config, s.log)
	if err != nil {
		panic("error creating kubernetes deployer; error: " + err.Error())
	}
	return deployer
}
